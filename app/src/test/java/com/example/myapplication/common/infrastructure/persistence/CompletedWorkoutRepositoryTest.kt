package com.example.myapplication.common.infrastructure.persistence

import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Workout
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.bson.types.ObjectId
import org.junit.Assert.assertEquals
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

// A mock implementation for testing the CompletedWorkoutRepository interface contract
class MockCompletedWorkoutRepository : CompletedWorkoutRepository {
    private val workouts = mutableListOf<Workout>()

    fun addWorkout(workout: Workout) {
        workouts.add(workout)
    }

    fun clearWorkouts() {
        workouts.clear()
    }

    override fun getPreviousSetForExercise(exerciseID: ObjectId, setNumber: Int): Set? = null
    override fun getCompletedWorkoutsCount(): Int = workouts.size
    override fun getCompletedSetsForExercise(exerciseID: ObjectId): Flow<List<Set>> = flowOf(emptyList())

    // This is the old function, should call the new one
    override fun getCompletedWorkouts(): List<Workout>? {
        return getCompletedWorkouts(null, null, null)
    }

    // This is the new function with filtering
    override fun getCompletedWorkouts(query: String?, startDate: Long?, endDate: Long?): List<Workout>? {
        var filteredWorkouts = workouts.toList()

        if (query != null) {
            filteredWorkouts = filteredWorkouts.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        if (startDate != null) {
            filteredWorkouts = filteredWorkouts.filter {
                (it.workoutEnded?.epochSeconds ?: 0) >= startDate
            }
        }
        if (endDate != null) {
            filteredWorkouts = filteredWorkouts.filter {
                (it.workoutEnded?.epochSeconds ?: 0) <= endDate
            }
        }
        return filteredWorkouts.ifEmpty { null } // Original interface returns null for empty
    }

    override fun getCompletedWorkoutByID(workoutID: ObjectId): Workout? =
        workouts.find { it.id == workoutID }

    override fun getLatestPreviousIdenticalWorkout(currentWorkout: Workout): Workout? = null
    override suspend fun upsertCompletedWorkout(workout: Workout) {
        workouts.removeAll { it.id == workout.id }
        workouts.add(workout)
    }
    override suspend fun deleteCompletedWorkout(workout: Workout): Boolean =
        workouts.removeIf { it.id == workout.id }
}

class CompletedWorkoutRepositoryTest {

    private lateinit var repository: MockCompletedWorkoutRepository
    private lateinit var now: kotlinx.datetime.Instant

    private lateinit var workout1: Workout // 10 days ago
    private lateinit var workout2: Workout // 5 days ago
    private lateinit var workout3: Workout // 1 day ago
    private lateinit var allWorkouts: List<Workout>

    @Before
    fun setUp() {
        repository = MockCompletedWorkoutRepository()
        now = Clock.System.now()

        workout1 = Workout(ObjectId(), "Morning Workout", workoutEnded = now.minus(10, DateTimeUnit.DAY, TimeZone.UTC))
        workout2 = Workout(ObjectId(), "Evening Run", workoutEnded = now.minus(5, DateTimeUnit.DAY, TimeZone.UTC))
        workout3 = Workout(ObjectId(), "Morning Yoga", workoutEnded = now.minus(1, DateTimeUnit.DAY, TimeZone.UTC))
        allWorkouts = listOf(workout1, workout2, workout3)

        allWorkouts.forEach { repository.addWorkout(it) }
    }

    @Test
    fun `getCompletedWorkouts with null filters returns all workouts`() {
        val result = repository.getCompletedWorkouts(null, null, null)
        assertEquals(allWorkouts.size, result?.size)
        assertTrue(result?.containsAll(allWorkouts) == true)
    }

    @Test
    fun `getCompletedWorkouts old function calls new one and returns all workouts`() {
        val result = repository.getCompletedWorkouts() // Calls the old function
        assertEquals(allWorkouts.size, result?.size)
        assertTrue(result?.containsAll(allWorkouts) == true)
    }

    @Test
    fun `getCompletedWorkouts with query matching some names returns filtered workouts`() {
        val result = repository.getCompletedWorkouts("Morning", null, null)
        assertEquals(2, result?.size)
        assertTrue(result?.contains(workout1) == true)
        assertTrue(result?.contains(workout3) == true)
    }

    @Test
    fun `getCompletedWorkouts with query case insensitive matching`() {
        val result = repository.getCompletedWorkouts("morning", null, null)
        assertEquals(2, result?.size)
        assertTrue(result?.contains(workout1) == true)
        assertTrue(result?.contains(workout3) == true)
    }

    @Test
    fun `getCompletedWorkouts with query partial matching`() {
        val result = repository.getCompletedWorkouts("Work", null, null)
        assertEquals(1, result?.size)
        assertTrue(result?.contains(workout1) == true)
    }

    @Test
    fun `getCompletedWorkouts with query not matching any names returns null`() {
        val result = repository.getCompletedWorkouts("Pilates", null, null)
        assertEquals(null, result)
    }

    @Test
    fun `getCompletedWorkouts with date range including some workouts`() {
        // Workouts are 10, 5, 1 days ago.
        // Range: 7 days ago to 4 days ago (exclusive of 4, inclusive of 7 for start)
        val startDate = now.minus(7, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val endDate = now.minus(4, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        // Expected: workout2 (5 days ago)
        val result = repository.getCompletedWorkouts(null, startDate, endDate)
        assertEquals(1, result?.size)
        assertTrue(result?.contains(workout2) == true)
    }

    @Test
    fun `getCompletedWorkouts with date range not including any workouts returns null`() {
        val startDate = now.minus(15, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val endDate = now.minus(12, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val result = repository.getCompletedWorkouts(null, startDate, endDate)
        assertEquals(null, result)
    }

    @Test
    fun `getCompletedWorkouts with open start date`() {
        // up to 3 days ago (inclusive, so includes workout2 and workout3)
        val endDate = now.minus(3, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        // Expected: workout2 (5 days ago), workout3 (1 day ago)
        val result = repository.getCompletedWorkouts(null, null, endDate)
        assertEquals(2, result?.size)
        assertTrue(result?.contains(workout2) == true)
        assertTrue(result?.contains(workout3) == true)
    }

    @Test
    fun `getCompletedWorkouts with open end date`() {
        // from 7 days ago (inclusive, so includes workout1 and workout2)
        val startDate = now.minus(7, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        // Expected: workout1 (10 days ago), workout2 (5 days ago)
        // Corrected expectation: only workout1 (10 days ago) is NOT <= 7 days ago.
        // The filter is >= startDate. So workout1 (10d), workout2 (5d) should be included.
        // No, the filter is workout.workoutEnded >= startDate.
        // startDate is 7 days ago. workout1 (10 days ago) is NOT >= 7 days ago from now.
        // workout2 (5 days ago) IS >= 7 days ago from now.
        // workout3 (1 day ago) IS >= 7 days ago from now.
        // This was confusing. Let's fix the comment and expectation.
        // startDate = 7 days ago. We want workouts that are more recent than or equal to 7 days ago.
        // This means workout1 (10 days ago) should be filtered out.
        // workout2 (5 days ago) and workout3 (1 day ago) should be included.
        val result = repository.getCompletedWorkouts(null, startDate, null)
        assertEquals(2, result?.size)
        assertTrue(result?.contains(workout2) == true)
        assertTrue(result?.contains(workout3) == true)
    }

    @Test
    fun `getCompletedWorkouts with query and date range`() {
        // Range: last 7 days
        val startDate = now.minus(7, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val endDate = now.epochSeconds // today
        // Expected: workout3 ("Morning Yoga", 1 day ago)
        val result = repository.getCompletedWorkouts("Yoga", startDate, endDate)
        assertEquals(1, result?.size)
        assertTrue(result?.contains(workout3) == true)
    }

    @Test
    fun `getCompletedWorkouts with query and date range not matching name`() {
        val startDate = now.minus(7, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val endDate = now.epochSeconds
        val result = repository.getCompletedWorkouts("NonExistent", startDate, endDate)
        assertEquals(null, result)
    }

    @Test
    fun `getCompletedWorkouts with query and date range not matching date`() {
        // Range: 8 to 6 days ago. workout1 ("Morning Workout") is 10 days ago.
        val startDate = now.minus(8, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val endDate = now.minus(6, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val result = repository.getCompletedWorkouts("Morning Workout", startDate, endDate)
        assertEquals(null, result) // workout1 is 10 days ago, so not in range
    }

    @Test
    fun `getCompletedWorkouts with query 'Run' and date range last 7 days`() {
        val startDate = now.minus(7, DateTimeUnit.DAY, TimeZone.UTC).epochSeconds
        val endDate = now.epochSeconds
        // workout2 "Evening Run" is 5 days old, so it's in range
        val result = repository.getCompletedWorkouts("Run", startDate, endDate)
        assertEquals(1, result?.size)
        assertTrue(result?.contains(workout2) == true)
    }
}
