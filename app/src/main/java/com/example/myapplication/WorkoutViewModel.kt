package com.example.myapplication

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class WorkoutViewModel : ViewModel() {
    fun currentTimeMillis(): Long = System.currentTimeMillis()
//    fun elapsedTimeMillis(): Long = System.

    var workoutStartTime: Long = 0

    init {
        workoutStartTime = Clock.System.now().epochSeconds
    }

    val workoutDuration: Long = workoutStartTime
    private val _workoutDuration = MutableStateFlow(workoutDuration)
    val durationWorkout: StateFlow<Long> = _workoutDuration.asStateFlow()
}
