package com.example.myapplication

import android.os.CountDownTimer
import android.util.Log
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.myapplication.data.ExerciseModel
import com.example.myapplication.data.ExerciseSetModel
import com.example.myapplication.data.SetModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class WorkoutViewModel : ViewModel() {
//    fun currentTimeMillis(): Long = System.currentTimeMillis()
//    fun elapsedTimeMillis(): Long = System.

    var workoutStartTime: Long = 0

    init {
        workoutStartTime = Clock.System.now().epochSeconds
    }

    val workoutDuration: Long = workoutStartTime

    private val _workoutDuration = MutableStateFlow(workoutDuration)
    val durationWorkout: StateFlow<Long> = _workoutDuration.asStateFlow()

    private var timerDuration: Long = 0L

    //        TODO Store User Settings for Rest Timer Adjustments
    private val restTimerAdjustment: Long =
        30.toDuration(DurationUnit.MILLISECONDS).inWholeMilliseconds

    private var _remainingTime: Long = 0L
    val remainingTime: Long
        get() = _remainingTime

    private var _remainingTimeString = _remainingTime.toDuration(DurationUnit.SECONDS)
        .toComponents { minutes, seconds, nanoseconds -> "$minutes:$seconds" }

    private val _timeString = MutableStateFlow(_remainingTimeString)
    val timeString: StateFlow<String> = _timeString.asStateFlow()

    private var timerProgress = _remainingTime.div(timerDuration).toFloat()

    private val _remainingTimeProgress = MutableStateFlow(timerProgress)
    val remainingTimeProgress: StateFlow<Float> = _remainingTimeProgress.asStateFlow()

    private var _timerFinished: Boolean = false
    val timerFinished: Boolean
        get() = _timerFinished

    private fun setCompletedTimerFinished() {
        //  TODO Trigger Notification & Sound, Update UI State
    }

    fun updateRemainingTime(): String {
        //  TODO Return Remaining Time as String
        return _remainingTimeString
    }

    private lateinit var timer: CountDownTimer

    private fun createTimer() {
        timer = object : CountDownTimer(timerDuration, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                //        TODO Return Remaining Time as String
                _remainingTime = millisUntilFinished
                _remainingTimeString = _remainingTime.toDuration(DurationUnit.SECONDS)
                    .toComponents { minutes, seconds, nanoseconds -> "$minutes:$seconds" }

                Log.i("onTick(): _remainingTime", "$millisUntilFinished")
            }

            override fun onFinish() {
                _timerFinished = true
                setCompletedTimerFinished()
            }
        }.start()
    }

    fun startSetCompletedTimer(exerciseSet: ExerciseSetModel, setID: Long) {
        val exercise = exerciseSet.exercise
        val set = exerciseSet.sets.first { it.id == setID }

        if (exercise.warmUpSetRestTimerEnabled && (set.setType.equals("Warm Up"))) {
            timerDuration = exercise.warmUpSetRestTimer.inWholeMilliseconds
            createTimer()
        } else if (exercise.workingSetRestTimerEnabled && !(set.setType.equals("Warm Up"))) {
            timerDuration = exercise.warmUpSetRestTimer.inWholeMilliseconds
            createTimer()
        }
    }

    fun cancelSetCompletedTimer() {
        timer.cancel()
    }

    fun moreRestTime() {
        timerDuration = _remainingTime + restTimerAdjustment
        timer.cancel()
        createTimer()
    }

    fun lessRestTime() {
        timerDuration = _remainingTime - restTimerAdjustment
        timer.cancel()
        createTimer()
    }

    private var _exercisesSets = initExercisesSets().toMutableStateList()
    val exercisesSets: List<ExerciseSetModel>
        get() = _exercisesSets
}

private fun initExercisesSets(): List<ExerciseSetModel> {
    return List(6) {
        ExerciseSetModel(
            exercise = ExerciseModel(),
            sets = List(6) { indices ->
                SetModel(
                    setNumber = indices,
                    exerciseWeight = 100.00,
                    repetitions = 10
                )
            }
        )
    }
}
