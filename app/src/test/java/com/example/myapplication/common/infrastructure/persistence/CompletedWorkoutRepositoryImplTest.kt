package com.example.myapplication.common.infrastructure.persistence

import android.util.Log
import com.couchbase.lite.*
import com.example.myapplication.common.domain.model.*
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.Description
import kotlin.test.assertEquals
import kotlin.test.assertNull
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
class CompletedWorkoutRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @MockK
    private lateinit var databaseManager: DatabaseManager
    @MockK
    private lateinit var db: Database
    @MockK(name = "completedWorkoutsCollection") // Naming mocks can help in debugging
    private lateinit var completedWorkoutsCollection: Collection
    @MockK(name = "completedSetsCollection")
    private lateinit var completedSetsCollection: Collection
    @MockK
    private lateinit var userProfileManager: UserProfileManager

    private lateinit var repository: CompletedWorkoutRepositoryImpl

    private val testUserId = ObjectId()
    private val oldExerciseId = ObjectId()
    private val newExerciseId = ObjectId()
    private val programId = ObjectId()
    private val otherExerciseId1 = ObjectId()


    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        val user = User(id = testUserId, name = "Test User", email = "test@example.com")
        every { userProfileManager.getCurrentUser() } returns user
        every { databaseManager.database } returns db
        every { db.getCollection("CompletedWorkouts") } returns completedWorkoutsCollection
        every { db.getCollection("CompletedSets") } returns completedSetsCollection

        mockkStatic(Log::class)
        every { Log.e(any<String>(), any<String>()) } returns 0
        every { Log.e(any<String>(), any<String>(), any()) } returns 0
        every { Log.i(any<String>(), any<String>()) } returns 0
        every { Log.d(any<String>(), any<String>()) } returns 0

        mockkStatic(QueryBuilder::class) // Mock QueryBuilder static methods

        repository = CompletedWorkoutRepositoryImpl(databaseManager)

        mockkStatic(org.koin.java.KoinJavaComponent::class)
        every { org.koin.java.KoinJavaComponent.inject<UserProfileManager>(UserProfileManager::class.java) } returns userProfileManager
    }

    @After
    fun tearDown() {
        unmockkAll()
        unmockkStatic(org.koin.java.KoinJavaComponent::class)
        unmockkStatic(Log::class)
        unmockkStatic(QueryBuilder::class)
    }

    private fun createCompletedWorkoutDao(workout: Workout): String {
        val dao = CompletedWorkoutsDao(CompletedWorkouts = workout)
        return Json.encodeToString(dao)
    }

    private fun createCompletedSetDao(set: Set): String {
        val dao = CompletedSetsDao(CompletedSets = set)
        return Json.encodeToString(dao)
    }

    private fun mockWorkoutsQueryForProgram(workoutsToReturn: List<Workout>) {
        val mockQuery = mockk<Query>()
        val mockResults = mockk<ResultSet>()
        val resultsList = workoutsToReturn.map {
            val mockResult = mockk<Result>()
            every { mockResult.toJSON() } returns createCompletedWorkoutDao(it)
            mockResult
        }

        val select = mockk<Select>()
        val from = mockk<From>()
        val where = mockk<Where>()

        every { QueryBuilder.select(SelectResult.all()) } returns select // This will be for workouts query
        every { select.from(DataSource.collection(completedWorkoutsCollection)) } returns from
        every { from.where(any()) } returns where // Expression for programId, ownerId, isCompleted
        every { where.execute() } returns mockResults
        every { mockResults.allResults() } returns resultsList
    }

    private fun mockSetsQueryForWorkouts(setsToReturn: List<Set>) {
        val mockQuery = mockk<Query>()
        val mockResults = mockk<ResultSet>()
        val resultsList = setsToReturn.map {
            val mockResult = mockk<Result>()
            every { mockResult.toJSON() } returns createCompletedSetDao(it)
            mockResult
        }

        val select = mockk<Select>() // Potentially new select mock for clarity
        val from = mockk<From>()
        val where = mockk<Where>()

        // This assumes the QueryBuilder.select for sets is distinguishable or happens after workouts one
        // If QueryBuilder.select(SelectResult.all()) is called again, it might re-use the above mock.
        // This can be tricky. A more robust way is to make the expressions more specific if possible,
        // or use `andThen` if the calls are strictly ordered and always happen.
        every { QueryBuilder.select(SelectResult.all()) } returns select // This might be for sets query
        every { select.from(DataSource.collection(completedSetsCollection)) } returns from
        every { from.where(any()) } returns where // Expression for workoutIDs, ownerId
        every { where.execute() } returns mockResults
        every { mockResults.allResults() } returns resultsList
    }


    private fun sampleWorkout(id: ObjectId, programId: ObjectId, oldExId: ObjectId, otherExId: ObjectId): Workout {
        val set1Id = ObjectId() // Belongs to oldExId
        val set2Id = ObjectId() // Belongs to oldExId (in superset)
        val set3Id = ObjectId() // Belongs to otherExId (in superset)
        val es1Id = ObjectId()
        val es2Id = ObjectId() // For otherExId
        val ssEs1Id = ObjectId()


        return Workout(
            id = id,
            name = "Completed Test Workout",
            order = 0,
            programID = programId,
            microcycleID = ObjectId(),
            isCompleted = true,
            workoutItems = mutableListOf(
                ExerciseSets( // Contains oldExId
                    id = es1Id, exerciseID = oldExId, order = 0, sets = mutableListOf(
                        Set(id = set1Id, exerciseID = oldExId, exerciseSetsID = es1Id, setNumber = 1, weight = 10.0, reps = 5, rpe = 8.0, setCompleted = true, ownerID = testUserId.toHexString(), workoutID = id)
                    )
                ),
                Superset(
                    id = ObjectId(), order = 1, exerciseSets = mutableListOf(
                        ExerciseSets( // Contains oldExId
                            id = ssEs1Id, exerciseID = oldExId, order = 0, sets = mutableListOf(
                                Set(id = set2Id, exerciseID = oldExId, exerciseSetsID = ssEs1Id, setNumber = 1, weight = 12.0, reps = 6, rpe = 8.5, setCompleted = true, ownerID = testUserId.toHexString(), workoutID = id)
                            )
                        ),
                        ExerciseSets( // Contains otherExId
                            id = es2Id, exerciseID = otherExId, order = 1, sets = mutableListOf(
                                Set(id = set3Id, exerciseID = otherExId, exerciseSetsID = es2Id, setNumber = 1, weight = 15.0, reps = 7, rpe = 9.0, setCompleted = true, ownerID = testUserId.toHexString(), workoutID = id)
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `substituteExerciseInCompletedWorkouts success path`() = runTest(mainCoroutineRule.dispatcher) {
        val workout1Id = ObjectId()
        val workout1 = sampleWorkout(workout1Id, programId, oldExerciseId, otherExerciseId1)

        val originalSet1 = workout1.workoutItems.filterIsInstance<ExerciseSets>().first().sets.first() // ID: set1Id, Exercise: oldExerciseId
        val originalSet2 = workout1.workoutItems.filterIsInstance<Superset>().first().exerciseSets.first { it.exerciseID == oldExerciseId }.sets.first() // ID: set2Id, Exercise: oldExerciseId
        val originalSet3 = workout1.workoutItems.filterIsInstance<Superset>().first().exerciseSets.first { it.exerciseID == otherExerciseId1 }.sets.first() // ID: set3Id, Exercise: otherExerciseId1

        mockWorkoutsQueryForProgram(listOf(workout1))
        mockSetsQueryForWorkouts(listOf(originalSet1, originalSet2, originalSet3))

        val docSlot = slot<MutableDocument>()
        // Mock getDocument to return null (doc doesn't exist) or a mockk<MutableDocument>() if testing updates to existing docs
        every { completedSetsCollection.getDocument(any()) } returns null
        every { completedSetsCollection.save(capture(docSlot)) } just Runs

        // We need to capture multiple saves
        val savedDocuments = mutableListOf<MutableDocument>()
        every { completedSetsCollection.save(captureLambda()) } answers {
            lambda<Function1<MutableDocument, Unit>>().captured.invoke(mockk(relaxed = true){
                every { id } returns lambda<Function1<MutableDocument, Unit>>().captured.id // Ensure captured doc has an ID for logging
            })
            savedDocuments.add(lambda<Function1<MutableDocument, Unit>>().captured)
        }
        // More idiomatic way to capture multiple calls
        val capturedDocsList = mutableListOf<MutableDocument>()
        every { completedSetsCollection.save(capture(capturedDocsList)) } just Runs


        every { db.inBatch<Unit>(any()) } answers {
            firstArg<kotlin.jvm.functions.Function0<Unit>>().invoke()
        }

        repository.substituteExerciseInCompletedWorkouts(programId, oldExerciseId, newExerciseId)

        assertEquals(2, capturedDocsList.size, "Should have saved two documents")

        val json = Json { ignoreUnknownKeys = true }

        val savedDoc1 = capturedDocsList.find { it.id == originalSet1.id.toHexString() }
        assertTrue(savedDoc1 != null, "Document for originalSet1 should be saved")
        assertEquals(newExerciseId, json.decodeFromString<CompletedSetsDao>(savedDoc1!!.toJSON()).CompletedSets.exerciseID)

        val savedDoc2 = capturedDocsList.find { it.id == originalSet2.id.toHexString() }
        assertTrue(savedDoc2 != null, "Document for originalSet2 should be saved")
        assertEquals(newExerciseId, json.decodeFromString<CompletedSetsDao>(savedDoc2!!.toJSON()).CompletedSets.exerciseID)

        // Verify that originalSet3 (which had otherExerciseId1) was not saved among the captured docs with newExerciseId
        val savedDoc3 = capturedDocsList.find { it.id == originalSet3.id.toHexString() }
        assertNull(savedDoc3, "Document for originalSet3 should NOT have been saved with changes")
    }

    @Test
    fun `substituteExerciseInCompletedWorkouts no workouts found`() = runTest(mainCoroutineRule.dispatcher) {
        mockWorkoutsQueryForProgram(emptyList())
        // No need to mock sets query if no workouts are returned, as it won't be called.
        // Or, if it might be called with an empty list of workout IDs:
        mockSetsQueryForWorkouts(emptyList())


        repository.substituteExerciseInCompletedWorkouts(programId, oldExerciseId, newExerciseId)

        verify(exactly = 0) { completedSetsCollection.save(any()) }
        verify(exactly = 0) { db.inBatch<Unit>(any()) }
    }

    @Test
    fun `substituteExerciseInCompletedWorkouts no sets with oldExerciseId`() = runTest(mainCoroutineRule.dispatcher) {
        val workout1Id = ObjectId()
        val workoutNotContainingOldId = sampleWorkout(workout1Id, programId, ObjectId(), otherExerciseId1) // oldExerciseId is a fresh one

        val setsFromWorkout = workoutNotContainingOldId.workoutItems.flatMap { wi ->
            when(wi) {
                is ExerciseSets -> wi.sets
                is Superset -> wi.exercisesSets.flatMap { it.sets }
                else -> emptyList()
            }
        }
        mockWorkoutsQueryForProgram(listOf(workoutNotContainingOldId))
        mockSetsQueryForWorkouts(setsFromWorkout)

        repository.substituteExerciseInCompletedWorkouts(programId, oldExerciseId, newExerciseId)

        verify(exactly = 0) { completedSetsCollection.save(any()) }
        verify(exactly = 0) { db.inBatch<Unit>(any()) }
    }

    @Test
    fun `substituteExerciseInCompletedWorkouts database error on save`() = runTest(mainCoroutineRule.dispatcher) {
        val workout1Id = ObjectId()
        val workout1 = sampleWorkout(workout1Id, programId, oldExerciseId, otherExerciseId1)

        val originalSet1 = workout1.workoutItems.filterIsInstance<ExerciseSets>().first().sets.first()

        mockWorkoutsQueryForProgram(listOf(workout1))
         val setsForWorkout1 = workout1.workoutItems.flatMap { wi ->
            when(wi) {
                is ExerciseSets -> wi.sets
                is Superset -> wi.exercisesSets.flatMap { it.sets }
                else -> emptyList()
            }
        }
        mockSetsQueryForWorkouts(setsForWorkout1)


        val errorMessage = "DB save failed"
        // Mock getDocument for the set that will fail, to simulate it exists or not as needed by save logic
        every { completedSetsCollection.getDocument(originalSet1.id.toHexString()) } returns null
        every { completedSetsCollection.save(match { it.id == originalSet1.id.toHexString() }) } throws Exception(errorMessage)

        // Mock other saves to succeed if necessary, for now, one failure is enough
        // every { completedSetsCollection.save(not(match { it.id == originalSet1.id.toHexString() }))) } just Runs


        every { db.inBatch<Unit>(any()) } answers {
            firstArg<kotlin.jvm.functions.Function0<Unit>>().invoke() // Execute the lambda containing the save operations
        }

        repository.substituteExerciseInCompletedWorkouts(programId, oldExerciseId, newExerciseId)

        verify { Log.e("CompletedWorkoutRepositoryImpl.substituteExercise", "Failed to save set ${originalSet1.id} to DB: $errorMessage", any()) }
    }
}
