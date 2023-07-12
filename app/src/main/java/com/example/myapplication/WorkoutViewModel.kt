package com.example.myapplication

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.components.setRestTimer.TimerState
import com.example.myapplication.data.ExerciseModel
import com.example.myapplication.data.ExerciseNote
import com.example.myapplication.data.ExerciseNotesRepository
import com.example.myapplication.data.ExerciseSets
import com.example.myapplication.data.ExerciseSetsRepository
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetsRepository
import com.example.myapplication.data.SetsWithExercise
import com.example.myapplication.data.Workout
import com.example.myapplication.data.WorkoutNotesRepository
import com.example.myapplication.data.WorkoutRepository
import com.example.myapplication.data.WorkoutWithSetsAndExercise
import com.example.myapplication.data.addWarmUpSets
import com.example.myapplication.data.pinNotes
import com.example.myapplication.data.resetSets
import com.example.myapplication.data.setComplete
import com.example.myapplication.data.updateSetRIR
import com.example.myapplication.data.updateSetRPE
import com.example.myapplication.data.updateSetReps
import com.example.myapplication.data.updateSetType
import com.example.myapplication.data.updateSetWeight
import com.example.myapplication.utils.AlarmReceiver
import com.example.myapplication.utils.cancelNotifications
import com.example.myapplication.utils.sendNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Random
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val WARM_UP_SET = 2

// TODO Croate queries to save workouts and sets
@HiltViewModel
class WorkoutViewModel
@Inject
constructor(
    private val application: Application,
    private val exerciseNotesRepository: ExerciseNotesRepository,
    private val workoutRepository: WorkoutRepository,
    private val workoutNotesRepository: WorkoutNotesRepository,
    private val setsRepository: SetsRepository,
    private val exerciseSetsRepository: ExerciseSetsRepository,
) : ViewModel() {
    //    fun currentTimeMillis(): Long = System.currentTimeMillis()
    //    fun elapsedTimeMillis(): Long = System.
    private val REQUEST_CODE = 0
    private val notifyPendingIntent: PendingIntent

    private var workoutDuration: Long = 0

    private var _workoutDurationFormatted =
        workoutDuration.toDuration(DurationUnit.MILLISECONDS).toComponents { hours,
                                                                             minutes,
                                                                             seconds,
                                                                             _ ->
            if (hours > 0) {
                "$hours:" +
                        minutes.toString().padStart(2, '0') +
                        ":" +
                        seconds.toString().padStart(2, '0')
            } else {
                "$minutes:${seconds.toString().padStart(2, '0')}"
            }
        }

    private val _workoutDurationString = MutableStateFlow(_workoutDurationFormatted)
    val workoutDurationString: StateFlow<String>
        get() = _workoutDurationString.asStateFlow()

    private val workoutCountUpTimer =
        object : CountUpTimer(1000) {
            override fun onTick(elapsedTime: Long) {
                workoutDuration = elapsedTime

                _workoutDurationString.value =
                    workoutDuration.toDuration(DurationUnit.MILLISECONDS).toComponents { hours,
                                                                                         minutes,
                                                                                         seconds,
                                                                                         _ ->
                        if (hours > 0) {
                            "$hours:" +
                                    minutes.toString().padStart(2, '0') +
                                    ":" +
                                    seconds.toString().padStart(2, '0')
                        } else {
                            "$minutes:${seconds.toString().padStart(2, '0')}"
                        }
                    }
            }
        }

    //  Rest Timer
    private val notificationManager =
        ContextCompat.getSystemService(
            application,
            NotificationManager::class.java,
        ) as NotificationManager

    private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val notifyIntent = Intent(application, AlarmReceiver::class.java)

    init {
        workoutCountUpTimer.start()

        notifyPendingIntent =
            PendingIntent.getBroadcast(
                application,
                REQUEST_CODE,
                notifyIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
    }

    private var _timerDuration: Long = 60000L
    private var _timerDurationFormatted =
        _timerDuration.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _ ->
            "$minutes:${
                seconds.toString().padStart(2, '0')
            }"
        }

    private val _timerDurationString = MutableStateFlow(_timerDurationFormatted)
    val timerDurationString: StateFlow<String>
        get() = _timerDurationString.asStateFlow()

    //        TODO Store User Settings for Rest Timer Adjustments
    private val _restTimerAdjustment: Long = 30.toDuration(DurationUnit.SECONDS).inWholeMilliseconds
    val restTimerAdjustmentString =
        _restTimerAdjustment.toDuration(DurationUnit.MILLISECONDS).inWholeSeconds.toString()

    private var _remainingTime: Long = 60000L

    private var _remainingTimeFormatted =
        _remainingTime.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _ ->
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

    private lateinit var timer: CountDownTimer

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
            timer =
                object : CountDownTimer(timerDuration, 1000L) {

                    override fun onTick(millisUntilFinished: Long) {
                        _state = TimerState.RUNNING
                        _timerState.value = TimerState.RUNNING
                        _remainingTime = millisUntilFinished
                        _remainingTimeString.value =
                            _remainingTime.toDuration(DurationUnit.MILLISECONDS)
                                .toComponents { minutes,
                                                seconds,
                                                _ ->
                                    "$minutes:${
                                        seconds.toString().padStart(2, '0')
                                    }"
                                }

                        _timerDurationString.value =
                            _timerDuration.toDuration(DurationUnit.MILLISECONDS)
                                .toComponents { minutes,
                                                seconds,
                                                _ ->
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
                        _state = TimerState.EXPIRED
                        _timerState.value = TimerState.EXPIRED
                        _timerFinished = true

                        Log.i("Timer: ", timerVersion)
                        Log.i("TimerState: ", _timerState.value.name)
                    }
                }
                    .start()
        }
    }

    private fun createRestTimerNotification() {
        Log.i("createTimer()", "called")
        Log.i("elapsedRealtime()", SystemClock.elapsedRealtime().toString())

        notificationManager.sendNotification(
            application.getString(R.string.rest_timer_running_text),
            application
        )
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

    private var completedSets = mutableListOf<UUID>()

    fun startSetCompletedTimer(exerciseSet: ExerciseSets, setUUID: UUID) {
        Log.i("startSetCompletedTimer() ", "called")

        val exercise = exerciseSet.exercise
        val set = exerciseSet.sets.first { it.uuid == setUUID }

        if (!completedSets.contains(setUUID)) {
            completedSets.add(setUUID)
            /*
             * Doesn't allow the new completed set to cancel the timer if one is already running
             */
            if (_state != TimerState.RUNNING) {
                if (exercise.warmUpSetRestTimerEnabled && (set.setTypeID == WARM_UP_SET)) {
                    _timerDuration = exercise.warmUpSetRestInWholeMilliseconds
                    startRestTimer(_timerDuration.toDuration(DurationUnit.MILLISECONDS))
                } else if (exercise.workingSetRestTimerEnabled && (set.setTypeID != WARM_UP_SET)) {
                    _timerDuration = exercise.workingSetRestInWholeMilliseconds
                    startRestTimer(_timerDuration.toDuration(DurationUnit.MILLISECONDS))
                }
            }
        }
    }

    private fun resetTimerState() {
        Log.i("resetTimerState() ", "called")
        _remainingTime = 0L
        _remainingTimeString.value =
            _remainingTime.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _
                ->
                "$minutes:${
                    seconds.toString().padStart(2, '0')
                }"
            }
        _remainingTimeProgress.value = 0f
        _state = TimerState.RESET
        _timerState.value = TimerState.RESET
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
            resetTimerState()
        }
    }

    //    Workout
    private var _workoutData = initWorkout()
    private var _workout = MutableStateFlow(_workoutData)
    val workout
        get() = _workout.asStateFlow()

    private var _workoutName = MutableStateFlow(_workoutData.name)
    val workoutName
        get() = _workoutName.asStateFlow()

    fun setWorkoutName(newName: String) {
        _workoutData.name = newName
        _workout.value = _workoutData
    }

    fun setWorkoutNote(note: String) {
        _workoutData.notes = note
        _workout.value = _workoutData
    }

    fun pinWorkoutNote(pinned: Boolean) {
        _workoutData.pinNotes(pinned)
        _workout.value = _workoutData
    }

    fun stopWorkoutTimer() {
        workoutCountUpTimer.stop()
        _workoutData.workoutEnded = LocalDateTime.now()
        _workoutData.workoutDuration = workoutDuration
        _workout.value = _workoutData
        //        TODO Navigate to Workout Complete Screen & Summary
    }

    //    ExerciseSets
    private var _exercisesSetsData = mutableListOf<ExerciseSets>()

    private var _exercisesSets = MutableStateFlow(_exercisesSetsData.toList())
    val exercisesSets
        get() = _exercisesSets.asStateFlow()

    private var _allSetsData: MutableList<SetModel> = allSets()
    private var _allSets = MutableStateFlow(_allSetsData.toList())
    val allSets
        get() = _allSets.asStateFlow()

    fun addEmptyExerciseSets(exercisesSelectedToBeAddedToWorkout: List<ExerciseModel>) {
        val temporaryExerciseSets = _exercisesSets.value.toMutableList()

        exercisesSelectedToBeAddedToWorkout.forEach {
            temporaryExerciseSets.add(
                ExerciseSets(
                    workoutID = _workout.value.workoutID,
                    workoutUUID = _workout.value.uuid,
                    exercise = it,
                    sets = mutableListOf()
                )
            )
        }

        /*        temporaryExerciseSets.forEach(){
            it.exerciseSetsID = fakeULID()
            it.exSetsLazyListItemID = newID()
        }*/

        _exercisesSetsData = temporaryExerciseSets
        _exercisesSets.value = _exercisesSetsData.toList()
    }

    fun removeExerciseSets(exerciseSets: ExerciseSets) {
        _exercisesSetsData = _exercisesSets.value.filter { it != exerciseSets }.toMutableList()
        _exercisesSets.value = _exercisesSetsData
    }

    fun addSet(exerciseSets: ExerciseSets) {
        val temporarySets: List<SetModel> =
            if (exerciseSets.sets.isNotEmpty()) {
                exerciseSets.sets +
                        exerciseSets.sets
                            .last()
                            .copy(
                                lazyListItemID = newID(),
                                uuid = UUID.randomUUID(),
                                setNumber = exerciseSets.sets.last().setNumber?.plus(1),
                                setCompleted = false,
                            )
            } else {
                listOf(
                    SetModel(
                        setNumber = 0,
                        exerciseID = exerciseSets.exercise.exerciseID,
                        exerciseUUID = exerciseSets.exercise.uuid,
                        progressionSchemeID =
                        Random()
                            .nextInt(), // TODO Replace with
                        // progressionScheme.progressionSchemeID
                        workoutID = workout.value.workoutID, // TODO Replace with workout.workoutID
                        workoutUUID = workout.value.uuid,
                    )
                )
            }

        updateExercisesSetsData(exerciseSets, temporarySets.toMutableList())
    }

    fun removeSet(exerciseSets: ExerciseSets, set: SetModel) {
        val temporarySets = exerciseSets.sets.minusElement(set).toMutableList()

        updateExercisesSetsData(exerciseSets, temporarySets)
    }

    private fun updateExercisesSetsData(
        exerciseSets: ExerciseSets,
        temporarySets: MutableList<SetModel>,
    ) {
        val index = _exercisesSetsData.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }
        _exercisesSetsData[index] =
            exerciseSets.copy(exSetsLazyListItemID = newID(), sets = temporarySets)
        _exercisesSets.value = _exercisesSetsData.toMutableList()
    }

    private fun updateSetData(
        exerciseSets: ExerciseSets,
    ) {
        val index = _exercisesSetsData.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }
        _exercisesSetsData[index] = exerciseSets.copy(exSetsLazyListItemID = newID())
        _exercisesSets.value = _exercisesSetsData.toMutableList()
        _allSets.value = allSets().toList()
    }

    private fun allSets(): MutableList<SetModel> {
        val setsList = mutableListOf<SetModel>()
        _exercisesSetsData.forEach { exerciseSet ->
            exerciseSet.sets.forEach { set -> setsList.add(set) }
        }

        return setsList
    }

    fun getSetData(setUUID: UUID): SetModel {
        return allSets().first { it.uuid == setUUID }
    }

    fun updateSetWeight(exerciseSets: ExerciseSets, setUUID: UUID, newWeight: Double?) {
        exerciseSets.updateSetWeight(
            uuid = setUUID,
            newWeight = newWeight,
        )

        updateSetData(exerciseSets)
    }

    fun updateSetReps(exerciseSets: ExerciseSets, setUUID: UUID, newReps: Int?) {
        exerciseSets.updateSetReps(uuid = setUUID, newReps = newReps)
        updateSetData(exerciseSets)
    }

    fun updateSetRIR(exerciseSets: ExerciseSets, setUUID: UUID, newRIR: Double?) {
        exerciseSets.updateSetRIR(uuid = setUUID, newRIR = newRIR)
        updateSetData(exerciseSets)
    }

    fun updateSetRPE(exerciseSets: ExerciseSets, setUUID: UUID, newRPE: Double?) {
        exerciseSets.updateSetRPE(uuid = setUUID, newRPE = newRPE)
        updateSetData(exerciseSets)
    }

    fun setComplete(exerciseSets: ExerciseSets, setUUID: UUID, setComplete: Boolean) {
        exerciseSets.setComplete(setUUID, setComplete)
        updateSetData(exerciseSets)
    }

    fun updateSetType(exerciseSets: ExerciseSets, setUUID: UUID, setTypeID: Int) {
        exerciseSets.updateSetType(uuid = setUUID, setTypeID = setTypeID)
        updateSetData(exerciseSets)
    }

    fun addWarmUpSets(exerciseSets: ExerciseSets, warmUpSets: List<SetModel>) {
        exerciseSets.addWarmUpSets(warmUpSets)

        updateExercisesSetsData(exerciseSets, exerciseSets.sets)
    }

    fun getWorkingSetWeight(exerciseSets: ExerciseSets): Double? {
        // TODO Get Previous Working Set if Sets is empty

        return exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.exerciseWeight
    }

    fun getWorkingSetRepetitions(exerciseSets: ExerciseSets): Int? {
        // TODO Get Previous Working Set if Sets is empty
        var repetitions: Int? = null
        if (exerciseSets.sets.isNotEmpty()) {
            repetitions = exerciseSets.sets.first { it.setTypeID != WARM_UP_SET }.repetitions
        }
        return repetitions
    }

    fun addWorkingSets(exerciseSets: ExerciseSets, workingSets: List<SetModel>) {
        workingSets.forEach { it.lazyListItemID = newID() }

        exerciseSets.sets.removeAll { it.setTypeID != WARM_UP_SET }

        exerciseSets.sets.addAll(workingSets)

        updateExercisesSetsData(exerciseSets = exerciseSets, temporarySets = exerciseSets.sets)
    }

    fun addBackOffSets(exerciseSets: ExerciseSets, backOffSets: List<SetModel>) {
        for (index in backOffSets.indices) {
            exerciseSets.sets.add(backOffSets[index].copy(lazyListItemID = newID()))
        }

        updateExercisesSetsData(exerciseSets = exerciseSets, temporarySets = exerciseSets.sets)
    }

    fun updateExerciseAutoRestTimer(tabRowState: Int, exercise: ExerciseModel, time: Duration) {
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

    fun addExerciseNote(note: ExerciseNote) {
        //        TODO Save Exercise Note to Database when Workout is saved
    }

    fun getExerciseNoteByExercise(exerciseID: Long): StateFlow<List<ExerciseNote>> {
        val exerciseNotes: MutableStateFlow<List<ExerciseNote>> = MutableStateFlow(emptyList())

        viewModelScope.launch {
            exerciseNotesRepository.getExerciseNotes(exerciseID).collect() {
                exerciseNotes.value = it
            }
        }

        return exerciseNotes.asStateFlow()
    }

    fun getWorkoutNotes(): StateFlow<List<ExerciseNote>> {
        val workoutNotes: MutableStateFlow<List<ExerciseNote>> = MutableStateFlow(emptyList())

        viewModelScope.launch {
            // TODO get Workout Notes
        }

        return workoutNotes.asStateFlow()
    }

    fun createWorkoutFromHistoryOrTemplate(workout: Workout) {

        val newWorkout =
            Workout(
                uuid = workout.uuid,
                name = workout.name,
                workoutAuthor = workout.workoutAuthor,
                day = workout.day,
                programID = workout.programID,
                mesocycleID = workout.mesocycleID,
                microcycleID = workout.microcycleID
            )

        var newExerciseSets: List<WorkoutWithSetsAndExercise> = emptyList()
        var exercisesSetsMap: Map<ExerciseModel, List<SetsWithExercise>> = emptyMap()

        val exercisesSets: MutableList<ExerciseSets> = mutableListOf()

        viewModelScope.launch {
            newExerciseSets = exerciseSetsRepository.getSetsAndExerciseByWorkout(workout.workoutID)
        }

        newExerciseSets.groupBy {
            it.sets.forEach { setsWithExercise -> setsWithExercise.exercise.exerciseID }
        }

        newExerciseSets.forEach() { workoutWithSetsAndExercise ->
            exercisesSetsMap = workoutWithSetsAndExercise.sets.groupBy { it.exercise }
        }

        exercisesSetsMap.forEach() {
            exercisesSets.add(
                ExerciseSets(
                    workoutID = newWorkout.workoutID,
                    workoutUUID = newWorkout.uuid,
                    exercise = it.key,
                    sets = MutableList(it.value.size) { i -> it.value[i].set },
                )
            )
        }

        exercisesSets.forEach { exerciseSets -> exerciseSets.resetSets(newWorkout) }
        _workoutData = newWorkout
        _exercisesSetsData = exercisesSets
    }

    /*    private val exercisesSelectedToBeAddedToWorkout: MutableSet<ExerciseModel> = mutableSetOf()

    fun selectExercise(exercise: ExerciseModel, isSelected: Boolean) {
        if (isSelected) {
            exercisesSelectedToBeAddedToWorkout.add(exercise)
        } else {
            exercisesSelectedToBeAddedToWorkout.remove(exercise)
        }
    }*/

    /*    fun cancelExerciseSelection() {
        clearExercisesSelectedToBeAddedToWorkout()
    }*/

    /*    private fun clearExercisesSelectedToBeAddedToWorkout() {
        exercisesSelectedToBeAddedToWorkout.clear()
    }*/

    /**
     * Saves the workout data to the database asynchronously. The function launches a coroutine in
     * the viewModelScope to perform the database operations. It first inserts the workout data
     * using the workoutRepository. Then, it iterates over all sets in the _workoutData and inserts
     * each set into the database using the setsRepository. The function does not return any value.
     */
    fun saveWorkout() {
        viewModelScope.launch {
            workoutRepository.insertWorkout(_workoutData)

            allSets().forEach {
                if (it.setCompleted) {
                    setsRepository.insertSetModel(it)
                }
            }
        }
    }

    fun finishWorkout() {
        stopWorkoutTimer()
        saveWorkout()
    }
}

private fun initWorkout(): Workout {
    return Workout(name = "Test Workout Name")
}

private fun initExercisesSets(): MutableList<ExerciseSets> {
    val exercise =
        ExerciseModel(
            equipmentID = "null",
            force = "null",
            mechanicsID = "null",
            muscleGroupID = "null"
        )

    val workoutUUID = UUID.randomUUID()
    val workoutID = fakeULID()

    return MutableList(1) {
        ExerciseSets(
            workoutID = workoutID,
            workoutUUID = workoutUUID,
            exercise = exercise,
            sets =
            MutableList(3) { indices ->
                SetModel(
                    setNumber = indices,
                    exerciseWeight = 100.00.times(it),
                    repetitions = 10.times(it),
                    exerciseID = exercise.exerciseID,
                    exerciseUUID = exercise.uuid,
                    workoutID = workoutID, // TODO Replace with workout.workoutID
                    workoutUUID = workoutUUID,
                    progressionSchemeID =
                    Random()
                        .nextInt(), // TODO Replace with
                    // progressionScheme.progressionSchemeID
                )
            }
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

    private val handler: Handler =
        object : Handler(Looper.getMainLooper()) {
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
