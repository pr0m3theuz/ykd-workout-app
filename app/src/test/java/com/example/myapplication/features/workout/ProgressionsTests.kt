@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout

import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.util.amrapProgression
import com.example.myapplication.common.util.doubleProgression
import com.example.myapplication.common.util.dynamicDoubleProgression
import com.example.myapplication.common.util.linearProgression
import com.example.myapplication.common.util.waveProgression
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.DoubleRange
import net.jqwik.api.constraints.IntRange
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.ExperimentalUuidApi

class DoubleProgressionTest {

  @Example
  fun testDoubleProgression() {
    // Create input sets
    val inputSets =
      listOf(
        Set(
          repetitions = 8,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 8,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    // Create mutable list to update
    val setsToUpdate =
      mutableListOf(
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    // Call the function
    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      // Assertions
      assertEquals(2, it.size, "Should return the same number of sets")

      it.first().let { set ->
        assertEquals(9, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }

      it[1].let { set ->
        assertEquals(8, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }
    }
  }

  @Example
  fun testDoubleProgressionMaxRepetitions() {
    // Similar to the previous test, but with repetitions at max
    // Create sample Set and input as before, but with repetitions at 10
    // Create input sets
    val inputSets =
      listOf(
        Set(
          repetitions = 10,
          weightPounds = 100.0,
          weightKilograms = 100.0.times(0.45359237),
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 10,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 100.0.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 10,
          weightPounds = 100.0,
          weightKilograms = 100.0.times(0.45359237),
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 10,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 100.0.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    // Create mutable list to update
    val setsToUpdate =
      mutableListOf(
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      it.first().let { set ->
        assertEquals(6, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(105.0, set.targetWeightPounds, "Weight should change")
        assertEquals(105.0.times(0.45359237), set.targetWeightKilograms, "Weight should change")
      }

      it[1].let { set ->
        assertEquals(6, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(105.0, set.targetWeightPounds, "Weight should change")
        assertEquals(105.0.times(0.45359237), set.targetWeightKilograms, "Weight should change")
      }
    }
  }

  @Example
  fun testDoubleProgressionBelowMinimum() {
    // Test case where repetitions are below the minimum
    // Create sample Set and input with repetitions below minimum

    // Create input sets
    val inputSets =
      listOf(
        Set(
          repetitions = 5,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 5,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    // Create mutable list to update
    val setsToUpdate =
      mutableListOf(
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      it.first().let { set ->
        assertEquals(8, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }

      it[1].let { set ->
        assertEquals(8, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }
    }
  }

  @Example
  fun `doubleProgression increases repetitions when above target repetitions and below maximum target repetitions`() {
    // Test case where repetitions are below the minimum
    // Create sample Set and input with repetitions below minimum

    // Create input sets
    val inputSets =
      listOf(
        Set(
          repetitions = 9,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 7,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 6,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 6,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 6,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    // Create mutable list to update
    val setsToUpdate =
      MutableList(inputSets.size) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      it.first().let { set ->
        assertEquals(9, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }

      it[1].let { set ->
        assertEquals(6, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }
      it[2].let { set ->
        assertEquals(6, set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }
    }
  }

  @Property
  fun `doubleProgression increases repetitions when below maximum`(
    @ForAll @IntRange(min = 2, max = 5) setCount: Int,
    @ForAll @IntRange(min = 6, max = 9) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val sets =
      List(setCount) {
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = _repetitions,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val setsToUpdate =
      MutableList(setCount) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = doubleProgression(sets, setsToUpdate)

    result.onSuccess {
      assertEquals(setCount, it.size)
      it.first().let { set ->
        assertEquals(_repetitions.inc().toString(), set.targetRepetitions)
        assertEquals(_weightPounds, set.targetWeightPounds)
        assertEquals(_weightPounds.times(0.45359237), set.targetWeightKilograms)
      }
    }
  }

  @Property
  fun `doubleProgression resets repetitions and increases weight when at maximum`(
    @ForAll @IntRange(min = 2, max = 5) setCount: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val sets =
      List(setCount) {
        Set(
          repetitions = 10,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 10,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val setsToUpdate =
      MutableList(setCount) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = doubleProgression(sets, setsToUpdate)

    result.onSuccess {
      assertEquals(setCount, it.size)
      it.first().let { set ->
        assertEquals(6, set.targetRepetitions)
        assertEquals(_weightPounds.plus(_incrementWeightPounds), set.targetWeightPounds)
        assertEquals(
          _weightPounds.times(0.45359237).plus(_incrementWeightPounds.times(0.45359237)),
          set.targetWeightKilograms,
        )
      }
    }
  }

  @Property
  fun `doubleProgression resets to target values when below minimum`(
    @ForAll @IntRange(min = 2, max = 5) setCount: Int,
    @ForAll @IntRange(min = 1, max = 7) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val sets =
      List(setCount) {
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = _weightPounds.plus(10),
          targetWeightKilograms = _weightPounds.plus(10).times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val setsToUpdate =
      MutableList(setCount) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = doubleProgression(sets, setsToUpdate)

    if (result.isFailure) {
      result.onFailure {
        assertFailsWith<IllegalArgumentException>(
          message = "Actual weight performed do not match target values",
          block = { it.message }
        )
      }
    } else if (result.isSuccess) {
      result.onSuccess {
        assertEquals(setCount, it.size)
        it.first().let { set ->
          assertEquals(8, set.targetRepetitions)
          assertEquals(_weightPounds.plus(10), set.targetWeightPounds)
          assertEquals(_weightPounds.plus(10).times(0.45359237), set.targetWeightKilograms)
        }
      }
    }
  }

  @Property
  fun `doubleProgression resets to target repetitions when below minimum`(
    @ForAll @IntRange(min = 2, max = 5) setCount: Int,
    @ForAll @IntRange(min = 1, max = 8) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val sets =
      List(setCount) {
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 9,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val setsToUpdate =
      MutableList(setCount) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val result = doubleProgression(sets, setsToUpdate)

    result.onSuccess {
      assertEquals(setCount, it.size)
      it.first().let { set ->
        assertEquals(9, set.targetRepetitions)
        assertEquals(_weightPounds, set.targetWeightPounds)
        assertEquals(_weightPounds.times(0.45359237), set.targetWeightKilograms)
      }
    }
  }

  @Example
  fun `doubleProgression long running`() {
    val inputSets =
      listOf(
        Set(
          repetitions = 9,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 7,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 6,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 6,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 6,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    // Create mutable list to update
    val setsToUpdate =
      MutableList(inputSets.size) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val newInputSets = inputSets.toMutableList()

    for (i in 0..50) {

      println("iteration input $i: ${newInputSets.map { it.targetRepetitions }}")
      println("iteration input $i: ${newInputSets.map { it.targetWeightPounds }}")

      val result = doubleProgression(newInputSets.toList(), setsToUpdate)
      result.onSuccess {
        newInputSets.zip(it).forEach { (updateSets, newSets) ->
          updateSets.repetitions = newSets.targetRepetitions?.toInt()?.inc()
          updateSets.weightPounds = newSets.targetWeightPounds
          updateSets.weightKilograms = newSets.targetWeightKilograms
          updateSets.incrementWeightPounds = newSets.incrementWeightPounds
          updateSets.incrementWeightKilograms = newSets.incrementWeightKilograms
          updateSets.targetMinimumRepetitions = newSets.targetMinimumRepetitions
          updateSets.targetMaximumRepetitions = newSets.targetMaximumRepetitions
          updateSets.targetRepetitions = newSets.targetRepetitions
          updateSets.targetWeightPounds = newSets.targetWeightPounds
          updateSets.targetWeightKilograms = newSets.targetWeightKilograms
        }
        println("iteration output $i: ${newInputSets.map { it.targetRepetitions }}")
        println("iteration output $i: ${newInputSets.map { it.targetWeightPounds }}")
      }
    }
    assertTrue(true, "Test passed")
  }

  @Property(tries = 1)
  fun `doubleProgression sets and setToUpdate are different sizes`(
    @ForAll @IntRange(min = 2, max = 5) setCount: Int,
    @ForAll @IntRange(min = 2, max = 5) setToUpdateCount: Int,
    @ForAll @IntRange(min = 1, max = 8) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val sets =
      List(setCount) {
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 9,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val setsToUpdate =
      MutableList(setToUpdateCount) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val result = doubleProgression(sets, setsToUpdate)

    sets.forEachIndexed { index, set ->
      println("set $index")
      println(Json.encodeToString(Set.serializer(), set))
    }
    println()

    assertFalse(result.isFailure)
    result.onSuccess {
      it.forEachIndexed { index, set ->
        println("setsToUpdate $index")
        println(Json.encodeToString(Set.serializer(), set))
        println()
      }

      it.first().let { set ->
        assertEquals(9, set.targetRepetitions)
        assertEquals(_weightPounds, set.targetWeightPounds)
        assertEquals(_weightPounds.times(0.45359237), set.targetWeightKilograms)
      }
    }
  }
}

class DynamicDoubleProgressionTest {

  @Example
  fun `increases repetitions when above target repetitions and below maximum target repetitions`() {
    val sets =
      listOf(
        Set(
          repetitions = 8,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.3,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      )

    val setsToUpdate =
      mutableListOf(
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      )

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(9, updatedSets[0].targetRepetitions)
    assertEquals(100.0, updatedSets[0].targetWeightPounds)
    assertEquals(45.36, updatedSets[0].targetWeightKilograms)
  }

  @Example
  fun `resets repetitions and increases weight when at maximum`() {
    val sets =
      listOf(
        Set(
          repetitions = 10,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 10,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.3,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 9,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 9,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.3,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 8,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.3,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    val setsToUpdate =
      MutableList(sets.size) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(6, updatedSets[0].targetRepetitions)
    assertEquals(105.0, updatedSets[0].targetWeightPounds)
    assertEquals(47.63, updatedSets[0].targetWeightKilograms)
  }

  @Example
  fun `resets to target values when below minimum`() {
    val sets =
      listOf(
        Set(
          repetitions = 5,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 5,
          weightPounds = 100.0,
          weightKilograms = 45.36,
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 2.27,
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 45.36,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )
    val setsToUpdate =
      MutableList(sets.size) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(8, updatedSets[0].targetRepetitions)
    assertEquals(100.0, updatedSets[0].targetWeightPounds)
    assertEquals(45.36, updatedSets[0].targetWeightKilograms)
  }

  @Example
  fun `returns failure when target repetitions are not the same`() {
    val sets =
      listOf(
        Set(
          repetitions = 10,
          weightPounds = 100.0,
          weightKilograms = 100.0.times(0.45359237),
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 10,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 100.0.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = 10,
          weightPounds = 100.0,
          weightKilograms = 100.0.times(0.45359237),
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 7,
          targetMaximumRepetitions = 10,
          targetRepetitions = 10,
          targetWeightPounds = 100.0,
          targetWeightKilograms = 100.0.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    val setsToUpdate =
      MutableList(sets.size) {
        Set(
          incrementWeightPounds = 5.0,
          incrementWeightKilograms = 5.0.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is IllegalArgumentException)
  }

  @Property
  fun `property increases repetitions when below maximum`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 6, max = 9) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val sets =
      List(setCount) {
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = _repetitions,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val setsToUpdate =
      MutableList(sets.size) {
        Set(
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    updatedSets.forEach { set ->
      assertEquals((_repetitions.plus(1)).toString(), set.targetRepetitions)
      assertEquals(_weightPounds, set.targetWeightPounds)
      assertEquals(_weightPounds.times(0.45359237), set.targetWeightKilograms)
    }
  }

  @Property
  fun `property resets repetitions and increases weight when at maximum`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val sets =
      List(setCount) {
        Set(
          repetitions = 10,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 10,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val setsToUpdate =
      MutableList(sets.size) {
        Set(
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    updatedSets.forEach { set ->
      assertEquals(6, set.targetRepetitions)
      assertEquals(_weightPounds.plus(_incrementWeightPounds), set.targetWeightPounds)
      assertEquals(
        _weightPounds.times(0.45359237).plus(_incrementWeightPounds.times(0.45359237)),
        set.targetWeightKilograms,
      )
    }
  }

  @Property
  fun `property resets to target values when below minimum`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 1, max = 5) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {

    val sets =
      List(setCount) {
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = 8,
          targetWeightPounds = _weightPounds.plus(10),
          targetWeightKilograms = _weightPounds.plus(10).times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val setsToUpdate =
      MutableList(sets.size) {
        Set(
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }


    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    updatedSets.forEach { set ->
      assertEquals(8, set.targetRepetitions)
      assertEquals(_weightPounds.plus(10), set.targetWeightPounds)
      assertEquals(_weightPounds.plus(10).times(0.45359237), set.targetWeightKilograms)
    }
  }

  @Property
  fun `property increases repetitions when below maximum using list`(
    @ForAll @IntRange(min = 3, max = 5) setCount: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {

    val repetitionsList = List(setCount) { Math.random().times(10).toInt() }

    val sets = mutableListOf<Set>()

    repetitionsList.forEach {
      sets.add(
        Set(
          repetitions = it,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = if (it < 6) 6 else it,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      )
    }

    val setsToUpdate =
      MutableList(sets.size) {
        Set(
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = dynamicDoubleProgression(sets.toList(), setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)

    if (repetitionsList.all
      { it >= 6 }
    ) {
      println("sets repetitions ${sets.map { it.repetitions }}")
      println("sets target repetitions ${sets.map { it.targetRepetitions }}")
      println("updatedSets target repetitions ${updatedSets.map { it.targetRepetitions }}")

      updatedSets.forEachIndexed { index, set ->
        assertEquals(
          if (repetitionsList[index] < 6 || repetitionsList[index] >= 10) 6
          else repetitionsList[index].plus(1).toString(),
          set.targetRepetitions,
        )
        assertEquals(
          if (repetitionsList[index] == 10) _weightPounds.plus(_incrementWeightPounds)
          else _weightPounds,
          set.targetWeightPounds,
        )
        assertEquals(
          if (repetitionsList[index] == 10)
            _weightPounds.plus(_incrementWeightPounds).times(0.45359237)
          else _weightPounds.times(0.45359237),
          set.targetWeightKilograms,
        )
      }
    } else if (repetitionsList.any
      { it < 6 }
    ) {
      updatedSets.forEachIndexed { index, set ->
        assertEquals(
          if (repetitionsList[index] < 6 || repetitionsList[index] >= 10) 6
          else repetitionsList[index].toString(),
          set.targetRepetitions,
        )
        assertEquals(
          if (repetitionsList[index] == 10) _weightPounds.plus(_incrementWeightPounds)
          else _weightPounds,
          set.targetWeightPounds,
        )
        assertEquals(
          if (repetitionsList[index] == 10)
            _weightPounds.plus(_incrementWeightPounds).times(0.45359237)
          else _weightPounds.times(0.45359237),
          set.targetWeightKilograms,
        )
      }
    }
  }
}

class WaveProgressionTest {
  @Property
  fun `test successful wave progression`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 6, max = 9) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val inputSets =
      List(setCount) {
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = _repetitions,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val setsToUpdate =
      MutableList(inputSets.size) {
        Set(
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        )
      }

    val result = waveProgression(inputSets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    val updatedReps = updatedSets[0].targetRepetitions!!.toInt()
    val updatedWeight = updatedSets[0].targetWeightPounds!!

    when (_repetitions) {
      6 -> {
        assertEquals(10, updatedReps)
        assertEquals(_weightPounds.minus(_incrementWeightPounds), updatedWeight, 0.01)
      }

      in 7..10 -> {
        assertEquals(_repetitions.minus(1), updatedReps)
        assertEquals(_weightPounds.plus(_incrementWeightPounds), updatedWeight, 0.01)
      }
    }
  }

  @Property
  fun `test failure when target repetitions are not the same`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 6, max = 9) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val inputSets =
      listOf(
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 6,
          targetMaximumRepetitions = 10,
          targetRepetitions = _repetitions,
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
        Set(
          repetitions = _repetitions,
          weightPounds = _weightPounds,
          weightKilograms = _weightPounds.times(0.45359237),
          incrementWeightPounds = _incrementWeightPounds,
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
          targetMinimumRepetitions = 7,
          targetMaximumRepetitions = 10,
          targetRepetitions = _repetitions.plus(1),
          targetWeightPounds = _weightPounds,
          targetWeightKilograms = _weightPounds.times(0.45359237),
          workoutID = ObjectId(),
          ownerID = "",
          exerciseSetsID = ObjectId(),
          exerciseID = ObjectId(),
        ),
      )

    val setsToUpdate = inputSets.toMutableList()

    val result = waveProgression(inputSets, setsToUpdate)

    assertTrue(result.isFailure)
    assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    assertEquals("All target repetitions must be the same", result.exceptionOrNull()?.message)
  }

  @Property
  fun `test repetition outside target range`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 6, max = 9) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val inputSets = List(setCount) {
      Set(
        repetitions = _repetitions,
        weightPounds = _weightPounds,
        weightKilograms = _weightPounds.times(0.45359237),
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 6,
        targetMaximumRepetitions = _repetitions.minus(1),
        targetRepetitions = _repetitions.minus(1),
        targetWeightPounds = _weightPounds,
        targetWeightKilograms = _weightPounds.times(0.45359237),
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val setsToUpdate = MutableList(inputSets.size) {
      Set(
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 6,
        targetMaximumRepetitions = 10,
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val result = waveProgression(inputSets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    assertEquals(_repetitions.minus(1).toString(), updatedSets[0].targetRepetitions)
    assertEquals(_weightPounds, updatedSets[0].targetWeightPounds)
    assertEquals(_weightPounds.times(0.45359237), updatedSets[0].targetWeightKilograms)
  }

  @Property
  fun `test repetitions below target range`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 4, max = 8) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 2.5, max = 10.0) _incrementWeightPounds: Double,
  ) {
    val inputSets = List(setCount) {
      Set(
        repetitions = _repetitions,
        weightPounds = _weightPounds,
        weightKilograms = _weightPounds.times(0.45359237),
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = _repetitions.plus(1),
        targetMaximumRepetitions = 10,
        targetRepetitions = _repetitions.plus(1),
        targetWeightPounds = _weightPounds,
        targetWeightKilograms = _weightPounds.times(0.45359237),
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val setsToUpdate = MutableList(inputSets.size) {
      Set(
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 6,
        targetMaximumRepetitions = 10,
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val result = waveProgression(inputSets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    assertEquals(_repetitions.plus(1), updatedSets[0].targetRepetitions)
    assertEquals(_weightPounds, updatedSets[0].targetWeightPounds)
    assertEquals(_weightPounds.times(0.45359237), updatedSets[0].targetWeightKilograms)
  }
}

class AmrapProgressionTest {
  @Property
  fun `test property-based AMRAP progression`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 5, max = 30) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double
  ) {
    val _incrementWeightPounds = 5.0

    val inputSets = listOf(
      Set(
        repetitions = _repetitions,
        weightPounds = _weightPounds,
        weightKilograms = _weightPounds.times(0.45359237),
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 10,
        targetMaximumRepetitions = 20,
        targetRepetitions = 10,
        targetWeightPounds = _weightPounds,
        targetWeightKilograms = _weightPounds.times(0.45359237),
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    )

    val setsToUpdate = MutableList(inputSets.size) {
      Set(
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 6,
        targetMaximumRepetitions = 10,
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val result = amrapProgression(inputSets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(1, updatedSets.size)

    val updatedTargetReps = updatedSets[0].targetRepetitions!!.toInt()
    val updatedTargetWeight = updatedSets[0].targetWeightPounds!!

    when {
      _repetitions >= 20 -> {
        assertEquals(_repetitions, updatedTargetReps)
        assertEquals(_weightPounds.plus(_incrementWeightPounds.times(2)), updatedTargetWeight, 0.01)
      }

      _repetitions in 10..19 -> {
        assertEquals(_repetitions, updatedTargetReps)
        assertEquals(_weightPounds.plus(_incrementWeightPounds), updatedTargetWeight, 0.01)
      }

      else -> {
        assertEquals(10, updatedTargetReps)
        assertEquals(_weightPounds, updatedTargetWeight, 0.01)
      }
    }
  }
}

class LinearProgressionTest {

  @Property
  fun `test property-based linear progression`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 1, max = 10) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 1.0, max = 10.0) _incrementWeightPounds: Double
  ) {
    val inputSets = listOf(
      Set(
        repetitions = _repetitions,
        weightPounds = _weightPounds,
        weightKilograms = _weightPounds.times(0.45359237),
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 10,
        targetMaximumRepetitions = 20,
        targetRepetitions = _repetitions,
        targetWeightPounds = _weightPounds,
        targetWeightKilograms = _weightPounds.times(0.45359237),
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    )

    val setsToUpdate = MutableList(inputSets.size) {
      Set(
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 6,
        targetMaximumRepetitions = 10,
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val result = linearProgression(inputSets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(1, updatedSets.size)

    val updatedReps = updatedSets[0].targetRepetitions!!.toInt()
    val updatedWeightPounds = updatedSets[0].targetWeightPounds!!
    val updatedWeightKilograms = updatedSets[0].targetWeightKilograms!!

    assertEquals(_repetitions, updatedReps)
    assertEquals(_weightPounds.plus(_incrementWeightPounds), updatedWeightPounds, 0.01)
    assertEquals(
      _weightPounds.plus(_incrementWeightPounds).times(0.45359237),
      updatedWeightKilograms,
      0.01
    )
  }

  @Property
  fun `test reset to target values when targets are missed`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 1, max = 10) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 1.0, max = 10.0) _incrementWeightPounds: Double
  ) {
    val inputSets = listOf(
      Set(
        repetitions = _repetitions,
        weightPounds = _weightPounds,
        weightKilograms = _weightPounds.times(0.45359237),
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 10,
        targetMaximumRepetitions = 20,
        targetRepetitions = _repetitions,
        targetWeightPounds = _weightPounds.plus(_incrementWeightPounds),
        targetWeightKilograms = _weightPounds.plus(_incrementWeightPounds).times(0.45359237),
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    )

    val setsToUpdate = MutableList(inputSets.size) {
      Set(
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 6,
        targetMaximumRepetitions = 10,
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val result = linearProgression(inputSets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!

    val updatedReps = updatedSets[0].targetRepetitions!!
    val updatedWeightPounds = updatedSets[0].targetWeightPounds!!
    val updatedWeightKilograms = updatedSets[0].targetWeightKilograms!!

    assertEquals(1, updatedSets.size)
    assertEquals(_repetitions, updatedReps)
    assertEquals(_weightPounds.plus(_incrementWeightPounds), updatedWeightPounds)
    assertEquals(
      _weightPounds.plus(_incrementWeightPounds).times(0.45359237),
      updatedWeightKilograms,
      0.01
    )
  }

  @Property
  fun `test reset repetitions and increment weights when targets are exceeded`(
    @ForAll @IntRange(min = 1, max = 5) setCount: Int,
    @ForAll @IntRange(min = 1, max = 10) _repetitions: Int,
    @ForAll @DoubleRange(min = 50.0, max = 200.0) _weightPounds: Double,
    @ForAll @DoubleRange(min = 1.0, max = 10.0) _incrementWeightPounds: Double
  ) {
    val inputSets = listOf(
      Set(
        repetitions = _repetitions,
        weightPounds = _weightPounds,
        weightKilograms = _weightPounds.times(0.45359237),
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 10,
        targetMaximumRepetitions = 20,
        targetRepetitions = _repetitions,
        targetWeightPounds = _weightPounds,
        targetWeightKilograms = _weightPounds.times(0.45359237),
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    )

    val setsToUpdate = MutableList(inputSets.size) {
      Set(
        incrementWeightPounds = _incrementWeightPounds,
        incrementWeightKilograms = _incrementWeightPounds.times(0.45359237),
        targetMinimumRepetitions = 6,
        targetMaximumRepetitions = 10,
        workoutID = ObjectId(),
        ownerID = "",
        exerciseSetsID = ObjectId(),
        exerciseID = ObjectId(),
      )
    }

    val result = linearProgression(inputSets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!

    val updatedReps = updatedSets[0].targetRepetitions!!
    val updatedWeightPounds = updatedSets[0].targetWeightPounds!!
    val updatedWeightKilograms = updatedSets[0].targetWeightKilograms!!

    assertEquals(1, updatedSets.size)
    assertEquals(_repetitions, updatedReps)
    assertEquals(_weightPounds.plus(_incrementWeightPounds), updatedWeightPounds)
    assertEquals(
      _weightPounds.plus(_incrementWeightPounds).times(0.45359237),
      updatedWeightKilograms,
      0.01
    )
  }
}