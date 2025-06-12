package com.example.myapplication.features.userProfileScreen

import app.cash.turbine.test
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var mockUserRepository: UserRepository
    private lateinit var mockUserProfileManager: UserProfileManager

    private val initialUserProfile = UserProfile(id = ObjectId(), name = "Test User", defaultRestTimerDurationMillis = 50000L, timerAdjustmentMillis = 20000L)
    private val userProfileFlow = MutableStateFlow<UserProfile?>(null) // Start with null initially then set

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepository = mock()
        mockUserProfileManager = mock()
        whenever(mockUserProfileManager.userProfile).thenReturn(userProfileFlow)

        // ViewModel instantiation will trigger init and loadUserProfile
        viewModel = ProfileViewModel(mockUserRepository, mockUserProfileManager)
        // Set initial profile value *after* ViewModel is created and starts collecting
        userProfileFlow.value = initialUserProfile
        testDispatcher.scheduler.advanceUntilIdle() // Ensure initial collection and map operations complete
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `userProfile StateFlow correctly emits value from UserProfileManager`() = runTest(testDispatcher) {
        viewModel.userProfile.test {
            assertEquals(initialUserProfile, awaitItem()) // Initial value after setup

            userProfileFlow.value = null
            testDispatcher.scheduler.advanceUntilIdle()
            assertNull(awaitItem())

            val newUser = UserProfile(id = ObjectId(), name = "New User")
            userProfileFlow.value = newUser
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(newUser, awaitItem())
        }
    }

    @Test
    fun `defaultRestTimerDurationMillis StateFlow emits correct initial and updated values`() = runTest(testDispatcher) {
        viewModel.defaultRestTimerDurationMillis.test {
            assertEquals(50000L, awaitItem()) // From initialUserProfile

            val updatedProfile = initialUserProfile.copy(defaultRestTimerDurationMillis = 70000L)
            userProfileFlow.value = updatedProfile
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(70000L, awaitItem())
        }
    }

    @Test
    fun `timerAdjustmentMillis StateFlow emits correct initial and updated values`() = runTest(testDispatcher) {
        viewModel.timerAdjustmentMillis.test {
            assertEquals(20000L, awaitItem()) // From initialUserProfile

            val updatedProfile = initialUserProfile.copy(timerAdjustmentMillis = 25000L)
            userProfileFlow.value = updatedProfile
            testDispatcher.scheduler.advanceUntilIdle()
            assertEquals(25000L, awaitItem())
        }
    }

    @Test
    fun `updateDefaultRestTimerDuration updates user profile and repository`() = runTest(testDispatcher) {
        val newDuration = 75000L
        viewModel.updateDefaultRestTimerDuration(newDuration)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedUser = initialUserProfile.copy(defaultRestTimerDurationMillis = newDuration)
        verify(mockUserRepository).updateCurrentUser(expectedUser)
        assertEquals(newDuration, viewModel.userProfile.value?.defaultRestTimerDurationMillis)
    }

    @Test
    fun `updateTimerAdjustment updates user profile and repository`() = runTest(testDispatcher) {
        val newAdjustment = 15000L
        viewModel.updateTimerAdjustment(newAdjustment)
        testDispatcher.scheduler.advanceUntilIdle()

        val expectedUser = initialUserProfile.copy(timerAdjustmentMillis = newAdjustment)
        verify(mockUserRepository).updateCurrentUser(expectedUser)
        assertEquals(newAdjustment, viewModel.userProfile.value?.timerAdjustmentMillis)
    }

    @Test
    fun `StateFlows emit default values when UserProfile is null`() = runTest(testDispatcher) {
        userProfileFlow.value = null
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.defaultRestTimerDurationMillis.test {
            assertEquals(60000L, awaitItem())
        }
        viewModel.timerAdjustmentMillis.test {
            assertEquals(30000L, awaitItem())
        }
    }
}
