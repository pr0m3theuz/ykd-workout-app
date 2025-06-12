package com.example.myapplication.features.userProfileScreen

import app.cash.turbine.test
import com.example.myapplication.common.domain.model.Barbell
import com.example.myapplication.common.domain.model.Collar
import com.example.myapplication.common.domain.model.Plate
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
verify // Fix for lint, "verify" is a mockk import but also a common word.
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var mockUserRepository: UserRepository
    private val testDispatcher = StandardTestDispatcher()

    private val placeholderUserIdString = "655ec82378551c270558a152"
    private val placeholderObjectId = ObjectId(placeholderUserIdString)

    // Helper for matching ObjectId in MockK
    private fun matchObjectId(expectedId: ObjectId): UserProfile {
        return io.mockk.match { it.id == expectedId }
    }
     private fun matchUserId(expectedIdString: String): UserProfile {
        return io.mockk.match { it.id.toHexString() == expectedIdString }
    }


    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockUserRepository = mockk(relaxed = true) // relaxed for some void returns
    }

    private fun setupViewModelWithMockUser(userProfile: UserProfile?) {
        coEvery { mockUserRepository.getUser(matchUserId(placeholderUserIdString)) } returns userProfile
        viewModel = ProfileViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle() // Ensure init coroutine completes
    }
     private fun setupViewModelWithNoUser() {
        coEvery { mockUserRepository.getUser(matchUserId(placeholderUserIdString)) } returns null
        viewModel = ProfileViewModel(mockUserRepository)
        testDispatcher.scheduler.advanceUntilIdle()
    }


    @Test
    fun `init loads user profile and equipment correctly when user exists`() = runTest {
        val plates = listOf(Plate("p1", 10f, "kg", 2, "#FF0000"))
        val barbells = listOf(Barbell("b1", 20f, "kg"))
        val collars = listOf(Collar("c1", 2.5f, "kg"))
        val mockProfile = UserProfile(
            id = placeholderObjectId,
            name = "Test User",
            plates = plates,
            barbells = barbells,
            collars = collars
        )
        setupViewModelWithMockUser(mockProfile)

        assertEquals(mockProfile, viewModel.userProfile.value)
        assertEquals(plates, viewModel.plates.value)
        assertEquals(barbells, viewModel.barbells.value)
        assertEquals(collars, viewModel.collars.value)
        coVerify { mockUserRepository.getUser(matchUserId(placeholderUserIdString)) }
    }

    @Test
    fun `init sets null profile and empty lists when user not found`() = runTest {
        setupViewModelWithNoUser()

        assertNull(viewModel.userProfile.value)
        assertTrue(viewModel.plates.value.isEmpty())
        assertTrue(viewModel.barbells.value.isEmpty())
        assertTrue(viewModel.collars.value.isEmpty())
        coVerify { mockUserRepository.getUser(matchUserId(placeholderUserIdString)) }
    }

    // --- Plate Tests ---
    @Test
    fun `addPlate when user exists calls repository and updates StateFlow`() = runTest {
        val initialPlate = Plate("p1", 10f, "kg", 1, "#FF0000")
        val mockProfile = UserProfile(id = placeholderObjectId, name = "Test User", plates = mutableListOf(initialPlate))
        setupViewModelWithMockUser(mockProfile)

        val newPlate = Plate("p2", 20f, "kg", 2, "#00FF00")
        // Mock the repository addPlate call, important: ensure relaxed=true or define behavior for all repo calls
        coEvery { mockUserRepository.addPlate(placeholderUserIdString, newPlate) } just Runs

        viewModel.addPlate(newPlate)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUserRepository.addPlate(placeholderUserIdString, newPlate) }

        viewModel.plates.test {
            val currentList = awaitItem()
            assertTrue(currentList.contains(initialPlate)) // Should still have initial
            assertTrue(currentList.contains(newPlate)) // Should now contain new
            assertEquals(2, currentList.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `addPlate when user is null does not call repository`() = runTest {
        setupViewModelWithNoUser() // User is null

        val newPlate = Plate("p2", 20f, "kg", 2, "#00FF00")
        viewModel.addPlate(newPlate)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { mockUserRepository.addPlate(any(), any()) }
        assertTrue(viewModel.plates.value.isEmpty()) // List should remain empty
    }

    @Test
    fun `removePlate when user exists calls repository and updates StateFlow`() = runTest {
        val plateToRemove = Plate("p1", 10f, "kg", 1, "#FF0000")
        val otherPlate = Plate("p2", 25f, "kg", 2, "#0000FF")
        val mockProfile = UserProfile(id = placeholderObjectId, name = "Test User", plates = mutableListOf(plateToRemove, otherPlate))
        setupViewModelWithMockUser(mockProfile)

        coEvery { mockUserRepository.removePlate(placeholderUserIdString, "p1") } just Runs

        viewModel.removePlate("p1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUserRepository.removePlate(placeholderUserIdString, "p1") }

        viewModel.plates.test {
            val currentList = awaitItem()
            assertFalse(currentList.any {it.id == "p1"})
            assertTrue(currentList.any {it.id == "p2"})
            assertEquals(1, currentList.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `removePlate when user is null does not call repository`() = runTest {
        setupViewModelWithNoUser() // User is null

        viewModel.removePlate("p1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 0) { mockUserRepository.removePlate(any(), any()) }
        assertTrue(viewModel.plates.value.isEmpty())
    }

    // --- Barbell Tests (Similar structure) ---
    @Test
    fun `addBarbell when user exists calls repository and updates StateFlow`() = runTest {
        val mockProfile = UserProfile(id = placeholderObjectId, name = "Test User", barbells = mutableListOf())
        setupViewModelWithMockUser(mockProfile)

        val newBarbell = Barbell("b1", 20f, "kg")
        coEvery { mockUserRepository.addBarbell(placeholderUserIdString, newBarbell) } just Runs

        viewModel.addBarbell(newBarbell)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUserRepository.addBarbell(placeholderUserIdString, newBarbell) }
        assertTrue(viewModel.barbells.value.contains(newBarbell))
    }

    @Test
    fun `removeBarbell when user exists calls repository and updates StateFlow`() = runTest {
        val barbellToRemove = Barbell("b1", 20f, "kg")
        val mockProfile = UserProfile(id = placeholderObjectId, name = "Test User", barbells = mutableListOf(barbellToRemove))
        setupViewModelWithMockUser(mockProfile)

        coEvery { mockUserRepository.removeBarbell(placeholderUserIdString, "b1") } just Runs

        viewModel.removeBarbell("b1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUserRepository.removeBarbell(placeholderUserIdString, "b1") }
        assertFalse(viewModel.barbells.value.contains(barbellToRemove))
    }


    // --- Collar Tests (Similar structure) ---
    @Test
    fun `addCollar when user exists calls repository and updates StateFlow`() = runTest {
        val mockProfile = UserProfile(id = placeholderObjectId, name = "Test User", collars = mutableListOf())
        setupViewModelWithMockUser(mockProfile)

        val newCollar = Collar("c1", 2.5f, "kg")
        coEvery { mockUserRepository.addCollar(placeholderUserIdString, newCollar) } just Runs

        viewModel.addCollar(newCollar)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUserRepository.addCollar(placeholderUserIdString, newCollar) }
        assertTrue(viewModel.collars.value.contains(newCollar))
    }

    @Test
    fun `removeCollar when user exists calls repository and updates StateFlow`() = runTest {
        val collarToRemove = Collar("c1", 2.5f, "kg")
        val mockProfile = UserProfile(id = placeholderObjectId, name = "Test User", collars = mutableListOf(collarToRemove))
        setupViewModelWithMockUser(mockProfile)

        coEvery { mockUserRepository.removeCollar(placeholderUserIdString, "c1") } just Runs

        viewModel.removeCollar("c1")
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { mockUserRepository.removeCollar(placeholderUserIdString, "c1") }
        assertFalse(viewModel.collars.value.contains(collarToRemove))
    }


    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
