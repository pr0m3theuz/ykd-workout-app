package com.example.myapplication.common.util

import kotlin.math.roundToInt

object OneRepMaxEstimateFormulas {

  /**
   *  Calculates the estimated 1 Rep Max (1RM) using different formulas.
   *
   *  @param weight The weight lifted (in any unit, e.g., kg or lbs). Must be greater than 0.
   *  @param reps The number of repetitions performed. Must be greater than 0 and generally less than 12 for best accuracy.
   *  @param formula The formula to use for the estimation. Defaults to Epley.
   *  @return The estimated 1RM, rounded to the nearest whole number, or null if input is invalid.
   *  @throws IllegalArgumentException if weight or reps are less than or equal to zero
   */
  fun calculateOneRepMax(weight: Double, reps: Int, formula: Formula = Formula.Epley): Double? {
    if (weight <= 0.0) {
      throw IllegalArgumentException("Weight must be greater than 0.")
    }
    if (reps <= 0) {
      throw IllegalArgumentException("Reps must be greater than 0.")
    }
    if (reps > 30) {
      return null //Return null for reps outside a reasonable estimation range
    }

    return when (formula) {
      Formula.Epley -> epleyFormula(weight, reps)
      Formula.Brzycki -> brzyckiFormula(weight, reps)
      Formula.Lander -> landerFormula(weight, reps)
      Formula.Lombardi -> lombardiFormula(weight, reps)
      Formula.MayhewEtAl -> mayhewEtAlFormula(weight, reps)
      Formula.OConnerEtAl -> oConnerEtAlFormula(weight, reps)
      Formula.Wathan -> wathanFormula(weight, reps)
      Formula.Average -> averageFormula(weight, reps) //Average of all formulas
    }
  }


  private fun epleyFormula(weight: Double, reps: Int): Double {
    return (weight * (1 + (reps / 30.0))).roundToInt().toDouble()
  }

  private fun brzyckiFormula(weight: Double, reps: Int): Double {
    return (weight * (36.0 / (37.0 - reps))).roundToInt().toDouble()
  }

  private fun landerFormula(weight: Double, reps: Int): Double {
    return ((100 * weight) / (101.3 - 2.67123 * reps)).roundToInt().toDouble()
  }

  private fun lombardiFormula(weight: Double, reps: Int): Double {
    return (weight * Math.pow(reps.toDouble(), 0.10)).roundToInt().toDouble()
  }

  private fun mayhewEtAlFormula(weight: Double, reps: Int): Double {
    return ((100 * weight) / (52.2 + (41.9 * Math.exp(-0.055 * reps)))).roundToInt().toDouble()
  }

  private fun oConnerEtAlFormula(weight: Double, reps: Int): Double {
    return (weight * (1 + 0.025 * reps)).roundToInt().toDouble()
  }

  private fun wathanFormula(weight: Double, reps: Int): Double {
    return ((100 * weight) / (48.8 + (53.8 * Math.exp(-0.075 * reps)))).roundToInt().toDouble()
  }

  private fun averageFormula(weight: Double, reps: Int): Double? {
    val formulas = listOf(
      Formula.Epley,
      Formula.Brzycki,
      Formula.Lander,
      Formula.Lombardi,
      Formula.MayhewEtAl,
      Formula.OConnerEtAl,
      Formula.Wathan
    )

    val results = formulas.mapNotNull { calculateOneRepMax(weight, reps, it) }

    if (results.isEmpty()) {
      return null
    }
    return (results.sum() / results.size).roundToInt().toDouble()

  }

  /**
   *  Enum representing the different 1RM estimation formulas.
   */
  enum class Formula {
    Epley,
    Brzycki,
    Lander, //McGlothin
    Lombardi,
    MayhewEtAl,
    OConnerEtAl,
    Wathan,
    Average // Average of all the other formulas
  }
}


//Example Usage in an Activity or ViewModel
class WorkoutActivity { //: AppCompatActivity() { Replace AppCompatActivity with your activity base class

  fun onCalculateClicked(
    weightInput: String,
    repsInput: String,
    selectedFormula: OneRepMaxEstimateFormulas.Formula
  ) {
    try {
      val weight = weightInput.toDouble()
      val reps = repsInput.toInt()

      val estimated1RM = OneRepMaxEstimateFormulas.calculateOneRepMax(weight, reps, selectedFormula)

      if (estimated1RM != null) {
        // Display the result, for example:
        showResult(estimated1RM) // Replace with your UI update logic (e.g., TextView.setText())
      } else {
        // Handle the case where estimation is not possible (e.g., too many reps)
        showError("Could not estimate 1RM. Please use fewer than 30 repetitions.")
      }

    } catch (e: NumberFormatException) {
      // Handle invalid input (e.g., non-numeric input)
      showError("Invalid weight or reps. Please enter numeric values.")

    } catch (e: IllegalArgumentException) {
      // Handle cases where weight or reps are <= 0
      showError(e.message ?: "Invalid input.") // Use the exception message, if available
    }
  }

  // Placeholder methods - Replace these with your actual UI interaction methods
  private fun showResult(oneRepMax: Double) {
    println("Estimated 1RM: $oneRepMax") // Example: Print to console
    //textViewResult.text = "Estimated 1RM: $oneRepMax"  // Example: Update a TextView
  }

  private fun showError(message: String) {
    println("Error: $message")  // Example: Print to console
    //Toast.makeText(this, message, Toast.LENGTH_SHORT).show() //Example: display an error message
  }
}
