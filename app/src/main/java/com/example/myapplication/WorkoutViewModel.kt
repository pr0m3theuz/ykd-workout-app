package com.example.myapplication

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.components.setRestTimer.TimerState
import com.example.myapplication.data.*
import com.example.myapplication.utils.AlarmReceiver
import com.example.myapplication.utils.cancelNotifications
import com.example.myapplication.utils.sendNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val WARM_UP_SET = "Warm Up Set"

@HiltViewModel
class WorkoutViewModel @Inject constructor(private val application: Application) :
    ViewModel() {
    //    fun currentTimeMillis(): Long = System.currentTimeMillis()
//    fun elapsedTimeMillis(): Long = System.
    private val REQUEST_CODE = 0
    private val notifyPendingIntent: PendingIntent

    private var workoutStartTime: Long = 0
    private var workoutEndTime: Long = 0

    private var workoutStartDuration: Long = 0

    private var workoutDuration: Long = 0

    private val _workoutDuration = MutableStateFlow(workoutDuration)
    val durationWorkout: StateFlow<Long> = _workoutDuration.asStateFlow()

    private var _workoutDurationFormatted = workoutDuration.toDuration(DurationUnit.MILLISECONDS)
        .toComponents { minutes, seconds, nanoseconds ->
            "$minutes:${
                seconds.toString().padStart(2, '0')
            }"
        }

    private val _workoutDurationString = MutableStateFlow(_workoutDurationFormatted)
    val workoutDurationString: StateFlow<String>
        get() = _workoutDurationString.asStateFlow()

    private val workoutCountUpTimer = object : CountUpTimer(1000) {
        override fun onTick(elapsedTime: Long) {
            workoutDuration = elapsedTime

            _workoutDurationString.value =
                workoutDuration.toDuration(DurationUnit.MILLISECONDS)
                    .toComponents { minutes, seconds, nanoseconds ->
                        "${minutes.toString().padStart(2, '0')}:${
                            seconds.toString().padStart(2, '0')
                        }"
                    }
        }
    }

    private val notificationManager = ContextCompat.getSystemService(
        application,
        NotificationManager::class.java,
    ) as NotificationManager

    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val notifyIntent = Intent(application, AlarmReceiver::class.java)

    init {
        workoutStartTime = System.currentTimeMillis()
        workoutStartDuration = SystemClock.elapsedRealtime()
        workoutCountUpTimer.start()

        notifyPendingIntent = PendingIntent.getBroadcast(
            application,
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun finishWorkout() {
        workoutEndTime = System.currentTimeMillis()
//        TODO Save Workout to Database
//        TODO Navigate to Workout Complete Screen & Summary
    }

    private var _timerDuration: Long = 60000L
    private var _timerDurationFormatted = _timerDuration.toDuration(DurationUnit.MILLISECONDS)
        .toComponents { minutes, seconds, nanoseconds ->
            "$minutes:${
                seconds.toString().padStart(2, '0')
            }"
        }

    private val _timerDurationString = MutableStateFlow(_timerDurationFormatted)
    val timerDurationString: StateFlow<String>
        get() = _timerDurationString.asStateFlow()

    //        TODO Store User Settings for Rest Timer Adjustments
    private val _restTimerAdjustment: Long =
        30.toDuration(DurationUnit.SECONDS).inWholeMilliseconds
    val restTimerAdjustmentString =
        _restTimerAdjustment.toDuration(DurationUnit.MILLISECONDS).inWholeSeconds.toString()

    private var _remainingTime: Long = 60000L
    val remainingTime: Long
        get() = _remainingTime

    private var _remainingTimeFormatted = _remainingTime.toDuration(DurationUnit.MILLISECONDS)
        .toComponents { minutes, seconds, nanoseconds ->
            "$minutes:${
                seconds.toString().padStart(2, '0')
            }"
        }

    private val _remainingTimeString = MutableStateFlow(_remainingTimeFormatted)
    val remainingTimeString: StateFlow<String>
        get() = _remainingTimeString.asStateFlow()

    private var timerProgress = _remainingTime.div(_timerDuration).toFloat()

    private val _remainingTimeProgress = MutableStateFlow(timerProgress)
    val remainingTimeProgress: StateFlow<Float>
        get() = _remainingTimeProgress.asStateFlow()

    private var _timerFinished: Boolean = false
    val timerFinished: Boolean
        get() = _timerFinished

//    private fun setCompletedTimerFinished() {
//        //  TODO Trigger Notification & Sound, Update UI State
//        if (_timerFinished) {
//            Log.i("onFinish():  _timerFinished", "$_timerFinished")
//        }
//    }

    //    TODO Implement as a Singleton
    private lateinit var timer: CountDownTimer

// TODO Make it so that the timer doesn't get trigger multiple times when there is a subsequent UI Event from the same set
// TODO Create Timer State Holder and for managing the Timer State
/* TODO Determine how I wanted quick sequential set completion events that start the timer, either
restart the timer or don't, unless the new timer duration is longer than the initial timer duration
 then adjust remaining time by the difference between new timer duration and the initial timer duration
 */

    private var _state: TimerState = TimerState.RESET

    private val _timerState = MutableStateFlow(_state)
    val workoutTimerState: StateFlow<TimerState>
        get() = _timerState.asStateFlow()

    val isReset: Boolean
        get() = _state == TimerState.RESET

    val isTimerRunning: Boolean
        get() = _state == TimerState.RUNNING

    val isExpired: Boolean
        get() = _state == TimerState.EXPIRED

    private fun createTimer(timerDuration: Long) {
        val timerVersion = System.currentTimeMillis().toString()
        val alarmTriggerTime = SystemClock.elapsedRealtime() + timerDuration

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            alarmTriggerTime,
            notifyPendingIntent
        )

        viewModelScope.launch {
            timer = object : CountDownTimer(timerDuration, 1000L) {

                override fun onTick(millisUntilFinished: Long) {
                    // TODO Return Remaining Time as String
                    _state = TimerState.RUNNING
                    _timerState.value = TimerState.RUNNING
                    _remainingTime = millisUntilFinished
                    _remainingTimeString.value =
                        _remainingTime.toDuration(DurationUnit.MILLISECONDS)
                            .toComponents { minutes, seconds, nanoseconds ->
                                "$minutes:${
                                    seconds.toString().padStart(2, '0')
                                }"
                            }

                    _timerDurationString.value =
                        _timerDuration.toDuration(DurationUnit.MILLISECONDS)
                            .toComponents { minutes, seconds, nanoseconds ->
                                "$minutes:${
                                    seconds.toString().padStart(2, '0')
                                }"
                            }

                    _remainingTimeProgress.value =
                        _remainingTime.toFloat().div(_timerDuration.toFloat())

                    Log.i("Timer: ", timerVersion)
                    Log.i("TimerState: ", _timerState.value.name)

                    Log.i("onTick(): _remainingTime", "$millisUntilFinished")
                    Log.i("onTick(): timerDuration", "$_timerDuration")
                    Log.i(
                        "onTick(): _remainingTimeProgress",
                        "${_remainingTime.toFloat().div(_timerDuration.toFloat())}",
                    )
                }

                override fun onFinish() {
                    // TODO Refactor into resetTimerState()
                    _state = TimerState.EXPIRED
                    _timerState.value = TimerState.EXPIRED
                    _timerFinished = true
//                    setCompletedTimerFinished()

                    Log.i("Timer: ", timerVersion)
                    Log.i("TimerState: ", _timerState.value.name)
                }
            }.start()
        }
    }

    private fun createRestTimerNotification() {
        Log.i("createTimer()", "called")
        Log.i("elapsedRealtime()", SystemClock.elapsedRealtime().toString())

        // TODO: Step 1.5 get an instance of NotificationManager and call sendNotification
        //        TODO move notificationManager and alarmManager to separate function so it isn't called when time is add or taken away from the timer

        notificationManager.sendNotification(
            application.getString(R.string.rest_timer_running_text),
            application
        )

        // TODO: Step 1.15 call cancel notification
    }

    fun startRestTimer(time: Duration) {
        Log.i("startRestTimer() ", "called")
        _timerDuration = time.inWholeMilliseconds

        createRestTimerNotification()
        createTimer(_timerDuration)
    }

    fun startLastTimer() {
        createRestTimerNotification()
        createTimer(_timerDuration)
    }

    private var completedSets = mutableListOf<Long>()

    fun startSetCompletedTimer(exerciseSet: ExerciseSetModel, setID: Long) {
        Log.i("startSetCompletedTimer() ", "called")

        val exercise = exerciseSet.exercise
        val set = exerciseSet.sets.first { it.id == setID }

        if (!completedSets.contains(setID)) {
            completedSets.add(setID)
            /*
            * Doesn't allow the new completed set to cancel the timer if one is already running
            */
            if (_state != TimerState.RUNNING) {
                if (exercise.warmUpSetRestTimerEnabled && (set.setType.name == WARM_UP_SET)) {
                    _timerDuration = exercise.warmUpSetRestInWholeMilliseconds
                    startRestTimer(_timerDuration.toDuration(DurationUnit.MILLISECONDS))
                } else if (exercise.workingSetRestTimerEnabled && (set.setType.name != WARM_UP_SET)) {
                    _timerDuration = exercise.workingSetRestInWholeMilliseconds
                    startRestTimer(_timerDuration.toDuration(DurationUnit.MILLISECONDS))
                }
            }
        }
    }

    private fun resetTimerState() {
        Log.i("resetTimerState() ", "called")
        _remainingTime = 0L
        _remainingTimeString.value = _remainingTime.toDuration(DurationUnit.MILLISECONDS)
            .toComponents { minutes, seconds, _ ->
                "$minutes:${
                    seconds.toString().padStart(2, '0')
                }"
            }
        _remainingTimeProgress.value = 0f
        _timerState.value = TimerState.RESET
        _state = TimerState.RESET
        Log.i("TimerState: ", _timerState.value.name)
    }

    fun cancelSetCompletedTimer() {
        Log.i("cancelSetCompletedTimer() ", "called")
        timer.cancel()
        resetTimerState()
        notificationManager.cancelNotifications()
        alarmManager.cancel(notifyPendingIntent)
    }

    fun moreRestTime() {
        _remainingTime += _restTimerAdjustment
        _timerDuration += _restTimerAdjustment
        timer.cancel()
        alarmManager.cancel(notifyPendingIntent)
        createTimer(_remainingTime)
    }

    fun lessRestTime() {
        _remainingTime -= _restTimerAdjustment
        _timerDuration -= _restTimerAdjustment
        timer.cancel()
        alarmManager.cancel(notifyPendingIntent)
        if (_remainingTime < _timerDuration) {
            createTimer(_remainingTime)
        } else {
            _state = TimerState.EXPIRED
            _timerState.value = TimerState.EXPIRED
            timer.cancel()
            alarmManager.cancel(notifyPendingIntent)
        }
    }

    private var _exercisesSetsData = initExercisesSets()

    private var _exercisesSets = MutableStateFlow(_exercisesSetsData.toList())
    val exercisesSets
        get() = _exercisesSets.asStateFlow()/*.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = _exercisesSetsData.toList()
        )*/

    fun addSet(exerciseSets: ExerciseSetModel) {
        val index = _exercisesSetsData.indexOf(exerciseSets)

        val temporarySets = exerciseSets.sets + exerciseSets.sets.last().copy(
            id = newID(),
            setNumber = exerciseSets.sets.last().setNumber?.plus(1),
            setCompleted = false,
        )

        _exercisesSetsData[index] =
            ExerciseSetModel(id = newID(), exerciseSets.exercise, temporarySets.toMutableList())

        _exercisesSetsData[index]
        _exercisesSetsData = _exercisesSetsData.toMutableList()
        _exercisesSets.value = _exercisesSetsData

//        Log.i(
//            "workoutViewModel.addSet()",
//            "${exerciseSets.id} & ${ _exercisesSetsData[index].id}"
//        )
    }

    fun removeExerciseSets(exerciseSets: ExerciseSetModel) {
        _exercisesSetsData = _exercisesSetsData.filter { it != exerciseSets }.toMutableList()
        _exercisesSets.value = _exercisesSetsData
    }

    fun removeSet(exerciseSets: ExerciseSetModel, set: SetModel) {
        val index = _exercisesSetsData.indexOf(exerciseSets)
        val temporarySets = exerciseSets.sets.minusElement(set).toMutableList()

        _exercisesSetsData[index] =
            ExerciseSetModel(id = newID(), exerciseSets.exercise, temporarySets.toMutableList())
        _exercisesSets.value = _exercisesSetsData.toMutableList()
    }

    fun updateSetWeight(exerciseSets: ExerciseSetModel, setID: Long, newWeight: Double?) {
        exerciseSets.updateSetWeight(
            id = setID,
            newWeight = newWeight,
        )
    }

    fun updateSetReps(exerciseSets: ExerciseSetModel, setID: Long, newReps: Int?) {
        exerciseSets.updateSetReps(id = setID, newReps = newReps)
    }

    fun updateSetRIR(exerciseSets: ExerciseSetModel, setID: Long, newRIR: Double?) {
        exerciseSets.updateSetRIR(id = setID, newRIR = newRIR)
    }

    fun updateSetRPE(exerciseSets: ExerciseSetModel, setID: Long, newRPE: Double?) {
        exerciseSets.updateSetRPE(id = setID, newRPE = newRPE)
    }

    fun setComplete(exerciseSets: ExerciseSetModel, setID: Long, setComplete: Boolean) {
        exerciseSets.setComplete(setID, setComplete)
    }

    fun updateSetType(exerciseSets: ExerciseSetModel, setID: Long, setType: SetTypeModel) {
        exerciseSets.updateSetType(id = setID, setType = setType)
    }

    fun addWarmUpSets(exerciseSets: ExerciseSetModel, warmUpSets: List<SetModel>) {
        val index = _exercisesSetsData.indexOf(exerciseSets)
        exerciseSets.addWarmUpSets(warmUpSets)

        val temporarySets = exerciseSets.sets

        _exercisesSetsData[index] =
            ExerciseSetModel(id = newID(), exerciseSets.exercise, temporarySets.toMutableList())
        _exercisesSets.value = _exercisesSetsData.toMutableList()
    }

    fun getWorkingSetWeight(exerciseSets: ExerciseSetModel): Double? {
        // TODO Add If Not Empty
        return exerciseSets.sets.first { it.setType.name != WARM_UP_SET }.exerciseWeight
    }

    fun getWorkingSetRepetitions(exerciseSets: ExerciseSetModel): Int? {
        // TODO Add If Not Empty
        return exerciseSets.sets.first { it.setType.name != WARM_UP_SET }.repetitions
    }

    fun addWorkingSets(exerciseSets: ExerciseSetModel, workingSets: List<SetModel>) {
        val index = _exercisesSetsData.indexOf(exerciseSets)

        workingSets.forEach { it.id = newID() }
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Before: ${_exerciseSets.toList()}")
        exerciseSets.sets.removeAll { it.setType.name != WARM_UP_SET }
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Removed Old Sets: ${_exerciseSets.toList()}")
        exerciseSets.sets.addAll(workingSets)
//        Log.i("ExerciseSetViewModel.addWorkingSets(): ", "exerciseSets Added New Sets: ${_exerciseSets.toList()}")

        val temporarySets = exerciseSets.sets

        _exercisesSetsData[index] =
            ExerciseSetModel(id = newID(), exerciseSets.exercise, temporarySets.toMutableList())
        _exercisesSets.value = _exercisesSetsData.toMutableList()
    }

    fun addBackOffSets(exerciseSets: ExerciseSetModel, backOffSets: List<SetModel>) {
        for (index in backOffSets.indices) {
            exerciseSets.sets.add(backOffSets[index].copy(id = newID()))
        }

        val index = _exercisesSetsData.indexOf(exerciseSets)

        val temporarySets = exerciseSets.sets

        _exercisesSetsData[index] =
            ExerciseSetModel(id = newID(), exerciseSets.exercise, temporarySets.toMutableList())
        _exercisesSets.value = _exercisesSetsData.toMutableList()
    }

    fun updateExerciseAutoRestTimer(tabRowState: Int, exercise: ExerciseModel, time: Duration) {
//        TODO Fix, Doesn't change time
        if (tabRowState == 1) {
            exercise.warmUpSetRestInWholeMilliseconds = time.inWholeMilliseconds
        } else if (tabRowState == 0) {
            exercise.workingSetRestInWholeMilliseconds = time.inWholeMilliseconds
        }
    }

    fun enableExerciseAutoRestTimer(tabRowState: Int, exercise: ExerciseModel, enable: Boolean) {
        if (tabRowState == 1) {
            exercise.warmUpSetRestTimerEnabled = enable
        } else if (tabRowState == 0) {
            exercise.workingSetRestTimerEnabled = enable
        }
    }

    fun checkAllSetsComplete(): Boolean {
        var countSets: Int = 0
        var countSetsCompleted: Int = 0

        _exercisesSetsData.forEach { exercisesSets ->
            exercisesSets.sets.forEach { set ->
                countSets++
                if (set.setCompleted) {
                    countSetsCompleted++
                }
            }
        }

        return countSetsCompleted == countSets
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
                    repetitions = 10.times(it),
                )
            },
        )
    }
}

abstract class CountUpTimer(private val interval: Long) {
    private var base: Long = 0

    fun start() {
        base = SystemClock.elapsedRealtime()
        handler.sendMessage(handler.obtainMessage(MSG))
    }

    fun stop() {
        handler.removeMessages(MSG)
    }

    fun reset() {
        synchronized(this) { base = SystemClock.elapsedRealtime() }
    }

    abstract fun onTick(elapsedTime: Long)

    private val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            synchronized(this@CountUpTimer) {
                val elapsedTime = SystemClock.elapsedRealtime() - base
                onTick(elapsedTime)
                sendMessageDelayed(obtainMessage(MSG), interval)
            }
        }
    }

    companion object {
        private const val MSG = 1
    }
}
