package com.example.myapplication.features.workout

import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.estimateOneRepMaxModernized
import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.Provide
import net.jqwik.api.constraints.DoubleRange
import net.jqwik.api.constraints.IntRange
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue


// Enable edge case generation by jqwik
class ModernizedLoadOneRepMaxEstimateTest {

  // Define a tolerance for comparing Double values due to potential floating-point inaccuracies
  private val doubleTolerance = 0.0001

  // Property tests for estimateOneRepMax

  @Property(tries = 100) // Run 100 tries with different generated inputs
  fun `estimateOneRepMax should return a non-null value for valid inputs`(
    @ForAll @DoubleRange(min = 0.1, max = 1000.0) weight: Double, // Positive weight
    @ForAll @IntRange(min = 1, max = 30) reps: Int, // Valid reps from charts
    @ForAll @IntRange(min = 0, max = 10) rir: Int, // Valid RIR
    @ForAll repCapacity: RepCapacity // All rep capacity enums
  ) {
    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, repCapacity)
    // For valid inputs within the chart ranges, the result should not be null
    assertNotNull(estimated1RM)
    // The estimated 1RM should be positive if the weight is positive
    assertTrue(estimated1RM!! > 0)
  }

  @Property(tries = 20) // Test with zero weight
  fun `estimateOneRepMax should return null for zero weight`(
    @ForAll @DoubleRange(min = -100.0, max = 0.0) weight: Double, // Zero or negative weight
    @ForAll @IntRange(min = 1, max = 30) reps: Int,
    @ForAll @IntRange(min = 0, max = 10) rir: Int,
    @ForAll repCapacity: RepCapacity
  ) {
    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, repCapacity)
    assertNull(estimated1RM)
  }

  @Property(tries = 20) // Test with negative reps
  fun `estimateOneRepMax should return null for negative reps`(
    @ForAll @DoubleRange(min = 0.1, max = 1000.0) weight: Double,
    @ForAll @IntRange(max = -1) reps: Int, // Negative reps
    @ForAll @IntRange(min = 0, max = 10) rir: Int,
    @ForAll repCapacity: RepCapacity
  ) {
    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, repCapacity)
    assertNull(estimated1RM)
  }

  @Property(tries = 20) // Test with negative RIR
  fun `estimateOneRepMax should return a non-null value for negative RIR (handled by min function)`(
    @ForAll @DoubleRange(min = 0.1, max = 1000.0) weight: Double,
    @ForAll @IntRange(min = 1, max = 30) reps: Int,
    @ForAll @IntRange(max = -1) rir: Int, // Negative RIR
    @ForAll repCapacity: RepCapacity
  ) {
    // The rpeToRir function (used internally by getPercentageFromChart implicitly via RIR lookup)
    // should handle negative RIR by clamping it to 0.
    // So, the result should not be null for valid weight and reps.
    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, repCapacity)
    assertNotNull(estimated1RM)
    assertTrue(estimated1RM!! > 0)
  }

  @Property(tries = 20) // Test with RIR outside the typical 0-10 range (handled by max/min)
  fun `estimateOneRepMax should return a non-null value for RIR outside 0-10 range`(
    @ForAll @DoubleRange(min = 0.1, max = 1000.0) weight: Double,
    @ForAll @IntRange(min = 1, max = 30) reps: Int,
    @ForAll @IntRange(min = 11, max = 20) rir: Int, // RIR > 10
    @ForAll repCapacity: RepCapacity
  ) {
    // The rpeToRir function (used internally by getPercentageFromChart implicitly via RIR lookup)
    // should handle RIR > 10 by clamping it to 10.
    // So, the result should not be null for valid weight and reps.
    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, repCapacity)
    assertNotNull(estimated1RM)
    assertTrue(estimated1RM!! > 0)
  }


  // Example tests for specific chart data points to verify accuracy

  @Example
  fun `estimateOneRepMax for Average capacity, 100kg, 6 reps, 2 RIR should be approximately 113_19kg`() {
    val weight = 100.0
    val reps = 6
    val rir = 2
    val capacity = RepCapacity.AVERAGE
    val expected1RM = 100.0 / 0.8305 // Based on chartData[AVERAGE][2][6]

    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, capacity)

    assertNotNull(estimated1RM)
    assertEquals(expected1RM, estimated1RM!!, doubleTolerance)
  }

  @Example
  fun `estimateOneRepMax for High capacity, 120kg, 8 reps, 3 RIR should be approximately 140_18kg`() {
    val weight = 120.0
    val reps = 8
    val rir = 3
    val capacity = RepCapacity.HIGH
    val expected1RM = 120.0 / 0.8585 // Based on chartData[HIGH][3][8]

    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, capacity)

    assertNotNull(estimated1RM)
    assertEquals(expected1RM, estimated1RM!!, doubleTolerance)
  }

  @Example
  fun `estimateOneRepMax for Low capacity, 80kg, 15 reps, 6 RIR should be approximately 130_17kg`() {
    val weight = 80.0
    val reps = 15
    val rir = 6
    val capacity = RepCapacity.LOW
    val expected1RM = 80.0 / 0.6132 // Based on chartData[LOW][6][15]

    val estimated1RM = estimateOneRepMaxModernized(weight, reps, rir, capacity)

    assertNotNull(estimated1RM)
    assertEquals(expected1RM, estimated1RM!!, doubleTolerance)
  }

  // --- Arbitrary Providers for Property Tests ---

  @Provide
  fun repCapacity(): Arbitrary<RepCapacity> {
    return Arbitraries.of(RepCapacity::class.java)
  }
}
