package com.example.myapplication.features.workout

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutDatastore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class WorkoutViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    // Mocks
    private lateinit var mockApplication: Application
    private lateinit var mockManageWorkoutUseCase: ManageWorkoutUseCase
    private lateinit var mockManagePreviewWorkoutUseCase: ManagePreviewWorkoutUseCase
    private lateinit var mockExerciseRepository: ExercisesRepository
    private lateinit var mockWorkoutDatastore: WorkoutDatastore
    private lateinit var mockSavedStateHandle: SavedStateHandle
    private lateinit var mockUserProfileManager: UserProfileManager

    // ViewModel
    private lateinit var viewModel: WorkoutViewModel

    // UserProfile related
    private val userProfileFlow = MutableStateFlow<UserProfile?>(null)
    private val defaultUserProfile = UserProfile(
        id = ObjectId(),
        defaultRestTimerDurationMillis = 50000L,
        timerAdjustmentMillis = 20000L
    )
    private val userProfileWithDifferentTimers = UserProfile(
        id = ObjectId(),
        defaultRestTimerDurationMillis = 70000L,
        timerAdjustmentMillis = 25000L
    )


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mock()
        mockManageWorkoutUseCase = mock()
        mockManagePreviewWorkoutUseCase = mock()
        mockExerciseRepository = mock()
        mockWorkoutDatastore = mock() // Mock the WorkoutDatastore
        mockSavedStateHandle = mock() // Standard mock, will define 'get' behavior per test
        mockUserProfileManager = mock()

        // Default behavior for UserProfileManager
        whenever(mockUserProfileManager.userProfile).thenReturn(userProfileFlow)
        // Default behavior for WorkoutDatastore to avoid issues during init
        whenever(mockWorkoutDatastore.getActiveWorkout).thenReturn(MutableStateFlow(com.example.myapplication.datastore.ActiveWorkout.getDefaultInstance()))


        // ViewModel needs to be created after mocks are set up, especially UserProfileManager
        // but before userProfileFlow has a value if we want to test initial null user state.
    }

    private fun initializeViewModel() {
        viewModel = WorkoutViewModel(
            application = mockApplication,
            manageWorkoutUseCase = mockManageWorkoutUseCase,
            managePreviewWorkoutUseCase = mockManagePreviewWorkoutUseCase,
            exerciseRepository = mockExerciseRepository,
            workoutDatastore = mockWorkoutDatastore,
            savedStateHandle = mockSavedStateHandle,
            userProfileManager = mockUserProfileManager
        )
        testDispatcher.scheduler.advanceUntilIdle() // Allow init block to run
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Tests for _timerDuration
    @Test
    fun `_timerDuration uses UserProfile value when SavedStateHandle is empty`() = runTest {
        userProfileFlow.value = defaultUserProfile // defaultRestTimerDurationMillis = 50000L
        whenever(mockSavedStateHandle.get<Long>(TIMER_DURATION)).thenReturn(null)
        initializeViewModel()

        // _timerDuration's initial value is 50000L.
        // _timerDurationFormatted becomes "0:50" (50000ms / 1000 = 50s; 0min 50sec)
        // _timerDurationString becomes "0:50"
        assertEquals("0:50", viewModel.timerDurationString.value)
    }

    @Test
    fun `_timerDuration uses SavedStateHandle value over UserProfile value`() = runTest {
        userProfileFlow.value = userProfileWithDifferentTimers // UserProfile has 70000L
        whenever(mockSavedStateHandle.get<Long>(TIMER_DURATION)).thenReturn(90000L) // SSH has 90000L
        initializeViewModel()

        // _timerDuration's initial value is 90000L.
        // _timerDurationFormatted becomes "1:30" (90000ms / 1000 = 90s; 1min 30sec)
        // _timerDurationString becomes "1:30"
        assertEquals("1:30", viewModel.timerDurationString.value)
    }

    @Test
    fun `_timerDuration uses hardcoded default when SavedStateHandle and UserProfile are unavailable`() = runTest {
        userProfileFlow.value = null // UserProfile is null
        whenever(mockSavedStateHandle.get<Long>(TIMER_DURATION)).thenReturn(null) // SSH is empty
        initializeViewModel()

        // _timerDuration's initial value is 60000L.
        // _timerDurationFormatted becomes "1:00" (60000ms / 1000 = 60s; 1min 0sec)
        // _timerDurationString becomes "1:00"
        assertEquals("1:00", viewModel.timerDurationString.value)
    }

    @Test
    fun `_timerDuration uses hardcoded default when UserProfile field is default and SavedStateHandle is null`() = runTest {
        // UserProfile() uses 60000L for defaultRestTimerDurationMillis by default
        userProfileFlow.value = UserProfile(id = ObjectId())
        whenever(mockSavedStateHandle.get<Long>(TIMER_DURATION)).thenReturn(null)
        initializeViewModel()

        // _timerDuration's initial value is 60000L.
        assertEquals("1:00", viewModel.timerDurationString.value)
    }

    // Tests for _restTimerAdjustment
    @Test
    fun `_restTimerAdjustment uses UserProfile value`() = runTest {
        userProfileFlow.value = defaultUserProfile // timerAdjustmentMillis = 20000L
        initializeViewModel()

        // _restTimerAdjustment is 20000L (20 seconds)
        // restTimerAdjustmentString becomes "20"
        assertEquals("20", viewModel.restTimerAdjustmentString)
    }

    @Test
    fun `_restTimerAdjustment uses hardcoded default when UserProfile is unavailable`() = runTest {
        userProfileFlow.value = null // UserProfile is null
        initializeViewModel()

        // _restTimerAdjustment is 30000L (30 seconds)
        assertEquals("30", viewModel.restTimerAdjustmentString)
    }

    @Test
    fun `_restTimerAdjustment uses hardcoded default when UserProfile field is default`() = runTest {
        // UserProfile() uses 30000L for timerAdjustmentMillis by default
        userProfileFlow.value = UserProfile(id = ObjectId())
        initializeViewModel()

        // _restTimerAdjustment is 30000L (30 seconds)
        assertEquals("30", viewModel.restTimerAdjustmentString)
    }
}
