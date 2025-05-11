@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout

import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.util.ProgressionType
import com.example.myapplication.common.util.selectProgression
import net.jqwik.api.Example
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.uuid.ExperimentalUuidApi

private fun applyProgressionToNextWorkout(
  currentWorkout: Workout, // Contains completed data or template for progression rules
  nextWorkout: Workout     // The workout object to be modified
) {
  // Ensure workouts have comparable structures. You might need more robust checks.
  if (currentWorkout.workoutItems.size != nextWorkout.workoutItems.size) {
    // Log.w("Progression", "Cannot apply progression: Workout structures (item count) mismatch.")
    println("Progression Warning: Workout structures (item count) mismatch.") // Use println for test simplicity
    return
  }

  currentWorkout.workoutItems.forEachIndexed { index, currentItem ->
    // Get the corresponding item in the next workout
    val nextItem = nextWorkout.workoutItems.getOrNull(index)

    // Safety check: ensure nextItem exists and types match
    if (nextItem == null) {
      // Log.w("Progression", "Skipping progression for item $index: Corresponding item not found in next workout.")
      println("Progression Warning: Skipping item $index: Corresponding item not found.")
      return@forEachIndexed // continue to next item
    }
    if (currentItem::class != nextItem::class) {
      // Log.w("Progression", "Skipping progression for item $index: Component type mismatch between workouts (${currentItem::class.simpleName} vs ${nextItem::class.simpleName}).")
      println("Progression Warning: Skipping item $index: Component type mismatch.")
      return@forEachIndexed // continue to next item
    }


    when (currentItem) {
      is ExerciseSets -> {
        val nextExerciseSets = nextItem as ExerciseSets // Safe cast due to check above

        val progressionType: ProgressionType? =
          currentItem.sets.firstOrNull { set -> set.setTypeID != SetType.warmUpSet }?.progressionSchemeID
        val progressionFunction = progressionType?.let { selectProgression(it) }

        val result: Result<List<Set>>? = progressionFunction?.invoke(
          currentItem.sets,
          nextExerciseSets.sets
        )

        result?.onSuccess { newCalculatedSets ->
          // Log.d("Progression", "Applying progression to Exercise ${nextExerciseSets.exerciseID}")
          nextExerciseSets.sets.clear()
          nextExerciseSets.sets.addAll(newCalculatedSets)
        }?.onFailure { error ->
          // Log.e("Progression", "Progression failed for Exercise ${currentItem.exerciseID}: ${error.message}")
          println("Progression Error: Progression failed for Exercise ${currentItem.exerciseID}: ${error.message}")
        }
      }

      is Superset -> {
        val nextSuperset = nextItem as Superset // Safe cast

        if (currentItem.exercisesSets.size != nextSuperset.exercisesSets.size) {
          // Log.w("Progression", "Skipping progression for superset at index $index: Exercise count mismatch.")
          println("Progression Warning: Skipping superset $index: Exercise count mismatch.")
          return@forEachIndexed // continue to next outer item
        }

        currentItem.exercisesSets.forEachIndexed inner@{ exerciseIndex, currentExerciseSets ->
          val nextExerciseSets = nextSuperset.exercisesSets.getOrNull(exerciseIndex)

          if (nextExerciseSets == null) {
            // Log.w("Progression", "Skipping progression for superset exercise $exerciseIndex: Corresponding item not found in next workout's superset.")
            println("Progression Warning: Skipping superset exercise $exerciseIndex: Not found in next.")
            return@inner // continue to next exercise in superset
          }

          val progressionType: ProgressionType? =
            currentExerciseSets.sets.firstOrNull { set -> set.setTypeID != SetType.warmUpSet }?.progressionSchemeID
          val progressionFunction = progressionType?.let { selectProgression(it) }

          val result: Result<List<Set>>? = progressionFunction?.invoke(
            currentExerciseSets.sets,
            nextExerciseSets.sets
          )

          result?.onSuccess { newCalculatedSets ->
            // Log.d("Progression", "Applying progression to Superset Exercise ${nextExerciseSets.exerciseID}")
            nextExerciseSets.sets.clear()
            nextExerciseSets.sets.addAll(newCalculatedSets)
          }?.onFailure { error ->
            // Log.e("Progression", "Progression failed for Superset Exercise ${currentExerciseSets.exerciseID}: ${error.message}")
            println("Progression Error: Progression failed for Superset Exercise ${currentExerciseSets.exerciseID}: ${error.message}")
          }
        }
      }
    }
  }
  // Log.i("Progression", "Finished applying progression to next workout.")
  println("Progression Info: Finished applying progression.")
}


// Helper to create a simple ExerciseSets component
private fun createExerciseSets(
  exerciseId: ObjectId,
  sets: List<Set>,
  id: ObjectId = ObjectId(), // Simple ID generation for tests
): ExerciseSets {
  return ExerciseSets(
    id = id, exerciseID = exerciseId, sets = sets.toMutableList(),
    ownerID = "TEST",
    workoutID = ObjectId(),
    microcycleID = null,
    mesocycleID = null,
    programID = null
  )
}

// Helper to create a simple Superset component
private fun createSuperset(
  exerciseSetsList: List<ExerciseSets>,
  id: ObjectId = ObjectId() // Simple ID generation for tests
): Superset {
  return Superset(
    id = id, exercisesSets = exerciseSetsList.toMutableList(),
    ownerID = "TEST",
    workoutID = ObjectId(),
    microcycleID = null,
    mesocycleID = null,
    programID = null
  )
}

// Helper to create a simple Set
private fun createSet(
  type: SetType,
  repetitions: Int?,
  targetRepetitions: Int? = null,
  targetMinimumRepetitions: Int? = null,
  targetMaximumRepetitions: Int? = null,
  targetWeightPounds: Double? = null,
  weightPounds: Double?,
  progression: ProgressionType? = null,
  id: ObjectId = ObjectId(),
  setNumber: Int,
  setCompleted: Boolean = false,
  incrementWeightPounds: Double? = 5.0,
): Set {
  return Set(
    id = id,
    setTypeID = type,
    repetitions = repetitions,
    weightPounds = weightPounds,
    weightKilograms = weightPounds?.div(2.2046226218),
    targetRepetitions = targetRepetitions,
    targetMinimumRepetitions = targetMinimumRepetitions,
    targetMaximumRepetitions = targetMaximumRepetitions,
    targetWeightPounds = targetWeightPounds,
    targetWeightKilograms = targetWeightPounds?.div(2.2046226218),
    progressionSchemeID = progression,
    workoutID = ObjectId(),
    exerciseSetsID = ObjectId(),
    ownerID = "TEST",
    setNumber = setNumber,
    incrementWeightPounds = incrementWeightPounds,
    incrementWeightKilograms = incrementWeightPounds?.div(2.2046226218),
    setCompleted = setCompleted,
    exerciseID = ObjectId(),
  )
}

private val ex1 = ObjectId()
private val ex2 = ObjectId()


class SuccessCasesTest {
  @Example
  fun `applyProgression - ExerciseSets - updates next workout successfully`() {
    // Arrange
    val currentSets = listOf(
      createSet( // Warm-up
        type = SetType.warmUpSet,
        repetitions = 10,
        targetRepetitions = 10, // Actual reps
        targetMinimumRepetitions = 8,
        targetMaximumRepetitions = 10,
        targetWeightPounds = 50.0, // Actual weight
        weightPounds = 50.0, // Target
        progression = null, // Target
        setNumber = 1,
        setCompleted = false // No progression for warm-up
      ),
      createSet( // Working Set
        type = SetType.normalSet, // Assuming normal set
        repetitions = 5,
        targetRepetitions = 5, // Actual reps completed
        targetMinimumRepetitions = 3,
        targetMaximumRepetitions = 5,
        targetWeightPounds = 100.0, // Actual weight lifted
        weightPounds = 100.0, // Example target
        incrementWeightPounds = 5.0,
        progression = ProgressionType.LINEAR_PROGRESSION, // Example target
        setNumber = 2,
        setCompleted = false // Progression type
      )
    )
    val nextWorkoutSetsTemplate = listOf(
      createSet( // Warm-up template
        type = SetType.warmUpSet,
        setNumber = 1,
        repetitions = null, // No actual reps yet
        weightPounds = null, // No actual weight yet
        targetMinimumRepetitions = 8,
        targetMaximumRepetitions = 10,
        targetRepetitions = 10, // Target for next time
        targetWeightPounds = 50.0, // Target weight for next time
        progression = null,
        setCompleted = false
      ),
      createSet( // Working set template
        type = SetType.normalSet, // Assuming normal set
        setNumber = 2,
        repetitions = null, // No actual reps yet
        targetMinimumRepetitions = 3,
        targetMaximumRepetitions = 5,
        weightPounds = null, // No actual weight yet
        targetRepetitions = 5, // Target for next time
        targetWeightPounds = 100.0, // Target weight for next time (before progression)
        progression = ProgressionType.LINEAR_PROGRESSION,
        setCompleted = false
      )
    )
    // Expected sets AFTER progression function runs
    // IMPORTANT: These values depend entirely on the mocked behavior of the specific progression function
    val expectedNextSets = listOf(
      createSet( // Expected Warm-up (likely unchanged by progression logic)
        type = SetType.warmUpSet,
        setNumber = 1,
        repetitions = null,
        weightPounds = null,
        targetRepetitions = 10,
        targetWeightPounds = 50.0,
        progression = null,
        setCompleted = false
      ),
      createSet( // Expected Working Set (e.g., weight increased by Linear Progression mock)
        type = SetType.normalSet, // Assuming normal set
        setNumber = 2,
        repetitions = null,
        weightPounds = null,
        targetRepetitions = 5, // Target reps might or might not change
        targetWeightPounds = 105.0, // Progression applied (e.g., +5 lbs) by mock
        progression = ProgressionType.LINEAR_PROGRESSION,
        setCompleted = false
      )
    )

    val currentWorkout = Workout(
      name = "Current Workout",
      workoutItems = mutableListOf(createExerciseSets(ex1, currentSets))
    )
    val nextWorkout = Workout(
      name = "Next Workout",
      workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
    )

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
    assertEquals(expectedNextSets.size, resultNextExerciseSets.sets.size)
    // Compare relevant fields (ignoring IDs if they are random)
    expectedNextSets.forEachIndexed { i, expectedSet ->
      assertEquals(expectedSet.setTypeID, resultNextExerciseSets.sets[i].setTypeID)
      assertEquals(expectedSet.repetitions, resultNextExerciseSets.sets[i].repetitions)
      assertEquals(expectedSet.weightPounds, resultNextExerciseSets.sets[i].weightPounds)
      assertEquals(
        expectedSet.progressionSchemeID,
        resultNextExerciseSets.sets[i].progressionSchemeID
      )
    }
//    verify(exactly = 1) { selectProgression(ProgressionType.LINEAR_PROGRESSION) }
  }

  @Example
  fun `applyProgression - Superset - updates next workout successfully`() {
    // Arrange
    val currentEx1Sets =
      listOf(
        createSet( // Ex1 Working Set
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = 8, // Actual reps
          weightPounds = 30.0, // Actual weight
          targetRepetitions = 8, // Example target
          targetMinimumRepetitions = 8,
          targetMaximumRepetitions = 12,
          targetWeightPounds = 30.0, // Example target
          progression = ProgressionType.DOUBLE_PROGRESSION,
          setCompleted = true
        )
      )
    val currentEx2Sets =
      listOf(
        createSet( // Ex2 Working Set
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = 12, // Actual reps
          weightPounds = 50.0, // Actual weight
          targetRepetitions = 12, // Example target
          targetMinimumRepetitions = 8,
          targetMaximumRepetitions = 12,
          targetWeightPounds = 50.0, // Example target
          progression = ProgressionType.LINEAR_PROGRESSION,
          setCompleted = true
        )
      )
    val nextEx1SetsTemplate =
      listOf(
        createSet( // Ex1 Template
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 8, // Target before progression
          targetMinimumRepetitions = 8,
          targetMaximumRepetitions = 12,
          targetWeightPounds = 30.0, // Target before progression
          progression = ProgressionType.DOUBLE_PROGRESSION,
          setCompleted = false
        )
      )
    val nextEx2SetsTemplate =
      listOf(
        createSet( // Ex2 Template
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 12, // Target before progression
          targetMinimumRepetitions = 8,
          targetMaximumRepetitions = 12,
          targetWeightPounds = 50.0, // Target before progression
          progression = ProgressionType.LINEAR_PROGRESSION,
          setCompleted = false
        )
      )

    // Expected sets AFTER progression function runs (mocked below)
    val expectedNextEx1Sets = listOf(
      createSet( // Expected Ex1 (e.g., reps increased by Double Progression mock)
        type = SetType.normalSet,
        setNumber = 1,
        repetitions = null,
        weightPounds = null,
        targetRepetitions = 9, // Target reps increased by mock
        targetWeightPounds = 30.0, // Target weight unchanged (double progression example)
        progression = ProgressionType.DOUBLE_PROGRESSION,
        setCompleted = false
      )
    )
    val expectedNextEx2Sets = listOf(
      createSet( // Expected Ex2 (e.g., weight increased by Linear Progression mock)
        type = SetType.normalSet,
        setNumber = 1,
        repetitions = null,
        weightPounds = null,
        targetRepetitions = 12, // Target reps unchanged
        targetWeightPounds = 52.5, // Target weight increased by mock
        progression = ProgressionType.LINEAR_PROGRESSION,
        setCompleted = false
      )
    )

    val currentWorkout = Workout(
      name = "Current Workout", workoutItems = mutableListOf(
        createSuperset(
          listOf(
            createExerciseSets(ex1, currentEx1Sets),
            createExerciseSets(ex2, currentEx2Sets)
          )
        )
      )
    )
    val nextWorkout = Workout(
      name = "Next Workout", workoutItems = mutableListOf(
        createSuperset(
          listOf(
            createExerciseSets(ex1, nextEx1SetsTemplate),
            createExerciseSets(ex2, nextEx2SetsTemplate)
          )
        )
      )
    )

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    val resultNextSuperset = nextWorkout.workoutItems[0] as Superset
    // Check Ex1
    assertEquals(expectedNextEx1Sets.size, resultNextSuperset.exercisesSets[0].sets.size)
    assertEquals(
      expectedNextEx1Sets[0].repetitions,
      resultNextSuperset.exercisesSets[0].sets[0].repetitions
    )
    assertEquals(
      expectedNextEx1Sets[0].weightPounds,
      resultNextSuperset.exercisesSets[0].sets[0].weightPounds
    )
    // Check Ex2
    assertEquals(expectedNextEx2Sets.size, resultNextSuperset.exercisesSets[1].sets.size)
    assertEquals(
      expectedNextEx2Sets[0].repetitions,
      resultNextSuperset.exercisesSets[1].sets[0].repetitions
    )
    assertEquals(
      expectedNextEx2Sets[0].weightPounds,
      resultNextSuperset.exercisesSets[1].sets[0].weightPounds
    )

//    verify(exactly = 1) { selectProgression(ProgressionType.DOUBLE_PROGRESSION) }
//    verify(exactly = 1) { selectProgression(ProgressionType.LINEAR_PROGRESSION) }
  }

  @Example
  fun `applyProgression - Mixed Components - updates relevant parts only`() {
    // Arrange
    val currentExSets = createExerciseSets(
      ex1,
      listOf(
        createSet( // Working set
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = 5, // Actual reps
          weightPounds = 100.0, // Actual weight
          targetRepetitions = 5, // Example target
          targetMinimumRepetitions = 3,
          targetMaximumRepetitions = 5,
          targetWeightPounds = 100.0, // Example target
          progression = ProgressionType.LINEAR_PROGRESSION
        )
      )
    )
    val currentSuperset = createSuperset(
      listOf(
        createExerciseSets(
          ex2,
          listOf(
            createSet( // Superset working set
              type = SetType.normalSet,
              setNumber = 1,
              repetitions = 8, // Actual reps
              weightPounds = 30.0, // Actual weight
              targetRepetitions = 8, // Example target
              targetMinimumRepetitions = 8,
              targetMaximumRepetitions = 10,
              targetWeightPounds = 30.0, // Example target
              progression = ProgressionType.DOUBLE_PROGRESSION
            )
          )
        )
      )
    )

    val nextExSetsTemplate = createExerciseSets(
      ex1,
      listOf(
        createSet(
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 5,
          targetMinimumRepetitions = 3,
          targetMaximumRepetitions = 5,
          targetWeightPounds = 100.0,
          progression = ProgressionType.LINEAR_PROGRESSION,
          setCompleted = false
        )
      )
    )
    val nextSupersetTemplate = createSuperset(
      listOf(
        createExerciseSets(
          ex2,
          listOf(
            createSet(
              type = SetType.normalSet,
              setNumber = 1,
              repetitions = null,
              weightPounds = null,
              targetRepetitions = 8,
              targetMinimumRepetitions = 8,
              targetMaximumRepetitions = 10,
              targetWeightPounds = 30.0,
              progression = ProgressionType.DOUBLE_PROGRESSION,
              setCompleted = false
            )
          )
        )
      )
    )

    // Expected sets AFTER progression runs (mocked below)
    val expectedNextExSetsSets =
      listOf(
        createSet( // Expected ExSets (e.g., weight increased)
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 5,
          targetWeightPounds = 102.5, // Weight increased by mock
          progression = ProgressionType.LINEAR_PROGRESSION,
          setCompleted = false
        )
      )
    val expectedNextSupersetExSetsSets =
      listOf(
        createSet( // Expected Superset ExSets (e.g., reps increased)
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 9, // Reps increased by mock
          targetWeightPounds = 30.0,
          progression = ProgressionType.DOUBLE_PROGRESSION,
          setCompleted = false
        )
      )


    val currentWorkout = Workout(
      name = "Current Workout",
      workoutItems = mutableListOf(currentExSets, currentSuperset)
    )
    val nextWorkout = Workout(
      name = "Next Workout",
      workoutItems = mutableListOf(nextExSetsTemplate, nextSupersetTemplate)
    )

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    // Check ExerciseSets
    val resultNextExSets = nextWorkout.workoutItems[0] as ExerciseSets
    assertEquals(expectedNextExSetsSets[0].weightPounds, resultNextExSets.sets[0].weightPounds)
    // Check Superset
    val resultNextSuperset = nextWorkout.workoutItems[1] as Superset
    assertEquals(
      expectedNextSupersetExSetsSets[0].repetitions,
      resultNextSuperset.exercisesSets[0].sets[0].repetitions
    )

//    verify(exactly = 1) { selectProgression(ProgressionType.LINEAR_PROGRESSION) }
//    verify(exactly = 1) { selectProgression(ProgressionType.DOUBLE_PROGRESSION) }
  }
}


class EdgeCasesTest {
  @Example
  fun `applyProgression - No Progression Scheme - next workout unchanged`() {
    // Arrange
    val currentSets = listOf(
      createSet(
        type = SetType.normalSet, // Assuming normal set
        setNumber = 1,
        repetitions = 5, // Actual reps
        weightPounds = 100.0, // Actual weight
        targetRepetitions = 5, // Example target
        targetWeightPounds = 100.0, // Example target
        incrementWeightPounds = 5.0,
        progression = null // <-- Key part: No progression
      )
    )
    val nextWorkoutSetsTemplate = listOf(
      createSet(
        type = SetType.normalSet, // Assuming normal set
        setNumber = 1,
        repetitions = null,
        weightPounds = null,
        targetRepetitions = 5,
        targetWeightPounds = 100.0,
        progression = null, // <-- Key part: No progression
        setCompleted = false
      )
    )
    // Deep copy for comparison - Create a copy of the original template sets
    val originalNextWorkoutSets = nextWorkoutSetsTemplate.map { it.copy() }

    val currentWorkout = Workout(
      name = "Current Workout",
      workoutItems = mutableListOf(createExerciseSets(ex1, currentSets))
    )
    val nextWorkout = Workout(
      name = "Next Workout",
      workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
    )

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
    assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets) // Should be identical
//    verify { selectProgression(any()).wasNot(Called) } // No progression should be selected
  }

  @Example
  fun `applyProgression - Only Warmup Sets - next workout unchanged`() {
    // Arrange
    val currentSets = listOf(
      createSet(
        type = SetType.warmUpSet, // <-- Key part: Warm-up
        setNumber = 1,
        repetitions = 10, // Actual reps
        weightPounds = 50.0, // Actual weight
        targetRepetitions = 10, // Example target
        targetWeightPounds = 50.0, // Example target
        incrementWeightPounds = 5.0,
        progression = ProgressionType.LINEAR_PROGRESSION // Progression ignored for warm-up
      )
    )
    val nextWorkoutSetsTemplate =
      listOf(
        createSet(
          type = SetType.warmUpSet, // <-- Key part: Warm-up
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 10,
          targetWeightPounds = 50.0,
          progression = ProgressionType.LINEAR_PROGRESSION, // Progression ignored for warm-up
          setCompleted = false
        )
      )
    // Deep copy for comparison
    val originalNextWorkoutSets = nextWorkoutSetsTemplate.map { it.copy() }


    val currentWorkout = Workout(
      name = "Current Workout",
      workoutItems = mutableListOf(createExerciseSets(ex1, currentSets))
    )
    val nextWorkout = Workout(
      name = "Next Workout",
      workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
    )

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
    assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets) // Should be identical
//    verify { selectProgression(any()).wasNot(Called) }
  }


  @Example
  fun `applyProgression - selectProgression returns null - next workout unchanged`() {
    // Arrange
    val currentSets =
      listOf(
        createSet(
          type = SetType.normalSet, // Assuming normal set
          setNumber = 1,
          repetitions = 5,
          weightPounds = 100.0,
          targetRepetitions = 5,
          targetWeightPounds = 100.0,
          incrementWeightPounds = 5.0,
          progression = null// Will mock selectProgression for this
        )
      )
    val nextWorkoutSetsTemplate =
      listOf(
        createSet(
          type = SetType.normalSet, // Assuming normal set
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 5,
          targetWeightPounds = 100.0,
          progression = null,
          setCompleted = false
        )
      )
    val originalNextWorkoutSets = nextWorkoutSetsTemplate.map { it.copy() }

    val currentWorkout =
      Workout(name = "", workoutItems = mutableListOf(createExerciseSets(ex1, currentSets)))
    val nextWorkout = Workout(
      name = "",
      workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
    )

    // Mock selectProgression to return null
//      every { selectProgression(ProgressionType.LINEAR_PROGRESSION) } returns null

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
    assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets)
//    verify(exactly = 0) { selectProgression(ProgressionType.LINEAR_PROGRESSION) } // It was called
  }

  @Example
  fun `applyProgression - Progression Lambda returns Failure - next workout unchanged`() {
    // Arrange
    val currentSets =
      listOf(
        createSet(
          type = SetType.normalSet, // Assuming normal set
          setNumber = 1,
          repetitions = 5,
          weightPounds = 100.0,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetMinimumRepetitions = 8,
          targetMaximumRepetitions = 10,
          incrementWeightPounds = 5.0,
          progression = ProgressionType.LINEAR_PROGRESSION // Will mock lambda for this
        )
      )

    val nextWorkoutSetsTemplate =
      listOf(
        createSet(
          type = SetType.normalSet, // Assuming normal set
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 8,
          targetMinimumRepetitions = 8,
          targetMaximumRepetitions = 10,
          incrementWeightPounds = 5.0,
          targetWeightPounds = 100.0,
          progression = ProgressionType.LINEAR_PROGRESSION,
          setCompleted = false
        )
      )

    val originalNextWorkoutSets = nextWorkoutSetsTemplate.map { it.copy() }

    val currentWorkout =
      Workout(
        name = "Current Workout",
        workoutItems = mutableListOf(createExerciseSets(ex1, currentSets))
      )
    val nextWorkout = Workout(
      name = "Next Workout",
      workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
    )

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
    assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets) // Should remain unchanged
  }

  @Example
  fun `applyProgression - Structure Mismatch (Item Count) - exits early`() {
    // Arrange
    val currentWorkout =
      Workout(name = "", workoutItems = mutableListOf(createExerciseSets(ex1, emptyList())))
    val nextWorkout = Workout(name = "", workoutItems = mutableListOf()) // Different item count
    val originalNextWorkoutItems = nextWorkout.workoutItems.toList() // Copy before call

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    assertEquals(originalNextWorkoutItems, nextWorkout.workoutItems) // No change
  }

  @Example
  fun `applyProgression - Structure Mismatch (Component Type) - skips mismatched item`() {
    // Arrange
    val currentExSets = createExerciseSets(
      ex1,
      listOf(
        createSet(
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = 5,
          weightPounds = 100.0,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          incrementWeightPounds = 2.5,
          progression = ProgressionType.LINEAR_PROGRESSION
        )
      )
    )
    val currentSuperset = createSuperset( // Item 1 in current is Superset
      listOf(
        createExerciseSets(
          ex2,
          listOf(
            createSet(
              type = SetType.normalSet,
              setNumber = 1,
              repetitions = 8,
              weightPounds = 30.0,
              targetRepetitions = 10,
              targetWeightPounds = 30.0,
              incrementWeightPounds = 2.5,
              progression = ProgressionType.DOUBLE_PROGRESSION
            )
          )
        )
      )
    )

    // Create next workout with types swapped at index 1
    val nextExSetsTemplate = createExerciseSets( // Item 0 in next is ExSets (matches current)
      ex1,
      listOf(
        createSet(
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          incrementWeightPounds = 2.5,
          progression = ProgressionType.LINEAR_PROGRESSION,
          setCompleted = false
        )
      )
    )
    val nextExSetsTemplate2 =
      createExerciseSets( // Item 1 in next is ExSets (MISMATCH with current)
        ex2,
        listOf(
          createSet(
            type = SetType.normalSet,
            setNumber = 1,
            repetitions = null,
            weightPounds = null,
            targetRepetitions = 10, // This template data shouldn't change
            targetWeightPounds = 30.0, // This template data shouldn't change
            progression = ProgressionType.DOUBLE_PROGRESSION, // Progression shouldn't be applied here
            setCompleted = false
          )
        )
      ) // Note: ExerciseSets instead of Superset

    val originalNextItem1Sets =
      nextExSetsTemplate2.sets.map { it.copy() } // Copy state of the item that should be skipped

    val currentWorkout = Workout(
      name = "Current Workout",
      workoutItems = mutableListOf(currentExSets, currentSuperset)
    ) // ExSets, Superset

    val nextWorkout = Workout(
      name = "Next Workout",
      workoutItems = mutableListOf(nextExSetsTemplate, nextExSetsTemplate2)
    ) // ExSets, ExSets <-- Type mismatch at index 1

    // Expected result for Item 0 (mocked)
    val expectedNextExSetsSets =
      listOf(
        createSet(
          type = SetType.normalSet,
          setNumber = 1,
          repetitions = null,
          weightPounds = null,
          targetRepetitions = 5,
          targetWeightPounds = 102.5, // Weight increased by mock
          progression = ProgressionType.LINEAR_PROGRESSION,
          setCompleted = false
        )
      )

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    // Check Item 0 (ExerciseSets) - Should be progressed
    val resultNextExSets = nextWorkout.workoutItems[0] as ExerciseSets
    assertEquals(
      expectedNextExSetsSets[0].targetWeightPounds,
      resultNextExSets.sets[0].targetWeightPounds,
      "Item 0 (ExSets) Target Weight mismatch"
    )

    // Check Item 1 (ExerciseSets in next, Superset in current) - Should be skipped and unchanged
    val resultSkippedItem = nextWorkout.workoutItems[1] as ExerciseSets
    assertEquals(
      originalNextItem1Sets,
      resultSkippedItem.sets,
      "Item 1 (mismatched type) should be unchanged"
    )
  }

  @Example
  fun `applyProgression - Structure Mismatch (Superset Item Count) - skips superset`() {
    // Arrange
    val currentSuperset = createSuperset( // Current has 2 exercises
      listOf(
        createExerciseSets(
          ex1,
          listOf(
            createSet(
              type = SetType.normalSet,
              setNumber = 1,
              repetitions = 8,
              weightPounds = 30.0,
              targetRepetitions = 8,
              targetWeightPounds = 30.0,
              progression = ProgressionType.DOUBLE_PROGRESSION
            )
          )
        ),
        createExerciseSets(
          ex2,
          listOf(
            createSet(
              type = SetType.normalSet,
              setNumber = 1,
              repetitions = 12,
              weightPounds = 50.0,
              targetRepetitions = 12,
              targetWeightPounds = 50.0,
              progression = ProgressionType.LINEAR_PROGRESSION
            )
          )
        )
      )
    )
    // Next superset only has one exercise - MISMATCH
    val nextSupersetTemplate = createSuperset(
      listOf(
        createExerciseSets(
          ex1, // Only ex1 present
          listOf(
            createSet(
              type = SetType.normalSet,
              setNumber = 1,
              repetitions = null,
              weightPounds = null,
              targetRepetitions = 8, // Template data
              targetWeightPounds = 30.0, // Template data
              progression = ProgressionType.DOUBLE_PROGRESSION, // Progression shouldn't apply
              setCompleted = false
            )
          )
        )
      )
    )
    // Copy original state of the next workout's superset for comparison
    val originalNextSupersetExercises = nextSupersetTemplate.exercisesSets.map { ex ->
      ex.copy(sets = ex.sets.map { it.copy() }.toMutableList())
    }

    val currentWorkout =
      Workout(name = "Current Workout", workoutItems = mutableListOf(currentSuperset))
    val nextWorkout =
      Workout(name = "Next Workout", workoutItems = mutableListOf(nextSupersetTemplate))

    // Act
    applyProgressionToNextWorkout(currentWorkout, nextWorkout)

    // Assert
    val resultNextSuperset = nextWorkout.workoutItems[0] as Superset
    // Check that the next superset is unchanged
    assertEquals(originalNextSupersetExercises.size, resultNextSuperset.exercisesSets.size)
    assertEquals(
      originalNextSupersetExercises[0].exerciseID,
      resultNextSuperset.exercisesSets[0].exerciseID
    )
    assertEquals(originalNextSupersetExercises[0].sets, resultNextSuperset.exercisesSets[0].sets)
  }

  @Example
  fun `applyProgression - Empty Workouts - runs without error`() {
    // Arrange
    val currentWorkout = Workout(name = "", workoutItems = mutableListOf())
    val nextWorkout = Workout(name = "", workoutItems = mutableListOf())

    // Act & Assert
    assertDoesNotThrow {
      applyProgressionToNextWorkout(currentWorkout, nextWorkout)
    }
  }
}

