@file:OptIn(ExperimentalUuidApi::class, FlowPreview::class)

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.PreviewExerciseSets
import com.example.myapplication.common.domain.model.PreviewSuperset
import com.example.myapplication.common.domain.model.Quadruple
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.SupersetExercisesSets
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.domain.model.WorkoutNote
import com.example.myapplication.common.domain.model.addWarmUpSets
import com.example.myapplication.common.domain.model.completeSet
import com.example.myapplication.common.domain.model.createWorkoutPreview
import com.example.myapplication.common.domain.model.deepCopy
import com.example.myapplication.common.domain.model.updateSetRIR
import com.example.myapplication.common.domain.model.updateSetRPE
import com.example.myapplication.common.domain.model.updateSetReps
import com.example.myapplication.common.domain.model.updateSetType
import com.example.myapplication.common.domain.model.updateSetWeight
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.datastore.WorkoutDatastore
import com.example.myapplication.common.infrastructure.persistence.datastore.toDomainWorkout
import com.example.myapplication.common.infrastructure.persistence.datastore.toProtoActiveWorkout
import com.example.myapplication.common.util.AlarmReceiver
import com.example.myapplication.common.util.ProgressionOutcome
import com.example.myapplication.common.util.ProgressionResults
import com.example.myapplication.common.util.cancelNotifications
import com.example.myapplication.common.util.newLazyListItemID
import com.example.myapplication.common.util.selectProgression
import com.example.myapplication.common.util.sendNotification
import com.example.myapplication.datastore.ActiveWorkout
import com.example.myapplication.features.workout.feature.setRestTimer.TimerState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
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
  private val managePreviewWorkoutUseCase: ManagePreviewWorkoutUseCase,
  private val exerciseRepository: ExercisesRepository,
  private val workoutDatastore: WorkoutDatastore, // Added WorkoutDatastore
  private val savedStateHandle: SavedStateHandle,
  private val userProfileManager: UserProfileManager
) : ViewModel() {
  private val user: UserProfile? = userProfileManager.userProfile.value

  private val WARM_UP_SET = SetType.warmUpSet

  var isWorkoutActive: Boolean
    get() = savedStateHandle.get<Boolean>(IS_WORKOUT_ACTIVE) ?: false
    set(value) = savedStateHandle.set(IS_WORKOUT_ACTIVE, value)

  private val REQUEST_CODE = 0
  private val notifyPendingIntent: PendingIntent

  private var workoutDuration = MutableStateFlow<Long>(0L)
//    get() = savedStateHandle.get<Long>(WORKOUT_DURATION) ?: 0L
//    set(value) = savedStateHandle.set(WORKOUT_DURATION, value)

  private var pausedElapsedTime: Long
    get() = savedStateHandle.get<Long>(WORKOUT_PAUSED_ELAPSED_TIME) ?: 0L
    set(value) = savedStateHandle.set(WORKOUT_PAUSED_ELAPSED_TIME, value)

  private var _isWorkoutCountUpTimerPaused = MutableStateFlow<Boolean>(true)

  val isWorkoutCountUpTimerPaused: StateFlow<Boolean>
    get() = _isWorkoutCountUpTimerPaused

  private var _workoutDurationFormatted =
    workoutDuration.value.toDuration(DurationUnit.MILLISECONDS)
      .toComponents { hours, minutes, seconds, _
        ->
        if (hours > 0) {
          "$hours:" + minutes.toString().padStart(2, '0') + ":" + seconds.toString()
            .padStart(2, '0')
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
        workoutDuration.value = elapsedTime

        _workoutDurationString.value =
          workoutDuration.value.toDuration(DurationUnit.MILLISECONDS).toComponents { hours,
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
    pausedElapsedTime = workoutDuration.value
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
    // testSaveStateHandle() // Potentially remove or adjust based on DataStore as SOT
    Log.d("WorkoutViewModel", "WorkoutViewModel init ${this@WorkoutViewModel}")
    Log.d(
      "WorkoutViewModel",
      "is initialWorkoutData.isInitialized : ${this::initialWorkoutData.isInitialized}"
    )


    // TODO there is a race condition between getWorkoutByID and load from DataStore
    // Load from DataStore first
    viewModelScope.launch {
      // This triggered each item the current workout is saved to the DataStore due to collecting the updated activeWorkout
//      workoutDatastore.getActiveWorkout.collect { activeWorkout ->
      val activeWorkout = workoutDatastore.getActiveWorkout.first()

      Log.d("WorkoutViewModel", "is _workout.value null: ${_workout.value == null}")
      Log.d("WorkoutViewModel", "_workout.name: ${_workout.value?.name}")
      Log.d("WorkoutViewModel", "isWorkoutActive: $isWorkoutActive")
//        Log.d("WorkoutViewModel", "ActiveWorkout.getDefaultInstance(): ${ActiveWorkout.getDefaultInstance()}")
//      Log.d(
//        "WorkoutViewModel",
//        "activeWorkout == getDefaultInstance(): ${activeWorkout != ActiveWorkout.getDefaultInstance()}"
//      )

      if (activeWorkout != ActiveWorkout.getDefaultInstance() && !isWorkoutActive) {
        val domainWorkout = activeWorkout.toDomainWorkout()
        _workout.value = domainWorkout
        _workoutName.value = domainWorkout.name
        _workoutNotesData =
          domainWorkout.notes.toMutableList() // Assuming notes are part of Workout domain model
        _workoutNotes.value = _workoutNotesData.toList()

        // Map WorkoutItemProto list to domain WorkoutComponent list
        val domainWorkoutComponents = domainWorkout.workoutItems.map { workoutItem ->
          // This mapping logic depends on how WorkoutItem is structured in your domain
          // and how it corresponds to ExerciseSets/Superset after mapping from proto.
          // The WorkoutItem.toDomain() from mappers should handle this.
          // For now, we assume workoutItem.toDomain() from mappers correctly returns ExerciseSets or Superset
          // which are subclasses of WorkoutComponent.
          // If WorkoutItem itself is not a WorkoutComponent, adjust this mapping.
          workoutItem // Assuming toDomain() in mappers produces correct WorkoutComponent types
        }
        setWorkoutComponents(
          loadWorkoutComponentsFromPreviousWorkout(
            previousWorkoutItems = domainWorkoutComponents,
//              _workoutID = domainWorkout.id
          )
        )


        // Update timer states if needed from the loaded workout
        workoutDuration.value = domainWorkout.workoutDuration
        pausedElapsedTime =
          domainWorkout.workoutDuration // If workout was paused, resume from its duration
        _workoutDurationString.value =
          workoutDuration.value.toDuration(DurationUnit.MILLISECONDS)
            .toComponents { hours, minutes, seconds, _ ->
              if (hours > 0) "$hours:${minutes.toString().padStart(2, '0')}:${
                seconds.toString().padStart(2, '0')
              }"
              else "$minutes:${seconds.toString().padStart(2, '0')}"
            }
        Log.d(
          "WorkoutViewModel",
          "Loaded workout duration from DataStore: ${domainWorkout.workoutDuration}"
        )

        Log.d(
          "WorkoutViewModel",
          "Loaded workout duration from DataStore: ${_workoutDurationString.value}"
        )

        if (!domainWorkout.isCompleted && domainWorkout.workoutStarted != null && domainWorkout.workoutEnded == null) {
          isWorkoutActive = true
          // If the workout was active and not paused, timer might need to be restarted
          // or its state restored more carefully. For now, setting pausedElapsedTime.
          // If it was running, the startWorkoutTimer() call below might handle it.
        }

        Log.d("WorkoutViewModel", "Loaded workout from DataStore: ${domainWorkout.name}")
        startAutoSavingWorkout() // Start auto-save after loading from DataStore
      } else {
        // No active workout in DataStore, try SavedStateHandle or initialize new
        // This part preserves existing logic for SavedStateHandle or new workout initialization
        // if DataStore is empty.
        Log.d(
          "WorkoutViewModel",
          "No active workout in DataStore, using SavedStateHandle or init new."
        )
        testSaveStateHandle() // Fallback to SavedStateHandle logic
        if (_workout.value == null || _workout.value?.name == initWorkout().name) { // Check if it's still default/uninitialized
          initWorkoutData() // Or specific logic to init new if not relying on this func for DataStore load
        }
      }
//      }
    }

    notifyPendingIntent =
      PendingIntent.getBroadcast(
        application,
        REQUEST_CODE,
        notifyIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
      )
  }


  private fun testSaveStateHandle() {
    savedStateHandle.get<String?>(WORKOUT_DETAILS)
  }

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

  //WorkoutPreviewScreen Data
  // The workout plan *before* progression logic ran
  private var originalWorkout: Workout? = null

  // The results of running progression logic on each applicable item
  private lateinit var progressionResults: Map<String, ProgressionResults>

  // State for the UI
  val previewItems =
    mutableStateListOf<Any>() // Holds PreviewExerciseSets, PreviewSuperset, or original items
  val overallProgressionSuccess = mutableStateOf(true)
  val progressionMessage = mutableStateOf("")

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


  private fun startAutoSavingWorkout() {
    Log.d(
      "WorkoutViewModel",
      "autoSaveJob?.isActive ${autoSaveJob?.isActive == true}\nautoSaveJob: $autoSaveJob"
    )
    // Cancel any previous auto-save job to avoid duplicates if called multiple times
//    autoSaveJob?.cancel()
    if (autoSaveJob?.isActive != true) {
      autoSaveJob = viewModelScope.launch {
        combine(
          _workout,
          _workoutNotes,
          _workoutComponentFlow,
          workoutDuration
        ) { workout, notes, components, duration ->
          Quadruple(workout, notes, components, duration)
        }
          .debounce(1000.milliseconds)
          .collect { (latestWorkout, latestNotes, latestComponents, latestDuration) ->
            runCatching {
              if (latestWorkout != null) {
                Log.d("WorkoutViewModel", "Auto-saving workout to DataStore: ${latestWorkout.name}")
                Log.d("WorkoutViewModel", "latestDuration: $latestDuration")
                Log.d(
                  "WorkoutViewModel",
                  "autoSaveJob._workoutDurationString: ${_workoutDurationString.value}"
                )
                // Ensure latestNotes and latestComponents are correctly reflected in latestWorkout instance
                // if they are not already part of its direct state.
                // The mappers expect Workout.notes and Workout.workoutItems to be up-to-date.
                // Let's assume _workout.value (which is latestWorkout) is the source of truth and
                // its 'notes' and 'workoutItems' are updated elsewhere when latestNotes/latestComponents change.
                // If not, we might need to create a new Workout instance here:
                val workoutToSave = latestWorkout.copy(
                  workoutDuration = latestDuration,
                  notes = latestNotes, // Assuming Workout data class has notes: List<WorkoutNote>
                  workoutItems = manageWorkoutUseCase.convertWorkoutComponentsToWorkoutItems(
                    latestComponents
                  ) // Assuming Workout data class has workoutItems: List<WorkoutItem>
                  // and WorkoutComponent can be filtered/mapped to WorkoutItem.
                  // This needs to be correct based on your domain model structure.
                  // If WorkoutComponent IS a WorkoutItem, then just `latestComponents` might be fine.
                )
                // TODO: Verify that latestWorkout actually contains the most up-to-date lists for notes and items,
                // or reconstruct it properly here before mapping.
                // For now, assuming latestWorkout is sufficiently up-to-date for toProtoActiveWorkout()
                val activeWorkoutProto = workoutToSave.toProtoActiveWorkout()
                workoutDatastore.saveActiveWorkout(activeWorkoutProto)
                Log.d("WorkoutViewModel", "Successfully auto-saved workout to DataStore.")

                // Optionally, keep the old repository call if it serves other purposes,
                // or remove if DataStore is the sole persistence for active workouts.
                // manageWorkoutUseCase.saveInProgressWorkout(
                //   workout = latestWorkout,
                //   workoutNotes = latestNotes,
                //   workoutComponents = latestComponents
                // )
              }
            }.onFailure { error ->
              Log.e("WorkoutViewModel", "Error auto-saving workout to DataStore", error)
            }
          }
      }
      Log.d("ViewModelLifecycle", "Auto-save job started.")
    }
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
    // This initWorkoutData might need adjustment.
    // If DataStore has data, it's already loaded.
    // This should primarily handle the case where DataStore is empty and we need a fresh start.
    if (!isWorkoutActive && (_workout.value == null || _workout.value?.name == initWorkout().name)) {
      Log.d(
        "WorkoutViewModel",
        "initWorkoutData: Initializing new workout as DataStore was empty or workout not active."
      )
      initialWorkoutData = initWorkout()
      workoutDetails = initialWorkoutData
      _workout.value = workoutDetails
      _workoutName.value = initialWorkoutData.name
      _workoutNotesData.clear()
      _workoutNotes.value = _workoutNotesData.toList()
      setWorkoutComponents(emptyList())
      isWorkoutActive = true
      startAutoSavingWorkout() // Start auto-save for the new workout
    } else if (isWorkoutActive) {
      Log.d("WorkoutViewModel", "initWorkoutData: Workout is already active, not re-initializing.")
    } else {
      Log.d(
        "WorkoutViewModel",
        "initWorkoutData: Workout loaded from DataStore, not re-initializing."
      )
    }
    // testSaveStateHandle() // Consider if this is needed here anymore
  }

  fun getExerciseByID(exerciseID: ObjectId?): Exercise? {
    return manageWorkoutUseCase.getExerciseByID(exerciseID)
  }

  fun getWorkoutByID(workoutID: ObjectId) {
//    Log.i("WorkoutViewModel.getWorkoutByID() ", "called. WorkoutID: $workoutID")
    // This function loads a workout template or a specific workout by ID,
    // potentially overriding what was loaded from DataStore if called explicitly.
    // If it's intended to be the primary way to start a *new* workout from template/existing,
    // it should probably clear DataStore first or handle the merge carefully.

    if (!isWorkoutActive || _workout.value?.id != workoutID) { // Only load if not already active with same ID
      Log.d(
        "WorkoutViewModel",
        "WorkoutViewModel.getWorkoutByID: Loading workout $workoutID explicitly."
      )
      Log.d("WorkoutViewModel", "WorkoutViewModel.getWorkoutByID: $this")
      val loadedWorkout = manageWorkoutUseCase.getWorkoutByID(workoutID)?.let { currentWorkout ->
        managePreviewWorkoutUseCase.getLatestPreviousIdenticalWorkout(currentWorkout)
          ?.let { previousWorkout ->
            setupWorkoutPreview(
              workoutToPrepare = currentWorkout,
              previousWorkout = previousWorkout
            )
          }
        currentWorkout
      } ?: initWorkout()

      initialWorkoutData = loadedWorkout
      copyWorkoutFromWorkoutTemplate(initialWorkoutData) // This sets _workout.value and other states
      isWorkoutActive = true
      startAutoSavingWorkout() // Ensure auto-saving starts for this explicitly loaded workout
      // testSaveStateHandle() // Consider if this is needed
    } else {
      Log.d(
        "WorkoutViewModel",
        "getWorkoutByID: Workout $workoutID already active or loaded, not reloading."
      )
    }
  }

  fun loadWorkoutFromPreviousWorkout(workoutID: ObjectId) {
    // Similar to getWorkoutByID, this could override DataStore's loaded workout.
    // Ensure this is the desired behavior.
    manageWorkoutUseCase.getCompletedWorkoutByID(workoutID)?.let { workout ->
      _workoutName.value = workout.name
      workoutDetails = workout
      _workout.value = workoutDetails

      getPinnedWorkoutNotes(workout.uuid, workout.id)
      setWorkoutComponents(loadWorkoutComponentsFromPreviousWorkout(workout.workoutItems))
    }
  }

  // TODO Rename function
  private fun copyWorkoutFromWorkoutTemplate(workoutTemplate: Workout) {
    val newWorkout = manageWorkoutUseCase.copyWorkoutFromWorkoutTemplate(workoutTemplate)

    _workoutName.value = newWorkout.name
    workoutDetails = newWorkout
    _workout.value = workoutDetails

    getPinnedWorkoutNotes(workoutTemplate.uuid, newWorkout.id)

    setWorkoutComponents(
      copyWorkoutItemsFromPreviousWorkout(workoutTemplate.workoutItems, newWorkout.id).toList()
    )
  }

  /**
   * Copies workout data from a previously completed workout or a workout template.
   *
   * This function retrieves a completed workout by its ID, creates a new workout
   * based on it, and updates the ViewModel's state with the new workout's details.
   * This includes the workout name, notes, and its constituent exercises and sets.
   *
   * @param previousWorkout The [ObjectId] of the previously completed workout to copy from.
   */
  fun copyWorkoutFromPreviousWorkout(previousWorkout: ObjectId) {

    manageWorkoutUseCase.getCompletedWorkoutByID(previousWorkout)?.let { workout ->
      val newWorkout = manageWorkoutUseCase.copyWorkoutFromPreviousWorkout(workout)
      _workoutName.value = newWorkout.name
      workoutDetails = newWorkout
      _workout.value = workoutDetails

      getPinnedWorkoutNotes(newWorkout.uuid, newWorkout.id)
      setWorkoutComponents(
        copyWorkoutItemsFromPreviousWorkout(newWorkout.workoutItems, newWorkout.id).toList()
      )
    }
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
  private var _workoutNotesData: MutableList<WorkoutNote> = mutableListOf()
//    get() = savedStateHandle.get<MutableList<WorkoutNote>>(WORKOUT_NOTES) ?: mutableListOf() // Removed SavedStateHandle
//    set(value) = savedStateHandle.set(WORKOUT_NOTES, value)

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
        ownerID = user?.id?.toHexString() ?: "MISSING VALUE"
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
    workoutDetails!!.workoutDuration = workoutDuration.value
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
    Log.i(
      "WorkoutViewModel.setWorkoutComponents",
      "WorkoutViewModel.setWorkoutComponents.workoutViewModel: $this"
    )
    Log.i(
      "WorkoutViewModel.setWorkoutComponents",
      "WorkoutViewModel.setWorkoutComponents._workoutComponents: ${workoutComponents.size}"
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
    return manageWorkoutUseCase.checkAllSetsComplete(_workoutComponents)
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
    //    TODO Only Add Completed Sets, Implement Check ond Remove Uncompl
    //     eted Sets
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
    if (_completedWorkout.value == null) {
      _completedWorkout.value = manageWorkoutUseCase.getCompletedWorkout(workoutID)
    }
  }

  fun getWorkoutsCount(): Int {
    return manageWorkoutUseCase.getWorkoutsCount()
  }

  fun finishWorkout() {
    //    TODO Navigate to Workout Summary Screen
    cancelSetCompletedTimer()
    stopWorkoutTimer()

    workoutDetails?.ownerID = user?.id?.toHexString() ?: "MISSING USER ID"

    saveWorkout()

    workoutDetails?.notes = _workoutNotesData
    workoutDetails?.workoutItems =
      manageWorkoutUseCase.convertWorkoutComponentsToWorkoutItems(workoutComponents.value)

    _completedWorkout.value = workoutDetails

    stopAutoSavingWorkout()
    viewModelScope.launch {
      workoutDatastore.clearActiveWorkout()
      Log.d("WorkoutViewModel", "Active workout cleared from DataStore in finishWorkout.")
    }
  }

  fun cancelWorkout() {
    //    TODO Pop workout screen of the backstack inclusively
    //    TODO navController.popBackStack( route = Programs.route, inclusive = true)
    cancelSetCompletedTimer()
    stopWorkoutTimer()

    workoutDetails = initWorkout() // Resets ViewModel state
    _workout.value = workoutDetails // Ensure LiveData/StateFlow is updated
    _workoutName.value = workoutDetails?.name
    _workoutNotesData.clear()
    _workoutNotes.value = _workoutNotesData.toList()
    setWorkoutComponents(emptyList()) // Clears UI components
    stopAutoSavingWorkout()
    viewModelScope.launch {
      workoutDatastore.clearActiveWorkout()
      Log.d("WorkoutViewModel", "Active workout cleared from DataStore in cancelWorkout.")
    }
    isWorkoutActive = false // Ensure workout is marked as not active
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

  @OptIn(ExperimentalUuidApi::class)
  fun setupWorkoutPreview(
    workoutToPrepare: Workout,
    previousWorkout: Workout // Needed for look-back progression
  ) {
    originalWorkout = workoutToPrepare.deepCopy() // Store a deep copy of the original state

    // --- Simulate Running Progression Logic (Method B: Look Back) ---
    // In a real app, this would call your actual WorkoutProgressorB logic
    val results = mutableMapOf<String, ProgressionResults>()
    workoutToPrepare.workoutItems.forEach { currentWorkoutItem ->
      when (currentWorkoutItem) {
        is ExerciseSets -> {
          val progressionType =
            currentWorkoutItem.sets.firstOrNull { it.setTypeID != SetType.warmUpSet }?.progressionSchemeID
          // Simulate progression call (replace with actual call)
          val previousExerciseSets =
            previousWorkout.workoutItems.find { (it as? ExerciseSets)?.exerciseID == currentWorkoutItem.exerciseID } as? ExerciseSets

          if (progressionType != null) {

            previousExerciseSets?.sets?.filter { it.setCompleted }
              ?.let {
                results[currentWorkoutItem.id.toString()] =
                  selectProgression(progressionType).invoke(it, currentWorkoutItem.sets)
              }
          } else {
            // No progression for this currentWorkoutItem]
            results[currentWorkoutItem.id.toString()] = ProgressionResults(
              // TODO Should this be null if no progression logic applied?
              // TODO Or should previous workout be shown regardless?
              completedSets = previousExerciseSets?.sets,
              currentSets = currentWorkoutItem.sets,
              outcome = ProgressionOutcome.NULL,
              message = "No progression logic applied"
            )
          }
        }

        is Superset -> {
          currentWorkoutItem.exercisesSets.forEach { currentSupersetExerciseSets ->
            val progressionType =
              currentSupersetExerciseSets.sets.firstOrNull { it.setTypeID != SetType.warmUpSet }?.progressionSchemeID
            val previousSupersetExerciseSets =
              previousWorkout.workoutItems.find { (it as? ExerciseSets)?.exerciseID == currentSupersetExerciseSets.exerciseID } as? ExerciseSets

            if (progressionType != null) {
              previousSupersetExerciseSets?.sets?.filter { it.setCompleted }
                ?.let {
                  results[currentSupersetExerciseSets.id.toString()] =
                    selectProgression(progressionType).invoke(it, currentSupersetExerciseSets.sets)
                }
            } else {
              // No progression for this currentWorkoutItem]
              results[currentSupersetExerciseSets.id.toString()] = ProgressionResults(
                // TODO Should this be null if no progression logic applied?
                // TODO Or should previous workout be shown regardless?
                completedSets = previousSupersetExerciseSets?.sets,
                currentSets = currentSupersetExerciseSets.sets,
                outcome = ProgressionOutcome.NULL,
                message = "No progression logic applied"
              ) // No progression for this currentWorkoutItem]
            }
          }
        }
      }
    }
    progressionResults = results
    // --- End Simulation ---

    // Create the preview data structure
    previewItems.clear()
    requireNotNull(originalWorkout) { "Original workout cannot be null" }
    previewItems.addAll(
      createWorkoutPreview(
        previousWorkout,
        originalWorkout!!,
        progressionResults
      )
    )

    // Determine overall status
    val failures = previewItems.count {
      (it is PreviewExerciseSets && it.overallOutcome == ProgressionOutcome.FAILED) ||
              (it is PreviewSuperset && it.previewExerciseSets.any { p -> p.overallOutcome == ProgressionOutcome.FAILED })
    }
    val successes = previewItems.count {
      (it is PreviewExerciseSets && it.overallOutcome == ProgressionOutcome.PROGRESSED) ||
              (it is PreviewSuperset && it.previewExerciseSets.any { p -> p.overallOutcome == ProgressionOutcome.PROGRESSED })
    }

    overallProgressionSuccess.value = failures == 0
    progressionMessage.value = when {
      failures > 0 -> "Progression failed for $failures exercise(s)."
      successes > 0 -> "All $successes progression(s) will be applied."
      else -> "No progressions applied (targets may not have been met)."
    }
  }

  fun acceptProgressions() {
    // Create the final workout object by applying successful progressions
    val finalWorkoutItems = originalWorkout?.workoutItems?.map { originalItem ->
      when (originalItem) {
        is ExerciseSets -> {
          val preview =
            previewItems.find { (it as? PreviewExerciseSets)?.originalItem?.id == originalItem.id } as? PreviewExerciseSets
          if (preview?.overallOutcome == ProgressionOutcome.PROGRESSED || preview?.overallOutcome == ProgressionOutcome.UNCHANGED) {
            // Apply the progressed sets if success or unchanged
            originalItem.copy(sets = preview.previewSets.map { it.progressedSet }.toMutableList())
          } else {
            // Revert to original if failed or rejected implicitly (or handle explicitly)
            originalItem // Return original
          }
        }

        is Superset -> {
          originalItem.copy(exercisesSets = originalItem.exercisesSets.map { exSets ->
            val preview =
              (previewItems.find { (it as? PreviewSuperset)?.originalItem?.id == originalItem.id } as? PreviewSuperset)
                ?.previewExerciseSets?.find { it.originalItem.id == exSets.id }

            if (preview?.overallOutcome == ProgressionOutcome.PROGRESSED || preview?.overallOutcome == ProgressionOutcome.UNCHANGED) {
              exSets.copy(sets = preview.previewSets.map { it.progressedSet }.toMutableList())
            } else {
              exSets
            }
          }.toMutableList())
        }
      }
    }?.toMutableList()

    if (finalWorkoutItems != null) {
      originalWorkout?.copy(workoutItems = finalWorkoutItems)
        ?.let {
          copyWorkoutFromWorkoutTemplate(it)
          previewItems.clear()
        }
    }
  }

  fun rejectProgressions() {
    // Return the original workout plan without any changes
    originalWorkout?.let { copyWorkoutFromWorkoutTemplate(it) }
    originalWorkout = null
    previewItems.clear()
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
        isPublic = false
      )
    }

    in 12..17 -> {
      Workout(
        name = "Afternoon Workout",
        dateCreated = Clock.System.now(),
        workoutStarted = Clock.System.now(),
        isPublic = false,
      )
    }

    else -> {
      Workout(
        name = "Evening Workout",
        dateCreated = Clock.System.now(),
        workoutStarted = Clock.System.now(),
        isPublic = false,
      )
    }
  }
}
