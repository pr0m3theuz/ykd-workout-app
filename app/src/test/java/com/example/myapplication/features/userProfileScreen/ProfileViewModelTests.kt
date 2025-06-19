package com.example.myapplication.features.userProfileScreen

import app.cash.turbine.test
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ProfileViewModelTests {

    private lateinit var mockUserRepository: UserRepository
    private lateinit var viewModel: ProfileViewModel

    private val testDispatcher = StandardTestDispatcher() // Changed from UnconfinedTestDispatcher

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepository = mockk(relaxed = true) // relaxed = true to simplify mocking void returns
        viewModel = ProfileViewModel(mockUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial defaultWeightUnit is POUNDS when no user profile loaded`() = runTest {
        // ViewModel's init block sets default to POUNDS if profile loading is simplified
        assertEquals(MassUnit.POUNDS, viewModel.defaultWeightUnit.value)
    }

    @Test
    fun `updateDefaultWeightUnit updates StateFlow and UserRepository when profile exists`() = runTest {
        val userId = ObjectId()
        val initialProfile = UserProfile(id = userId, name = "Test User", defaultWeightUnit = MassUnit.KILOGRAMS)

        // Simulate profile being loaded
        coEvery { mockUserRepository.getUser(any()) } returns initialProfile
        // Manually set the profile in ViewModel after mock setup, as init might run before this setup.
        // Or, ensure init logic in ViewModel can be tested directly.
        // For this test, let's assume _userProfile can be set for testing or init is re-triggered/tested.

        // To properly test init, we'd need to control its execution or make loadUserProfile public.
        // Alternative: Directly set internal state for testing update function if init is complex.
        // For now, we will assume a profile is loaded into the viewModel's _userProfile StateFlow
        // One way to do this is to call a method that loads it, if such method is public
        // Or, if ProfileViewModel's constructor loaded it, then we'd need to ensure that happens.

        // Let's refine the test to directly manipulate the internal state for `_userProfile`
        // This is not ideal but works for testing `updateDefaultWeightUnit` in isolation of `init`.
        val internalUserProfileSetter = ProfileViewModel::class.java.getDeclaredField("_userProfile").apply {
            isAccessible = true
        }
        val internalDefaultWeightUnitSetter = ProfileViewModel::class.java.getDeclaredField("_defaultWeightUnit").apply {
            isAccessible = true
        }

        val mutableUserProfileFlow = MutableStateFlow<UserProfile?>(initialProfile)
        internalUserProfileSetter.set(viewModel, mutableUserProfileFlow)

        val mutableWeightUnitFlow = MutableStateFlow<MassUnit?>(initialProfile.defaultWeightUnit)
        internalDefaultWeightUnitSetter.set(viewModel, mutableWeightUnitFlow)

        coEvery { mockUserRepository.updateCurrentUser(any()) } just runs

        viewModel.defaultWeightUnit.test {
            assertEquals(MassUnit.KILOGRAMS, awaitItem()) // Initial value from manual setup

            viewModel.updateDefaultWeightUnit(MassUnit.POUNDS)
            testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines complete

            assertEquals(MassUnit.POUNDS, awaitItem()) // New value
            cancelAndConsumeRemainingEvents()
        }

        val expectedProfileToSave = initialProfile.copy(defaultWeightUnit = MassUnit.POUNDS)
        coVerify { mockUserRepository.updateCurrentUser(expectedProfileToSave) }
        assertEquals(expectedProfileToSave, viewModel.userProfile.value)
    }

    @Test
    fun `updateDefaultWeightUnit updates only local StateFlow when no profile`() = runTest {
        // Ensure _userProfile is null
         val internalUserProfileSetter = ProfileViewModel::class.java.getDeclaredField("_userProfile").apply {
            isAccessible = true
        }
        internalUserProfileSetter.set(viewModel, MutableStateFlow<UserProfile?>(null))

        // Ensure defaultWeightUnit is also null or some initial state if needed
        val internalDefaultWeightUnitSetter = ProfileViewModel::class.java.getDeclaredField("_defaultWeightUnit").apply {
            isAccessible = true
        }
        internalDefaultWeightUnitSetter.set(viewModel, MutableStateFlow<MassUnit?>(MassUnit.KILOGRAMS))


        viewModel.defaultWeightUnit.test {
            assertEquals(MassUnit.KILOGRAMS, awaitItem()) // Initial value

            viewModel.updateDefaultWeightUnit(MassUnit.POUNDS)
            testDispatcher.scheduler.advanceUntilIdle()

            assertEquals(MassUnit.POUNDS, awaitItem()) // New value updated locally
            cancelAndConsumeRemainingEvents()
        }

        coVerify(exactly = 0) { mockUserRepository.updateCurrentUser(any()) } // Verify no repo call
        assertNull(viewModel.userProfile.value) // Profile should still be null
    }

    // TODO: Add tests for the init block's user loading logic once it's more defined
    // e.g., how it fetches/sets UserProfile and defaultWeightUnit on startup.
    // This would require a more sophisticated way to mock user retrieval during init.
}
