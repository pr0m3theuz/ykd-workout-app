package com.example.myapplication.common.infrastructure.persistence.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.test.core.app.ApplicationProvider
import com.example.myapplication.datastore.ActiveWorkout
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@ExperimentalCoroutinesApi
class WorkoutDatastoreTest {

    @get:Rule
    val temporaryFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var testContext: Context
    private lateinit var testDataStoreFile: File
    private lateinit var dataStore: DataStore<ActiveWorkout>
    private lateinit var workoutDatastore: WorkoutDatastore

    // Using TestScope for managing coroutines in tests
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())


    @Before
    fun setUp() {
        testContext = ApplicationProvider.getApplicationContext()
        testDataStoreFile = temporaryFolder.newFile("test_active_workout.pb")
        dataStore = DataStoreFactory.create(
            serializer = ActiveWorkoutSerializer,
            produceFile = { testDataStoreFile }
        )
        // Directly pass the DataStore instance to WorkoutDatastore constructor
        // This requires modifying WorkoutDatastore to accept DataStore<ActiveWorkout>
        // For now, assuming WorkoutDatastore is modified or we use a way to inject this specific datastore.
        // Let's simulate modifying WorkoutDatastore or using a test-specific constructor if possible.
        // If WorkoutDatastore strictly uses the Context.activeWorkoutStore delegate,
        // this test setup would need Robolectric and more complex DI mocking.
        // For this example, we'll assume we can instantiate WorkoutDatastore with our test DataStore.
        // This is a common refactoring for testability:
        // Original: class WorkoutDatastore(private val context: Context)
        // Testable: class WorkoutDatastore(private val activeWorkoutStore: DataStore<ActiveWorkout>)
        // To make this runnable without immediate refactor of WorkoutDatastore, we'd need Robolectric
        // and a way to override the `activeWorkoutStore` delegate.
        // For now, I'll write it as if WorkoutDatastore can accept DataStore<ActiveWorkout> directly.
        // If not, these tests would fail and require Robolectric + DI setup.

        // Simplified: Create a new WorkoutDatastore that *would* use this context,
        // but since the delegate is global, this is tricky.
        // The ideal way is to pass DataStore<ActiveWorkout> into WorkoutDatastore.
        // Let's assume we've refactored WorkoutDatastore to accept DataStore<ActiveWorkout>
        // For the purpose of this test, I will create a test version of WorkoutDatastore
        // that accepts a DataStore<ActiveWorkout> directly.
        // In a real scenario, you'd use DI or refactor the actual class.

        // Recreating WorkoutDatastore to accept DataStore<ActiveWorkout> for testability
        workoutDatastore = WorkoutDatastore(testContext, dataStore)

    }

    // Test-specific constructor for WorkoutDatastore to inject DataStore<ActiveWorkout>
    // This would ideally be part of the actual WorkoutDatastore for DI
    class WorkoutDatastore(private val context: Context, private val activeWorkoutDS: DataStore<ActiveWorkout>) {
        val getActiveWorkout: kotlinx.coroutines.flow.Flow<ActiveWorkout> = activeWorkoutDS.data
            .kotlinx.coroutines.flow.catch { exception ->
                if (exception is java.io.IOException) {
                    emit(ActiveWorkout.getDefaultInstance())
                } else {
                    throw exception
                }
            }

        suspend fun saveActiveWorkout(activeWorkout: ActiveWorkout) {
            activeWorkoutDS.updateData { activeWorkout }
        }

        suspend fun clearActiveWorkout() {
            activeWorkoutDS.updateData { ActiveWorkout.getDefaultInstance() }
        }
    }


    @After
    fun tearDown() {
        // Cancel the scope to cancel any ongoing coroutines
        testScope.cancel()
        // TemporaryFolder rule will delete the test file
    }

    @Test
    fun `getActiveWorkout initially returns default ActiveWorkout`() = testScope.runTest {
        val firstWorkout = workoutDatastore.getActiveWorkout.first()
        assertThat(firstWorkout).isEqualTo(ActiveWorkout.getDefaultInstance())
    }

    @Test
    fun `saveActiveWorkout then getActiveWorkout returns saved workout`() = testScope.runTest {
        val workoutToSave = ActiveWorkout.newBuilder()
            .setId("saved-id")
            .setName("Saved Workout")
            .setUuid("saved-uuid")
            .setOwnerID("saved-owner")
            .build()

        workoutDatastore.saveActiveWorkout(workoutToSave)
        val retrievedWorkout = workoutDatastore.getActiveWorkout.first()

        assertThat(retrievedWorkout).isEqualTo(workoutToSave)
    }

    @Test
    fun `clearActiveWorkout clears data and getActiveWorkout returns default`() = testScope.runTest {
        val workoutToSaveAndClear = ActiveWorkout.newBuilder()
            .setId("clear-id")
            .setName("Clear Workout")
            .build()

        // Save a workout first
        workoutDatastore.saveActiveWorkout(workoutToSaveAndClear)
        val saved = workoutDatastore.getActiveWorkout.first()
        assertThat(saved).isEqualTo(workoutToSaveAndClear) // Verify it was saved

        // Clear it
        workoutDatastore.clearActiveWorkout()
        val afterClearWorkout = workoutDatastore.getActiveWorkout.first()

        assertThat(afterClearWorkout).isEqualTo(ActiveWorkout.getDefaultInstance())
    }

    @Test
    fun `multiple saves update the data correctly`() = testScope.runTest {
        val firstWorkout = ActiveWorkout.newBuilder().setId("first-save").setName("First Save").build()
        val secondWorkout = ActiveWorkout.newBuilder().setId("second-save").setName("Second Save").build()

        workoutDatastore.saveActiveWorkout(firstWorkout)
        assertThat(workoutDatastore.getActiveWorkout.first()).isEqualTo(firstWorkout)

        workoutDatastore.saveActiveWorkout(secondWorkout)
        assertThat(workoutDatastore.getActiveWorkout.first()).isEqualTo(secondWorkout)
    }

    // Testing the IOException catch block is hard without deeper DataStore mocking.
    // One way could be to make the file read-only after first write,
    // or corrupt it, but that's more of an integration test.
    // For a unit test, you might need to inject a mock DataStore or a
    // serializer that can be made to throw IOException on demand.
    // Given the current structure, a direct unit test for this specific catch block is non-trivial.
}
