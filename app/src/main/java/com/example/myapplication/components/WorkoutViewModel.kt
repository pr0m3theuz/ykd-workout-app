package com.example.myapplication.components

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.R
import com.example.myapplication.components.setRestTimer.TimerState
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.WorkoutNote
import com.example.myapplication.data.model.addWarmUpSets
import com.example.myapplication.data.model.convertExerciseExerciseSetsToExerciseSets
import com.example.myapplication.data.model.setComplete
import com.example.myapplication.data.model.updateSetRIR
import com.example.myapplication.data.model.updateSetRPE
import com.example.myapplication.data.model.updateSetReps
import com.example.myapplication.data.model.updateSetType
import com.example.myapplication.data.model.updateSetWeight
import com.example.myapplication.data.network.ExerciseNotesRepository
import com.example.myapplication.data.network.ExercisesRepository
import com.example.myapplication.data.network.UserRepository
import com.example.myapplication.data.network.WorkoutNotesRepository
import com.example.myapplication.data.network.WorkoutRepository
import com.example.myapplication.utils.AlarmReceiver
import com.example.myapplication.utils.newID
import com.example.myapplication.utils.sendNotification
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.time.LocalTime
import java.util.Random
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private const val WARM_UP_SET = 2

class WorkoutViewModel(
  private val application: Application,
  private val workoutRepository: WorkoutRepository,
  //  private val setsRepository: SetsRepository,
  //  private val exerciseSetsRepository: ExerciseSetsRepository,
  private val exerciseRepository: ExercisesRepository,
  private val workoutNotesRepository: WorkoutNotesRepository,
  private val exerciseNotesRepository: ExerciseNotesRepository,
  userRepository: UserRepository,
) : ViewModel() {

  //    fun currentTimeMillis(): Long = System.currentTimeMillis()
  //    fun elapsedTimeMillis(): Long = System.
  private val REQUEST_CODE = 0
  private val notifyPendingIntent: PendingIntent

  private var workoutDuration: Long = 0
  private var pausedElapsedTime: Long = 0
  private var _isWorkoutCountUpTimerPaused = MutableStateFlow<Boolean>(true)
  val isWorkoutCountUpTimerPaused: StateFlow<Boolean>
    get() = _isWorkoutCountUpTimerPaused.asStateFlow()

  private var _workoutDurationFormatted =
    workoutDuration.toDuration(DurationUnit.MILLISECONDS).toComponents { hours, minutes, seconds, _
      ->
      if (hours > 0) {
        "$hours:" + minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
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

  // TODO Create WorkoutCountUpTimer State Object to use to manage timer TextButton
  fun startWorkoutTimer() {
    workoutCountUpTimer.start(pausedElapsedTime)
    _isWorkoutCountUpTimerPaused.value = false
  }

  fun pauseWorkoutTimer() {
    workoutCountUpTimer.stop()
    pausedElapsedTime = workoutDuration
    _isWorkoutCountUpTimerPaused.value = true
  }

  //  Rest Timer
  private val notificationManager =
    ContextCompat.getSystemService(
      application,
      NotificationManager::class.java,
    ) as NotificationManager

  private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

  private val notifyIntent = Intent(application, AlarmReceiver::class.java)

  val userDefaultWeightUnit: String =
    userRepository.getUser()?.defaultWeightUnit ?: MassUnit.POUNDS.abbreviation

  init {
    //    startWorkoutTimer()
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

  // TODO Store User Settings for Rest Timer Adjustments
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

  @RequiresApi(Build.VERSION_CODES.S)
  private fun createTimer(timerDuration: Long) {
    val timerVersion = System.currentTimeMillis().toString()
    val alarmTriggerTime = SystemClock.elapsedRealtime() + timerDuration

    if (alarmManager.canScheduleExactAlarms()) {
      alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.ELAPSED_REALTIME_WAKEUP,
        alarmTriggerTime,
        notifyPendingIntent
      )
    } else {
      startActivity(
        application.applicationContext,
        Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM),
        null
      )
    }

    viewModelScope.launch {
      timer =
        object : CountDownTimer(timerDuration, 1000L) {

            override fun onTick(millisUntilFinished: Long) {
              _state = TimerState.RUNNING
              _timerState.value = TimerState.RUNNING
              _remainingTime = millisUntilFinished
              _remainingTimeString.value =
                _remainingTime.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes,
                                                                                    seconds,
                                                                                    _ ->
                  "$minutes:${seconds.toString().padStart(2, '0')}"
                }

              _timerDurationString.value =
                _timerDuration.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes,
                                                                                    seconds,
                                                                                    _ ->
                  "$minutes:${seconds.toString().padStart(2, '0')}"
                }

              _remainingTimeProgress.value = _remainingTime.toFloat().div(_timerDuration.toFloat())

              /*              Log.i("Timer: ", timerVersion)
              Log.i("TimerState: ", _timerState.value.name)

              Log.i("onTick(): _remainingTime", "$millisUntilFinished")
              Log.i("onTick(): timerDuration", "$_timerDuration")
              Log.i(
                "onTick(): _remainingTimeProgress",
                "${_remainingTime.toFloat().div(_timerDuration.toFloat())}",
              )*/
            }

            override fun onFinish() {
              _state = TimerState.EXPIRED
              _timerState.value = TimerState.EXPIRED
              _timerFinished = true

              //              Log.i("Timer: ", timerVersion)
              //              Log.i("TimerState: ", _timerState.value.name)
            }
        }
          .start()
    }
  }

  private fun createRestTimerNotification() {
    //    Log.i("createTimer()", "called")
    //    Log.i("elapsedRealtime()", SystemClock.elapsedRealtime().toString())

    notificationManager.sendNotification(
      application.getString(R.string.rest_timer_running_text),
      application
    )
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun startRestTimer(time: Duration) {
    //    Log.i("startRestTimer() ", "called")
    _timerDuration = time.inWholeMilliseconds

    //    createRestTimerNotification()
    createTimer(_timerDuration)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun startLastTimer() {
    //    createRestTimerNotification()
    createTimer(_timerDuration)
  }

  private var completedSets = mutableListOf<RealmUUID>()

  @RequiresApi(Build.VERSION_CODES.S)
  fun startSetCompletedTimer(exerciseSet: ExerciseExerciseSets, setUUID: RealmUUID) {
    //    Log.i("startSetCompletedTimer() ", "called")

    val exercise = exerciseSet.exercise
    val set = exerciseSet.sets.first { it.uuid == setUUID }

    if (!completedSets.contains(setUUID)) {
      completedSets.add(setUUID)
      /*
       * Doesn't allow the new completed set to cancel the timer if one is already running
       */
      if (exercise != null && _state != TimerState.RUNNING) {
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
      _remainingTime.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _ ->
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
    //    TODO Bug: lateinit property timer has not been initialized

    if (this::timer.isInitialized) {
      timer.cancel()
      resetTimerState()
    }
    //    notificationManager.cancelNotifications()
    //    alarmManager.cancel(notifyPendingIntent)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun moreRestTime() {
    _remainingTime += _restTimerAdjustment
    _timerDuration += _restTimerAdjustment
    timer.cancel()
    alarmManager.cancel(notifyPendingIntent)
    createTimer(_remainingTime)
  }

  @RequiresApi(Build.VERSION_CODES.S)
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

  private var _completedWorkout = MutableStateFlow<Workout?>(null)
  val completedWorkout
    get() = _completedWorkout.asStateFlow()

  //    Workout
  private lateinit var initialWorkoutData: Workout
  private var _workoutData: Workout = initWorkout()
  private var _workout = MutableStateFlow(_workoutData)
  val workout
    get() = _workout.asStateFlow()

  private var _workoutName: MutableStateFlow<String?> = MutableStateFlow(_workoutData.name)
  val workoutName
    get() = _workoutName.asStateFlow()

  fun setWorkoutName(newName: String) {
    _workoutData.name = newName
    _workout.value = _workoutData
    _workout.value = _workoutData
  }

  fun forceInitWorkoutData() {
    if (this::initialWorkoutData.isInitialized) {
      _workoutData = initialWorkoutData
      _workout.value = _workoutData
      _workoutName.value = initialWorkoutData.name
    }
  }

  fun initWorkoutData() {
    if (!this::initialWorkoutData.isInitialized) {
      initialWorkoutData = initWorkout()
      _workoutData = initialWorkoutData
      _workout.value = _workoutData
    }
  }

  fun getExerciseByID(exerciseID: ObjectId?): Exercise? {
    return exerciseRepository.getExercise(exerciseID)
  }

  fun getWorkoutByID(workoutID: ObjectId) {
    if (!this::initialWorkoutData.isInitialized) {
      initialWorkoutData = workoutRepository.getWorkoutByID(workoutID = workoutID) ?: initWorkout()
      // TODO Add logic for when copying from a template (active program) vs when copying from a
      // previous workout
      copyWorkoutFromWorkoutTemplate(initialWorkoutData)
      //      copyWorkoutFromPreviousWorkout(initialWorkoutData)

      //      _workoutName.value = initialWorkoutData.name
      //      _workoutData = initialWorkoutData
      //      _workout.value = _workoutData
      //      _exercisesSetsData = initialWorkoutData.exercisesSets
      //      _exercisesSets.value = _exercisesSetsData.toList()
    }
  }

  fun copyWorkoutFromWorkoutTemplate(workoutTemplate: Workout) {
    val newWorkout =
      Workout().apply {
        _id = workoutTemplate._id
        uuid = workoutTemplate.uuid
        name = workoutTemplate.name
        workoutAuthor = workoutTemplate.workoutAuthor
        //        notes = workoutTemplate.notes.filter { it.pinned }.toRealmList()
        workoutType = workoutTemplate.workoutType
        dateCreated = workoutTemplate.dateCreated
        workoutStarted = RealmInstant.now()
        day = workoutTemplate.day
        //        exercisesSets =
        // copyExercisesSetsFromPreviousWorkout(workoutTemplate.exercisesSets)
        microcycleID = workoutTemplate.microcycleID
        programID = workoutTemplate.programID
      }
    _workoutName.value = newWorkout.name
    _workoutData = newWorkout
    _workout.value = _workoutData

    getPinnedWorkoutNotes(workoutTemplate.uuid)
    _exercisesSetsData =
      copyExercisesSetsFromPreviousWorkout(workoutTemplate.exercisesSets, newWorkout._id)
    _exercisesSets.value = _exercisesSetsData.toList()
  }

  fun copyWorkoutFromPreviousWorkout(previousWorkout: Workout) {
    val newWorkout =
      Workout().apply {
        uuid = previousWorkout.uuid
        name = previousWorkout.name
        workoutAuthor = previousWorkout.workoutAuthor
        //        notes = previousWorkout.notes.filter { it.pinned }.toRealmList()
        workoutType = previousWorkout.workoutType
        workoutStarted = RealmInstant.now()
        day = previousWorkout.day
        //        exercisesSets =
        // copyExercisesSetsFromPreviousWorkout(previousWorkout.exercisesSets)
        microcycleID = previousWorkout.microcycleID
      }

    _workoutName.value = newWorkout.name
    _workoutData = newWorkout
    _workout.value = _workoutData

    getPinnedWorkoutNotes(previousWorkout.uuid)
    _exercisesSetsData =
      copyExercisesSetsFromPreviousWorkout(previousWorkout.exercisesSets, newWorkout._id)
  }

  private fun copyExercisesSetsFromPreviousWorkout(
    previousExercisesSets: RealmList<ExerciseSets>,
    _workoutID: ObjectId
  ): MutableList<ExerciseExerciseSets> {
    val newExercisesSets = mutableListOf<ExerciseExerciseSets>()
    previousExercisesSets.forEach { exerciseSets ->
      //      TODO get exercise by ID
      val newExerciseExerciseSets =
        ExerciseExerciseSets().apply {
          exSetsUUID = exerciseSets.exSetsUUID
          exerciseOrder = exerciseSets.exerciseOrder
          exercise = exerciseRepository.getExercise(exerciseSets.exerciseID)
          sets = copySetsFromPreviousWorkout(exerciseSets.sets)
          notes = exerciseSets.notes.filter { it.pinned }.toRealmList()
          workoutID = _workoutID
        }

      if (exerciseSets.exerciseID != null) {
        newExerciseExerciseSets.notes =
          exerciseNotesRepository.getPinnedExerciseNotes(exerciseSets.exerciseID!!).toRealmList()
      }

      newExercisesSets.add(newExerciseExerciseSets)
    }

    return newExercisesSets
  }

  private fun copySetsFromPreviousWorkout(previousSets: RealmList<Set>): RealmList<Set> {
    // TODO Think out how to get Previous Set from the last time the exercise was performed and what
    // data structure to store them in

    val newSets = realmListOf<Set>()
    val latestPreviousSets = realmListOf<Set>()
    previousSets.forEach { set ->
      val newSet =
        Set().apply {
          uuid = set.uuid
          setNumber = set.setNumber
          weightPounds = set.weightPounds
          weightKilograms = set.weightKilograms
          repetitions = set.repetitions
          ratePerceivedEffort = set.ratePerceivedEffort
          repetitionsInReserve = set.repetitionsInReserve
          targetWeight = set.targetWeight
          targetRepetitions = set.targetRepetitions
          targetRatePerceivedEffort = set.targetRatePerceivedEffort
          targetRepetitionsInReserve = set.targetRepetitionsInReserve
          percentage1RM = set.percentage1RM
          setTypeID = set.setTypeID
          progressionSchemeID = set.progressionSchemeID
        }
      newSets.add(newSet)
      if (set.exerciseID != null && set.setNumber != null) {
        val previousSet =
          workoutRepository.getPreviousSetForExercise(set.exerciseID!!, set.setNumber!!)
        previousSet?.let { latestPreviousSets.add(it) }
      }
    }

    return newSets
  }

  fun populatePreviousSetField(exerciseID: ObjectId, setNumber: Int): Set? {
    return workoutRepository.getPreviousSetForExercise(exerciseID, setNumber)
  }

  private var _workoutNotesData: MutableList<WorkoutNote> = mutableListOf()
  private var _workoutNotes: MutableStateFlow<List<WorkoutNote>> =
    MutableStateFlow(_workoutNotesData.toList())
  val workoutNotes
    get() = _workoutNotes.asStateFlow()

  private fun getPinnedWorkoutNotes(workoutUUID: RealmUUID) {
    _workoutNotesData = workoutNotesRepository.getPinnedWorkoutNotes(workoutUUID).toMutableList()
    _workoutNotesData.forEach { workoutNote -> workoutNote.workout = _workout.value._id }
    _workoutNotes.value = _workoutNotesData
  }

  fun addWorkoutNote() {
    _workoutNotesData.add(
      WorkoutNote().apply {
        workout = _workout.value._id
        workoutUUID = _workout.value.uuid
        //        workoutID = _workout.value.workoutID,
        //        programID = _workout.value.programID,
        //        mesocycleID = _workout.value.mesocycleID,
        //        microcycleID = _workout.value.microcycleID
      }
    )

    _workoutNotes.value = _workoutNotesData.toList()
  }

  fun removeWorkoutNote(workoutNote: WorkoutNote) {
    _workoutNotesData = _workoutNotesData.filter { it != workoutNote }.toMutableList()
    _workoutNotes.value = _workoutNotesData.toList()
  }

  fun setWorkoutNote(workoutNote: WorkoutNote, note: String) {
    val index = _workoutNotesData.indexOf(workoutNote)
    _workoutNotesData[index].note = note

    _workoutNotes.value = _workoutNotesData.toList()
  }

  fun pinWorkoutNote(workoutNote: WorkoutNote, pinned: Boolean) {
    val index = _workoutNotes.value.indexOf(workoutNote)
    _workoutNotes.value[index].pinned = pinned
  }

  fun updateWorkoutNotes(note: WorkoutNote) {
    _workoutNotesData.first { it == note }.lazyListID = newID()
    _workoutNotes.value = _workoutNotesData.toList()
  }

  private fun stopWorkoutTimer() {
    workoutCountUpTimer.stop()
    _workoutData.workoutEnded = RealmInstant.now()
    _workoutData.workoutDuration = workoutDuration
    _workout.value = _workoutData
    // TODO Navigate to Workout Complete Screen & Summary
  }

  //    ExerciseExerciseSets
  private var _exercisesSetsData = mutableListOf<ExerciseExerciseSets>()

  private var _exercisesSets = MutableStateFlow(_exercisesSetsData.toList())
  val exercisesSets
    get() = _exercisesSets.asStateFlow()

  //  TODO What is this used for or purpose, Workout Summary?
  private var _allSetsData: MutableList<Set> = allSets()
  private var _allSets = MutableStateFlow(_allSetsData.toList())
  val allSets
    get() = _allSets.asStateFlow()

  fun addMultipleEmptyExerciseSets(exercisesSelectedToBeAddedToWorkout: List<Exercise>) {
    val temporaryExerciseExerciseSets = _exercisesSets.value.toMutableList()

    exercisesSelectedToBeAddedToWorkout.forEach { _exercise ->
      val pinnedNotes = exerciseNotesRepository.getPinnedExerciseNotes(_exercise._id)

      temporaryExerciseExerciseSets.add(
        ExerciseExerciseSets().apply {
          //          workoutID = _workout.value.workoutID
          //          workoutUUID = _workout.value.uuid
          exerciseOrder =
            if (temporaryExerciseExerciseSets.isEmpty()) 1
            else temporaryExerciseExerciseSets.last().exerciseOrder.plus(1)
          exercise = _exercise
          notes = pinnedNotes.toRealmList()
          datePerformed = RealmInstant.now()
          workoutID = _workout.value._id
        }
      )
    }

    _exercisesSetsData = temporaryExerciseExerciseSets
    _exercisesSets.value = _exercisesSetsData.toList()
  }

  fun removeExerciseSets(exerciseSets: ExerciseExerciseSets) {
    _exercisesSetsData = _exercisesSets.value.filter { it != exerciseSets }.toMutableList()
    _exercisesSets.value = _exercisesSetsData
  }

  fun addSet(exerciseSets: ExerciseExerciseSets) {
    val temporarySets: List<Set> =
      if (exerciseSets.sets.isNotEmpty()) {
        exerciseSets.sets +
                Set().apply {
                  setNumber = exerciseSets.sets.last().setNumber?.plus(1)
                  weightPounds = exerciseSets.sets.last().weightPounds
                  weightKilograms = exerciseSets.sets.last().weightKilograms
                  repetitions = exerciseSets.sets.last().repetitions
                  ratePerceivedEffort = exerciseSets.sets.last().ratePerceivedEffort
                  repetitionsInReserve = exerciseSets.sets.last().repetitionsInReserve
                  setTypeID = exerciseSets.sets.last().setTypeID
                  percentage1RM = exerciseSets.sets.last().percentage1RM
                }
      } else {
        listOf(
          Set().apply {
            setNumber = 0
            progressionSchemeID = Random().nextInt() // TODO Replace with
            // progressionScheme.progressionSchemeID
          }
        )
      }

    updateExercisesSetsData(exerciseSets, temporarySets)
  }

  fun removeSet(exerciseSets: ExerciseExerciseSets, set: Set) {
    val temporarySets = exerciseSets.sets.minusElement(set)

    updateExercisesSetsData(exerciseSets, temporarySets)
  }

  private fun updateExercisesSetsData(
    exerciseSets: ExerciseExerciseSets,
    temporarySets: List<Set>,
  ) {
    exerciseSets.sets = temporarySets.toRealmList()

    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun addExerciseNote(exerciseSets: ExerciseExerciseSets) {
    exerciseSets.notes.add(
      ExerciseNote().apply {
        exercise = exerciseSets.exercise?._id
        workout = _workout.value._id
      }
    )

    Log.i("addExerciseNote", "$exerciseSets.notes")

    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
  }

  fun removeExerciseNote(exerciseSets: ExerciseExerciseSets, exerciseNote: ExerciseNote) {
    exerciseSets.notes.minusElement(exerciseNote)

    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
    Log.i("removeExerciseNote", "removeExerciseNote: ${exerciseSets.notes.size}")
  }

  fun pinExerciseNote(
    exerciseSets: ExerciseExerciseSets,
    exerciseNote: ExerciseNote,
    pinned: Boolean
  ) {
    val index = exerciseSets.notes.indexOf(exerciseNote)

    exerciseSets.notes[index].pinned = pinned

    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
  }

  private fun updateExercisesNotesData(
    exerciseSets: ExerciseExerciseSets,
    temporaryNotes: List<ExerciseNote>,
  ) {
    exerciseSets.notes = temporaryNotes.toRealmList()

    updateExerciseExerciseSetsData(exerciseSets)
  }

  private fun updateExerciseExerciseSetsData(exerciseSets: ExerciseExerciseSets) {
    val index = _exercisesSetsData.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }

    exerciseSets.exSetsLazyListItemID = newID()

    _exercisesSetsData[index] =
      ExerciseExerciseSets().apply {
        exSetsUUID = exerciseSets.exSetsUUID
        exerciseOrder = exerciseSets.exerciseOrder
        exercise = exerciseSets.exercise
        sets = exerciseSets.sets
        notes = exerciseSets.notes
      }
    _allSets.value = allSets().toList()
    _exercisesSets.value = _exercisesSetsData.toMutableList()
  }

  private fun allSets(): MutableList<Set> {
    val setsList = mutableListOf<Set>()
    _exercisesSetsData.forEach { exerciseSet ->
      exerciseSet.sets.forEach { set -> setsList.add(set) }
    }

    return setsList
  }

  private fun allExerciseNotes(): MutableList<ExerciseNote> {
    val notesList = mutableListOf<ExerciseNote>()
    _exercisesSetsData.forEach { exerciseSet ->
      exerciseSet.notes.forEach { note -> notesList.add(note) }
    }

    notesList.removeAll { it.note.isNullOrBlank() }

    return notesList
  }

  fun getSetData(setUUID: RealmUUID): Set {
    return allSets().first { it.uuid == setUUID }
  }

  fun updateSetWeight(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newWeight: Double?) {
    val weightUnit = exerciseSets.exercise?.weightUnit ?: userDefaultWeightUnit
    exerciseSets.updateSetWeight(uuid = setUUID, newWeight = newWeight, weightUnit = weightUnit)

    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetReps(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newReps: Int?) {
    exerciseSets.updateSetReps(uuid = setUUID, newReps = newReps)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRIR(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newRIR: Double?) {
    exerciseSets.updateSetRIR(uuid = setUUID, newRIR = newRIR)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRPE(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, newRPE: Double?) {
    exerciseSets.updateSetRPE(uuid = setUUID, newRPE = newRPE)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun setComplete(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, setComplete: Boolean) {
    exerciseSets.setComplete(setUUID, setComplete)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetType(exerciseSets: ExerciseExerciseSets, setUUID: RealmUUID, setTypeID: Int) {
    exerciseSets.updateSetType(uuid = setUUID, setTypeID = setTypeID)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun addWarmUpSets(exerciseSets: ExerciseExerciseSets, warmUpSets: List<Set>) {
    exerciseSets.addWarmUpSets(warmUpSets)

    updateExercisesSetsData(exerciseSets, exerciseSets.sets)
  }

  fun getWorkingSetWeight(exerciseSets: ExerciseExerciseSets): Double? {
    // TODO Get Previous Working Set if Sets is empty

    return when (exerciseSets.exercise?.weightUnit) {
      MassUnit.KILOGRAMS.abbreviation ->
        exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightKilograms

      MassUnit.POUNDS.abbreviation ->
        exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightPounds

      else -> {
        if (userDefaultWeightUnit == MassUnit.KILOGRAMS.abbreviation) {
          exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightKilograms
        } else {
          exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightPounds
        }
      }
    }

    //    return exerciseSets.sets.firstOrNull { it.setTypeID != WARM_UP_SET }?.weightPounds
  }

  fun getWeightUnit(exerciseSets: ExerciseExerciseSets): String {
    return if (exerciseSets.exercise?.weightUnit != null) {
      exerciseSets.exercise?.weightUnit ?: MassUnit.POUNDS.abbreviation
    } else {
      userDefaultWeightUnit
    }
  }

  fun getWorkingSetRepetitions(exerciseSets: ExerciseExerciseSets): Int? {
    // TODO Get Previous Working Set if Sets is empty
    var repetitions: Int? = null
    if (exerciseSets.sets.isNotEmpty()) {
      repetitions = exerciseSets.sets.first { it.setTypeID != WARM_UP_SET }.repetitions
    }
    return repetitions
  }

  fun addWorkingSets(exerciseSets: ExerciseExerciseSets, workingSets: List<Set>) {
    workingSets.forEach { it.lazyListItemID = newID() }

    exerciseSets.sets.removeAll { it.setTypeID != WARM_UP_SET }

    exerciseSets.sets.addAll(workingSets)

    updateExercisesSetsData(exerciseSets = exerciseSets, temporarySets = exerciseSets.sets)
  }

  fun addBackOffSets(exerciseSets: ExerciseExerciseSets, backOffSets: List<Set>) {
    for (index in backOffSets.indices) {
      exerciseSets.sets.add(
        Set().apply {
          setNumber = exerciseSets.sets.last().setNumber?.plus(1)
          weightPounds = backOffSets[index].weightPounds
          weightKilograms = backOffSets[index].weightKilograms
          repetitions = backOffSets[index].repetitions
          ratePerceivedEffort = backOffSets[index].ratePerceivedEffort
          repetitionsInReserve = backOffSets[index].repetitionsInReserve
          setTypeID = backOffSets[index].setTypeID
          percentage1RM = backOffSets[index].percentage1RM
        }
        //        backOffSets[index].copy(lazyListItemID = newID())
      )
    }

    updateExercisesSetsData(exerciseSets = exerciseSets, temporarySets = exerciseSets.sets)
  }

  fun updateExerciseAutoRestTimer(tabRowState: Int, exercise: Exercise, time: Duration) {
    if (tabRowState == 1) {
      exercise.warmUpSetRestInWholeMilliseconds = time.inWholeMilliseconds
    } else if (tabRowState == 0) {
      exercise.workingSetRestInWholeMilliseconds = time.inWholeMilliseconds
    }
  }

  fun enableExerciseAutoRestTimer(tabRowState: Int, exercise: Exercise, enable: Boolean) {
    if (tabRowState == 1) {
      exercise.warmUpSetRestTimerEnabled = enable
    } else if (tabRowState == 0) {
      exercise.workingSetRestTimerEnabled = enable
    }
  }

  fun checkAllSetsComplete(): Boolean {
    var countSets: Int = 0
    var countSetsCompleted: Int = 0

    _exercisesSetsData.forEach { exerciseSets ->
      exerciseSets.sets.forEach { set ->
        countSets++
        if (set.setCompleted) {
          countSetsCompleted++
        }
      }
    }

    return countSetsCompleted == countSets && countSets > 0
  }

  fun saveExerciseNote(note: ExerciseNote) {
    //        TODO Save Exercise Note to Database when Workout is saved
  }

  fun saveWorkoutNote(note: WorkoutNote) {
    //        TODO Save Workout Notes to Database when Workout is saved
  }

  fun saveExerciseNotes() {
    //      TODO Save Exercise Notes to Database when Workout is saved
  }

  /*
    private fun getPinnedExerciseNotesByExercise(
      exercise: Exercise
    ): RealmList<ExerciseNote> {  }
  */

  /**
   * Saves the workout data to the database asynchronously. The function launches a coroutine in the
   * viewModelScope to perform the database operations. It first inserts the workout data using the
   * workoutRepository. Then, it iterates over all sets in the _workoutData and inserts each set if
   * it is completed into the database using the setsRepository. The function does not return any
   * value.
   */

  /**
   * Saves the workout data to the database asynchronously. The function launches a coroutine in the
   * viewModelScope to perform the database operations. It first inserts the workout data using the
   * workoutRepository. Then, it iterates over all sets in the _workoutData and inserts each set if
   * it is completed into the database using the setsRepository. The function does not return any
   * value.
   */
  private fun saveWorkout() {
    //    TODO Implement this
    //
    _workoutData.notes = _workoutNotesData.toRealmList()
    //    TODO Only Add Completed Sets, Implement Check ond Remove Uncompleted Sets
    removeUncompletedSets()

    _workoutData.exercisesSets =
      convertExerciseExerciseSetsToExerciseSets(_exercisesSetsData, userDefaultWeightUnit)

    viewModelScope.launch { workoutRepository.insertWorkout(_workoutData) }
  }

  fun getCompletedWorkout(workoutID: ObjectId) {
    _completedWorkout.value = workoutRepository.getWorkoutByID(workoutID)
  }

  fun getWorkoutsCount(): Int {
    return workoutRepository.getWorkoutsCount()
  }

  fun finishWorkout() {
    //    TODO Navigate to Workout Summary Screen
    cancelSetCompletedTimer()
    stopWorkoutTimer()
    saveWorkout()
  }

  fun cancelWorkout() {
    //    TODO Pop workout screen of the backstack inclusively
    //    TODO navController.popBackStack( route = Programs.route, inclusive = true)
    cancelSetCompletedTimer()
    stopWorkoutTimer()
    _workoutData = initWorkout()
    _exercisesSetsData = mutableListOf()
    _exercisesSets.value = _exercisesSetsData
  }

  private fun removeUncompletedSets() {
    //    TODO Doesn't Work, FIX
    val newData = _exercisesSetsData

    newData.forEach { it.sets.removeIf { set -> !set.setCompleted } }
    newData.filter { it.sets.isNotEmpty() }
    _exercisesSetsData = newData
    _exercisesSets.value = _exercisesSetsData
  }

  fun replaceExerciseInExerciseSets(exerciseSets: ExerciseExerciseSets, newExercise: Exercise) {
    val index = _exercisesSetsData.indexOfFirst { it.exSetsUUID == exerciseSets.exSetsUUID }

    _exercisesSetsData[index].exercise = newExercise
    _exercisesSetsData[index].exSetsLazyListItemID = newID()

    _exercisesSets.value = _exercisesSetsData
  }
}

private fun initWorkout(): Workout {
  val currentTime = LocalTime.now()

  return when (currentTime.hour) {
    in 0..11 -> {
      Workout().apply {
        name = "Morning Workout"
        dateCreated = RealmInstant.now()
        workoutStarted = RealmInstant.now()
      }
    }
    in 12..17 -> {
      Workout().apply {
        name = "Afternoon Workout"
        dateCreated = RealmInstant.now()
        workoutStarted = RealmInstant.now()
      }
    }
    else -> {
      Workout().apply {
        name = "Evening Workout"
        dateCreated = RealmInstant.now()
        workoutStarted = RealmInstant.now()
      }
    }
  }
}

/*private fun initExercisesSets(): MutableList<ExerciseExerciseSets> {
  val exercise =
    Exercise(
      equipmentID = "null",
      force = "null",
      mechanicsID = "null",
      muscleGroupID = "null"
    )

  val workoutUUID = RealmUUID.random()
  val workoutID = fakeULID()

  return MutableList(1) {
    ExerciseExerciseSets(
        workoutID = workoutID,
        workoutUUID = workoutUUID,
        exercise = exercise,
        sets =
        MutableList(3) { indices ->
          Set(
            setNumber = indices,
            exerciseWeight = 100.00.times(it),
            repetitions = 10.times(it),
            exerciseID = exercise.exerciseID,
            exerciseUUID = exercise.uuid,
            workoutID = workoutID, // TODO Replace with workout.workoutID
            workoutUUID = workoutUUID,
            progressionSchemeID = Random().nextInt(), // TODO Replace with
            // progressionScheme.progressionSchemeID
          )
        },
        notes = mutableListOf()
    )
  }
}*/

abstract class CountUpTimer(private val interval: Long) {
  //  TODO Figure out to old elapsed time to new elapsed time
  private var base: Long = 0
  private var oldElapsedTime: Long = 0

  fun start(elapsedTime: Long) {
    base = SystemClock.elapsedRealtime()
    oldElapsedTime = elapsedTime
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
          val elapsedTime = SystemClock.elapsedRealtime() - base + oldElapsedTime
          onTick(elapsedTime)
          sendMessageDelayed(obtainMessage(MSG), interval)
        }
      }
    }

  companion object {
    private const val MSG = 1
  }
}
