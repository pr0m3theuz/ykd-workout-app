package com.example.myapplication.features.program

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.myapplication.common.domain.model.*
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.InProgressWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ProgramsRepository
import com.example.myapplication.common.infrastructure.persistence.WorkoutRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import org.junit.runners.model.Statement
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
class MainCoroutineRule(private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}

@ExperimentalCoroutinesApi
class ProgramsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var programsRepository: ProgramsRepository

    @MockK
    private lateinit var completedWorkoutRepository: CompletedWorkoutRepository

    @MockK
    private lateinit var exercisesRepository: ExercisesRepository

    @MockK
    private lateinit var inProgressWorkoutRepository: InProgressWorkoutRepository

    @MockK
    private lateinit var workoutRepository: WorkoutRepository

    private lateinit var viewModel: ProgramsViewModel

    // Define static ObjectIds for exercises that are not the "oldId"
    private val otherExerciseId1 = ObjectId()
    private val otherExerciseId2 = ObjectId()

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = ProgramsViewModel(
            programsRepository,
            inProgressWorkoutRepository,
            workoutRepository,
            exercisesRepository,
            completedWorkoutRepository
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    private fun createSampleProgram(programId: ObjectId, oldExerciseId: ObjectId, newExerciseIdForAssertion: ObjectId): Program {
        val exerciseSet1Id = ObjectId()
        val exerciseSet2Id = ObjectId()
        val supersetExerciseSet1Id = ObjectId()
        val supersetExerciseSet2Id = ObjectId()

        // This is the ID we expect to be substituted
        val idToSubstitute = oldExerciseId

        return Program(
            id = programId,
            name = "Test Program",
            description = "A program for testing",
            ownerID = ObjectId().toHexString(),
            isActive = false,
            template = true,
            tags = emptyList(),
            createdTimestamp = System.currentTimeMillis(),
            updatedTimestamp = System.currentTimeMillis(),
            mesocycles = listOf(
                Mesocycle(
                    id = ObjectId(),
                    name = "Meso 1",
                    order = 0,
                    programID = programId,
                    microcycles = listOf(
                        Microcycle(
                            id = ObjectId(),
                            name = "Micro 1",
                            order = 0,
                            mesocycleID = ObjectId(),
                            workouts = listOf(
                                Workout(
                                    id = ObjectId(),
                                    name = "Workout A",
                                    order = 0,
                                    microcycleID = ObjectId(),
                                    programID = programId,
                                    workoutItems = mutableListOf(
                                        ExerciseSets( // Should be updated
                                            id = exerciseSet1Id,
                                            exerciseID = idToSubstitute,
                                            order = 0,
                                            notes = "",
                                            sets = mutableListOf(Set(exerciseID = idToSubstitute, exerciseSetsID = exerciseSet1Id))
                                        ),
                                        Superset(
                                            id = ObjectId(),
                                            order = 1,
                                            exerciseSets = mutableListOf(
                                                ExerciseSets( // Should be updated
                                                    id = supersetExerciseSet1Id,
                                                    exerciseID = idToSubstitute,
                                                    order = 0,
                                                    notes = "",
                                                    sets = mutableListOf(Set(exerciseID = idToSubstitute, exerciseSetsID = supersetExerciseSet1Id))
                                                ),
                                                ExerciseSets( // Should NOT be updated
                                                    id = supersetExerciseSet2Id,
                                                    exerciseID = otherExerciseId1,
                                                    order = 1,
                                                    notes = "",
                                                    sets = mutableListOf(Set(exerciseID = otherExerciseId1, exerciseSetsID = supersetExerciseSet2Id))
                                                )
                                            )
                                        ),
                                        ExerciseSets( // Should NOT be updated
                                            id = exerciseSet2Id,
                                            exerciseID = otherExerciseId2,
                                            order = 2,
                                            notes = "",
                                            sets = mutableListOf(Set(exerciseID = otherExerciseId2, exerciseSetsID = exerciseSet2Id))
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `substituteExerciseInProgram success path`() = runTest(mainCoroutineRule.dispatcher) {
        val oldId = ObjectId()
        val newId = ObjectId() // This is the ID we are substituting TO
        val programId = ObjectId()
        val sampleProgram = createSampleProgram(programId, oldId, newId) // oldId is what we search for

        coEvery { programsRepository.getProgramByID(programId) } returns sampleProgram
        coEvery { programsRepository.updateProgram(any()) } just Runs
        coEvery { completedWorkoutRepository.substituteExerciseInCompletedWorkouts(any(), any(), any()) } just Runs

        val messages = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.toastMessage.collect { messages.add(it) }
        }

        viewModel.substituteExerciseInProgram(programId, oldId, newId)

        coVerify(exactly = 1) { programsRepository.getProgramByID(programId) }

        val programSlot = slot<Program>()
        coVerify(exactly = 1) { programsRepository.updateProgram(capture(programSlot)) }

        val capturedProgram = programSlot.captured
        capturedProgram.mesocycles.forEach { meso ->
            meso.microcycles.forEach { micro ->
                micro.workouts.forEach { workout ->
                    workout.workoutItems.forEach { item ->
                        when (item) {
                            is ExerciseSets -> {
                                if (item.id == ObjectId("5f8f1c9b0e1b9c0001a7b1b1") || item.id == ObjectId("5f8f1c9b0e1b9c0001a7b1b4")) { // Hypothetical IDs of items that had oldId
                                     // This check is tricky without knowing original IDs. Better: check if original was oldId, now it's newId.
                                     // The sampleProgram construction needs to be more deterministic for this.
                                     // For now, we assume if it's not otherExerciseId1 or otherExerciseId2, it must have been updated to newId if it was oldId.
                                }
                            }
                            is Superset -> {
                                item.exerciseSets.forEach { es ->
                                   // Similar conditional check as above
                                }
                            }
                        }
                    }
                }
            }
        }
        // Simplified assertion: check specific items that should have changed and those that shouldn't
        val updatedEs1 = capturedProgram.mesocycles.first().microcycles.first().workouts.first().workoutItems.first { it is ExerciseSets } as ExerciseSets
        assertEquals(newId, updatedEs1.exerciseID)

        val updatedSuperset = capturedProgram.mesocycles.first().microcycles.first().workouts.first().workoutItems[1] as Superset
        assertEquals(newId, updatedSuperset.exerciseSets[0].exerciseID) // First element of superset was oldId
        assertEquals(otherExerciseId1, updatedSuperset.exerciseSets[1].exerciseID) // Second element of superset was otherExerciseId1

        val notUpdatedEs = capturedProgram.mesocycles.first().microcycles.first().workouts.first().workoutItems[2] as ExerciseSets
        assertEquals(otherExerciseId2, notUpdatedEs.exerciseID)


        coVerify(exactly = 1) { completedWorkoutRepository.substituteExerciseInCompletedWorkouts(programId, oldId, newId) }
        assertEquals("Exercise substituted successfully in program '${sampleProgram.name}' and its completed workouts.", messages.firstOrNull())

        job.cancel()
    }

    @Test
    fun `substituteExerciseInProgram program not found`() = runTest(mainCoroutineRule.dispatcher) {
        val oldId = ObjectId()
        val newId = ObjectId()
        val programId = ObjectId()

        coEvery { programsRepository.getProgramByID(programId) } returns null

        val messages = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.toastMessage.collect { messages.add(it) }
        }

        viewModel.substituteExerciseInProgram(programId, oldId, newId)

        coVerify(exactly = 1) { programsRepository.getProgramByID(programId) }
        coVerify(exactly = 0) { programsRepository.updateProgram(any()) }
        coVerify(exactly = 0) { completedWorkoutRepository.substituteExerciseInCompletedWorkouts(any(), any(), any()) }
        assertEquals("Failed to find program with ID $programId.", messages.firstOrNull())

        job.cancel()
    }

    @Test
    fun `substituteExerciseInProgram error during program update`() = runTest(mainCoroutineRule.dispatcher) {
        val oldId = ObjectId()
        val newId = ObjectId()
        val programId = ObjectId()
        val sampleProgram = createSampleProgram(programId, oldId, newId)
        val errorMessage = "Database update error"

        coEvery { programsRepository.getProgramByID(programId) } returns sampleProgram
        coEvery { programsRepository.updateProgram(any()) } throws Exception(errorMessage)

        val messages = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.toastMessage.collect { messages.add(it) }
        }

        viewModel.substituteExerciseInProgram(programId, oldId, newId)

        coVerify(exactly = 1) { programsRepository.getProgramByID(programId) }
        coVerify(exactly = 1) { programsRepository.updateProgram(any()) }
        coVerify(exactly = 0) { completedWorkoutRepository.substituteExerciseInCompletedWorkouts(any(), any(), any()) }
        assertEquals("Error substituting exercise: $errorMessage", messages.firstOrNull())

        job.cancel()
    }

    @Test
    fun `substituteExerciseInProgram error during completed workouts update`() = runTest(mainCoroutineRule.dispatcher) {
        val oldId = ObjectId()
        val newId = ObjectId()
        val programId = ObjectId()
        val sampleProgram = createSampleProgram(programId, oldId, newId)
        val errorMessage = "Completed workouts update error"

        coEvery { programsRepository.getProgramByID(programId) } returns sampleProgram
        coEvery { programsRepository.updateProgram(any()) } just Runs
        coEvery { completedWorkoutRepository.substituteExerciseInCompletedWorkouts(programId, oldId, newId) } throws Exception(errorMessage)

        val messages = mutableListOf<String>()
        val job = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.toastMessage.collect { messages.add(it) }
        }

        viewModel.substituteExerciseInProgram(programId, oldId, newId)

        coVerify(exactly = 1) { programsRepository.getProgramByID(programId) }
        coVerify(exactly = 1) { programsRepository.updateProgram(any()) }
        coVerify(exactly = 1) { completedWorkoutRepository.substituteExerciseInCompletedWorkouts(programId, oldId, newId) }
        assertEquals("Error substituting exercise: $errorMessage", messages.firstOrNull())

        job.cancel()
    }
}
