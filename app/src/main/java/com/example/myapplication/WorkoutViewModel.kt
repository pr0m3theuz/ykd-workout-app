package com.example.myapplication

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class WorkoutViewModel : ViewModel() {
//    fun currentTimeMillis(): Long = System.currentTimeMillis()
//    fun elapsedTimeMillis(): Long = System.

    private var workoutStartTime: Long = 0

    init {
        workoutStartTime = Clock.System.now().epochSeconds
    }

    val workoutDuration: Long = Clock.System.now().epochSeconds - workoutStartTime

    private val _workoutDuration = MutableStateFlow(workoutDuration)
    val durationWorkout: StateFlow<Long> = _workoutDuration.asStateFlow()

    private var timerDuration: Long = 1L

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
        if (_timerFinished) {
            Log.i("onFinish():  _timerFinished", "$_timerFinished")
        }
    }

    fun updateRemainingTime(): String {
        //  TODO Return Remaining Time as String
        return _remainingTimeString
    }

    private lateinit var timer: CountDownTimer

    private fun createTimer(timerDuration: Long) {
        viewModelScope.launch {
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
                    _remainingTime = 0L
                    setCompletedTimerFinished()
                }
            }.start()
        }
    }

    fun startSetCompletedTimer(exerciseSet: ExerciseSetModel, setID: Long) {
        val exercise = exerciseSet.exercise
        val set = exerciseSet.sets.first { it.id == setID }

        if (exercise.warmUpSetRestTimerEnabled && (set.setType.equals("Warm Up"))) {
            timerDuration = exercise.warmUpSetRestTimer.inWholeMilliseconds
            createTimer(timerDuration)
        } else if (exercise.workingSetRestTimerEnabled && !(set.setType.equals("Warm Up"))) {
            timerDuration = exercise.warmUpSetRestTimer.inWholeMilliseconds
            createTimer(timerDuration)
        }
    }

    fun cancelSetCompletedTimer() {
        timer.cancel()
    }

    fun moreRestTime() {
        timerDuration = _remainingTime + restTimerAdjustment
        timer.cancel()
        createTimer(timerDuration)
    }

    fun lessRestTime() {
        timerDuration = _remainingTime - restTimerAdjustment
        timer.cancel()
        createTimer(timerDuration)
    }

    private var _exercisesSetsData = initExercisesSets()

    private var _exercisesSets = MutableStateFlow(_exercisesSetsData.toList())
    val exercisesSets
        get() = _exercisesSets.asStateFlow()/*.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _exercisesSetsData.toList()
        )*/

/*    private var _exercisesSets = initExercisesSets()
    val exercisesSets
        get() = _exercisesSets*/

    fun addSet(exerciseSets: ExerciseSetModel) {
        removeExerciseSets(exerciseSets)
    }

    fun removeExerciseSets(exerciseSets: ExerciseSetModel) {
        _exercisesSetsData = _exercisesSetsData.filter { it != exerciseSets }.toMutableList()
        _exercisesSets.value = _exercisesSetsData
    }

    fun removeSet(exerciseSets: ExerciseSetModel, setID: Long) {
//        val exerciseSets = _exercisesSets.first { it.id == exerciseSetsID }

        exerciseSets.removeSet(setID)
    }

    fun updateSetWeight(exerciseSets: ExerciseSetModel, setID: Long, newWeight: Double?) {
        exerciseSets.sets.forEach { if (it.id == setID) it.exerciseWeight = newWeight }
    }

    fun updateSetReps(exerciseSets: ExerciseSetModel, setID: Long, newReps: Int?) {
        exerciseSets.sets.forEach { if (it.id == setID) it.repetitions = newReps }
    }

    fun updateSetRIR(exerciseSets: ExerciseSetModel, setID: Long, newRIR: Double?) {
        exerciseSets.sets.forEach { if (it.id == setID) it.ratePerceivedEffort = newRIR }
    }

    fun updateSetRPE(exerciseSets: ExerciseSetModel, setID: Long, newRPE: Double?) {
        exerciseSets.sets.forEach { if (it.id == setID) it.ratePerceivedEffort = newRPE }
    }

    fun setComplete(exerciseSets: ExerciseSetModel, setID: Long, setComplete: Boolean) {
        exerciseSets.setComplete(setID, setComplete)
    }

    fun updateSetType(exerciseSets: ExerciseSetModel, setID: Long, setType: SetTypeModel) {
        exerciseSets.sets.forEach { if (it.id == setID) it.setType = setType }
    }

    fun addWarmUpSets(exerciseSets: ExerciseSetModel, warmUpSets: List<SetModel>) {
        exerciseSets.addWarmUpSets(warmUpSets)
    }
}

private fun initExercisesSets(): MutableList<ExerciseSetModel> {
    return MutableList(6) {
        ExerciseSetModel(
            exercise = ExerciseModel(),
            sets = MutableList(3) { indices ->
                SetModel(
                    setNumber = indices,
                    exerciseWeight = 100.00.times(it),
                    repetitions = 10.times(it)
                )
            }
        )
    }
}
