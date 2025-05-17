@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication

import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.util.ProgressionType
import com.example.myapplication.common.util.areWorkoutsStructurallyIdentical
import net.jqwik.api.Example
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Assuming areWorkoutsStructurallyIdentical and helpers are accessible,
// either directly or via an object/class instance.
// For simplicity, assuming they are top-level functions here.

class WorkoutStructuralIdentityTest {

  // --- Helper Functions to Create Test Data ---


  private fun createTestSet(
    setNumber: Int,
    setType: SetType = SetType.normalSet,
    reps: Int? = 10, // Ignored field
    weight: Double? = 100.0, // Ignored field
    completed: Boolean = true, // Ignored field
    progressionId: ProgressionType? = null, // Compared field (adjust type if needed)
    workoutId: ObjectId = ObjectId(),
    exerciseSetsId: ObjectId = ObjectId(),
    exerciseId: ObjectId = ObjectId()
  ): Set {
    // Adapt progressionSchemeID type/value as needed for your actual Set model
    // Using String? here for simplicity based on previous examples
//    val progressionSchemeObjectId = progressionId?.let { ObjectId(it) } // Or keep as String if needed

    return Set(
      id = ObjectId(),
      uuid = Uuid.random(),
      setNumber = setNumber,
      setTypeID = setType, // Structural
      progressionSchemeID = progressionId, // Structural (replace with progressionSchemeObjectId if type is ObjectId)
      repetitions = reps, // Ignored
      weightPounds = weight, // Ignored
      setCompleted = completed, // Ignored
      workoutID = workoutId,
      exerciseSetsID = exerciseSetsId,
      exerciseID = exerciseId,
      ownerID = "test",
      isPublic = false
      // Add any other required fields from your Set definition
    )
  }

  private fun createTestExerciseSets(
    exerciseObjectId: ObjectId,
    setTypes: List<SetType>,
    order: Int = 1,
    progressionIds: List<ProgressionType?> = List(setTypes.size) { null }
  ): ExerciseSets {
    val exerciseSetsId = ObjectId()
    val workoutId = ObjectId()
    return ExerciseSets(
      id = exerciseSetsId,
      exSetsUUID = Uuid.random(),
      exerciseID = exerciseObjectId, // Structural
      order = order, // Structural (via list position)
      sets = setTypes.mapIndexed { index, setType ->
        createTestSet(
          setNumber = index + 1,
          setType = setType, // Structural
          progressionId = progressionIds.getOrElse(index) { null }, // Structural
          workoutId = workoutId,
          exerciseSetsId = exerciseSetsId,
          exerciseId = exerciseObjectId
        )
      }.toMutableList(),
      ownerID = "test",
      workoutID = workoutId,
      programID = null,
      mesocycleID = null,
      microcycleID = null
      // Add any other required fields
    )
  }

  private fun createTestSuperset(
    innerExerciseSets: List<ExerciseSets>,
    order: Int = 1
  ): Superset {
    return Superset(
      id = ObjectId(),
      uuid = Uuid.random(),
      exercisesSets = innerExerciseSets, // Structural
      order = order, // Structural (via list position)
      // Add any other required fields
      ownerID = "test",
      workoutID = ObjectId(),
      programID = null,
      mesocycleID = null,
      microcycleID = null
    )
  }

  private fun createTestWorkout(
    name: String,
    items: List<WorkoutItem>
  ): Workout {
    return Workout(
      id = ObjectId(),
      uuid = Uuid.random(),
      name = name, // Not structural
      workoutItems = items, // Structural
      ownerID = "test"
      // Add any other required fields
    )
  }

  // --- Test Cases ---

  @Example
  fun `areWorkoutsStructurallyIdentical - two identical simple workouts - returns true`() {
    val exId1 = ObjectId()
    val w1 = createTestWorkout(
      "Workout A", listOf(
        createTestExerciseSets(
          exId1,
          listOf(SetType.warmUpSet, SetType.normalSet, SetType.normalSet)
        )
      )
    )
    val w2 = createTestWorkout(
      "Workout A - Copy", listOf(
        createTestExerciseSets(
          exId1,
          listOf(SetType.warmUpSet, SetType.normalSet, SetType.normalSet)
        )
      )
    )

    assertTrue(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - two empty workouts - returns true`() {
    val w1 = createTestWorkout("Empty 1", emptyList())
    val w2 = createTestWorkout("Empty 2", emptyList())

    assertTrue(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - identical structure different targets - returns true`() {
    val exId1 = ObjectId()
    val exerciseSets1 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    exerciseSets1.sets[0].targetRepetitions = 8 // Different Target
    exerciseSets1.sets[0].weightPounds = 100.0 // Different Target

    val exerciseSets2 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    exerciseSets2.sets[0].targetRepetitions = 12 // Different Target
    exerciseSets2.sets[0].weightPounds = 90.0 // Different Target

    val w1 = createTestWorkout("Workout A", listOf(exerciseSets1))
    val w2 = createTestWorkout("Workout B", listOf(exerciseSets2))

    assertTrue(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - identical structure different actual performance - returns true`() {
    val exId1 = ObjectId()
    val exerciseSets1 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    exerciseSets1.sets[0].repetitions = 8 // Different Actual
    exerciseSets1.sets[0].weightPounds = 100.0 // Different Actual

    val exerciseSets2 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    exerciseSets2.sets[0].repetitions = 10 // Different Actual
    exerciseSets2.sets[0].weightPounds = 100.0 // Different Actual

    val w1 = createTestWorkout("Workout A", listOf(exerciseSets1))
    val w2 = createTestWorkout("Workout B", listOf(exerciseSets2))

    assertTrue(areWorkoutsStructurallyIdentical(w1, w2))
  }


  @Example
  fun `areWorkoutsStructurallyIdentical - different number of top level items - returns false`() {
    val exId1 = ObjectId()
    val exId2 = ObjectId()
    val w1 = createTestWorkout(
      "Workout A", listOf(
        createTestExerciseSets(exId1, listOf(SetType.normalSet))
      )
    )
    val w2 = createTestWorkout(
      "Workout B", listOf(
        createTestExerciseSets(exId1, listOf(SetType.normalSet)),
        createTestExerciseSets(exId2, listOf(SetType.normalSet)) // Extra item
      )
    )

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - different item types at same position - returns false`() {
    val exId1 = ObjectId()
    val exId2 = ObjectId()
    val es1 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    val es2 = createTestExerciseSets(exId2, listOf(SetType.normalSet))
    val ss1 = createTestSuperset(listOf(es2))

    val w1 = createTestWorkout("Workout A", listOf(es1))
    val w2 = createTestWorkout("Workout B", listOf(ss1)) // Superset instead of ExerciseSets

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - same structure different top level order - returns false`() {
    val exId1 = ObjectId()
    val exId2 = ObjectId()
    val es1 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    val es2 = createTestExerciseSets(exId2, listOf(SetType.normalSet))

    val w1 = createTestWorkout("Workout A", listOf(es1, es2))
    val w2 = createTestWorkout("Workout B", listOf(es2, es1)) // Different order

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - different exercise ID - returns false`() {
    val exId1 = ObjectId()
    val exId2 = ObjectId() // Different ID
    val w1 = createTestWorkout(
      "Workout A", listOf(
        createTestExerciseSets(exId1, listOf(SetType.normalSet))
      )
    )
    val w2 = createTestWorkout(
      "Workout B", listOf(
        createTestExerciseSets(exId2, listOf(SetType.normalSet)) // Different exercise
      )
    )

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - different number of sets - returns false`() {
    val exId1 = ObjectId()
    val w1 = createTestWorkout(
      "Workout A", listOf(
        createTestExerciseSets(exId1, listOf(SetType.normalSet, SetType.normalSet)) // 2 sets
      )
    )
    val w2 = createTestWorkout(
      "Workout B", listOf(
        createTestExerciseSets(
          exId1,
          listOf(SetType.normalSet, SetType.normalSet, SetType.normalSet)
        ) // 3 sets
      )
    )

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - different set type - returns false`() {
    val exId1 = ObjectId()
    val w1 = createTestWorkout(
      "Workout A", listOf(
        createTestExerciseSets(exId1, listOf(SetType.normalSet))
      )
    )
    val w2 = createTestWorkout(
      "Workout B", listOf(
        createTestExerciseSets(exId1, listOf(SetType.warmUpSet)) // Different type
      )
    )

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - different progression scheme ID - returns false`() {
    // Assuming progressionSchemeID is compared (adapt Set creation if type differs)
    val exId1 = ObjectId()
    val w1 = createTestWorkout(
      "Workout A", listOf(
        createTestExerciseSets(
          exId1,
          listOf(SetType.normalSet),
          progressionIds = listOf(ProgressionType.LINEAR_PROGRESSION)
        )
      )
    )
    val w2 = createTestWorkout(
      "Workout B", listOf(
        createTestExerciseSets(
          exId1,
          listOf(SetType.normalSet),
          progressionIds = listOf(ProgressionType.WAVE_PROGRESSION)
        ) // Different progression
      )
    )

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - identical complex superset workouts - returns true`() {
    val exId1 = ObjectId()
    val exId2 = ObjectId()
    val exId3 = ObjectId()

    val es1 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    val es2 = createTestExerciseSets(exId2, listOf(SetType.normalSet, SetType.normalSet))
    val es3 = createTestExerciseSets(exId3, listOf(SetType.warmUpSet, SetType.normalSet))

    val ss1 = createTestSuperset(listOf(es1, es2))
    val ss2 = createTestSuperset(listOf(es1, es2)) // Identical superset structure

    val w1 = createTestWorkout("Complex A", listOf(ss1, es3))
    val w2 = createTestWorkout("Complex B", listOf(ss2, es3)) // Same structure

    assertTrue(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - different inner exercise in superset - returns false`() {
    val exId1 = ObjectId()
    val exId2 = ObjectId()
    val exId3 = ObjectId() // Different inner exercise

    val es1 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    val es2 = createTestExerciseSets(exId2, listOf(SetType.normalSet))
    val es3_diff = createTestExerciseSets(exId3, listOf(SetType.normalSet)) // Uses exId3

    val ss1 = createTestSuperset(listOf(es1, es2))
    val ss2 = createTestSuperset(listOf(es1, es3_diff)) // Different inner exercise

    val w1 = createTestWorkout("Complex A", listOf(ss1))
    val w2 = createTestWorkout("Complex B", listOf(ss2))

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }

  @Example
  fun `areWorkoutsStructurallyIdentical - different inner set structure in superset - returns false`() {
    val exId1 = ObjectId()
    val exId2 = ObjectId()

    val es1 = createTestExerciseSets(exId1, listOf(SetType.normalSet))
    val es2_v1 =
      createTestExerciseSets(exId2, listOf(SetType.normalSet, SetType.normalSet)) // 2 sets
    val es2_v2 = createTestExerciseSets(exId2, listOf(SetType.normalSet)) // 1 set

    val ss1 = createTestSuperset(listOf(es1, es2_v1))
    val ss2 = createTestSuperset(listOf(es1, es2_v2)) // Different inner set count

    val w1 = createTestWorkout("Complex A", listOf(ss1))
    val w2 = createTestWorkout("Complex B", listOf(ss2))

    assertFalse(areWorkoutsStructurallyIdentical(w1, w2))
  }
}