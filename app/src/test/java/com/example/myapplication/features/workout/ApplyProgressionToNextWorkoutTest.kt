@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout

import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.util.ProgressionType
import io.mockk.Called
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import io.mockk.verify
import net.jqwik.api.Example
import net.jqwik.api.Group
import net.jqwik.api.Label
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import kotlin.uuid.ExperimentalUuidApi

//region Function Under Example (Placeholder - Assume it's in an object or class)
object WorkoutProgressor {

  // Mockable function selector - In a real app, this might be injected
  // For testing, we'll mock this function directly using mockkStatic if needed,
  // or preferably, inject a mockable interface/class providing this function.
  // Let's assume for simplicity we can mock it directly or it's part of the class under test.
  fun selectProgression(progression: ProgressionType): ((List<Set>, List<Set>) -> Result<List<Set>>)? {
    // Real implementation would go here
    // Mocked during tests
    throw NotImplementedError("This should be mocked in tests")
  }

  // --- Paste the applyProgressionToNextWorkout function here ---
  // @OptIn(ExperimentalUuidApi::class) // If needed
  fun applyProgressionToNextWorkout(
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

          currentItem.exercisesSets.forEachIndexed { exerciseIndex, currentExerciseSets ->
            val nextExerciseSets = nextSuperset.exercisesSets.getOrNull(exerciseIndex)

            if (nextExerciseSets == null) {
              // Log.w("Progression", "Skipping progression for superset exercise $exerciseIndex: Corresponding item not found in next workout's superset.")
              println("Progression Warning: Skipping superset exercise $exerciseIndex: Not found in next.")
              return@forEachIndexed // continue to next exercise in superset
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
}
//endregion


class ApplyProgressionToNextWorkoutTest {

  // Mock the progression logic function
  // Use relaxed=true if you don't need to specify behavior for every single case
  // Use mockkObject if WorkoutProgressor is an object, or regular mockk if it's a class instance
  @BeforeEach
  fun setUp() {
    // Mock the object containing selectProgression if it's an object
    mockkObject(WorkoutProgressor)
    // If selectProgression is a top-level function or extension, use mockkStatic
    // mockkStatic("com.yourpackage.FileNameKt") // Replace with actual package/file
  }

  @AfterEach
  fun tearDown() {
    // Unmock objects or static mocks after each test
    unmockkObject(WorkoutProgressor)
    // unmockkStatic("com.yourpackage.FileNameKt")
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
    weightPounds: Double?,
    progression: ProgressionType? = null,
    id: ObjectId = ObjectId()
  ): Set {
    return Set(
      id = id,
      setTypeID = type,
      repetitions = repetitions,
      weightPounds = weightPounds,
      progressionSchemeID = progression,
      workoutID = ObjectId(),
      exerciseSetsID = ObjectId(),
      ownerID = "TEST",
    )
  }

  val ex1 = ObjectId()
  val ex2 = ObjectId()

  @Group
  @Label("Success Scenarios")
  inner class SuccessCases {

    @Example
    fun `applyProgression - ExerciseSets - updates next workout successfully`() {
      // Arrange
      val currentSets = listOf(
        createSet(SetType.warmUpSet, 10, 50.0),
        createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION)
      )
      val nextWorkoutSetsTemplate = listOf(
        createSet(SetType.warmUpSet, 10, 50.0), // Warm-up usually stays same
        createSet(
          SetType.warmUpSet,
          5,
          100.0,
          ProgressionType.LINEAR_PROGRESSION
        ) // Template for next
      )
      val expectedNextSets = listOf(
        createSet(SetType.warmUpSet, 10, 50.0), // Unchanged warm-up
        createSet(
          SetType.warmUpSet,
          5,
          105.0,
          ProgressionType.LINEAR_PROGRESSION
        ) // Expect weightPounds increase
      )

      val currentWorkout = Workout(
        name = "Current Workout",
        workoutItems = mutableListOf(createExerciseSets(ex1, currentSets))
      )
      val nextWorkout = Workout(
        name = "Next Workout",
        workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
      )

      // Mock the progression function
      val mockProgressionLambda: (List<Set>, List<Set>) -> Result<List<Set>> =
        { completed, template -> Result.success(expectedNextSets) } // Simulate successful progression

      every { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } returns mockProgressionLambda

      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

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
      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) }
    }

    @Example
    fun `applyProgression - Superset - updates next workout successfully`() {
      // Arrange
      val currentEx1Sets =
        listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
      val currentEx2Sets =
        listOf(createSet(SetType.warmUpSet, 12, 50.0, ProgressionType.LINEAR_PROGRESSION))
      val nextEx1SetsTemplate =
        listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
      val nextEx2SetsTemplate =
        listOf(createSet(SetType.warmUpSet, 12, 50.0, ProgressionType.LINEAR_PROGRESSION))

      val expectedNextEx1Sets = listOf(
        createSet(
          SetType.warmUpSet,
          9,
          30.0,
          ProgressionType.DOUBLE_PROGRESSION
        )
      ) // Reps increased
      val expectedNextEx2Sets = listOf(
        createSet(
          SetType.warmUpSet,
          12,
          52.5,
          ProgressionType.LINEAR_PROGRESSION
        )
      ) // Weight increased

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

      // Mock progression functions
      val mockProgressionLambdaReps: (List<Set>, List<Set>) -> Result<List<Set>> =
        { _, _ -> Result.success(expectedNextEx1Sets) }
      val mockProgressionLambdaWeight: (List<Set>, List<Set>) -> Result<List<Set>> =
        { _, _ -> Result.success(expectedNextEx2Sets) }

      every { WorkoutProgressor.selectProgression(ProgressionType.DOUBLE_PROGRESSION) } returns mockProgressionLambdaReps
      every { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } returns mockProgressionLambdaWeight

      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

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

      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.DOUBLE_PROGRESSION) }
      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) }
    }

    @Example
    fun `applyProgression - Mixed Components - updates relevant parts only`() {
      // Arrange
      val currentExSets = createExerciseSets(
        ex1,
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      )
      val currentSuperset = createSuperset(
        listOf(
          createExerciseSets(
            ex2,
            listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
          )
        )
      )

      val nextExSetsTemplate = createExerciseSets(
        ex1,
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      )
      val nextSupersetTemplate = createSuperset(
        listOf(
          createExerciseSets(
            ex2,
            listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
          )
        )
      )

      val expectedNextExSetsSets =
        listOf(createSet(SetType.warmUpSet, 5, 102.5)) // Weight increased
      val expectedNextSupersetExSetsSets =
        listOf(createSet(SetType.warmUpSet, 9, 30.0)) // Reps increased


      val currentWorkout = Workout(
        name = "Current Workout",
        workoutItems = mutableListOf(currentExSets, currentSuperset)
      )
      val nextWorkout = Workout(
        name = "Next Workout",
        workoutItems = mutableListOf(nextExSetsTemplate, nextSupersetTemplate)
      )

      // Mock progression functions
      val mockProgressionWeight: (List<Set>, List<Set>) -> Result<List<Set>> =
        { _, _ -> Result.success(expectedNextExSetsSets) }
      val mockProgressionReps: (List<Set>, List<Set>) -> Result<List<Set>> =
        { _, _ -> Result.success(expectedNextSupersetExSetsSets) }

      every { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } returns mockProgressionWeight
      every { WorkoutProgressor.selectProgression(ProgressionType.DOUBLE_PROGRESSION) } returns mockProgressionReps

      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

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

      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) }
      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.DOUBLE_PROGRESSION) }
    }
  }

  @Group
  @Label("Edge Cases and Failures")
  inner class EdgeCases {

    @Example
    fun `applyProgression - No Progression Scheme - next workout unchanged`() {
      // Arrange
      val currentSets = listOf(createSet(SetType.warmUpSet, 5, 100.0, null)) // No progression ID
      val nextWorkoutSetsTemplate = listOf(createSet(SetType.warmUpSet, 5, 100.0, null))
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
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

      // Assert
      val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
      assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets) // Should be identical
      verify {
        WorkoutProgressor.selectProgression(any())?.wasNot(Called)
      } // No progression should be selected
    }

    @Example
    fun `applyProgression - Only Warmup Sets - next workout unchanged`() {
      // Arrange
      val currentSets = listOf(
        createSet(
          SetType.warmUpSet,
          10,
          50.0,
          ProgressionType.LINEAR_PROGRESSION
        )
      ) // Progression on warm-up ignored
      val nextWorkoutSetsTemplate =
        listOf(createSet(SetType.warmUpSet, 10, 50.0, ProgressionType.LINEAR_PROGRESSION))
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
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

      // Assert
      val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
      assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets) // Should be identical
      verify { WorkoutProgressor.selectProgression(any())?.wasNot(Called) }
    }


    @Example
    fun `applyProgression - selectProgression returns null - next workout unchanged`() {
      // Arrange
      val currentSets =
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      val nextWorkoutSetsTemplate =
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      val originalNextWorkoutSets = nextWorkoutSetsTemplate.map { it.copy() }

      val currentWorkout =
        Workout(name = "", workoutItems = mutableListOf(createExerciseSets(ex1, currentSets)))
      val nextWorkout = Workout(
        name = "",
        workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
      )

      // Mock selectProgression to return null
      every { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } returns null

      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

      // Assert
      val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
      assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets)
      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } // It was called
    }

    @Example
    fun `applyProgression - Progression Lambda returns Failure - next workout unchanged`() {
      // Arrange
      val currentSets =
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      val nextWorkoutSetsTemplate =
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      val originalNextWorkoutSets = nextWorkoutSetsTemplate.map { it.copy() }

      val currentWorkout =
        Workout(name = "", workoutItems = mutableListOf(createExerciseSets(ex1, currentSets)))
      val nextWorkout = Workout(
        name = "",
        workoutItems = mutableListOf(createExerciseSets(ex1, nextWorkoutSetsTemplate))
      )

      // Mock the progression function to return failure
      val mockProgressionLambda: (List<Set>, List<Set>) -> Result<List<Set>> =
        { _, _ -> Result.failure(Exception("Progression calculation error")) }

      every { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } returns mockProgressionLambda

      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

      // Assert
      val resultNextExerciseSets = nextWorkout.workoutItems[0] as ExerciseSets
      assertEquals(originalNextWorkoutSets, resultNextExerciseSets.sets) // Should remain unchanged
      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) }
    }

    @Example
    fun `applyProgression - Structure Mismatch (Item Count) - exits early`() {
      // Arrange
      val currentWorkout =
        Workout(name = "", workoutItems = mutableListOf(createExerciseSets(ex1, emptyList())))
      val nextWorkout = Workout(name = "", workoutItems = mutableListOf()) // Different item count
      val originalNextWorkoutItems = nextWorkout.workoutItems.toList() // Copy before call

      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

      // Assert
      assertEquals(originalNextWorkoutItems, nextWorkout.workoutItems) // No change
      verify {
        WorkoutProgressor.selectProgression(any())?.wasNot(Called)
      } // Should not even try to progress
    }

    @Example
    fun `applyProgression - Structure Mismatch (Component Type) - skips mismatched item`() {
      // Arrange
      val currentExSets = createExerciseSets(
        ex1,
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      )
      val currentSuperset = createSuperset(
        listOf(
          createExerciseSets(
            ex2,
            listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
          )
        )
      )

      // Create next workout with types swapped at index 1
      val nextExSetsTemplate = createExerciseSets(
        ex1,
        listOf(createSet(SetType.warmUpSet, 5, 100.0, ProgressionType.LINEAR_PROGRESSION))
      )
      val nextExSetsTemplate2 = createExerciseSets(
        ex2,
        listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
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

      val expectedNextExSetsSets =
        listOf(createSet(SetType.warmUpSet, 5, 102.5)) // Progression for item 0

      // Mock progression functions
      val mockProgressionWeight: (List<Set>, List<Set>) -> Result<List<Set>> =
        { _, _ -> Result.success(expectedNextExSetsSets) }
      // No need to mock _root_ide_package_.com.example.myapplication.common.util.ProgressionType.DOUBLE_PROGRESSION as it shouldn't be called for the mismatched item

      every { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } returns mockProgressionWeight

      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

      // Assert
      // Check Item 0 (ExerciseSets) - Should be progressed
      val resultNextExSets = nextWorkout.workoutItems[0] as ExerciseSets
      assertEquals(expectedNextExSetsSets[0].weightPounds, resultNextExSets.sets[0].weightPounds)

      // Check Item 1 (ExerciseSets in next, Superset in current) - Should be skipped and unchanged
      val resultSkippedItem = nextWorkout.workoutItems[1] as ExerciseSets
      assertEquals(originalNextItem1Sets, resultSkippedItem.sets)

      verify(exactly = 1) { WorkoutProgressor.selectProgression(ProgressionType.LINEAR_PROGRESSION) } // Called for item 0
      verify {
        WorkoutProgressor.selectProgression(ProgressionType.DOUBLE_PROGRESSION)
          ?.wasNot(Called)
      } // Not called for item 1
    }

    @Example
    fun `applyProgression - Structure Mismatch (Superset Item Count) - skips superset`() {
      // Arrange
      val currentSuperset = createSuperset(
        listOf(
          createExerciseSets(
            ex1,
            listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
          ),
          createExerciseSets(
            ex2,
            listOf(createSet(SetType.warmUpSet, 12, 50.0, ProgressionType.LINEAR_PROGRESSION))
          )
        )
      )
      // Next superset only has one exercise
      val nextSupersetTemplate = createSuperset(
        listOf(
          createExerciseSets(
            ex1,
            listOf(createSet(SetType.warmUpSet, 8, 30.0, ProgressionType.DOUBLE_PROGRESSION))
          )
        )
      )
      val originalNextSupersetExercises = nextSupersetTemplate.exercisesSets.map { ex ->
        ex.copy(sets = ex.sets.map { it.copy() }.toMutableList())
      }


      val currentWorkout =
        Workout(name = "Current Workout", workoutItems = mutableListOf(currentSuperset))
      val nextWorkout =
        Workout(name = "Next Workout", workoutItems = mutableListOf(nextSupersetTemplate))


      // Act
      WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)

      // Assert
      val resultNextSuperset = nextWorkout.workoutItems[0] as Superset
      // Check that the next superset is unchanged
      assertEquals(originalNextSupersetExercises.size, resultNextSuperset.exercisesSets.size)
      assertEquals(
        originalNextSupersetExercises[0].exerciseID,
        resultNextSuperset.exercisesSets[0].exerciseID
      )
      assertEquals(originalNextSupersetExercises[0].sets, resultNextSuperset.exercisesSets[0].sets)

      verify {
        WorkoutProgressor.selectProgression(any())?.wasNot(Called)
      } // Should skip the whole superset
    }

    @Example
    fun `applyProgression - Empty Workouts - runs without error`() {
      // Arrange
      val currentWorkout = Workout(name = "", workoutItems = mutableListOf())
      val nextWorkout = Workout(name = "", workoutItems = mutableListOf())

      // Act & Assert
      assertDoesNotThrow {
        WorkoutProgressor.applyProgressionToNextWorkout(currentWorkout, nextWorkout)
      }
      verify { WorkoutProgressor.selectProgression(any())?.wasNot(Called) }
    }
  }
}
