package com.example.myapplication.components.plateCalculator

import net.jqwik.api.Example

class plateCalulatorTest() {
  // Example usage
  @Example
  fun example() {
    val desiredWeight = 135

    val (plates, totalPlates) = calculatePlates(desiredWeight, 45)

    println("To reach $desiredWeight lbs, add:")

    for ((weight, quantity) in plates) {
      if (quantity > 0) {
        println("$quantity x $weight lb plates")
      }
    }

    println("Total plates: $totalPlates")
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

  val smallestPlate = plateWeights.keys.sortedByDescending { it }.last().times(2)

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