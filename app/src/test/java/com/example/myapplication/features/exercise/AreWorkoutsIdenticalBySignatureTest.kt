@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.exercise

import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.util.ProgressionType
import com.example.myapplication.common.util.areWorkoutsIdenticalBySignature
import com.example.myapplication.common.util.generateExerciseSetsSignature
import com.example.myapplication.common.util.generateSetSignature
import com.example.myapplication.common.util.generateSupersetSignature
import com.example.myapplication.common.util.generateWorkoutSignature
import net.jqwik.api.Example
import net.jqwik.api.Group
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Assume the signature generation functions and comparison function are top-level or accessible
// Assume SetType has a companion object or similar for accessing types like SetType.normalSet

class WorkoutSignatureIdentityTest {

  // --- Test Data Setup ---
  // Using fixed ObjectIds for predictable signatures in tests
  private val exId1 = ObjectId("662300000000000000000001")
  private val exId2 = ObjectId("662300000000000000000002")
  private val exId3 = ObjectId("662300000000000000000003")
  private val workoutId1 = ObjectId("662310000000000000000001")
  private val workoutId2 = ObjectId("662310000000000000000002")
  private val esId1 = ObjectId("662320000000000000000001")
  private val esId2 = ObjectId("662320000000000000000002")
  private val esId3 = ObjectId("662320000000000000000003")
  private val setId1 = ObjectId("662330000000000000000001")
  private val setId2 = ObjectId("662330000000000000000002")
  private val setId3 = ObjectId("662330000000000000000003")


  // Helper to create Sets (adapt progressionSchemeID type if necessary)
  private fun createTestSet(
    setNumber: Int,
    setType: SetType = SetType.normalSet,
    targetReps: String? = "8-12",
    targetLbs: Double? = 100.0,
    targetRir: Double? = 2.0,
    targetRpe: Double? = null, // Example: RPE might be null if using RIR
    progressionId: String? = "ProgA", // Example progression ID
    id: ObjectId = ObjectId(),
    uuid: Uuid = Uuid.random(),
    workoutId: ObjectId,
    exerciseSetsId: ObjectId,
    exerciseId: ObjectId
  ): Set {
    // Adapt progressionSchemeID type/value as needed for your actual Set model
    // Using String? here for simplicity based on previous examples
    // val progressionSchemeObjectId = progressionId?.let { ObjectId(it) }

    return Set(
      id = id,
      uuid = uuid,
      setNumber = setNumber,
      setTypeID = setType,
      progressionSchemeID = ProgressionType.LINEAR_PROGRESSION, // Replace with actual progression scheme logic/type if needed
      targetRepetitions = targetReps,
      targetWeightPounds = targetLbs,
      targetRepetitionsInReserve = targetRir,
      targetRatePerceivedEffort = targetRpe,
      // Ignored fields for structural comparison, but needed for target comparison
      weightPounds = targetLbs?.minus(5.0), // Example actuals
      repetitions = 10,
      setCompleted = true,
      workoutID = workoutId,
      exerciseSetsID = exerciseSetsId,
      exerciseID = exerciseId,
      ownerID = "test",
      isPublic = false
      // Add other fields...
    )
  }

  // Helper to create ExerciseSets
  private fun createTestExerciseSets(
    exerciseObjectId: ObjectId,
    setsData: List<Pair<SetType, String?>>, // Pair of SetType and ProgressionID
    order: Int = 1,
    id: ObjectId = ObjectId(),
    uuid: Uuid = Uuid.random(),
    workoutId: ObjectId
  ): ExerciseSets {
    return ExerciseSets(
      id = id,
      exSetsUUID = uuid,
      exerciseID = exerciseObjectId,
      order = order,
      sets = setsData.mapIndexed { index, (setType, progId) ->
        createTestSet(
          setNumber = index + 1,
          setType = setType,
          progressionId = progId,
          workoutId = workoutId,
          exerciseSetsId = id,
          exerciseId = exerciseObjectId
        )
      }.toMutableList(),
      ownerID = "test",
      workoutID = workoutId,
      programID = null, mesocycleID = null, microcycleID = null
    )
  }

  // Helper to create Supersets
  private fun createTestSuperset(
    innerExerciseSets: List<ExerciseSets>,
    order: Int = 1,
    id: ObjectId = ObjectId(),
    uuid: Uuid = Uuid.random()
  ): Superset {
    return Superset(
      id = id,
      uuid = uuid,
      exercisesSets = innerExerciseSets,
      order = order,
      lazyListItemID = Math.random().toLong(),
      restBetweenExercises = Math.random().toLong(),
      enableRestBetweenExercises = false,
      enableRestAfterSuperset = false,
      restAfterSuperset = Math.random().toLong(),
      supersetNotes = emptyList(),
      ownerID = "TEST",
      isPublic = false,
      workoutID = ObjectId(),
      microcycleID = ObjectId(),
      mesocycleID = ObjectId(),
      programID = ObjectId(),
    )
  }

  // Helper to create Workouts
  private fun createTestWorkout(
    name: String,
    items: List<WorkoutItem>,
    id: ObjectId = ObjectId(),
    uuid: Uuid = Uuid.random()
  ): Workout {
    return Workout(
      id = id,
      uuid = uuid,
      name = name,
      workoutItems = items,
      ownerID = "test",
      programID = ObjectId() // Add a program ID if needed for context
      // Add other fields...
    )
  }


  // --- Tests for Signature Generation ---

  @Group
  inner class SignatureGenerationTests {

    @Example
    fun `generateSetSignature - structure only`() {
      val set = createTestSet(
        1,
        SetType.normalSet,
        progressionId = "ProgA",
        workoutId = workoutId1,
        exerciseSetsId = esId1,
        exerciseId = exId1
      )
      val expected =
        "S{type:1#prog:null}" // Assuming progressionSchemeID is null in test helper for now
      assertEquals(expected, generateSetSignature(set, includeTargets = false))
    }

    @Example
    fun `generateSetSignature - with targets`() {
      val set = createTestSet(
        1,
        SetType.dropSet,
        targetReps = "10",
        targetLbs = 150.0,
        targetRir = 1.0,
        targetRpe = null,
        progressionId = "ProgB",
        workoutId = workoutId1,
        exerciseSetsId = esId1,
        exerciseId = exId1
      )
      // Note: Adjust expected string based on exact fields included and null handling
      val expected = "S{type:3#prog:null#trgReps:10#trgLbs:150.0#trgRIR:1.0#trgRPE:null}"
      assertEquals(expected, generateSetSignature(set, includeTargets = true))
    }

    @Example
    fun `generateExerciseSetsSignature - structure only`() {
      val es = createTestExerciseSets(
        exId1,
        listOf(SetType.warmUpSet to "ProgW", SetType.normalSet to "ProgA"),
        id = esId1,
        workoutId = workoutId1
      )
      val expected =
        "ES(exID:662300000000000000000001|sets:[S{type:2#prog:null},S{type:1#prog:null}])" // Assumes prog is null for now
      assertEquals(expected, generateExerciseSetsSignature(es, includeTargets = false))
    }

    @Example
    fun `generateExerciseSetsSignature - with targets`() {
      val es = createTestExerciseSets(
        exId1,
        listOf(SetType.warmUpSet to "ProgW", SetType.normalSet to "ProgA"),
        id = esId1,
        workoutId = workoutId1
      )
      // Modify targets for testing
      es.sets[0].targetRepetitions = "15"
      es.sets[1].targetWeightPounds = 120.0
      val expected =
        "ES(exID:662300000000000000000001|sets:[S{type:2#prog:null#trgReps:15#trgLbs:100.0#trgRIR:2.0#trgRPE:null},S{type:1#prog:null#trgReps:8-12#trgLbs:120.0#trgRIR:2.0#trgRPE:null}])"
      assertEquals(expected, generateExerciseSetsSignature(es, includeTargets = true))
    }

    @Example
    fun `generateSupersetSignature - structure only`() {
      val es1 = createTestExerciseSets(
        exId1,
        listOf(SetType.normalSet to "ProgA"),
        id = esId1,
        workoutId = workoutId1
      )
      val es2 = createTestExerciseSets(
        exId2,
        listOf(SetType.normalSet to "ProgB", SetType.normalSet to "ProgB"),
        id = esId2,
        workoutId = workoutId1
      )
      val ss = createTestSuperset(listOf(es1, es2))
      val expected =
        "SS{items:[ES(exID:662300000000000000000001|sets:[S{type:1#prog:null}]);ES(exID:662300000000000000000002|sets:[S{type:1#prog:null},S{type:1#prog:null}])]}"
      assertEquals(expected, generateSupersetSignature(ss, includeTargets = false))
    }

    @Example
    fun `generateWorkoutSignature - mixed items - structure only`() {
      val es1 = createTestExerciseSets(
        exId1,
        listOf(SetType.normalSet to "ProgA"),
        id = esId1,
        workoutId = workoutId1
      )
      val es2 = createTestExerciseSets(
        exId2,
        listOf(SetType.normalSet to "ProgB", SetType.normalSet to "ProgB"),
        id = esId2,
        workoutId = workoutId1
      )
      val es3 = createTestExerciseSets(
        exId3,
        listOf(SetType.warmUpSet to "ProgW"),
        id = esId3,
        workoutId = workoutId1
      )
      val ss1 = createTestSuperset(listOf(es1, es2))
      val w = createTestWorkout("Mixed Workout", listOf(es3, ss1), id = workoutId1)
      val expected =
        "W[ES(exID:662300000000000000000003|sets:[S{type:2#prog:null}]);SS{items:[ES(exID:662300000000000000000001|sets:[S{type:1#prog:null}]);ES(exID:662300000000000000000002|sets:[S{type:1#prog:null},S{type:1#prog:null}])]}]"
      assertEquals(expected, generateWorkoutSignature(w, includeTargets = false))
    }

    @Example
    fun `generateWorkoutSignature - empty workout`() {
      val w = createTestWorkout("Empty", emptyList())
      assertEquals("W[]", generateWorkoutSignature(w, includeTargets = false))
      assertEquals("W[]", generateWorkoutSignature(w, includeTargets = true))
    }
  }

  // --- Tests for Comparison Function ---

  @Group
  inner class SignatureComparisonTests {

    @Example
    fun `areWorkoutsIdenticalBySignature - identical workouts - structure only - returns true`() {
      val es1 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId1)
      val es2 = createTestExerciseSets(
        exId1,
        listOf(SetType.normalSet to "ProgA"),
        workoutId = workoutId2
      ) // Different workout ID ok
      val w1 = createTestWorkout("Workout 1", listOf(es1), id = workoutId1)
      val w2 = createTestWorkout("Workout 2", listOf(es2), id = workoutId2) // Different name ok

      assertTrue(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = false))
    }

    @Example
    fun `areWorkoutsIdenticalBySignature - identical workouts - with targets - returns true`() {
      val es1 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId1)
      val es2 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId2)
      // Ensure targets are identical
      es1.sets[0].targetRepetitions = "5"
      es2.sets[0].targetRepetitions = "5"
      es1.sets[0].targetWeightPounds = 50.0
      es2.sets[0].targetWeightPounds = 50.0

      val w1 = createTestWorkout("Workout 1", listOf(es1), id = workoutId1)
      val w2 = createTestWorkout("Workout 2", listOf(es2), id = workoutId2)

      assertTrue(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = true))
    }

    @Example
    fun `areWorkoutsIdenticalBySignature - structurally identical different targets - structure only - returns true`() {
      val es1 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId1)
      val es2 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId2)
      es1.sets[0].targetRepetitions = "5" // Different target
      es2.sets[0].targetRepetitions = "8" // Different target

      val w1 = createTestWorkout("Workout 1", listOf(es1), id = workoutId1)
      val w2 = createTestWorkout("Workout 2", listOf(es2), id = workoutId2)

      assertTrue(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = false))
    }

    @Example
    fun `areWorkoutsIdenticalBySignature - structurally identical different targets - with targets - returns false`() {
      val es1 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId1)
      val es2 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId2)
      es1.sets[0].targetRepetitions = "5" // Different target
      es2.sets[0].targetRepetitions = "8" // Different target

      val w1 = createTestWorkout("Workout 1", listOf(es1), id = workoutId1)
      val w2 = createTestWorkout("Workout 2", listOf(es2), id = workoutId2)

      assertFalse(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = true))
    }

    @Example
    fun `areWorkoutsIdenticalBySignature - different structure - returns false`() {
      val es1 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId1)
      val es2 = createTestExerciseSets(
        exId2,
        listOf(SetType.normalSet to "ProgA"),
        workoutId = workoutId2
      ) // Different Exercise ID

      val w1 = createTestWorkout("Workout 1", listOf(es1), id = workoutId1)
      val w2 = createTestWorkout("Workout 2", listOf(es2), id = workoutId2)

      assertFalse(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = false))
      assertFalse(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = true))
    }

    @Example
    fun `areWorkoutsIdenticalBySignature - different set type - returns false`() {
      val es1 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId1)
      val es2 = createTestExerciseSets(
        exId1,
        listOf(SetType.warmUpSet to "ProgA"),
        workoutId = workoutId2
      ) // Different Set Type

      val w1 = createTestWorkout("Workout 1", listOf(es1), id = workoutId1)
      val w2 = createTestWorkout("Workout 2", listOf(es2), id = workoutId2)

      assertFalse(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = false))
      assertFalse(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = true))
    }

    @Example
    fun `areWorkoutsIdenticalBySignature - empty vs non-empty - returns false`() {
      val es1 =
        createTestExerciseSets(exId1, listOf(SetType.normalSet to "ProgA"), workoutId = workoutId1)
      val w1 = createTestWorkout("Workout 1", listOf(es1), id = workoutId1)
      val w2 = createTestWorkout("Workout 2", emptyList(), id = workoutId2)

      assertFalse(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = false))
      assertFalse(areWorkoutsIdenticalBySignature(w1, w2, includeTargets = true))
    }
  }
}