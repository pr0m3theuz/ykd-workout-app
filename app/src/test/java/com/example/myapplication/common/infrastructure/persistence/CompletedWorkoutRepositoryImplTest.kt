@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.common.infrastructure.persistence

import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.serialization.json.Json
import net.jqwik.api.Example
import org.junit.Test
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.time.Duration.Companion.days
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class CompletedWorkoutRepositoryImplTest {

  @Example
  fun calculateWorkoutWeekStreak() {
    val timezone = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()

    val startDate =
      LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .atStartOfDay()
        .toKotlinLocalDateTime()
        .toInstant(timezone)

    val endDate =
      LocalDate.now()
        .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
        .atStartOfDay()
        .minusNanos(1)
        .toKotlinLocalDateTime()
        .toInstant(timezone)

    val filename =
      "/mnt/lil_nas/icefoxneoalligator/Documents/AndroidStudioProjects/MyApplication4/app/src/test/java/com/example/myapplication/resources/features/mockDatesForWorkoutWeekStreakTest.json"

    val workoutEndedDateTimes =
      Json.decodeFromString<List<Map<String, String>>>(File(filename).bufferedReader().readText())
        .map {
          it.map {
            Instant.parse(it.value)
          }
        }.flatten().sortedDescending()


    val workoutWeekStreakMap: MutableMap<ClosedRange<Instant>, Int> = mutableMapOf()

    var dateRange = startDate.minus(7.days)..endDate.minus(7.days)
    var workoutCountForWeek = -1
    while (true) {
      workoutCountForWeek = workoutEndedDateTimes.count { it in dateRange }

      if (workoutCountForWeek == 0) break
      workoutWeekStreakMap.put(dateRange, workoutCountForWeek)

      dateRange = dateRange.start.minus(7.days)..dateRange.endInclusive.minus(7.days)
    }

    val workoutWeekStreakCount = workoutWeekStreakMap.count()

    var workoutWeekStreak = 0
    workoutWeekStreakMap.forEach { if (it.value > 0) workoutWeekStreak++ else return@forEach }

    println("\nWorkout Week Streak Count: $workoutWeekStreakCount\n")
    println("\nWorkout Week Streak: $workoutWeekStreak\n")
  }

  @Test
  fun `getWorkoutStreakWeek with no completed workouts`() {
    // Verify that the function returns 0 when there are no completed workouts in the database.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with workouts in the current week only`() {
    // Verify that the function returns 1 when there are completed workouts only in the current
    // week.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with workouts in consecutive previous weeks`() {
    // Verify that the function correctly calculates the streak when workouts exist in the current
    // week and several consecutive preceding weeks.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with a gap in weekly workouts`() {
    // Verify that the streak resets and returns the count of consecutive weeks from the most recent
    // workout week if there's a week with no workouts in between.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with workouts only in past weeks  not current`() {
    // Verify that the function returns 0 if there are workouts in previous weeks but none in the
    // current week.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek spanning year change`() {
    // Verify correct streak calculation when the workout weeks span across the end of one year and
    // the beginning of the next.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with workouts at the exact start end of a week`() {
    // Test with workouts logged precisely at the beginning of Monday (00:00:00) or end of Sunday
    // (23:59:59.999999999) to ensure boundary conditions for week calculation are handled
    // correctly.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with multiple workouts within the same week`() {
    // Verify that multiple workouts within the same week still count as one week towards the
    // streak.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek when database is null`() {
    // Verify that the function returns null and logs an error if the database instance `db` is
    // null.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek when  CompletedWorkouts  collection is null`() {
    // Verify that the function returns null and logs an error if the 'CompletedWorkouts' collection
    // cannot be retrieved.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek when database query fails`() {
    // Verify that the function returns null and logs an error if the database query to fetch
    // `workoutEnded` dates fails for any reason.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with workoutEnded dates being null in database`() {
    // Verify that workout entries with null `workoutEnded` dates are correctly filtered out and do
    // not affect the streak calculation.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with different timezones`() {
    // Test how the function behaves when the default timezone of the system running the code is
    // different from the timezone in which workout data might have been stored or is expected,
    // especially around week boundaries.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek with a very long streak`() {
    // Test with data representing a streak spanning many months or years to check for performance
    // or calculation issues with large datasets.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek when input date is far in the past or future`() {
    // Although the input `date: Instant` is not directly used in the current logic for `startDate`
    // and `endDate` calculation, if its usage changes, this would be relevant.
    // Currently, it verifies behavior with `LocalDate.now()`.
    // TODO implement test
  }

  @Test
  fun `getWorkoutStreakWeek when  workoutEnded  is malformed or not a date`() {
    // Verify robustness if `result.getDate("workoutEnded")` returns data that cannot be converted
    // to an Instant (e.g., wrong type or corrupted data). The current code handles nulls, but not
    // malformed data causing exceptions during `toInstant()`.
    // TODO implement test
  }
}
