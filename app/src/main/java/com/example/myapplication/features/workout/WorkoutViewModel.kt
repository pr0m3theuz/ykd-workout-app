@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout

//import com.example.myapplication.data.model.ListExerciseExerciseSets
import android.app.AlarmManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import android.os.CountDownTimer
import android.os.SystemClock
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.saveable.Saver
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getString
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.ExerciseNote
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.SupersetExercisesSets
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.domain.model.WorkoutNote
import com.example.myapplication.common.domain.model.addWarmUpSets
import com.example.myapplication.common.domain.model.completeSet
import com.example.myapplication.common.domain.model.updateSetRIR
import com.example.myapplication.common.domain.model.updateSetRPE
import com.example.myapplication.common.domain.model.updateSetReps
import com.example.myapplication.common.domain.model.updateSetType
import com.example.myapplication.common.domain.model.updateSetWeight
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.util.AlarmReceiver
import com.example.myapplication.common.util.cancelNotifications
import com.example.myapplication.common.util.newLazyListItemID
import com.example.myapplication.common.util.sendNotification
import com.example.myapplication.features.workout.feature.setRestTimer.TimerState
import com.google.firebase.perf.metrics.AddTrace
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
import java.time.LocalTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


const val WORKOUT_DURATION = "WORKOUT_DURATION"
const val WORKOUT_PAUSED_ELAPSED_TIME = "WORKOUT_PAUSED_ELAPSED_TIME"
const val TIMER_DURATION = "TIMER_DURATION"
const val REMAINING_TIMER_TIME = "REMAINING_TIMER_TIME"
const val WORKOUT_DETAILS = "WORKOUT_DETAILS"
const val WORKOUT_NOTES = "WORKOUT_NOTES"
const val EXERCISE_EXERCISE_SETS = "EXERCISE_EXERCISE_SETS"
const val IS_WORKOUT_ACTIVE = "IS_WORKOUT_ACTIVE"

@OptIn(SavedStateHandleSaveableApi::class)
class WorkoutViewModel(
  private val application: Application,
  private val manageWorkoutUseCase: ManageWorkoutUseCase,
  private val exerciseRepository: ExercisesRepository,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  private val WARM_UP_SET = SetType.warmUpSet

  var isWorkoutActive: Boolean
    get() = savedStateHandle.get<Boolean>(IS_WORKOUT_ACTIVE) ?: false
    set(value) = savedStateHandle.set(IS_WORKOUT_ACTIVE, value)

  private val REQUEST_CODE = 0
  private val notifyPendingIntent: PendingIntent

  private var workoutDuration: Long
    get() = savedStateHandle.get<Long>(WORKOUT_DURATION) ?: 0L
    set(value) = savedStateHandle.set(WORKOUT_DURATION, value)

  private var pausedElapsedTime: Long
    get() = savedStateHandle.get<Long>(WORKOUT_PAUSED_ELAPSED_TIME) ?: 0L
    set(value) = savedStateHandle.set(WORKOUT_PAUSED_ELAPSED_TIME, value)

  private var _isWorkoutCountUpTimerPaused = MutableStateFlow<Boolean>(true)

  val isWorkoutCountUpTimerPaused: StateFlow<Boolean>
    get() = _isWorkoutCountUpTimerPaused

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
    ContextCompat.getSystemService(application, NotificationManager::class.java)
            as NotificationManager

  private val name = getString(application, R.string.notification_channel_id)
  private val descriptionText = getString(application, R.string.notification_channel_id)
  private val importance = NotificationManager.IMPORTANCE_DEFAULT

  private val channel =
    NotificationChannel(name, name, importance).apply { description = descriptionText }

  private val alarmManager = application.getSystemService(Context.ALARM_SERVICE) as AlarmManager

  private val notifyIntent = Intent(application, AlarmReceiver::class.java)
  //  private val notifyIntent = MainActivity.newIntent(application)

  val userDefaultWeightUnit: String = manageWorkoutUseCase.userDefaultWeightUnit

  init {
    //    startWorkoutTimer()
    testSaveStateHandle()

    notifyPendingIntent =
      PendingIntent.getBroadcast(
        application,
        REQUEST_CODE,
        notifyIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
      )

    // TODO java.lang.NullPointerException: Attempt to invoke interface method 'java.lang.Object kotlinx.coroutines.flow.MutableStateFlow.getValue()' on a null object reference
    /*    viewModelScope.launch {
          runCatching {
            _workout.value?.let { _ ->
              _workout.collect { workout ->
                _workoutNotes.collect { workoutNotes ->
                  _workoutComponentFlow.collect { workoutComponents ->
                    runCatching {
                      workout?.let {
                        manageWorkoutUseCase.saveInProgressWorkout(
                          workout = workout,
                          workoutNotes = workoutNotes,
                          workoutComponents = workoutComponents,
                        )
                      }
                    }.onFailure {
                      // Handle error, e.g., log error, show error message
                      Log.e("saveInProgressWorkout", "Error saving in progress workout", it)
                    }
                  }
                }
              }
            }
          }.onFailure {
            // Handle error, e.g., log error, show error message
            Log.e("saveInProgressWorkout", "Error saving in progress workout", it)
          }
        }*/
  }


  @AddTrace(name = "savedStateHandle WORKOUT_DETAILS", enabled = true)
  private fun testSaveStateHandle() {
    savedStateHandle.get<String?>(WORKOUT_DETAILS)
  }

  //  private var _timerDuration: Long = 60000L
  private var _timerDuration: Long
    get() = savedStateHandle.get<Long>(TIMER_DURATION) ?: 60000L
    set(value) = savedStateHandle.set(TIMER_DURATION, value)

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

  //  private var _remainingTime: Long = 60000L
  private var _remainingTimerTime: Long
    get() = savedStateHandle.get<Long>(REMAINING_TIMER_TIME) ?: 60000L
    set(value) = savedStateHandle.set(REMAINING_TIMER_TIME, value)

  private var _remainingTimerTimeFormatted =
    _remainingTimerTime.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _ ->
      "$minutes:${
        seconds.toString().padStart(2, '0')
      }"
    }

  private val _remainingTimerTimeString = MutableStateFlow(_remainingTimerTimeFormatted)
  val remainingTimeString: StateFlow<String>
    get() = _remainingTimerTimeString.asStateFlow()

  private var timerProgress = _remainingTimerTime.div(_timerDuration).toFloat()

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
        notifyPendingIntent,
      )
    } else {
      val intent = Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
      intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
      startActivity(application.applicationContext, intent, null)
    }

    viewModelScope.launch {
      timer =
        object : CountDownTimer(timerDuration, 1000L) {

          override fun onTick(millisUntilFinished: Long) {
            _state = TimerState.RUNNING
            _timerState.value = TimerState.RUNNING
            _remainingTimerTime = millisUntilFinished
            _remainingTimerTimeString.value =
              _remainingTimerTime.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes,
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

            _remainingTimeProgress.value =
              _remainingTimerTime.toFloat().div(_timerDuration.toFloat())

            notificationManager.sendNotification(
              messageBody = "Remaining Time: ${_remainingTimerTimeString.value}",
              application,
              silent = true,
            )

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

            /*Log.i("Timer: ", timerVersion)
            Log.i("TimerState: ", _timerState.value.name)*/
          }
        }
          .start()
    }
  }

  private fun createRestTimerNotification() {
    //    Log.i("createTimer()", "called")
    //    Log.i("elapsedRealtime()", SystemClock.elapsedRealtime().toString())

    notificationManager.createNotificationChannel(channel)

    //    notificationManager.sendNotification("Remaining Time: ${_remainingTimerTimeString.value}",
    // application, silent = true)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun startRestTimer(time: Duration) {
    //    Log.i("startRestTimer() ", "called")
    _timerDuration = time.inWholeMilliseconds
    notificationManager.cancelNotifications()
    createRestTimerNotification()
    createTimer(_timerDuration)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun startLastTimer() {
    notificationManager.cancelNotifications()
    createRestTimerNotification()
    createTimer(_timerDuration)
  }

  private var completedSets = mutableSetOf<Long>()

  @RequiresApi(Build.VERSION_CODES.S)
  fun startSetCompletedTimer(
//    workoutComponent: WorkoutComponent, // Removed - We find the context from setUUID
    setUUID: Uuid,
    setLazyListItemID: Long,
  ) {
    //    Log.i("startSetCompletedTcimer() ", "called")
    var foundExercise: Exercise? = null
    var foundSet: Set? = null

    // Search through the workout components to find the exercise and set matching the UUID
    // Using run breaking@forEach to exit early once found
    run breaking@{
      _workoutComponents.forEach { component ->
        when (component) {
          is ExerciseExerciseSets -> {
            // Check sets directly in this component
            val set = component.sets.firstOrNull { it.uuid == setUUID }
            if (set != null) {
              foundExercise = component.exercise
              foundSet = set
              return@breaking // Exit loops, we found it
            }
          }

          is SupersetExercisesSets -> {
            // Check sets within each ExerciseExerciseSets of the superset
            component.exercisesSets.forEach innerLoop@{ innerExSets ->
              val set = innerExSets.sets.firstOrNull { it.uuid == setUUID }
              if (set != null) {
                foundExercise = innerExSets.exercise
                foundSet = set
                return@breaking // Exit loops, we found it
              }
            }
          }
        }
      }
    }

    if (foundExercise == null || foundSet == null) {
      Log.e("startSetCompletedTimer", "Could not find Exercise or Set for UUID: $setUUID")
      return
    }

    // Only allows new completed sets to start the timer once
    if (completedSets.add(setLazyListItemID)) { // Use add() which returns true if added (wasn't present)

      /*
       * Doesn't allow the new completed set to cancel the timer if one is already running
       */
      if (_state != TimerState.RUNNING) {
        if (foundExercise!!.warmUpSetRestTimerEnabled && (foundSet!!.setTypeID == WARM_UP_SET)) {
          _timerDuration = foundExercise!!.warmUpSetRestInWholeMilliseconds

          startRestTimer(_timerDuration.toDuration(DurationUnit.MILLISECONDS))
        } else if (foundExercise!!.workingSetRestTimerEnabled && (foundSet!!.setTypeID != WARM_UP_SET)) {
          _timerDuration = foundExercise!!.workingSetRestInWholeMilliseconds

          startRestTimer(_timerDuration.toDuration(DurationUnit.MILLISECONDS))
        }
        // If neither condition is met (e.g., rest timer disabled for the set type), no timer starts
      }
    }
    // If completedSets.add() returns false, it means this ID was already processed, so do nothing.
  }

  private fun resetTimerState() {
    Log.i("resetTimerState() ", "called")
    _remainingTimerTime = 0L
    _remainingTimerTimeString.value =
      _remainingTimerTime.toDuration(DurationUnit.MILLISECONDS).toComponents { minutes, seconds, _
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
    if (this::timer.isInitialized) {
      timer.cancel()
      resetTimerState()
    }
    // TODO Cancel Notification notificationManager.cancelNotifications()
    alarmManager.cancel(notifyPendingIntent)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun moreRestTime() {
    _remainingTimerTime += _restTimerAdjustment
    _timerDuration += _restTimerAdjustment
    timer.cancel()
    alarmManager.cancel(notifyPendingIntent)
    createTimer(_remainingTimerTime)
  }

  @RequiresApi(Build.VERSION_CODES.S)
  fun lessRestTime() {
    _remainingTimerTime -= _restTimerAdjustment
    _timerDuration -= _restTimerAdjustment
    timer.cancel()
    alarmManager.cancel(notifyPendingIntent)
    if (_remainingTimerTime < _timerDuration) {
      createTimer(_remainingTimerTime)
    } else {
      resetTimerState()
    }
  }

  private var _completedWorkout = MutableStateFlow<Workout?>(null)
  val completedWorkout
    get() = _completedWorkout.asStateFlow()

  //    Workout
  private lateinit var initialWorkoutData: Workout
//  private var _workoutData: Workout = initWorkout()

  /*
    private var workoutDetails: Workout
      get() = savedStateHandle.get<Workout>(WORKOUT_DETAILS) ?: initWorkout()
      set(value) = savedStateHandle.set(WORKOUT_DETAILS, value)
  */

  private var workoutDetails: Workout? =
    savedStateHandle.saveable(
      key = WORKOUT_DETAILS,
      saver =
      Saver(
        save = { Json.encodeToString(Workout.serializer(), it) },
        restore = { Json.decodeFromString(Workout.serializer(), it) },
      ),
    ) {
      initWorkout()
    }

  private var _workout = MutableStateFlow(workoutDetails)
  val workout
    get() = _workout.asStateFlow()

  private var _workoutName: MutableStateFlow<String?> = MutableStateFlow(workoutDetails!!.name)
  val workoutName
    get() = _workoutName.asStateFlow()

  fun setWorkoutName(newName: String) {
    workoutDetails!!.name = newName
    _workout.value = workoutDetails
  }

  fun forceInitWorkoutData() {
    if (this::initialWorkoutData.isInitialized) {
      workoutDetails = initialWorkoutData
      _workout.value = workoutDetails
      _workoutName.value = initialWorkoutData.name
    }
  }

  private var autoSaveJob: Job? = null // Keep track of the job

  @OptIn(FlowPreview::class)
  private fun startAutoSavingWorkout() {
    // Cancel any previous auto-save job to avoid duplicates if called multiple times
    autoSaveJob?.cancel()
    autoSaveJob = viewModelScope.launch {
      combine(
        _workout,
        _workoutNotes,
        _workoutComponentFlow
      ) { workout, notes, components ->
        Triple(workout, notes, components)
      }
        .debounce(1000.milliseconds)
        .collect { (latestWorkout, latestNotes, latestComponents) ->
          runCatching {
            Log.d("saveInProgressWorkout", "Auto-saving workout state...")
            if (latestWorkout != null) {
              manageWorkoutUseCase.saveInProgressWorkout(
                workout = latestWorkout,
                workoutNotes = latestNotes,
                workoutComponents = latestComponents
              )
            }
          }.onFailure { error ->
            Log.e("saveInProgressWorkout", "Error auto-saving in-progress workout", error)
          }
        }
    }
    Log.d("ViewModelLifecycle", "Auto-save job started.")
  }

  private fun stopAutoSavingWorkout() {
    autoSaveJob?.cancel()
    autoSaveJob = null
    Log.d("ViewModelLifecycle", "Auto-save job stopped.")
  }

  override fun onCleared() {
    // viewModelScope is automatically cancelled, but good practice to nullify the job
    autoSaveJob = null
    super.onCleared()
    Log.d("ViewModelLifecycle", "ViewModel cleared, auto-save implicitly stopped.")
  }

  fun initWorkoutData() {
    if (!this::initialWorkoutData.isInitialized && !isWorkoutActive) {
      initialWorkoutData = initWorkout()
      workoutDetails = initialWorkoutData
      _workout.value = workoutDetails
      isWorkoutActive = true

      startAutoSavingWorkout()

      testSaveStateHandle()
    }
  }

  fun getExerciseByID(exerciseID: ObjectId?): Exercise? {
    return manageWorkoutUseCase.getExerciseByID(exerciseID)
  }

  fun getWorkoutByID(workoutID: ObjectId) {
//    Log.i("WorkoutViewModel.getWorkoutByID() ", "called. WorkoutID: $workoutID")
    if (!this::initialWorkoutData.isInitialized && !isWorkoutActive) {
      initialWorkoutData = manageWorkoutUseCase.getWorkoutByID(workoutID) ?: initWorkout()
      // TODO Add logic for when copying from a template (active program) vs when copying from a
      // previous workout
      copyWorkoutFromWorkoutTemplate(initialWorkoutData)
      isWorkoutActive = true

      startAutoSavingWorkout()

      testSaveStateHandle()
    }
  }

  fun loadWorkoutFromPreviousWorkout(workoutID: ObjectId) {
    manageWorkoutUseCase.getWorkoutByID(workoutID)?.let { workout ->
      _workoutName.value = workout.name
      workoutDetails = workout
      _workout.value = workoutDetails

      getPinnedWorkoutNotes(workout.uuid, workout.id)
      setWorkoutComponents(loadWorkoutComponentsFromPreviousWorkout(workout.workoutItems))
    }
  }

  fun copyWorkoutFromWorkoutTemplate(workoutTemplate: Workout) {
    val newWorkout = manageWorkoutUseCase.copyWorkoutFromWorkoutTemplate(workoutTemplate)

    _workoutName.value = newWorkout.name
    workoutDetails = newWorkout
    _workout.value = workoutDetails

    getPinnedWorkoutNotes(workoutTemplate.uuid, newWorkout.id)

    setWorkoutComponents(
      copyWorkoutItemsFromPreviousWorkout(workoutTemplate.workoutItems, newWorkout.id).toList()
    )
  }

  fun copyWorkoutFromPreviousWorkout(previousWorkout: Workout) {
    val newWorkout = manageWorkoutUseCase.copyWorkoutFromPreviousWorkout(previousWorkout)

    _workoutName.value = newWorkout.name
    workoutDetails = newWorkout
    _workout.value = workoutDetails

    getPinnedWorkoutNotes(previousWorkout.uuid, newWorkout.id)
    setWorkoutComponents(
      copyWorkoutItemsFromPreviousWorkout(previousWorkout.workoutItems, newWorkout.id).toList()
    )
    //    _exercisesSets.value = _exercisesSetsData.toList()
  }

  private fun copyWorkoutItemsFromPreviousWorkout(
    previousWorkoutItems: List<WorkoutItem>,
    _workoutID: ObjectId,
  ): List<WorkoutComponent> {
    return manageWorkoutUseCase.createNewWorkoutComponentsFromPreviousWorkout(
      previousWorkoutItems = previousWorkoutItems,
      workoutID = _workoutID,
    )
  }

  private fun loadWorkoutComponentsFromPreviousWorkout(
    previousWorkoutItems: List<WorkoutItem>
  ): List<WorkoutComponent> {
    return manageWorkoutUseCase.loadWorkoutItemsFromWorkout(
      previousWorkoutItems = previousWorkoutItems
    )
  }

// TODO Think out how to get Previous Set from the last time the exercise was performed
//   and what data structure to store them in

  /* private fun copySetsFromPreviousWorkout(previousSets: List<Set>): List<Set> {
    return manageWorkoutUseCase.copySetsFromPreviousWorkout(previousSets)
  }*/

  fun populatePreviousSetField(exerciseID: ObjectId, setNumber: Int): Set? {
    return manageWorkoutUseCase.getPreviousSetForExercise(exerciseID, setNumber)
  }

  //  private var _workoutNotesData: MutableList<WorkoutNote> = mutableListOf()
  private var _workoutNotesData: MutableList<WorkoutNote>
    get() = savedStateHandle.get<MutableList<WorkoutNote>>(WORKOUT_NOTES) ?: mutableListOf()
    set(value) = savedStateHandle.set(WORKOUT_NOTES, value)

  private var _workoutNotes: MutableStateFlow<List<WorkoutNote>> =
    MutableStateFlow(_workoutNotesData.toList())
  val workoutNotes
    get() = _workoutNotes.asStateFlow()

  private fun getPinnedWorkoutNotes(workoutUUID: Uuid, workoutID: ObjectId) {
    _workoutNotes.value = manageWorkoutUseCase.getPinnedWorkoutNotes(workoutUUID, workoutID)
  }

  fun addWorkoutNote() {
    _workoutNotesData.add(
      WorkoutNote().apply {
        workout = _workout.value!!.id
        workoutUUID = _workout.value!!.uuid
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
    _workoutNotesData.first { it == note }.lazyListID = newLazyListItemID()
    _workoutNotes.value = _workoutNotesData.toList()
  }

  private fun stopWorkoutTimer() {
    workoutCountUpTimer.stop()
    workoutDetails!!.workoutEnded = Clock.System.now()
    workoutDetails!!.workoutDuration = workoutDuration
    _workout.value = workoutDetails
    // TODO Navigate to Workout Complete Screen & Summary
  }

//    ExerciseExerciseSets
//  private var _exercisesSetsData = mutableListOf<ExerciseExerciseSets>()

//  private var _exercisesSets = MutableStateFlow(listOf<ExerciseExerciseSets>())

  private var _workoutComponents: MutableList<WorkoutComponent> = mutableListOf() /*=
    savedStateHandle.saveable(
      key = EXERCISE_EXERCISE_SETS,
      saver =
      Saver(
        save = {
          Json.encodeToString(
            ListExerciseExerciseSets.serializer(),
            ListExerciseExerciseSets().apply { list = it.toMutableList() },
          )
        },
        restore = {
          Json.decodeFromString(ListExerciseExerciseSets.serializer(), it).list.toMutableList()
        },
      ),
    ) {
      mutableListOf()
    }*/

  private var _workoutComponentFlow = MutableStateFlow(_workoutComponents.toList())

  private fun setWorkoutComponents(workoutComponents: List<WorkoutComponent>) {
    _workoutComponents = workoutComponents.toMutableList()
    _workoutComponentFlow.value = workoutComponents
    Log.i("setWorkoutComponents", "setWorkoutComponents.workoutViewModel: $this")
    Log.i(
      "setWorkoutComponents",
      "setWorkoutComponents._workoutComponents: ${workoutComponents.size}"
    )
    //    savedStateHandle[EXERCISE_EXERCISE_SETS] = exercisesSets.toMutableList()
  }

  val workoutComponents: StateFlow<List<WorkoutComponent>>
    get() = _workoutComponentFlow.asStateFlow()

//  TODO What is this used for or purpose, Workout Summary?
//  private var _allSetsData: MutableList<Set> = allSets(_exercisesSets.value)
//  private var _allSets = MutableStateFlow(_allSetsData.toList())
//  val allSets
//    get() = _allSets.asStateFlow()

  fun getWeightUnit(exerciseSets: ExerciseExerciseSets): String {
    return manageWorkoutUseCase.getWeightUnit(exerciseSets)
  }

  fun createSupersetFromExercise(exercises: List<Exercise>) {
    requireNotNull(_workout.value) { "Workout details cannot be null" }
    _workout.value?.let { workout ->
      val temporaryWorkoutComponents = manageWorkoutUseCase.createSupersetFromExercise(
        _workoutComponents,
        exercises,
        workout,
      )

      setWorkoutComponents(temporaryWorkoutComponents.toList())
    }

  }

  fun createSupersetFromExercisesSets(exercisesSets: List<ExerciseExerciseSets>) {
    requireNotNull(_workout.value) { "Workout details cannot be null" }
    _workout.value?.let { workout ->
      setWorkoutComponents(
        manageWorkoutUseCase.createSupersetFromExercisesSets(
          _workoutComponents,
          exercisesSets,
          workout,
        )
      )
    }
  }

  fun addMultipleEmptyExerciseSets(exercisesSelectedToBeAddedToWorkout: List<Exercise>) {
    requireNotNull(_workout.value) { "Workout details cannot be null" }
    val temporaryExerciseExerciseSets =
      manageWorkoutUseCase.addMultipleEmptyExerciseSets(
        _workoutComponents,
        exercisesSelectedToBeAddedToWorkout,
        _workout.value!!.id,
      )

    //    _exercisesSetsData = temporaryExerciseExerciseSets
    setWorkoutComponents(temporaryExerciseExerciseSets.toList())
  }

  fun removeExerciseSets(exerciseSets: WorkoutComponent) {
    setWorkoutComponents(_workoutComponents.filter { it != exerciseSets }.toMutableList())
    //    _exercisesSets.value = _exercisesSetsData
  }

  fun addSet(
//    workoutComponent: List<WorkoutComponent>, // Input: immutable list
    supersetUuid: Uuid?, // ID of the Superset component
    targetExSetsUuid: Uuid // ID of the ExerciseExerciseSets within the Superset
  ) {
    setWorkoutComponents(
      manageWorkoutUseCase.addSetToExerciseExerciseSet(
        workoutComponent = _workoutComponents,
        supersetUuid = supersetUuid,
        targetExSetsUuid = targetExSetsUuid
      )
    )
//    val temporarySets: List<Set> = manageWorkoutUseCase.addSet(workoutComponent)
//    updateExercisesSetsData (workoutComponent, temporarySets)

  }

  fun removeSet(exerciseSets: ExerciseExerciseSets, set: Set) {
    val temporarySets = manageWorkoutUseCase.removeSet(exerciseSets, set)

    updateExercisesSetsData(exerciseSets, temporarySets)
  }

  private fun updateExercisesSetsData(
    exerciseSets: ExerciseExerciseSets,
    temporarySets: List<Set>,
  ) {
    exerciseSets.sets = temporarySets.toMutableList()

    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun addExerciseNote(exerciseSets: ExerciseExerciseSets) {
    exerciseSets.notes.add(
      ExerciseNote().apply {
        exercise = exerciseSets.exercise.id
        workout = _workout.value!!.id
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
    pinned: Boolean,
  ) {
    val index = exerciseSets.notes.indexOf(exerciseNote)

    exerciseSets.notes[index].pinned = pinned

    updateExercisesNotesData(exerciseSets, exerciseSets.notes)
  }

  private fun updateExercisesNotesData(
    exerciseSets: ExerciseExerciseSets,
    temporaryNotes: List<ExerciseNote>,
  ) {
    exerciseSets.notes = temporaryNotes.toMutableList()

    updateExerciseExerciseSetsData(exerciseSets)
  }

//  private fun allSets(exerciseSets: List<ExerciseExerciseSets>): MutableList<Set> {
//    val setsList = mutableListOf<Set>()
//    exerciseSets.forEach { exerciseSet ->
//      exerciseSet.sets.forEach { set -> setsList.add(set) }
//    }
//
//    return setsList
//  fun getSetData(setUUID: RealmUUID): Set {
//    return allSets().first { it.uuid == setUUID }

  private fun updateExerciseExerciseSetsData(workoutComponent: ExerciseExerciseSets) {

    setWorkoutComponents(
      manageWorkoutUseCase.refreshWorkoutComponentLazyListItemID(
        workoutComponents = _workoutComponents,
        targetExerciseEerciseSets = workoutComponent,
      )
    )
    //    _allSets.value = allSets(_exercisesSets.value).toList()
  }

//  }

//  }

  fun updateSetWeight(exerciseSets: ExerciseExerciseSets, setUUID: Uuid, newWeight: Double?) {
    val weightUnit = exerciseSets.exercise?.weightUnit ?: userDefaultWeightUnit
    exerciseSets.updateSetWeight(uuid = setUUID, newWeight = newWeight, weightUnit = weightUnit)

    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetReps(exerciseSets: ExerciseExerciseSets, setUUID: Uuid, newReps: Int?) {
    exerciseSets.updateSetReps(uuid = setUUID, newReps = newReps)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRIR(exerciseSets: ExerciseExerciseSets, setUUID: Uuid, newRIR: Double?) {
    exerciseSets.updateSetRIR(uuid = setUUID, newRIR = newRIR)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetRPE(exerciseSets: ExerciseExerciseSets, setUUID: Uuid, newRPE: Double?) {
    exerciseSets.updateSetRPE(uuid = setUUID, newRPE = newRPE)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun completeSet(exerciseSets: ExerciseExerciseSets, setUUID: Uuid, completed: Boolean) {
    exerciseSets.completeSet(setUUID, completed)
    updateExerciseExerciseSetsData(exerciseSets)
  }

  fun updateSetType(exerciseSets: ExerciseExerciseSets, setUUID: Uuid, setType: SetType) {
    exerciseSets.updateSetType(uuid = setUUID, setType = setType)
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

  fun getWorkingSetRepetitions(exerciseSets: ExerciseExerciseSets): Int? {
    // TODO Get Previous Working Set if Sets is empty
    var repetitions: Int? = null
    if (exerciseSets.sets.isNotEmpty()) {
      repetitions = exerciseSets.sets.first { it.setTypeID != WARM_UP_SET }.repetitions
    }
    return repetitions
  }

  fun addWorkingSets(exerciseSets: ExerciseExerciseSets, workingSets: List<Set>) {
    workingSets.forEach { it.lazyListItemID = newLazyListItemID() }

    exerciseSets.sets.removeAll { it.setTypeID != WARM_UP_SET }

    exerciseSets.sets.addAll(workingSets)

    updateExercisesSetsData(exerciseSets = exerciseSets, temporarySets = exerciseSets.sets)
  }

  fun addBackOffSets(exerciseSets: ExerciseExerciseSets, backOffSets: List<Set>) {
    for (index in backOffSets.indices) {
      exerciseSets.sets.add(
        Set(
          setNumber = exerciseSets.sets.last().setNumber?.plus(1),
          weightPounds = backOffSets[index].weightPounds,
          weightKilograms = backOffSets[index].weightKilograms,
          repetitions = backOffSets[index].repetitions,
          ratePerceivedEffort = backOffSets[index].ratePerceivedEffort,
          repetitionsInReserve = backOffSets[index].repetitionsInReserve,
          setTypeID = backOffSets[index].setTypeID,
          percentage1RM = backOffSets[index].percentage1RM,
          exerciseID = exerciseSets.exercise.id,
          exerciseSetsID = exerciseSets.id,
          workoutID = exerciseSets.workoutID,
          ownerID = exerciseSets.ownerID,
        )
        //        backOffSets[index].copy(lazyListItemID = newID())
      )
    }

    updateExercisesSetsData(exerciseSets = exerciseSets, temporarySets = exerciseSets.sets)
  }

  fun updateExerciseWeightUnit(exercise: Exercise?, weightUnit: String?) {
    exercise?.weightUnit = weightUnit
    viewModelScope.launch {
      if (exercise != null) {
        exerciseRepository.updateExercise(exercise)
      }
    }
  }

  fun updateExerciseAutoRestTimer(tabRowState: Int, exercise: Exercise, time: Duration) {
    if (tabRowState == 1) {
      exercise.warmUpSetRestInWholeMilliseconds = time.inWholeMilliseconds
    } else if (tabRowState == 0) {
      exercise.workingSetRestInWholeMilliseconds = time.inWholeMilliseconds
    }
    viewModelScope.launch { exerciseRepository.updateExercise(exercise) }
  }

  fun enableExerciseAutoRestTimer(tabRowState: Int, exercise: Exercise, enable: Boolean) {
    if (tabRowState == 1) {
      exercise.warmUpSetRestTimerEnabled = enable
    } else if (tabRowState == 0) {
      exercise.workingSetRestTimerEnabled = enable
    }
    viewModelScope.launch { exerciseRepository.updateExercise(exercise) }
  }

  fun checkAllSetsComplete(): Pair<Int, Int> {
    var countSets: Int = 0
    var countSetsCompleted: Int = 0

    manageWorkoutUseCase.checkAllSetsComplete(_workoutComponents)

    return Pair(countSetsCompleted, countSets)
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
    ): List<ExerciseNote> {  }
  */

  /**
   * Saves the workout data to the database asynchronously. The function launches a coroutine in the
   * viewModelScope to perform the database operations. It first inserts the workout data using the
   * workoutRepository. Then, it iterates over all sets in the _workoutData and inserts each set if
   * it is completed into the database using the setsRepository. The function does not return any
   * value.
   */
  fun saveWorkout() {
    //    TODO Implement this
    //
    //    _workoutData.notes = _workoutNotesData.toMutableList()
    //    TODO Only Add Completed Sets, Implement Check ond Remove Uncompleted Sets
    removeUncompletedSets()

    //    _workoutData.exercisesSets =
    //      convertExerciseExerciseSetsToExerciseSets(_exercisesSets.value, userDefaultWeightUnit)
    //
    //    _workoutData.calculateTotalVolume(userDefaultWeightUnit)

    viewModelScope.launch {
      requireNotNull(workoutDetails) { "Workout details cannot be null" }
      workoutDetails?.let { workoutDetails ->
        manageWorkoutUseCase.saveWorkout(workoutDetails, _workoutNotesData, _workoutComponents)
      }
    }

  }

  fun getCompletedWorkout(workoutID: ObjectId) {
    _completedWorkout.value = manageWorkoutUseCase.getCompletedWorkout(workoutID)
  }

  fun getWorkoutsCount(): Int {
    return manageWorkoutUseCase.getWorkoutsCount()
  }

  fun finishWorkout() {
    //    TODO Navigate to Workout Summary Screen
    cancelSetCompletedTimer()
    stopWorkoutTimer()
    saveWorkout()
    stopAutoSavingWorkout()
  }

  fun cancelWorkout() {
    //    TODO Pop workout screen of the backstack inclusively
    //    TODO navController.popBackStack( route = Programs.route, inclusive = true)
    deleteWorkout()
    cancelSetCompletedTimer()
    stopWorkoutTimer()
    workoutDetails = initWorkout()
    //    _exercisesSetsData = mutableListOf()
    setWorkoutComponents(listOf())
    stopAutoSavingWorkout()
  }

  private fun deleteWorkout() {
    _workout.value?.let { manageWorkoutUseCase.deleteInProgressWorkout(it.id) }
  }

  private fun removeUncompletedSets() {
    //    TODO Doesn't Work, FIX
    //    val newData = _exercisesSetsData
    //    newData.forEach { it.sets.removeIf { set -> !set.setCompleted } }
    //    newData.filter { it.sets.isNotEmpty() }
    //    _exercisesSetsData = newData
    setWorkoutComponents(manageWorkoutUseCase.removeUncompletedSets(_workoutComponents))
  }

  fun replaceExerciseInExerciseSets(exerciseSets: ExerciseExerciseSets, newExercise: Exercise) {
    setWorkoutComponents(
      manageWorkoutUseCase.replaceExerciseInExerciseSets(
        _workoutComponents,
        exerciseSets,
        newExercise
      )
    )
  }
}

private fun initWorkout(): Workout {
  val currentTime = LocalTime.now()

  return when (currentTime.hour) {
    in 0..11 -> {
      Workout(
        name = "Morning Workout",
        dateCreated = Clock.System.now(),
        workoutStarted = Clock.System.now(),
      )
    }

    in 12..17 -> {
      Workout(
        name = "Afternoon Workout",
        dateCreated = Clock.System.now(),
        workoutStarted = Clock.System.now(),
      )
    }

    else -> {
      Workout(
        name = "Evening Workout",
        dateCreated = Clock.System.now(),
        workoutStarted = Clock.System.now(),
      )
    }
  }
}
