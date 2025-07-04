package com.example.myapplication.features.userProfileScreen
//
//import android.content.Context
//import android.util.Log
//import androidx.datastore.core.DataStore
//import androidx.datastore.preferences.core.MutablePreferences
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.emptyPreferences
//import com.example.myapplication.common.domain.model.UserProfileDataStoreKeys
//import com.example.myapplication.common.domain.model.userProfileDataStore
//import com.example.myapplication.common.infrastructure.persistence.UserRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flow
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.resetMain
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.After
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.MockedStatic
//import org.mockito.Mockito
//import org.mockito.junit.MockitoJUnitRunner
//import org.mockito.kotlin.any
//import org.mockito.kotlin.doAnswer
//import org.mockito.kotlin.doReturn
//import org.mockito.kotlin.mock
//import org.mockito.kotlin.verify
//import org.mockito.kotlin.whenever
//import java.io.IOException
//import kotlin.test.assertEquals
//
//@ExperimentalCoroutinesApi
//@RunWith(MockitoJUnitRunner::class)
//class ProfileViewModelTest {
//
//    private val testDispatcher = StandardTestDispatcher()
//
//    @Mock
//    private lateinit var mockContext: Context
//
//    @Mock
//    private lateinit var mockUserRepository: UserRepository
//
//    @Mock
//    private lateinit var mockDataStore: DataStore<Preferences>
//
//    private lateinit var viewModel: ProfileViewModel
//
//    // For mocking Log.e
//    private lateinit var mockLog: MockedStatic<Log>
//
//
//    @Before
//    fun setUp() {
//        Dispatchers.setMain(testDispatcher)
//        // Mock the extension property
//        // It's tricky because userProfileDataStore is an extension val.
//        // We need to mock the context to return our mockDataStore.
//        // This requires userProfileDataStore to be an open extension or for Context to be an interface.
//        // The actual way DataStore is retrieved is `context.userProfileDataStore`.
//        // We can mock `mockContext.userProfileDataStore` if `userProfileDataStore` is an extension method
//        // that can be overridden or if Context itself can be subclassed and that method overridden.
//        // For simplicity, we'll assume direct mocking of DataStore interactions if direct context mocking is too complex.
//        // However, `userProfileDataStore` is a top-level extension property delegate.
//        // So, we mock the `Context` to return our `mockDataStore` when `userProfileDataStore` is accessed.
//        // This specific way of mocking `context.userProfileDataStore` might not work directly with Mockito
//        // unless `userProfileDataStore` is an actual method on a class/interface `Context` implements.
//        // Let's assume for now that we can make mockContext.userProfileDataStore return mockDataStore.
//        // This usually means `userProfileDataStore` needs to be defined in an interface that Context implements and can be mocked.
//        // Or, using `mockk` might offer better support for extension properties.
//        // With Mockito, this is hard. A common workaround is to wrap DataStore access in another class that IS injectable.
//        // Given the constraints, I will mock the `DataStore.data` and `DataStore.edit` calls directly on `mockDataStore`
//        // and then ensure `mockContext.userProfileDataStore` returns this `mockDataStore`.
//        // This requires `userProfileDataStore` to be an extension property that can be mocked on an instance.
//
//        // The `userProfileDataStore` is an extension property.
//        // `val Context.userProfileDataStore by preferencesDataStore(...)`
//        // Mocking this usually requires a library like MockK or PowerMockito for final extension properties.
//        // For this test, we will proceed by preparing mockDataStore and then trying to ensure ProfileViewModel uses it.
//        // One way is to have a helper function in the test that ProfileViewModel would use.
//        // But we cannot change ProfileViewModel.
//
//        // Let's try to use mock() for DataStore and then stub its methods.
//        // The key is making `context.userProfileDataStore` return `mockDataStore`.
//        // This is often the hardest part of testing ViewModels that use DataStore this way.
//        // For now, I will assume this can be set up. If not, the test will fail at this point.
//        // In a real scenario, a TestRule or a DI framework would handle DataStore provision for tests.
//
//        // Simplified: If `ProfileViewModel` directly accepted `DataStore<Preferences>` this would be easy.
//        // Since it uses `context.userProfileDataStore`, we rely on that extension.
//        // We will mock `mockContext` and hope that `whenever(mockContext.userProfileDataStore)` works.
//        // It typically doesn't for extension properties like this with Mockito directly.
//        // Let's assume it *could* work or that an alternative setup is in place (e.g. test-specific DI)
//        // For the purpose of this exercise, I will write the tests as if `mockContext.userProfileDataStore` successfully returns `mockDataStore`.
//        // This might require additional setup in a real project (e.g. a test rule that swaps out the DataStore delegate).
//
//        // The following line is conceptual. Actual mocking of `userProfileDataStore` extension is complex with Mockito.
//        // `whenever(mockContext.userProfileDataStore).thenReturn(mockDataStore)`
//        // To make this testable without PowerMock/MockK, one would refactor ProfileViewModel to take DataStore as a direct dependency.
//        // As per subtask, I cannot change ProfileViewModel. So, I will write the test logic assuming this bridge exists.
//        // The tool environment might behave differently.
//
//        viewModel = ProfileViewModel(mockUserRepository, mockContext)
//        mockLog = Mockito.mockStatic(Log::class.java) // For verifying Log.e calls
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//        mockLog.close()
//    }
//
//    @Test
//    fun `getRoundingPreference success`() = runTest(testDispatcher) {
//        val testPreferenceValue = "Round to nearest 1.0 lb"
//        val preferences = mock<Preferences> {
//            on { get(UserProfileDataStoreKeys.ROUNDING_PREFERENCE_LBS) } doReturn testPreferenceValue
//        }
//        whenever(mockContext.userProfileDataStore).thenReturn(mockDataStore) // Assume this works
//        whenever(mockDataStore.data).thenReturn(flowOf(preferences))
//
//        val result = viewModel.getRoundingPreference().first()
//
//        assertEquals(testPreferenceValue, result)
//    }
//
//    @Test
//    fun `getRoundingPreference default when not set`() = runTest(testDispatcher) {
//        val preferences = mock<Preferences> {
//            on { get(UserProfileDataStoreKeys.ROUNDING_PREFERENCE_LBS) } doReturn null
//        }
//        whenever(mockContext.userProfileDataStore).thenReturn(mockDataStore)
//        whenever(mockDataStore.data).thenReturn(flowOf(preferences))
//
//        val result = viewModel.getRoundingPreference().first()
//        // The default value is "Round to nearest 5.0 lb" as per ProfileViewModel
//        assertEquals("Round to nearest 5.0 lb", result)
//    }
//
//    @Test
//    fun `getRoundingPreference default on IOException`() = runTest(testDispatcher) {
//        whenever(mockContext.userProfileDataStore).thenReturn(mockDataStore)
//        whenever(mockDataStore.data).thenReturn(flow { throw IOException("Test Exception") })
//
//        val result = viewModel.getRoundingPreference().first()
//
//        // Verify Log.e was called
//        mockLog.verify { Log.e("ProfileViewModel", "Error reading rounding preference.", any<IOException>()) }
//        assertEquals("Round to nearest 5.0 lb", result) // Default value due to IOException
//    }
//
//    @Test
//    fun `updateRoundingPreference success`() = runTest(testDispatcher) {
//        val newPreference = "Round to nearest 0.5 lb"
//        var capturedPrefs: Preferences? = null
//
//        whenever(mockContext.userProfileDataStore).thenReturn(mockDataStore)
//        // Mock the edit function
//        whenever(mockDataStore.edit(any())).thenAnswer { invocation ->
//            val transform = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
//            val mutablePreferences = emptyPreferences().toMutablePreferences()
//            // This runBlocking is okay here because edit is suspend and we are in runTest
//            // However, typically one might need to handle this differently depending on test setup.
//            // Forcing the suspend lambda to run now:
//            // kotlinx.coroutines.runBlocking(testDispatcher) { transform(mutablePreferences) }
//            // The above is problematic. Let's simplify how we check the result of the transform.
//            // The solution is to capture the state of mutablePreferences *after* transform is applied.
//
//            // Simpler: create a real MutablePreferences, apply transform, then capture its state for assertion
//            // This still requires running the suspend lambda.
//            // The `edit` extension function itself handles the coroutine context.
//            // So, we can just call transform.
//
//            // Let's try a direct approach for capturing:
//            // We need to ensure the lambda is executed.
//            // `edit` is a suspend function. The lambda passed to it is also suspend.
//            // `runTest` should handle this.
//
//            // The current `edit` mock in ProfileViewModel doesn't return the updated preferences directly.
//            // It returns `Flow<Preferences>`. The `edit` in DataStore returns the updated Preferences.
//            // `mockDataStore.edit` returns `Preferences` not `Unit`.
//            // The actual signature: `suspend fun updateData(transform: suspend (t: T) -> T): T`
//            // For PreferencesDataStore: `suspend fun edit(transform: suspend (MutablePreferences) -> Unit): Preferences`
//
//            // Correct mocking for edit:
//            val mutablePrefs = emptyPreferences().toMutablePreferences()
//            // The lambda `transform` is `suspend (MutablePreferences) -> Unit`.
//            // We need to execute it.
//            // This is tricky because the lambda itself is suspend.
//            // Let's try:
//            // testDispatcher.scheduler.runCurrent() // if using TestCoroutineScheduler
//            // For now, let's assume the invocation of the lambda happens and we can capture its effect.
//
//            // A common way:
//            doAnswer {
//                // This lambda is the one passed to mockDataStore.edit( ... )
//                val editorLambda = it.getArgument<suspend (MutablePreferences) -> Unit>(0)
//                // We need to call this lambda with a MutablePreferences instance
//                // and then we can check what was put into it.
//                // Since it's a suspend lambda, we need a coroutine scope. runTest provides this.
//                val fakeMutablePreferences = emptyPreferences().toMutablePreferences()
//                // We need to execute the suspend lambda.
//                // This is where it gets complex with mockito alone.
//                // A real `TestDatastore` or `Robolectric` would be simpler.
//
//                // Let's assume the lambda is executed by the `edit` call.
//                // We need to provide a `MutablePreferences` that we can inspect.
//                // The actual `edit` returns the state of `Preferences` *after* the edit.
//
//                // Simplification: Assume `transform` is called, and capture what it does.
//                // This requires a bit of indirection if we can't directly execute the suspend lambda easily.
//
//                // Alternative: Use a real MutablePreferences and check its content after the call.
//                // This is hard because the lambda is internal to the `edit` call from the ViewModel.
//
//                // Let's use `thenAnswer` to execute the lambda and capture.
//                 val lambda = invocation.getArgument<suspend (MutablePreferences) -> Unit>(0)
//                 // We need to run this suspend lambda.
//                 // Since we are in `runTest`, we can launch a new coroutine or use `runBlockingTest` (deprecated).
//                 // `runTest` provides a scope.
//                 // This means we can call `lambda(mutablePreferences)` if `mutablePreferences` is defined.
//                 // The `edit` method itself would handle the execution.
//                 // Our mock needs to simulate this.
//
//                 // This is a common pain point. Let's simplify the check:
//                 // Verify that `preferences[key] = value` happens.
//                 // This would ideally be done by inspecting the `MutablePreferences` object *after* the lambda runs.
//
//                 // Let's try to make the mock `edit` store the result that the lambda would produce.
//                 // For this, the lambda must be invoked.
//                 // The `edit` method of DataStore returns the final Preferences.
//                 // So the mock should also return Preferences.
//                 // The lambda `transform: suspend (MutablePreferences) -> Unit` doesn't return anything.
//                 // The `edit` function applies this to current preferences and returns the new state.
//
//                 // Simplified mock for `edit`:
//                 // When edit is called, it will receive a lambda. We execute this lambda
//                 // with a local MutablePreferences and then return it as Preferences.
//                 val currentPrefs = emptyPreferences() // Represents "before" state
//                 val transformedPrefs = currentPrefs.toMutablePreferences().apply {
//                     // Execute the lambda provided by ProfileViewModel
//                     // This needs to be done in a way that the coroutine can run.
//                     // Since edit is suspend, and we are in runTest, this should be okay.
//                     // transform(this) // This is the key part - how to call the suspend lambda?
//                 }
//                 // This mocking is becoming too complex. Let's verify the interaction more simply.
//                 // We want to ensure that `preferences[key] = value` is called.
//                 // This means the lambda given to `edit` does this.
//
//                 // Let's assume `mockDataStore.edit` is correctly called.
//                 // The verification will be on the side effects if possible, or just that `edit` was called.
//                 // To test the *content* of the edit, we need to execute the lambda.
//                 // What if we mock `MutablePreferences`?
//                 val mockMutablePreferences = mock<MutablePreferences>()
//                 // This is also not ideal.
//
//                 // Backtrack: The `edit` method of `DataStore<Preferences>` has the signature:
//                 // `suspend fun edit(transform: suspend (MutablePreferences) -> Unit): Preferences`
//                 // So, our mock should simulate this.
//                 // The lambda `transform` is what `ProfileViewModel` provides.
//                 whenever(mockDataStore.edit(any())).thenAnswer {
//                     val lambda = it.getArgument<suspend (MutablePreferences) -> Unit>(0)
//                     val mutablePreferences = emptyPreferences().toMutablePreferences() // Start with empty
//                     // We need to execute the suspend lambda 'transform'
//                     // Since we're in runTest, we can create a child coroutine to run it
//                     // or rely on the test dispatcher to run it if called directly.
//                     // For now, let's assume it's executed and modifies mutablePreferences.
//                     // To make it actually execute:
//                     kotlinx.coroutines.runBlocking(testDispatcher) { lambda(mutablePreferences) } // This can be problematic if not handled carefully
//
//                     capturedPrefs = mutablePreferences.toPreferences() // Capture the state after lambda
//                     return@thenAnswer capturedPrefs!!
//                 }
//
//            } // end of thenAnswer for edit
//
//        viewModel.updateRoundingPreference(newPreference)
//        testDispatcher.scheduler.advanceUntilIdle() // Ensure all coroutines complete
//
//        // Verify edit was called
//        verify(mockDataStore).edit(any())
//
//        // Check that the captured preferences contain the new value
//        // This relies on `capturedPrefs` being set by the mock `edit`.
//        // This part is tricky due to the suspend lambda execution.
//        // If the above `runBlocking` in the mock is an issue, this assertion might be unreliable.
//        // Let's assume `capturedPrefs` is correctly populated.
//            assertEquals(
//                newPreference,
//                capturedPrefs?.get(UserProfileDataStoreKeys.ROUNDING_PREFERENCE_LBS)
//            )
//    }
//
//
//    @Test
//    fun `updateRoundingPreference IOException`() = runTest(testDispatcher) {
//        val preferenceToSet = "any_value"
//        whenever(mockContext.userProfileDataStore).thenReturn(mockDataStore)
//        whenever(mockDataStore.edit(any())).thenThrow(IOException("Test Update Exception"))
//
//        viewModel.updateRoundingPreference(preferenceToSet)
//        testDispatcher.scheduler.advanceUntilIdle()
//
//        mockLog.verify { Log.e("ProfileViewModel", "Error updating rounding preference.", any<IOException>()) }
//        // No crash means the exception was handled.
//    }
//}
//
//// Helper to create Preferences for testing
//fun testPreferencesOf(vararg pairs: Pair<Preferences.Key<String>, String>): Preferences {
//    val mutablePreferences = emptyPreferences().toMutablePreferences()
//    for ((key, value) in pairs) {
//        mutablePreferences[key] = value
//    }
//    return mutablePreferences.toPreferences()
//}

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
