package com.example.myapplication.components.exercise

import com.example.myapplication.data.model.Set
import com.example.myapplication.utils.doubleProgression
import com.example.myapplication.utils.dynamicDoubleProgression
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.DoubleRange
import net.jqwik.api.constraints.IntRange
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UpdatedProgressionsTests {}

class DoubleProgressionTest {

  @Example
  fun testDoubleProgression() {
    // Create input sets
    val inputSets =
      listOf(
        Set().apply {
          repetitions = 8
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
        Set().apply {
          repetitions = 8
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
      )

    // Create mutable list to update
    val setsToUpdate =
      mutableListOf(
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        },
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        },
      )

    // Call the function
    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      // Assertions
      assertEquals(2, it.size, "Should return the same number of sets")

      it.first().let { set ->
        assertEquals("9", set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }

      it[1].let { set ->
        assertEquals("8", set.targetRepetitions, "Repetitions should increase by 1")
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
        Set().apply {
          repetitions = 10
          weightPounds = 100.0
          weightKilograms = 100.0.times(0.45359237)
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "10"
          targetWeightPounds = 100.0
          targetWeightKilograms = 100.0.times(0.45359237)
        },
        Set().apply {
          repetitions = 10
          weightPounds = 100.0
          weightKilograms = 100.0.times(0.45359237)
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "10"
          targetWeightPounds = 100.0
          targetWeightKilograms = 100.0.times(0.45359237)
        },
      )

    // Create mutable list to update
    val setsToUpdate =
      mutableListOf(
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        },
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        },
      )

    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      it.first().let { set ->
        assertEquals("6", set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(105.0, set.targetWeightPounds, "Weight should change")
        assertEquals(105.0.times(0.45359237), set.targetWeightKilograms, "Weight should change")
      }

      it[1].let { set ->
        assertEquals("6", set.targetRepetitions, "Repetitions should increase by 1")
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
        Set().apply {
          repetitions = 5
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
        Set().apply {
          repetitions = 5
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
      )

    // Create mutable list to update
    val setsToUpdate =
      mutableListOf(
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        },
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        },
      )

    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      it.first().let { set ->
        assertEquals("8", set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }

      it[1].let { set ->
        assertEquals("8", set.targetRepetitions, "Repetitions should increase by 1")
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
        Set().apply {
          repetitions = 9
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
        Set().apply {
          repetitions = 7
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "6"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
        Set().apply {
          repetitions = 6
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "6"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
      )

    // Create mutable list to update
    val setsToUpdate =
      MutableList(inputSets.size) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }

    val result = doubleProgression(inputSets, setsToUpdate)

    result.onSuccess {
      it.first().let { set ->
        assertEquals("9", set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }

      it[1].let { set ->
        assertEquals("6", set.targetRepetitions, "Repetitions should increase by 1")
        assertEquals(100.0, set.targetWeightPounds, "Weight should not change")
        assertEquals(45.36, set.targetWeightKilograms, "Weight should not change")
      }
      it[2].let { set ->
        assertEquals("6", set.targetRepetitions, "Repetitions should increase by 1")
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
        Set().apply {
          repetitions = _repetitions
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = _repetitions.toString()
          targetWeightPounds = _weightPounds
          targetWeightKilograms = _weightPounds.times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(setCount) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
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
        Set().apply {
          repetitions = 10
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "10"
          targetWeightPounds = _weightPounds
          targetWeightKilograms = _weightPounds.times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(setCount) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }
    val result = doubleProgression(sets, setsToUpdate)

    result.onSuccess {
      assertEquals(setCount, it.size)
      it.first().let { set ->
        assertEquals("6", set.targetRepetitions)
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
        Set().apply {
          repetitions = _repetitions
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = _weightPounds.plus(10)
          targetWeightKilograms = _weightPounds.plus(10).times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(setCount) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }
    val result = doubleProgression(sets, setsToUpdate)

    if (result.isFailure) {
      result.onFailure {
        assertFailsWith<IllegalArgumentException>(
          message = "Actual weight performed do not match target values",
          block = { it.message },
        )
      }
    } else if (result.isSuccess) {
      result.onSuccess {
        assertEquals(setCount, it.size)
        it.first().let { set ->
          assertEquals("8", set.targetRepetitions)
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
        Set().apply {
          repetitions = _repetitions
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "9"
          targetWeightPounds = _weightPounds
          targetWeightKilograms = _weightPounds.times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(setCount) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }
    val result = doubleProgression(sets, setsToUpdate)

    result.onSuccess {
      assertEquals(setCount, it.size)
      it.first().let { set ->
        assertEquals("9", set.targetRepetitions)
        assertEquals(_weightPounds, set.targetWeightPounds)
        assertEquals(_weightPounds.times(0.45359237), set.targetWeightKilograms)
      }
    }
  }

  @Example
  fun `doubleProgression long running`() {
    val inputSets =
      listOf(
        Set().apply {
          repetitions = 9
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
        Set().apply {
          repetitions = 7
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "6"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
        Set().apply {
          repetitions = 6
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "6"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
      )

    // Create mutable list to update
    val setsToUpdate =
      MutableList(inputSets.size) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }

    var newInputSets = inputSets.toMutableList()

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
        Set().apply {
          repetitions = _repetitions
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "9"
          targetWeightPounds = _weightPounds
          targetWeightKilograms = _weightPounds.times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(setToUpdateCount) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
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
        assertEquals("9", set.targetRepetitions)
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
        Set().apply {
          repetitions = 8
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.3
        }
      )
    val setsToUpdate =
      mutableListOf(
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      )

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals("9", updatedSets[0].targetRepetitions)
    assertEquals(100.0, updatedSets[0].targetWeightPounds)
    assertEquals(45.36, updatedSets[0].targetWeightKilograms)
  }

  @Example
  fun `resets repetitions and increases weight when at maximum`() {
    val sets =
      listOf(
        Set().apply {
          repetitions = 10
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "10"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.3
        },
        Set().apply {
          repetitions = 9
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "9"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.3
        },
        Set().apply {
          repetitions = 8
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.3
        },
      )
    val setsToUpdate =
      MutableList(sets.size) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals("6", updatedSets[0].targetRepetitions)
    assertEquals(105.0, updatedSets[0].targetWeightPounds)
    assertEquals(47.63, updatedSets[0].targetWeightKilograms)
  }

  @Example
  fun `resets to target values when below minimum`() {
    val sets =
      listOf(
        Set().apply {
          repetitions = 5
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
        Set().apply {
          repetitions = 5
          weightPounds = 100.0
          weightKilograms = 45.36
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 2.27
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = 100.0
          targetWeightKilograms = 45.36
        },
      )
    val setsToUpdate =
      MutableList(sets.size) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals("8", updatedSets[0].targetRepetitions)
    assertEquals(100.0, updatedSets[0].targetWeightPounds)
    assertEquals(45.36, updatedSets[0].targetWeightKilograms)
  }

  @Example
  fun `returns failure when target repetitions are not the same`() {
    val sets =
      listOf(
        Set().apply {
          repetitions = 10
          weightPounds = 100.0
          weightKilograms = 100.0.times(0.45359237)
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "10"
          targetWeightPounds = 100.0
          targetWeightKilograms = 100.0.times(0.45359237)
        },
        Set().apply {
          repetitions = 10
          weightPounds = 100.0
          weightKilograms = 100.0.times(0.45359237)
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 7
          targetMaximumRepetitions = 10
          targetRepetitions = "10"
          targetWeightPounds = 100.0
          targetWeightKilograms = 100.0.times(0.45359237)
        },
      )

    val setsToUpdate =
      MutableList(sets.size) {
        Set().apply {
          incrementWeightPounds = 5.0
          incrementWeightKilograms = 5.0.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
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
        Set().apply {
          repetitions = _repetitions
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = _repetitions.toString()
          targetWeightPounds = _weightPounds
          targetWeightKilograms = _weightPounds.times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(sets.size) {
        Set().apply {
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
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
        Set().apply {
          repetitions = 10
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "10"
          targetWeightPounds = _weightPounds
          targetWeightKilograms = _weightPounds.times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(sets.size) {
        Set().apply {
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }

    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    updatedSets.forEach { set ->
      assertEquals("6", set.targetRepetitions)
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
        Set().apply {
          repetitions = _repetitions
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = "8"
          targetWeightPounds = _weightPounds.plus(10)
          targetWeightKilograms = _weightPounds.plus(10).times(0.45359237)
        }
      }

    val setsToUpdate =
      MutableList(sets.size) {
        Set().apply {
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }
    val result = dynamicDoubleProgression(sets, setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)
    updatedSets.forEach { set ->
      assertEquals("8", set.targetRepetitions)
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
        Set().apply {
          repetitions = it
          weightPounds = _weightPounds
          weightKilograms = _weightPounds.times(0.45359237)
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
          targetRepetitions = if (it < 6) "6" else it.toString()
          targetWeightPounds = _weightPounds
          targetWeightKilograms = _weightPounds.times(0.45359237)
        }
      )
    }

    val setsToUpdate =
      MutableList(sets.size) {
        Set().apply {
          incrementWeightPounds = _incrementWeightPounds
          incrementWeightKilograms = _incrementWeightPounds.times(0.45359237)
          targetMinimumRepetitions = 6
          targetMaximumRepetitions = 10
        }
      }
    val result = dynamicDoubleProgression(sets.toList(), setsToUpdate)

    assertTrue(result.isSuccess)
    val updatedSets = result.getOrNull()!!
    assertEquals(setCount, updatedSets.size)


    if (repetitionsList.all { it >= 6 }) {
      println("sets repetitions ${sets.map { it.repetitions }}")
      println("sets target repetitions ${sets.map { it.targetRepetitions }}")
      println("updatedSets target repetitions ${updatedSets.map { it.targetRepetitions }}")

      updatedSets.forEachIndexed { index, set ->
        assertEquals(
          if (repetitionsList[index] < 6 || repetitionsList[index] >= 10) "6"
          else repetitionsList[index].plus(1).toString(),
          set.targetRepetitions,
        )
        assertEquals(
          if (repetitionsList[index] == 10) _weightPounds.plus(_incrementWeightPounds)
          else _weightPounds,
          set.targetWeightPounds,
        )
        assertEquals(
          if (repetitionsList[index] == 10) _weightPounds.plus(_incrementWeightPounds)
            .times(0.45359237)
          else _weightPounds.times(0.45359237),
          set.targetWeightKilograms,
        )
      }
    } else if (repetitionsList.any { it < 6 }) {
      updatedSets.forEachIndexed { index, set ->
        assertEquals(
          if (repetitionsList[index] < 6 || repetitionsList[index] >= 10) "6"
          else repetitionsList[index].toString(),
          set.targetRepetitions,
        )
        assertEquals(
          if (repetitionsList[index] == 10) _weightPounds.plus(_incrementWeightPounds)
          else _weightPounds,
          set.targetWeightPounds,
        )
        assertEquals(
          if (repetitionsList[index] == 10) _weightPounds.plus(_incrementWeightPounds)
            .times(0.45359237)
          else _weightPounds.times(0.45359237),
          set.targetWeightKilograms,
        )
      }
    }
  }
}
