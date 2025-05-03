package com.example.myapplication.features.workout

import com.example.myapplication.common.util.OneRepMaxEstimateFormulas
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.DoubleRange
import net.jqwik.api.constraints.IntRange
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue

/**
 * Property-based tests for the OneRepMaxEstimator using jqwik.
 */
class OneRepMaxEstimateFormulaTest {

  // Instance of the class under test (using companion object methods here)
  private val estimator = OneRepMaxEstimateFormulas

  /**
   * Property: Estimating 1RM with 1 rep should return the original weight.
   */
  @Property
  fun `estimate with 1 rep returns the weight`(@ForAll @DoubleRange(min = 0.1) weight: Double) {
    // When estimating with exactly 1 rep
    val estimated1RM = estimator.calculateOneRepMax(weight, 1)
    // Then the estimated 1RM should be equal to the weight lifted
    if (estimated1RM != null) {
      assertEquals(weight, estimated1RM, 0.001, "For 1 rep, 1RM should be the weight itself")
    }
  }

  /**
   * Property: Estimating 1RM with more than 1 rep should result in a value
   * greater than or equal to the original weight. (Using Epley, it should be strictly greater).
   */
  @Property
  fun `estimate with more than 1 rep is greater than or equal to weight`(
    @ForAll @DoubleRange(min = 0.1, max = 1000.0) weight: Double,
    @ForAll @IntRange(
      min = 2,
      max = 30
    ) reps: Int // Epley is less accurate > 10-12 reps, but test up to 30
  ) {
    // When estimating with more than 1 rep
    val estimated1RM = estimator.calculateOneRepMax(weight, reps)
    // Then the estimated 1RM should be greater than or equal to the weight
    // Using Epley, it will actually be strictly greater for reps > 1
    if (estimated1RM != null) {
      assertTrue(
        estimated1RM > weight,
        "For reps > 1, 1RM estimate ($estimated1RM) should be greater than weight ($weight)"
      )
    }
  }

  /**
   * Property: For a fixed number of reps (>1), the estimated 1RM should increase as weight increases.
   */
  @Property
  fun `estimate increases with weight for fixed reps`(
    @ForAll @DoubleRange(min = 0.1, max = 500.0) weight1: Double,
    @ForAll @DoubleRange(min = 500.1, max = 1000.0) weight2: Double, // Ensure weight2 > weight1
    @ForAll @IntRange(min = 2, max = 30) reps: Int
  ) {
    // Assume weight1 < weight2
    assertTrue(weight1 < weight2)

    // When estimating 1RM for both weights with the same reps
    val estimate1 = estimator.calculateOneRepMax(weight1, reps)
    val estimate2 = estimator.calculateOneRepMax(weight2, reps)

    // Then the estimate for the heavier weight should be greater
    if (estimate2 != null && estimate1 != null) {
      assertTrue(
        estimate2 > estimate1,
        "Estimate should increase with weight (Estimate1: $estimate1, Estimate2: $estimate2)"
      )
    }
  }

  /**
   * Property: For a fixed weight (>0), the estimated 1RM should increase as reps increase (for reps > 1).
   */
  @Property
  fun `estimate increases with reps for fixed weight`(
    @ForAll @DoubleRange(min = 0.1, max = 1000.0) weight: Double,
    @ForAll @IntRange(min = 2, max = 15) reps1: Int,
    @ForAll @IntRange(min = 16, max = 30) reps2: Int // Ensure reps2 > reps1
  ) {
    // Assume reps1 < reps2
    assertTrue(reps1 < reps2)

    // When estimating 1RM for both rep counts with the same weight
    val estimate1 = estimator.calculateOneRepMax(weight, reps1)
    val estimate2 = estimator.calculateOneRepMax(weight, reps2)

    // Then the estimate for the higher reps should be greater
    if (estimate2 != null && estimate1 != null) {
      assertTrue(
        estimate2 > estimate1,
        "Estimate should increase with reps (Estimate1: $estimate1, Estimate2: $estimate2)"
      )
    }
  }

  /**
   * Property: Estimating with non-positive weight should throw IllegalArgumentException.
   */
  @Property
  fun `estimate throws exception for non-positive weight`(
    @ForAll @DoubleRange(min = -1000.0, max = 0.0) weight: Double,
    @ForAll @IntRange(min = 1) reps: Int
  ) {
    // Expect an IllegalArgumentException when weight is zero or negative
    assertThrows(IllegalArgumentException::class.java) {
      estimator.calculateOneRepMax(weight, reps)
    }
  }

  /**
   * Property: Estimating with non-positive reps should throw IllegalArgumentException.
   */
  @Property
  fun `estimate throws exception for non-positive reps`(
    @ForAll @DoubleRange(min = 0.1) weight: Double,
    @ForAll @IntRange(min = -10, max = 0) reps: Int
  ) {
    // Expect an IllegalArgumentException when reps is zero or negative
    assertThrows(IllegalArgumentException::class.java) {
      estimator.calculateOneRepMax(weight, reps)
    }
  }
}
