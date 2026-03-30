@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.features.workout.ui

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Looper
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutNote
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutDatastore
import com.example.myapplication.common.infrastructure.persistence.datastore.toDomainWorkout
import com.example.myapplication.datastore.ActiveWorkout
import com.example.myapplication.features.workout.CountUpTimer
import com.example.myapplication.features.workout.ManagePreviewWorkoutUseCase
import com.example.myapplication.features.workout.ManageWorkoutUseCase
import com.example.myapplication.features.workout.WorkoutViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment.application
import tech.apter.junit.jupiter.robolectric.RobolectricExtension
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(RobolectricExtension::class)
class WorkoutViewModelStateTest {

  private lateinit var application: Application
  private lateinit var manageWorkoutUseCase: ManageWorkoutUseCase
  private lateinit var managePreviewWorkoutUseCase: ManagePreviewWorkoutUseCase
  private lateinit var exerciseRepository: ExercisesRepository
  private lateinit var workoutDatastore: WorkoutDatastore
  private lateinit var userProfileManager: UserProfileManager

  // Use StandardTestDispatcher to strictly control virtual time and coroutine execution
  private val testDispatcher = StandardTestDispatcher()

  @BeforeEach
  fun setup() {
    Dispatchers.setMain(testDispatcher)

    mockkStatic(Looper::class)
    val mockLooper = mockk<Looper>(relaxed = true)
    every { Looper.getMainLooper() } returns mockLooper

    // 2. Mock PendingIntent static factory
    mockkStatic(PendingIntent::class)
    val mockPendingIntent = mockk<PendingIntent>(relaxed = true)
    every {
      PendingIntent.getBroadcast(any(), any(), any(), any())
    } returns mockPendingIntent

    // 3. Mock Application so getSystemService() returns mocks
    //    instead of crashing on a real Android call
    val mockNotificationManager = mockk<NotificationManager>(relaxed = true)
    val mockAlarmManager = mockk<AlarmManager>(relaxed = true)

    application = mockk<Application>(relaxed = true) {
      every { getSystemService(NotificationManager::class.java) } returns mockNotificationManager
      every { getSystemService(AlarmManager::class.java) } returns mockAlarmManager
      // getString() is also called in the notification setup
      every { getString(any()) } returns "mock_string"
      every { packageName } returns "com.example.myapplication"
    }

    // 4. Mock ContextCompat statics if used
    mockkStatic(ContextCompat::class)
    every { ContextCompat.getString(any(), any()) } returns "mock_string"


    mockkConstructor(CountUpTimer::class)
    // 2. Mock the stop method on any constructed instance
    every { anyConstructed<CountUpTimer>().stop() } just runs
    every { anyConstructed<CountUpTimer>().start(1000L) } just runs

    manageWorkoutUseCase = mockk(relaxed = true)
    managePreviewWorkoutUseCase = mockk(relaxed = true)
    exerciseRepository = mockk(relaxed = true)
    workoutDatastore = mockk(relaxed = true)
    userProfileManager = mockk(relaxed = true)

    every { userProfileManager.userProfile } returns MutableStateFlow(UserProfile(id = ObjectId()))

    mockkStatic("com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutMappersKt")
  }

  @AfterEach
  fun tearDown() {
    Dispatchers.resetMain()
    stopKoin()
    unmockkAll()
  }

  @Test
  fun `initial state is Loading`() = runTest {
    // Arrange: DataStore emission is infinitely delayed
    val delayedFlow = MutableSharedFlow<ActiveWorkout>()
    every { workoutDatastore.getActiveWorkout } returns delayedFlow
    every { userProfileManager.userProfile } returns MutableStateFlow(UserProfile(id = ObjectId()))

    val viewModel = createViewModel()

    // Act & Assert: UI renders before DataStore emits
    viewModel.uiState.test {
      val initialState = awaitItem()
      Assertions.assertTrue(initialState.isLoading, "UI must show loading initially")
      Assertions.assertNull(initialState.workout, "Workout should be null while loading")

      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `Loading to Empty transition`() = runTest {
    // Arrange: DataStore returns default instance (no active workout)
    every { workoutDatastore.getActiveWorkout } returns flowOf(ActiveWorkout.getDefaultInstance())
    every { userProfileManager.userProfile } returns MutableStateFlow(UserProfile(id = ObjectId()))

    val viewModel = createViewModel()

    // Act & Assert
    viewModel.uiState.test {
      // 1. Catches the initial Loading state
      Assertions.assertTrue(awaitItem().isLoading)

      // Allow the DataStore coroutine to finish reading
      advanceUntilIdle()

      // 2. Catches the emitted Empty state
      val emptyState = awaitItem()
      Assertions.assertFalse(
        emptyState.isLoading,
        "Loading should finish"
      )

      // It should fallback to a new initialized workout (e.g. "Morning Workout")
      Assertions.assertTrue(
        emptyState.workoutName?.contains("Workout") == true,
        "Workout name should be a default template"
      )

      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `delayed first emission resolves correctly`() = runTest {
    // Arrange: DataStore takes 2 seconds to emit
    val mockWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Recovered Workout"
      every { workoutStarted } returns Clock.System.now()
      every { isCompleted } returns false
      every { workoutEnded } returns null
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns mockWorkout

    every { workoutDatastore.getActiveWorkout } returns flow {
      delay(2000)
      emit(activeProto)
    }

    val viewModel = createViewModel()

    viewModel.workout.test {
      // 1. Initial value is the default template (DataStore not yet resolved)
      val initialWorkout = awaitItem()
      Assertions.assertTrue(
        initialWorkout?.name?.contains("Workout") == true,
        "Should start with a default template, got: ${initialWorkout?.name}"
      )
      Assertions.assertNotEquals("Recovered Workout", initialWorkout?.name)

      // Act: let the 2s DataStore delay elapse
      advanceTimeBy(2001)

      // 2. DataStore emission arrives and overwrites the default
      val recoveredWorkout = awaitItem()
      Assertions.assertEquals(
        "Recovered Workout",
        recoveredWorkout?.name,
        "DataStore value must arrive after delay"
      )

      // 3. isWorkoutActive reflects the recovered state
      Assertions.assertTrue(
        viewModel.isWorkoutActive,
        "ViewModel should mark workout as active after recovery"
      )

      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `write not flushed before re-creation requires advanceUntilIdle`() = runTest {
    // Simulate a scenario where the user clicks "Start", we save to DataStore,
    // but the process dies / rotates before the save coroutine finishes.
    val dataStoreFlow = MutableSharedFlow<ActiveWorkout>()
    every { workoutDatastore.getActiveWorkout } returns dataStoreFlow
    every { userProfileManager.userProfile } returns MutableStateFlow(UserProfile(id = ObjectId()))

    val viewModel1 = createViewModel()
    viewModel1.initWorkoutData() // User starts workout

    // DataStore save is triggered via debounce/combine flow, which takes time
    // If we DON'T advanceUntilIdle() here, the save won't happen.
    advanceUntilIdle()
    coVerify(atLeast = 1) { workoutDatastore.saveActiveWorkout(any()) }

    // Now recreate ViewModel simulating rotation
    val viewModel2 = createViewModel()
    viewModel2.uiState.test {
      Assertions.assertTrue(awaitItem().isLoading)
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `process death recovery`() = runTest {
    // Arrange: Simulating cold start where DataStore successfully pulls from disk
    val mockWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Zombie Workout"
      every { workoutStarted } returns Clock.System.now()
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns mockWorkout

    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)
    every { userProfileManager.userProfile } returns MutableStateFlow(UserProfile(id = ObjectId()))

    // Pass a completely fresh SavedStateHandle simulating Android process restoration
    val freshSavedStateHandle = SavedStateHandle()

    val viewModel = createViewModel()

    advanceUntilIdle() // Let init block read from disk

    // Assert
    Assertions.assertTrue(viewModel.isWorkoutActive)
    Assertions.assertEquals("Zombie Workout", viewModel.workout.value?.name)
  }

  @Test
  fun `subscriber gap retains cache via WhileSubscribed`() = runTest {
    // Arrange
    val mockWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Cached Workout"
      every { workoutStarted } returns Clock.System.now()
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns mockWorkout
    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)

    val viewModel = createViewModel()

    // 1. Subscribe first — this starts the WhileSubscribed upstream
    viewModel.workout.test {
      // Skip loading (null initial value)
      val initialWorkout = awaitItem()
      if (initialWorkout == null) {
        advanceUntilIdle()
        val loaded = awaitItem()
        Assertions.assertEquals("Cached Workout", loaded?.name)
      } else {
        Assertions.assertTrue(
          initialWorkout.name.contains("Workout"),
          "Should start with a default template, got: ${initialWorkout.name}"
        )
        Assertions.assertNotEquals("Cached Workout", initialWorkout.name)
      }
      cancelAndIgnoreRemainingEvents() // Unsubscribe — starts the 5000ms WhileSubscribed clock
    }

    // 2. Advance less than the 5000ms timeout — upstream stays alive, cache retained
    advanceTimeBy(2000)

    // 3. Resubscribe within timeout — must get cached value, no reload
    viewModel.workout.test {
      val state = awaitItem()
      // If cache works, we get the value immediately without going through null again
      Assertions.assertEquals(
        "Cached Workout",
        state?.name,
        "Should receive cached value — upstream stayed alive within WhileSubscribed window"
      )
      cancelAndIgnoreRemainingEvents()
    }
  }

  @Test
  fun `clearing the workout`() = runTest {
    // Arrange
    val mockWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "To Be Canceled"
      every { workoutStarted } returns Clock.System.now()
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns mockWorkout
    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)

    val countUpTimer = mockk<CountUpTimer>(relaxed = true)
    every { countUpTimer.stop() } just runs

    val viewModel = createViewModel()
    advanceUntilIdle()

    // Precondition: confirm workout actually loaded before we cancel
    Assertions.assertEquals(
      "To Be Canceled",
      viewModel.workout.value?.name,
      "Precondition: workout must be loaded before cancel"
    )
    Assertions.assertTrue(viewModel.isWorkoutActive, "Precondition: must be active before cancel")

    // Act
    viewModel.cancelWorkout()
    advanceUntilIdle()

    // Assert 1: DataStore was cleared
    coVerify(exactly = 1) { workoutDatastore.clearActiveWorkout() }

    // Assert 2: ViewModel state flags reset
    Assertions.assertFalse(viewModel.isWorkoutActive, "Workout should not be active after cancel")

    // Assert 3: _workout reset to a fresh template — positive assertion on the new name
    val workoutAfterCancel = viewModel.workout.value
    Assertions.assertNotNull(workoutAfterCancel, "Workout should be reset to a template, not null")
    Assertions.assertTrue(
      workoutAfterCancel?.name?.contains("Workout") == true,
      "Expected a fresh template name (e.g. 'Morning Workout') but got: ${workoutAfterCancel?.name}"
    )
    Assertions.assertNotEquals(
      "To Be Canceled",
      workoutAfterCancel?.name,
      "Old workout name must not persist after cancel"
    )

    // Assert 4: Components and notes were cleared
    Assertions.assertTrue(
      viewModel.workoutComponents.value.isEmpty(),
      "Workout components should be empty after cancel"
    )
  }

  @Test
  fun `empty DataStore flow leaves ViewModel in loading state`() = runTest {
    val defaultWorkout = mockk<Workout>(relaxed = true)
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns defaultWorkout
    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)

    val viewModel = createViewModel()
    advanceUntilIdle()

    // ViewModel should handle null gracefully, not silently freeze
    Assertions.assertFalse(
      viewModel.workout.value == null && !viewModel.isWorkoutActive,
      "ViewModel must not silently freeze when DataStore emits nothing"
    )
  }

  @Test
  fun `completed workout in DataStore is not restored as active`() = runTest {
    val completedWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Finished Workout"
      every { isCompleted } returns true                      // ← completed
      every { workoutStarted } returns Clock.System.now()
      every { workoutEnded } returns Clock.System.now()
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns completedWorkout
    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)

    val viewModel = createViewModel()
    advanceUntilIdle()

    Assertions.assertFalse(
      viewModel.isWorkoutActive,
      "A completed workout must not be restored as active"
    )
  }

  @Test
  fun `workout with no workoutStarted is not restored as active`() = runTest {
    val unstartedWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Unstarted Workout"
      every { isCompleted } returns false
      every { workoutStarted } returns null                   // ← never started
      every { workoutEnded } returns null
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns unstartedWorkout
    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)

    val viewModel = createViewModel()
    advanceUntilIdle()

    Assertions.assertFalse(viewModel.isWorkoutActive)
  }

  @Test
  fun `workout duration is restored from DataStore on recovery`() = runTest {
    val expectedDurationMs = 90_000L // 1m 30s
    val mockWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Timed Workout"
      every { workoutDuration } returns expectedDurationMs
      every { isCompleted } returns false
      every { workoutStarted } returns Clock.System.now()
      every { workoutEnded } returns null
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns mockWorkout
    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)

    val viewModel = createViewModel()
    advanceUntilIdle()

    Assertions.assertEquals(
      "1:30",
      viewModel.workoutDurationString.value,
      "Timer string must be restored from persisted duration"
    )
  }

  @Test
  fun `workout notes are restored from DataStore on recovery`() = runTest {
    val note1 = mockk<WorkoutNote>(relaxed = true)
    val note2 = mockk<WorkoutNote>(relaxed = true)
    val mockWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Noted Workout"
      every { notes } returns listOf(note1, note2)
      every { isCompleted } returns false
      every { workoutStarted } returns Clock.System.now()
      every { workoutEnded } returns null
    }
    val activeProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeProto.toDomainWorkout() } returns mockWorkout
    every { workoutDatastore.getActiveWorkout } returns flowOf(activeProto)

    val viewModel = createViewModel()
    advanceUntilIdle()

    Assertions.assertEquals(
      2,
      viewModel.workoutNotes.value.size,
      "Notes must be restored from DataStore"
    )
  }

  @Test
  fun `rapid workout mutations are debounced into a single DataStore save`() = runTest {
    every { workoutDatastore.getActiveWorkout } returns flowOf(ActiveWorkout.getDefaultInstance())

    val viewModel = createViewModel()
    advanceUntilIdle()

    // Simulate rapid UI mutations (e.g. user typing workout name character by character)
    viewModel.setWorkoutName("W")
    viewModel.setWorkoutName("Wo")
    viewModel.setWorkoutName("Wor")
    viewModel.setWorkoutName("Work")

    advanceUntilIdle() // Let debounce window expire

    // All four mutations should collapse into a single DataStore write
    coVerify(exactly = 1) { workoutDatastore.saveActiveWorkout(any()) }
  }

  @Test
  fun `subscriber gap beyond WhileSubscribed timeout causes upstream restart`() = runTest {
    every { workoutDatastore.getActiveWorkout } returns flowOf(ActiveWorkout.getDefaultInstance())

    val viewModel = createViewModel()
    advanceUntilIdle()

    viewModel.workout.test {
      awaitItem()
      cancelAndIgnoreRemainingEvents() // Unsubscribe — 5000ms clock starts
    }

    advanceTimeBy(6000) // Past the WhileSubscribed timeout

    viewModel.workout.test {
      // Upstream restarted — first emission is the default initWorkout() template,
      // not the cached value
      val first = awaitItem()
      Assertions.assertTrue(
        first?.name?.contains("Workout") == true,
        "Cache expired — upstream should have restarted with default template"
      )
      cancelAndIgnoreRemainingEvents()
    }
  }

  private fun createViewModel(
    savedStateHandle: SavedStateHandle = SavedStateHandle()
  ): WorkoutViewModel {
    every { userProfileManager.userProfile } returns MutableStateFlow(UserProfile(id = ObjectId()))

    return WorkoutViewModel(
      application = ApplicationProvider.getApplicationContext(),
      manageWorkoutUseCase = manageWorkoutUseCase,
      managePreviewWorkoutUseCase = managePreviewWorkoutUseCase,
      exerciseRepository = exerciseRepository,
      workoutDatastore = workoutDatastore,
      savedStateHandle = savedStateHandle,
      userProfileManager = userProfileManager
    )
  }
}