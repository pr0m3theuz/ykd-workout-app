@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.features.workout.ui

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Looper
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutDatastore
import com.example.myapplication.common.infrastructure.persistence.datastore.toDomainWorkout
import com.example.myapplication.datastore.ActiveWorkout
import com.example.myapplication.features.workout.CountUpTimer
import com.example.myapplication.features.workout.ManagePreviewWorkoutUseCase
import com.example.myapplication.features.workout.ManageWorkoutUseCase
import com.example.myapplication.features.workout.WorkoutViewModel
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
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
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.core.context.GlobalContext.stopKoin
import tech.apter.junit.jupiter.robolectric.RobolectricExtension
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(RobolectricExtension::class)
class WorkoutUserJourneyTest {

  private lateinit var application: Application
  private lateinit var manageWorkoutUseCase: ManageWorkoutUseCase
  private lateinit var managePreviewWorkoutUseCase: ManagePreviewWorkoutUseCase
  private lateinit var exerciseRepository: ExercisesRepository
  private lateinit var workoutDatastore: WorkoutDatastore
  private lateinit var userProfileManager: UserProfileManager

  @get:Rule
  val composeTestRule = createComposeRule()

  // We use a test dispatcher so we can manipulate time in the UI
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
//    val mockAlarmManager = mockk<AlarmManager>(relaxed = true)

    application = mockk<Application>(relaxed = true) {
      every { getSystemService(NotificationManager::class.java) } returns mockNotificationManager
//      every { getSystemService(AlarmManager::class.java) } returns mockAlarmManager
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
  fun impatientUserJourney_clicksStartBeforeDataStoreLoads_keepsNewWorkout() =
    runTest(testDispatcher) {
      // 1. Setup
      mockkStatic("com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutMappersKt")

      val staleDomainWorkout = mockk<Workout>(relaxed = true) {
        every { name } returns "Yesterday's Unfinished Workout"
        every { isCompleted } returns false
        every { workoutStarted } returns Clock.System.now()
        every { workoutEnded } returns null
        every { notes } returns emptyList()
        every { workoutItems } returns emptyList()
      }
      val staleWorkoutProto = mockk<ActiveWorkout>(relaxed = true)
      every { staleWorkoutProto == ActiveWorkout.getDefaultInstance() } returns false
      every { staleWorkoutProto.toDomainWorkout() } returns staleDomainWorkout

      val slowDataStore = mockk<WorkoutDatastore>(relaxed = true)
      every { slowDataStore.getActiveWorkout } returns flow {
        delay(2000)
        emit(staleWorkoutProto)
      }

      val viewModel = createViewModel(workoutDatastore = slowDataStore) // assumes userProfile is stubbed inside

      // 2. Mount UI
      // TODO UI State is not currently implemented
      composeTestRule.setContent {
        val uiState by viewModel.uiState.collectAsState()
        when {
          uiState.isLoading -> Text("Loading Workout...")
          else -> {
            Text("Current: ${uiState.workoutName}")
            Button(onClick = { viewModel.initWorkoutData() }) {
              Text("Start Fresh Workout")
            }
          }
        }
      }

      // Step A: Loading state visible — DataStore hasn't emitted yet
      composeTestRule.waitForIdle()
      composeTestRule.onNodeWithText("Loading Workout...").assertIsDisplayed()

      // Step B: User taps before DataStore resolves
      // Advance coroutine time slightly but NOT past the 2000ms delay
      advanceTimeBy(500) // ✅ coroutine virtual time, not mainClock
      composeTestRule.waitForIdle()

      viewModel.initWorkoutData() // isWorkoutActive = true, _workoutName = time-based template
      composeTestRule.waitForIdle()

      // Step C: UI shows fresh workout — time-agnostic assertion
      val freshName = viewModel.workoutName.value
      Assertions.assertTrue(
        freshName?.contains("Workout") == true,
        "Expected a time-based template name, got: $freshName"
      )
      composeTestRule.onNodeWithText("Current: $freshName").assertIsDisplayed()

      // Step D: DataStore finally emits the stale workout
      advanceTimeBy(2000) // ✅ triggers delay(2000) in the flow
      composeTestRule.waitForIdle()

      // Step E: Guard holds — stale DataStore value did NOT overwrite the user's fresh workout
      composeTestRule.onNodeWithText("Current: $freshName").assertIsDisplayed()
      composeTestRule.onNodeWithText("Current: Yesterday's Unfinished Workout")
        .assertDoesNotExist()

      // Step F: isWorkoutActive stayed true — the guard worked
      Assertions.assertTrue(
        viewModel.isWorkoutActive,
        "isWorkoutActive must remain true — DataStore must not have reset it"
      )
    }

  @Test
  fun processDeathJourney_recoversWorkoutSuccessfully() = runTest(testDispatcher) {
    // 1. Setup
    mockkStatic("com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutMappersKt")
    mockkStatic(PendingIntent::class)
    every { PendingIntent.getBroadcast(any(), any(), any(), any()) } returns mockk(relaxed = true)

    val mockDomainWorkout = mockk<Workout>(relaxed = true) {
      every { name } returns "Leg Day Heavy"
      every { isCompleted } returns false
      every { workoutStarted } returns Clock.System.now()
      every { workoutEnded } returns null
      every { notes } returns emptyList()
      every { workoutItems } returns emptyList()
    }
    val activeWorkoutProto = mockk<ActiveWorkout>(relaxed = true)
    every { activeWorkoutProto == ActiveWorkout.getDefaultInstance() } returns false
    every { activeWorkoutProto.toDomainWorkout() } returns mockDomainWorkout

    val viewModel = createViewModel(workoutDatastore = workoutDatastore)

    // 2. Mount UI
    // TODO UI State is not currently implemented
    composeTestRule.setContent {
      val uiState by viewModel.uiState.collectAsState()
      if (uiState.isLoading) Text("Loading Workout...")
      else Text("Current: ${uiState.workoutName}")
    }

    advanceUntilIdle()
    composeTestRule.waitForIdle()

    composeTestRule.onNodeWithText("Current: Leg Day Heavy").assertIsDisplayed()
  }

  // Helper to construct the ViewModel
  private fun createViewModel(
    savedStateHandle: SavedStateHandle = SavedStateHandle(),
    workoutDatastore: WorkoutDatastore,
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