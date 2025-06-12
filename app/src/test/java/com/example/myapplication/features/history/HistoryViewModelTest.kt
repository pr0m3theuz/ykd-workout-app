package com.example.myapplication.features.history

import android.app.Application
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.infrastructure.persistence.CompletedWorkoutRepository
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.features.workout.ManageExercisesUseCase
import com.example.myapplication.features.workout.ManageWorkoutUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class HistoryViewModelTest {

    // Coroutine Test Rule
    private val testDispatcher = StandardTestDispatcher()

    // Mocks
    private lateinit var application: Application
    private lateinit var manageWorkoutUseCase: ManageWorkoutUseCase
    private lateinit var manageExercisesUseCase: ManageExercisesUseCase
    private lateinit var completedWorkoutRepository: CompletedWorkoutRepository
    private lateinit var exerciseRepository: ExercisesRepository

    // ViewModel
    private lateinit var historyViewModel: HistoryViewModel

    private val workout1 = Workout(ObjectId(), "Workout Alpha")
    private val workout2 = Workout(ObjectId(), "Workout Beta")
    private val allWorkouts = listOf(workout1, workout2)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        application = mockk(relaxed = true)
        manageWorkoutUseCase = mockk(relaxed = true)
        manageExercisesUseCase = mockk(relaxed = true)
        completedWorkoutRepository = mockk() // Not relaxed, to specify behavior
        exerciseRepository = mockk(relaxed = true)

        // Default behavior for repository
        coEvery { completedWorkoutRepository.getCompletedWorkouts(any(), any(), any()) } returns allWorkouts

        historyViewModel = HistoryViewModel(
            application,
            manageWorkoutUseCase,
            manageExercisesUseCase,
            completedWorkoutRepository,
            exerciseRepository
        )
        // Advance past initial load
        testDispatcher.scheduler.advanceUntilIdle()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial load sets workouts from repository`() = runTest(testDispatcher) {
        val workouts = historyViewModel.workouts.first()
        assertEquals(allWorkouts.size, workouts.size)
        assertEquals(allWorkouts, workouts)
        coVerify(exactly = 1) { completedWorkoutRepository.getCompletedWorkouts(null, null, null) }
    }

    @Test
    fun `updateFilters with new query updates searchQuery and reloads workouts`() = runTest(testDispatcher) {
        val newQuery = "Alpha"
        val filteredWorkouts = listOf(workout1)
        coEvery { completedWorkoutRepository.getCompletedWorkouts(newQuery, null, null) } returns filteredWorkouts

        historyViewModel.updateFilters(newQuery = newQuery)
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines launched by updateFilters complete

        assertEquals(newQuery, historyViewModel.searchQuery.first())
        assertNull(historyViewModel.startDate.first())
        assertNull(historyViewModel.endDate.first())
        assertEquals(filteredWorkouts, historyViewModel.workouts.first())

        coVerify(exactly = 1) { completedWorkoutRepository.getCompletedWorkouts(newQuery, null, null) }
    }

    @Test
    fun `updateFilters with new dates updates date StateFlows and reloads workouts`() = runTest(testDispatcher) {
        val startDate = 1000L
        val endDate = 2000L
        val filteredWorkouts = listOf(workout2)
        coEvery { completedWorkoutRepository.getCompletedWorkouts(null, startDate, endDate) } returns filteredWorkouts

        historyViewModel.updateFilters(newStartDate = startDate, newEndDate = endDate)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(historyViewModel.searchQuery.first())
        assertEquals(startDate, historyViewModel.startDate.first())
        assertEquals(endDate, historyViewModel.endDate.first())
        assertEquals(filteredWorkouts, historyViewModel.workouts.first())

        coVerify(exactly = 1) { completedWorkoutRepository.getCompletedWorkouts(null, startDate, endDate) }
    }

    @Test
    fun `updateFilters with query and dates reloads workouts with all filters`() = runTest(testDispatcher) {
        val query = "Beta"
        val startDate = 500L
        val endDate = 1500L
        val specificWorkouts = listOf(workout2)
        coEvery { completedWorkoutRepository.getCompletedWorkouts(query, startDate, endDate) } returns specificWorkouts

        historyViewModel.updateFilters(newQuery = query, newStartDate = startDate, newEndDate = endDate)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(query, historyViewModel.searchQuery.first())
        assertEquals(startDate, historyViewModel.startDate.first())
        assertEquals(endDate, historyViewModel.endDate.first())
        assertEquals(specificWorkouts, historyViewModel.workouts.first())

        coVerify(exactly = 1) { completedWorkoutRepository.getCompletedWorkouts(query, startDate, endDate) }
    }


    @Test
    fun `deleteWorkout calls repository delete and then reloads workouts with current filters`() = runTest(testDispatcher) {
        val query = "Alpha"
        val initialFiltered = listOf(workout1)
        coEvery { completedWorkoutRepository.getCompletedWorkouts(query, null, null) } returns initialFiltered

        // First, apply a filter
        historyViewModel.updateFilters(newQuery = query)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(initialFiltered, historyViewModel.workouts.first())
        coVerify(exactly = 1) { completedWorkoutRepository.getCompletedWorkouts(query, null, null) } // initial load + this filter

        // Mock delete operation
        coEvery { completedWorkoutRepository.deleteCompletedWorkout(workout1) } returns true
        // After deletion, expect loadWorkouts to be called again with the same "Alpha" query
        // Let's say deleting workout1 results in an empty list for that query
        val afterDeleteWorkouts = emptyList<Workout>()
        coEvery { completedWorkoutRepository.getCompletedWorkouts(query, null, null) } returns afterDeleteWorkouts

        historyViewModel.deleteWorkout(workout1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) { completedWorkoutRepository.deleteCompletedWorkout(workout1) }
        // Check that loadWorkouts was called again with the same query
        coVerify(exactly = 2) { completedWorkoutRepository.getCompletedWorkouts(query, null, null) } // Once for filter, once after delete
        assertEquals(afterDeleteWorkouts, historyViewModel.workouts.first())
    }

    @Test
    fun `updateFilters with null query clears previous query`() = runTest(testDispatcher) {
        // Initial filter
        historyViewModel.updateFilters(newQuery = "Alpha")
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Alpha", historyViewModel.searchQuery.first())
        coVerify(exactly = 1) { completedWorkoutRepository.getCompletedWorkouts("Alpha", null, null) }


        // Clear query by calling updateFilters with null
        coEvery { completedWorkoutRepository.getCompletedWorkouts(null, null, null) } returns allWorkouts // Default mock
        historyViewModel.updateFilters(newQuery = null)
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(historyViewModel.searchQuery.first())
        assertEquals(allWorkouts, historyViewModel.workouts.first())
        coVerify(exactly = 1) { completedWorkoutRepository.getCompletedWorkouts(null, null, null) } // This is the second call overall to this version
    }
}
