package com.example.myapplication.components.plateCalculator

import net.jqwik.api.Assume
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.IntRange
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


// --- jqwik Tests ---
class PlateCalculatorTest { // Renamed class to follow Kotlin conventions

  private val BAR_WEIGHT = 45 // Standard bar weight for tests

  @Property
  fun `calculated total weight should match desired weight`(
    @ForAll @IntRange(min = 0, max = 1000) weightToAddIncrement: Int // Generate increments to add
  ) {
    // Ensure the increment is divisible by the smallest plate pair (2 * 2.5 = 5)
    Assume.that(weightToAddIncrement % 5 == 0)

    val desiredWeight = BAR_WEIGHT + weightToAddIncrement
    Assume.that(desiredWeight >= BAR_WEIGHT) // Ensure desired weight is valid

    val (platesPerSide, _) = calculatePlates(desiredWeight, BAR_WEIGHT)

    // Calculate the total weight based on the returned plates map
    val calculatedAddedWeight = platesPerSide.entries.sumOf { (plateWeight, quantity) ->
      plateWeight * quantity * 2 // Multiply quantity by 2 for both sides
    }
    val calculatedTotalWeight = BAR_WEIGHT + calculatedAddedWeight

    assertThat(calculatedTotalWeight).isEqualTo(desiredWeight.toDouble())
  }

  @Property
  fun `should throw exception if desired weight is less than bar weight`() {
    val desiredWeight: Int = BAR_WEIGHT - 1

    // Exclude the exact bar weight case if it's handled separately or allowed
    Assume.that(desiredWeight < BAR_WEIGHT)

    Assertions.assertThrows(IllegalArgumentException::class.java) {
      calculatePlates(desiredWeight, BAR_WEIGHT)
    }
    // Or using AssertJ:
    // assertThatThrownBy { calculatePlates(desiredWeight, BAR_WEIGHT) }
    //     .isInstanceOf(IllegalArgumentException::class.java)
  }

  @Property
  fun `should allow desired weight equal to bar weight`(
    // No @ForAll needed, just a specific case
  ) {
    val desiredWeight = BAR_WEIGHT
    val (platesPerSide, totalPlates) = calculatePlates(desiredWeight, BAR_WEIGHT)

    assertThat(totalPlates).isEqualTo(0)
    assertThat(platesPerSide.values.all { it == 0 }).isTrue() // All plate counts should be zero
  }


  @Property
  fun `should throw exception if weight increment is not divisible by smallest plate pair`(
    @ForAll @IntRange(min = 1, max = 1000) weightIncrement: Int
  ) {
    // Smallest plate pair is 2.5 * 2 = 5
    Assume.that(weightIncrement % 5 != 0) // Ensure it's NOT divisible by 5

    val desiredWeight = BAR_WEIGHT + weightIncrement

    Assertions.assertThrows(IllegalArgumentException::class.java) {
      calculatePlates(desiredWeight, BAR_WEIGHT)
    }
    // Or using AssertJ:
    // assertThatThrownBy { calculatePlates(desiredWeight, BAR_WEIGHT) }
    //     .isInstanceOf(IllegalArgumentException::class.java)
    //     .hasMessageContaining("must be divisible by the smallest plate weight") // Check message if needed
  }

  @Property
  fun `plate counts per side should be non-negative`(
    @ForAll @IntRange(min = 0, max = 1000) weightToAddIncrement: Int
  ) {
    Assume.that(weightToAddIncrement % 5 == 0)
    val desiredWeight = BAR_WEIGHT + weightToAddIncrement

    val (platesPerSide, _) = calculatePlates(desiredWeight, BAR_WEIGHT)

    assertThat(platesPerSide.values).allMatch { it >= 0 }
  }

  @Property
  fun `total plates count should be sum of plates per side times two`(
    @ForAll @IntRange(min = 0, max = 1000) weightToAddIncrement: Int
  ) {
    Assume.that(weightToAddIncrement % 5 == 0)
    val desiredWeight = BAR_WEIGHT + weightToAddIncrement

    val (platesPerSide, totalPlates) = calculatePlates(desiredWeight, BAR_WEIGHT)

    val calculatedTotalFromMap = platesPerSide.values.sum() * 2

    assertThat(totalPlates).isEqualTo(calculatedTotalFromMap)
  }

  // Example test for a specific known case (like the original @Example)
  @Test // Using standard JUnit @Test for a specific example
  fun specificCase_135lbs() {
    val desiredWeight = 135
    val (platesPerSide, totalPlates) = calculatePlates(desiredWeight, BAR_WEIGHT)

    println("--- Test Case: $desiredWeight lbs ---")
    println("To reach $desiredWeight lbs with a $BAR_WEIGHT lb bar, add per side:")
    platesPerSide.forEach { (weight, quantity) ->
      if (quantity > 0) {
        println("$quantity x $weight lb plates")
      }
    }
    println("Total physical plates: $totalPlates")
    println("------------------------------------")


    assertThat(platesPerSide[45.0]).isEqualTo(1) // 1 x 45lb plate per side
    assertThat(platesPerSide[35.0]).isEqualTo(0)
    assertThat(platesPerSide[25.0]).isEqualTo(0)
    assertThat(platesPerSide[10.0]).isEqualTo(0)
    assertThat(platesPerSide[5.0]).isEqualTo(0)
    assertThat(platesPerSide[2.5]).isEqualTo(0)
    // Add more assertions based on expected counts per side if needed

    val calculatedWeight = BAR_WEIGHT + platesPerSide.entries.sumOf { (w, q) -> w * q * 2 }
    assertThat(calculatedWeight).isEqualTo(desiredWeight.toDouble())
    assertThat(totalPlates).isEqualTo(2) // 1 plate per side * 2 sides
  }
}


fun calculatePlates(desiredWeight: Int, barWeight: Int): Pair<Map<Double, Int>, Int> {

  /*  This function calculates the weight of plates to be added to a 45lb barbell to reach a desired weight.
  Args:
      desiredWeight: The total weight the user wants on the barbell (including the 45lb bar).

  Returns:
      A pair containing a map of plate weights (in lbs) to their quantities and the total number of plates.

  Throws:
      IllegalArgumentException: If the desired weight is less than or equal to the weight of the barbell (45lbs).
  */

  // Initialize a mutable map to store plate weights and quantities
  val plateWeights = mutableMapOf(45.0 to 0, 35.0 to 0, 25.0 to 0, 10.0 to 0, 5.0 to 0, 2.5 to 0)
  var totalPlates = 0

  val smallestPlate = plateWeights.keys.minByOrNull { it }!!.times(2)

  if (desiredWeight <= barWeight) {
    throw IllegalArgumentException(
      "Desired weight must be greater than 45lbs (weight of the barbell)"
    )
  } else if (desiredWeight.mod(smallestPlate.toInt()) != 0) {
    throw IllegalArgumentException(
      "Desired weight must be divisible by 5lbs (the smallest amount of weight that can be added to the barbell)"
    )
  }


  // Calculate the total weight to be added to the barbell
  var weightToAdd: Double = (desiredWeight - barWeight).toDouble()
  println("weightToAdd: $weightToAdd")


  // Iterate through plate weights in descending order (prioritize larger plates)
  for (plateWeight in plateWeights.keys.sortedByDescending { it }) {
    // Calculate the maximum number of plates of this weight that can be added
    val maxPlates = (weightToAdd / plateWeight).toInt()
    if (maxPlates < 2) {
      continue
    }

    // Add plates to the weight and update the total number of plates
    plateWeights[plateWeight] = maxPlates
    totalPlates += maxPlates
    weightToAdd -= maxPlates * plateWeight

    // If all weight has been added, break the loop
    if (weightToAdd == 0.0) {
      break
    }
  }

  return plateWeights to totalPlates
}