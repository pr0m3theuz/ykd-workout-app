@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.features.exercises

import android.util.Log
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.Equipment
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.Force
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Mechanic
import com.example.myapplication.common.domain.model.MovementType
import com.example.myapplication.common.domain.model.Muscle
import com.example.myapplication.common.domain.model.MuscleExerciseWithMuscle
import com.example.myapplication.common.domain.model.MuscleGroup
import com.example.myapplication.common.domain.model.MuscleRoleFunction
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.domain.model.Utility
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.convertPoundToKg
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import com.example.myapplication.common.infrastructure.persistence.ModernizedLoadRIRReposistory
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.OneRepMaxEstimateFormulas
import com.example.myapplication.common.util.RepCapacity
import com.example.myapplication.common.util.estimateTargetOneRepMaxModernized
import com.example.myapplication.features.userProfileScreen.like.boostcamp.ChartData
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.TimeZone
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

// TODO Implement Clone Exercise Feature

sealed class ExercisesUiEvent {
  data class SetExpand(val value: Boolean) : ExercisesUiEvent()

  data class SetExerciseDetailsLayoutTabState(val value: Int) : ExercisesUiEvent()

  data class UpdateMuscleGroupsChecked(val muscleGroup: MuscleGroup, val checked: Boolean) :
    ExercisesUiEvent()

  data class UpdateEquipmentsChecked(val equipment: Equipment, val checked: Boolean) :
    ExercisesUiEvent()

  data class SetSearchExerciseName(val value: String) : ExercisesUiEvent()

  data object FilterExercises : ExercisesUiEvent()

  data class GetExerciseDetails(val exerciseID: String?) : ExercisesUiEvent()

  //  data class GetExerciseRecords(val exercise: Exercise) : ExercisesUiEvent()

  data class SetMovementTypesChecked(val movementType: MovementType, val checked: Boolean) :
    ExercisesUiEvent()
}

data class ExercisesUiState(
  val expand: Boolean = false,
  val exerciseDetailsLayoutTabState: Int = 0,
  val exercises: List<Exercise> = emptyList(), // Filtered list of exercises shown in UI
  val allExercises: List<Exercise> = emptyList(), // Full list of exercises from repository
  val equipment: List<Equipment> = emptyList(),
  val muscles: List<Muscle> = emptyList(),
  val muscleGroupsChecked: Map<MuscleGroup, Boolean> = emptyMap(),
  val equipmentsChecked: Map<Equipment, Boolean> = emptyMap(),
  val movementTypesChecked: Map<MovementType, Boolean> = emptyMap(),
  val searchExerciseName: String = "",
)

data class ExerciseDetailsUiState(
  val userDefaultWeightUnit: MassUnit = MassUnit.POUNDS,
  val exercise: Exercise? = null,
  val exerciseHistory: Pair<List<Program>, Map<Workout, List<Set>>>? = null,
  val equipment: List<Equipment> = emptyList(),
  val muscles: List<Muscle> = emptyList(),
  val musclesExerciseList: List<MuscleExerciseWithMuscle> = emptyList(),
  val muscleRoleFunctions: List<MuscleRoleFunction> = emptyList(),
  val muscleGroup: List<MuscleGroup> = emptyList(),
  val utility: List<Utility> = emptyList(),
  val force: List<Force> = emptyList(),
  val mechanic: List<Mechanic> = emptyList(),
  val selectedExerciseDetails: Exercise? = null,
  val exerciseRecords: ExerciseRecords? = null,
  val bestSetEstimatedOneRepMaxPoundsChartData: ChartData? = null,
  val bestSetEstimatedOneRepMaxKilogramsChartData: ChartData? = null,
  val bestSetMaxWeightPoundsLiftedChartData: ChartData? = null,
  val bestSetMaxWeightKilogramsLiftedChartData: ChartData? = null,
  val bestSetTotalVolumePoundsChartData: ChartData? = null,
  val bestSetTotalVolumeKilogramsChartData: ChartData? = null,
  val bestSetRepetitionsChartData: ChartData? = null,
)

data class ExerciseRecords(
  val maxVolumePounds: Double? = null,
  val maxVolumeKilograms: Double? = null,
  val maxWeightLiftedPounds: Double? = null,
  val maxWeightLiftedKilograms: Double? = null,
  val totalReps: Int? = null,
  val totalVolumePounds: Double? = null,
  val totalVolumeKilograms: Double? = null,
  val bestPerformanceByRepetitions: List<BestPerformanceByRepetitions?> = emptyList(),
)

class ExercisesViewModel
constructor(
  private val userProfileManager: UserProfileManager,
  private val exercisesRepository: ExercisesRepository,
  private val modernizedLoadRIRReposistory: ModernizedLoadRIRReposistory,
) : ViewModel() {
  private val userProfile = userProfileManager.userProfile.value

  private var _expand: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val expand: StateFlow<Boolean>
    get() = _expand.asStateFlow()

  private val _uiState = MutableStateFlow(ExercisesUiState())
  val uiState: StateFlow<ExercisesUiState> = _uiState.asStateFlow()

  private val _exerciseDetailsUiState = MutableStateFlow(ExerciseDetailsUiState())
  val exerciseDetailsUiState: StateFlow<ExerciseDetailsUiState> =
    _exerciseDetailsUiState.asStateFlow()

  fun setExpand(value: Boolean) {
    _expand.value = value
  }

  private var _modernizedLoadRIRItems: List<ModernizedLoadRIR>? = null

  private var _exerciseDetailsLayoutTabState: MutableStateFlow<Int> = MutableStateFlow(0)
  val exerciseDetailsLayoutTabState: StateFlow<Int>
    get() = _exerciseDetailsLayoutTabState.asStateFlow()

  fun setExerciseDetailsLayoutTabState(value: Int) {
    _exerciseDetailsLayoutTabState.value = value
  }

  private var allExercises = emptyList<Exercise>()

  private val _exercises: MutableStateFlow<List<Exercise>> = MutableStateFlow(emptyList())
  val exercises: StateFlow<List<Exercise>>
    get() = _exercises.asStateFlow()

  private val _equipment: MutableStateFlow<List<Equipment>> = MutableStateFlow(emptyList())
  val equipment: StateFlow<List<Equipment>>
    get() = _equipment.asStateFlow()

  private var _muscleGroup: MutableStateFlow<List<MuscleGroup>> = MutableStateFlow(emptyList())
  val muscleGroup: StateFlow<List<MuscleGroup>>
    get() = _muscleGroup

  private val _utility: MutableStateFlow<List<Utility>> = MutableStateFlow(emptyList())
  val utility: StateFlow<List<Utility>>
    get() = _utility.asStateFlow()

  private val _force: MutableStateFlow<List<Force>> = MutableStateFlow(emptyList())
  val force: StateFlow<List<Force>>
    get() = _force.asStateFlow()

  private val _mechanic: MutableStateFlow<List<Mechanic>> = MutableStateFlow(emptyList())
  val mechanic: StateFlow<List<Mechanic>>
    get() = _mechanic.asStateFlow()

  private val _muscleGroupsChecked = MutableStateFlow(mutableMapOf<MuscleGroup, Boolean>())
  val muscleGroupsChecked
    get() = _muscleGroupsChecked.asStateFlow()

  private val _equipmentsChecked = MutableStateFlow(mutableMapOf<Equipment, Boolean>())
  val equipmentsChecked
    get() = _equipmentsChecked.asStateFlow()

  val movementTypes = MovementType.entries.sortedBy { it.name }

  val movementTypesChecked = movementTypes.map { it to false }.toMutableStateMap()

  init {
    viewModelScope.launch {
      //      Log.i("ExercisesViewModel", "init: getAllExercises")
      getAllExercises()
    }

    viewModelScope.launch {
      //      Log.i("ExercisesViewModel", "init: getEquipment")
      getAllEquipment()
    }

    viewModelScope.launch {
      //      Log.i("ExercisesViewModel", "init: getMuscleGroups")
      getAllMuscleGroups()
    }

    viewModelScope.launch { getAllMechanics() }

    viewModelScope.launch { getAllForces() }

    viewModelScope.launch { getAllMuscleRoleFunctions() }

    viewModelScope.launch { getAllUtilities() }

    viewModelScope.launch { getAllModernizedLoadRIR() }

    _exerciseDetailsUiState.update {
      it.copy(userDefaultWeightUnit = userProfile.defaultWeightUnit)
    }
  }

  /**
   * Central event handler for all UI interactions. This function receives [ExercisesUiEvent]s and
   * updates the [_uiState] accordingly. Using a single entry point for events simplifies state
   * management and debugging.
   */
  fun onEvent(event: ExercisesUiEvent) {
    when (event) {
      is ExercisesUiEvent.SetExpand -> {
        _uiState.update { it.copy(expand = event.value) }
      }

      is ExercisesUiEvent.SetExerciseDetailsLayoutTabState -> {
        _uiState.update { it.copy(exerciseDetailsLayoutTabState = event.value) }
      }

      is ExercisesUiEvent.UpdateMuscleGroupsChecked -> {
        _uiState.update { currentState ->
          // Create a mutable copy of the map to update, then copy back to immutable
          val updatedMap = currentState.muscleGroupsChecked.toMutableMap()
          updatedMap[event.muscleGroup] = event.checked
          currentState.copy(muscleGroupsChecked = updatedMap)
        }
        // Log the updated state for debugging purposes
        Log.i(
          "ExercisesViewModel",
          "updateMuscleGroupsChecked(): ${
            uiState.value.muscleGroupsChecked.toSortedMap { o1, o2 ->
              o1.name.compareTo(o2.name)
            }
          }",
        )
      }

      is ExercisesUiEvent.UpdateEquipmentsChecked -> {
        _uiState.update { currentState ->
          val updatedMap = currentState.equipmentsChecked.toMutableMap()
          updatedMap[event.equipment] = event.checked
          currentState.copy(equipmentsChecked = updatedMap)
        }
        Log.i("ExercisesViewModel", "updateEquipmentsChecked(): ${uiState.value.equipmentsChecked}")
      }

      is ExercisesUiEvent.SetMovementTypesChecked -> {
        _uiState.update { currentState ->
          val updatedMap = currentState.movementTypesChecked.toMutableMap()
          updatedMap[event.movementType] = event.checked
          currentState.copy(movementTypesChecked = updatedMap)
        }
      }

      is ExercisesUiEvent.SetSearchExerciseName -> {
        _uiState.update { it.copy(searchExerciseName = event.value) }
      }

      is ExercisesUiEvent.FilterExercises -> {
        filterExercises()
      }

      is ExercisesUiEvent.GetExerciseDetails -> {
        getExerciseDetails(event.exerciseID)
      }

      //      is ExercisesUiEvent.GetExerciseRecords -> {
      //        getExerciseRecords(event.exercise)
      //      }
    }
  }

  private suspend fun getAllExercises() {
    //    Log.i("ExercisesViewModel", "getAllExercises")

    return coroutineScope {
      exercisesRepository.getAllExercises().collect { list ->
        _exercises.value = list.sortedBy { exercise -> exercise.name }
        allExercises = list.sortedBy { exercise -> exercise.name }
        //        _uiState.update { it.copy(exercises = _exercises.value, allExercises =
        // allExercises) }
        //        Log.i("ExercisesViewModel.getAllExercises()", list.toString())
      }
    }
  }

  private suspend fun getAllEquipment() {
    //    Log.i("ExercisesViewModel", "getEquipment")
    return coroutineScope {
      exercisesRepository.getAllEquipment().collect {
        _equipment.value = it
        _exerciseDetailsUiState.update { it.copy(equipment = _equipment.value) }
        //        Log.i("ExercisesViewModel.getEquipment()", "getEquipment(): $it")

        _equipmentsChecked.value =
          it.map { equipment -> equipment to false }.toMutableStateMap().toMutableMap()
        /*        Log.i(
          "ExercisesViewModel",
          "init _equipmentsChecked: ${_equipmentsChecked.value.keys.toList()}"
        )*/
      }
    }
  }

  private suspend fun getAllMuscleGroups() {
    //    Log.i("ExercisesViewModel", "getMuscleGroups")
    return coroutineScope {
      exercisesRepository.getAllMuscleGroups().collect {
        _muscleGroup.value = it
        _exerciseDetailsUiState.update { it.copy(muscleGroup = _muscleGroup.value) }
        //        Log.i("ExercisesViewModel", "getMuscleGroups() $it")

        _muscleGroupsChecked.value =
          it.map { muscleGroup -> muscleGroup to false }.toMutableStateMap().toMutableMap()
        /*        Log.i(
          "ExercisesViewModel",
          "init _muscleGroupsChecked: ${_muscleGroupsChecked.value.keys.toList()}"
        )*/
      }
    }
  }

  private suspend fun getAllModernizedLoadRIR() {
    //    Log.i("ExercisesViewModel", "getEquipment")
    return coroutineScope {
      modernizedLoadRIRReposistory.getAllModernizedLoadRIR().collect {
        _modernizedLoadRIRItems = it
        //        Log.i("ExercisesViewModel.getEquipment()", "getEquipment(): $it")
      }
    }
  }

  fun getAllUtilities() {
    viewModelScope.launch {
      exercisesRepository.getAllUtilities().collect {
        _utility.value = it
        _exerciseDetailsUiState.update { it.copy(utility = _utility.value) }
      }
    }
  }

  fun getAllForces() {
    viewModelScope.launch {
      exercisesRepository.getAllForces().collect {
        _force.value = it
        _exerciseDetailsUiState.update { it.copy(force = _force.value) }
      }
    }
  }

  fun getAllMechanics() {
    viewModelScope.launch {
      exercisesRepository.getAllMechanics().collect {
        _mechanic.value = it
        _exerciseDetailsUiState.update { it.copy(mechanic = _mechanic.value) }
      }
    }
  }

  fun getAllMuscleRoleFunctions() {
    viewModelScope.launch {
      exercisesRepository.getAllMuscleRolesFunctions().collect { list ->
        _exerciseDetailsUiState.update { it.copy(muscleRoleFunctions = list) }
      }
    }
  }

  fun getMuscleGroupNameByID(muscleGroupID: String): String? {
    var muscleGroupName: String? = null

    viewModelScope.launch {
      muscleGroupName = exercisesRepository.getMuscleGroupNameByID(muscleGroupID)
    }

    return muscleGroupName
  }

  fun updateMuscleGroupsChecked(muscleGroup: MuscleGroup, checked: Boolean) {
    //    val temp = _muscleGroupsChecked.value.apply {  }
    //    temp[muscleGroup] = checked
    _muscleGroupsChecked.value = _muscleGroupsChecked.value.apply { this[muscleGroup] = checked }

    Log.i(
      "ExercisesViewModel",
      "updateMuscleGroupsChecked(): ${
        _muscleGroupsChecked.value.toSortedMap { o1, o2 ->
          o1.name.compareTo(o2.name)
        }
      }",
    )
  }

  fun updateEquipmentsChecked(equipment: Equipment, checked: Boolean) {
    val temp = _equipmentsChecked.value
    _equipmentsChecked.value[equipment] = checked
    _equipmentsChecked.value = temp

    Log.i("ExercisesViewModel", "updateEquipmentsChecked(): ${_equipmentsChecked.value}")
  }

  private var _searchExerciseName = ""

  val searchExerciseName: String
    get() = _searchExerciseName

  fun setSearchExerciseName(value: String) {
    _searchExerciseName = value
  }

  fun filterExercises() {

    val currentState = _uiState.value
    //    if (searchExerciseName.isEmpty()) {
    //      _searchExerciseName = ".*"
    //    }

    val selectedMuscleGroups = mutableListOf<String>()
    _muscleGroupsChecked.value
      .filterValues { it }
      .keys
      .sortedBy { it.name }
      .forEach { muscleGroup -> muscleGroup.id.let { selectedMuscleGroups.add(it) } }

    val selectedEquipments = mutableListOf<String>()
    _equipmentsChecked.value
      .filterValues { it }
      .keys
      .sortedBy { it.name }
      .forEach { equipment -> equipment.id.let { selectedEquipments.add(it) } }

    val selectedMovementTypes = mutableListOf<String>()
    movementTypesChecked
      .filterValues { it }
      .keys
      .sortedBy { it.name }
      .forEach { selectedMovementTypes.add(it.printableName) }

    if (selectedMuscleGroups.isEmpty()) {
      _muscleGroupsChecked.value
        .filterValues { !it }
        .keys
        .sortedBy { it.name }
        .forEach { muscleGroup -> muscleGroup.id.let { selectedMuscleGroups.add(it) } }
    }

    if (selectedEquipments.isEmpty()) {
      _equipmentsChecked.value
        .filterValues { !it }
        .keys
        .sortedBy { it.name }
        .forEach { equipment -> equipment.id.let { selectedEquipments.add(it) } }
    }

    /*
    if (selectedMovementTypes.size == movementTypesChecked.value.size) {
      selectedMovementTypes = mutableListOf("%")
    }
    */

    // Start with all exercises and apply filters sequentially

    Log.i("ExercisesViewModel", "filterExercises().selectedMuscleGroups: $selectedMuscleGroups")
    Log.i("ExercisesViewModel", "filterExercises().selectedEquipments: $selectedEquipments")
    //    Log.i("ExercisesViewModel", "filterExercises().selectedMovementTypes:
    // $selectedMovementTypes")
    Log.i("ExercisesViewModel", "filterExercises()._searchExerciseName: $_searchExerciseName")

    // TODO If _exercises is empty, display "No results" Message
    // TODO replace with _exercises.value.filter { exercise ->
    // exercise.name.contains(_searchExerciseName) }
    //  see Gemini/AIStudio for implementation in a test

    _exercises.update {
      allExercises
        .filter { exercise -> exercise.name.contains(_searchExerciseName, ignoreCase = true) }
        .filter { exercise -> selectedMuscleGroups.contains(exercise.muscleGroupID) }
        .filter { exercise -> selectedEquipments.contains(exercise.equipmentID) }
        .sortedBy { exercise -> exercise.name }
    }

    // Update the UI state with the filtered list
    _uiState.update {
      it.copy(
        exercises =
          allExercises
            .filter { exercise -> exercise.name.contains(_searchExerciseName, ignoreCase = true) }
            .filter { exercise -> selectedMuscleGroups.contains(exercise.muscleGroupID) }
            .filter { exercise -> selectedEquipments.contains(exercise.equipmentID) }
            .sortedBy { exercise -> exercise.name }
      )
    }

    //    viewModelScope.launch {
    //      exercisesRepository
    //        .filterExercises(
    //          exercise = searchExerciseName,
    //          muscleGroupIDs = selectedMuscleGroups.toList(),
    //          equipmentIDs = selectedEquipments.toList(),
    //          //                movementTypes = selectedMovementTypes.toList()
    //        )
    //        .collect() { _exercises.value = it.sortedBy { exercise -> exercise.name } }
    //    }
  }

  // TODO Get Exercise Details
  private var _exercise: MutableStateFlow<Exercise?> = MutableStateFlow(null)

  /**
   * Retrieves the detailed information of an exercise from the repository.
   *
   * @param exerciseID The exercise for which to fetch the details.
   * @return The exercise model with detailed information.
   */
  fun getExerciseDetails(exerciseID: String?) {
    if (exerciseID != null) {
      viewModelScope.launch {
        _exercise.value = exercisesRepository.getExerciseDetails(exerciseID = ObjectId(exerciseID))
        _exerciseDetailsUiState.update {
          it.copy(
            selectedExerciseDetails = _exercise.value,
            bestSetEstimatedOneRepMaxPoundsChartData = null,
            bestSetEstimatedOneRepMaxKilogramsChartData = null,
            bestSetMaxWeightPoundsLiftedChartData = null,
            bestSetMaxWeightKilogramsLiftedChartData = null,
            bestSetTotalVolumePoundsChartData = null,
            bestSetTotalVolumeKilogramsChartData = null,
            bestSetRepetitionsChartData = null,
          )

        }

        _exercise.value?.exrxExerciseID?.let { exrxExerciseID ->
          _exerciseDetailsUiState.update {
            it.copy(musclesExerciseList = exercisesRepository.getMuscleExercise(exrxExerciseID))
          }
        }

        _exercise.value?.let { exercise ->
          val exerciseHistory = getCompletedSetsByWorkout(exercise)
          exerciseHistory?.second?.values?.flatten()?.let { sets ->
            getExerciseRecords(sets, exercise)
            generateChartData(sets)
          }
        }
      }
    }
  }

  fun getRelativeLoadPercentage(
    repCapacity: RepCapacity,
    rpe: Double,
    rir: Double?,
    repsPerformed: Int,
  ): Double? {
    var resultList =
      _modernizedLoadRIRItems
        ?.filter { it.repCapacity == repCapacity }
        ?.filter { it.repsPerformed == repsPerformed }

    resultList = resultList?.filter { it.rir == rir }?.filter { it.rpe == rpe }

    val result = resultList?.firstOrNull()?.relativeLoadPercent

    return result
  }

  fun getCompletedSetsByWorkout(exercise: Exercise): Pair<List<Program>, Map<Workout, List<Set>>>? {
    // TODO store in viewmodel
    val result = exercisesRepository.getCompletedSetsByExercise(exercise)

    _exerciseDetailsUiState.update { it.copy(exerciseHistory = result) }

    return result
  }

  private fun getExerciseRecords(sets: List<Set>, exercise: Exercise) {
    // TODO use List<Set> from getCompletedSetsByWorkout()
    // TODO max volume in 1 workout, max weight lifted, total reps, total volume,
    //   estimated 1RM based on ModernizedLoadRIR or a 1RM formula.
    //   List of best performance by reps & weight. calculate estimated 1RM for each performance

    val maxVolumePounds = sets.mapNotNull { it.setVolumeInPounds }.max()
    val maxVolumeKilograms = sets.mapNotNull { it.setVolumeInKilograms }.max()
    val maxWeightLiftedPounds = sets.mapNotNull { it.weightPounds }.max()
    val maxWeightLiftedKilograms = sets.mapNotNull { it.weightKilograms }.max()

    val totalReps = sets.mapNotNull { it.repetitions }.sum()
    val totalVolumePounds = sets.mapNotNull { it.setVolumeInPounds }.sum()
    val totalVolumeKilograms = sets.mapNotNull { it.setVolumeInKilograms }.sum()

    val bestPerformanceByRepetitions: MutableList<BestPerformanceByRepetitions> = mutableListOf()

    // TODO handle missing data, i.e. where there is no actual performance data

    for (reps in 1..15) {
      val bestPerformanceByRepetitionsItem =
        BestPerformanceByRepetitions(reps, null, null, null, null, null, null, null, null)

      val sortedSets =
        sets.sortedWith(compareByDescending(Set::weightPounds).thenByDescending(Set::repetitions))

      var set =
        sortedSets.filter { it.repetitions!! >= reps }.maxByOrNull { it.weightPounds ?: 0.0 }
      // TODO Calculate 1RM based on ModernizedLoadRIR or a 1RM formula when saving workouts
      // TODO Calculate 1RM based on ModernizedLoadRIR or a 1RM formula.

      if (set == null) {
        set = sortedSets.firstOrNull { it.repetitions != null && it.repetitions!! >= reps }
        if (set == null) {
          set = sortedSets.firstOrNull { it.repetitions != null && it.repetitions!! <= reps }
        }
      }

      bestPerformanceByRepetitionsItem.repetitionsPerformed = set?.repetitions
      bestPerformanceByRepetitionsItem.weightPounds = set?.weightPounds
      bestPerformanceByRepetitionsItem.weightKilograms = set?.weightKilograms
      bestPerformanceByRepetitionsItem.datePerformed = set?.dateCreated
      bestPerformanceByRepetitionsItem.repetitionsInReserve = set?.repetitionsInReserve
      bestPerformanceByRepetitionsItem.ratePerceivedEffort = set?.ratePerceivedEffort

      val estimated1RMPounds =
        if (
          bestPerformanceByRepetitionsItem.ratePerceivedEffort != null ||
            bestPerformanceByRepetitionsItem.repetitionsInReserve != null
        ) {
          estimateTargetOneRepMaxModernized(
            bestPerformanceByRepetitionsItem.weightPounds ?: 0.0,
            bestPerformanceByRepetitionsItem.repetitionsPerformed ?: 0,
            targetReps = reps,
            bestPerformanceByRepetitionsItem.repetitionsInReserve
              ?: 10.0.minus(bestPerformanceByRepetitionsItem.ratePerceivedEffort!!),
            bestPerformanceByRepetitionsItem.ratePerceivedEffort
              ?: 10.0.minus(bestPerformanceByRepetitionsItem.repetitionsInReserve!!),
            repCapacity = exercise.repCapacity,
            getRelativeLoadPercentage = ::getRelativeLoadPercentage,
          )
        } else {
          OneRepMaxEstimateFormulas.calculateOneRepMax(
            bestPerformanceByRepetitionsItem.weightPounds ?: 0.0,
            bestPerformanceByRepetitionsItem.repetitions,
            OneRepMaxEstimateFormulas.Formula.Brzycki,
          )
        }

      bestPerformanceByRepetitionsItem.estimated1RMPounds = estimated1RMPounds
      bestPerformanceByRepetitionsItem.estimated1RMKilograms =
        estimated1RMPounds?.convertPoundToKg()

      bestPerformanceByRepetitions.add(bestPerformanceByRepetitionsItem)
    }

    _exerciseDetailsUiState.update {
      it.copy(
        exerciseRecords =
          ExerciseRecords(
            maxVolumePounds = maxVolumePounds,
            maxVolumeKilograms = maxVolumeKilograms,
            maxWeightLiftedPounds = maxWeightLiftedPounds,
            maxWeightLiftedKilograms = maxWeightLiftedKilograms,
            totalReps = totalReps,
            totalVolumePounds = totalVolumePounds,
            totalVolumeKilograms = totalVolumeKilograms,
            bestPerformanceByRepetitions =
              bestPerformanceByRepetitions.sortedBy { set -> set.repetitions },
          )
      )
    }
  }

  private val timezone = TimeZone.getDefault().toZoneId().toKotlinTimeZone()

  private fun generateChartData(sets: List<Set>) {
    // TODO Add hover text

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val startDate = LocalDate.now().atStartOfDay().toKotlinLocalDateTime().toInstant(timezone)

    val endDate =
      LocalDate.now()
        .plusDays(1)
        .atStartOfDay()
        .minusNanos(1)
        .toKotlinLocalDateTime()
        .toInstant(timezone)

    val day = 1.toDuration(DurationUnit.DAYS)

    val dateSequence =
      generateSequence(seed = startDate..endDate) { currentRange ->
        currentRange.start.minus(day)..currentRange.endInclusive.minus(day)
      }
        .take(90)

    var bestSetEstimated1RMPounds: MutableMap<String, Double> = mutableMapOf()
    var bestSetEstimated1RMKilograms: MutableMap<String, Double> = mutableMapOf()
    var bestSetMaxWeightPoundsLifted: MutableMap<String, Double> = mutableMapOf()
    var bestSetMaxWeightKilogramsLifted: MutableMap<String, Double> = mutableMapOf()
    var bestSetTotalVolumePounds: MutableMap<String, Double> = mutableMapOf()
    var bestSetTotalVolumeKilograms: MutableMap<String, Double> = mutableMapOf()
    var bestSetRepetitions: MutableMap<String, Int> = mutableMapOf()

    dateSequence.forEach { dateRange ->
      val filteredSets =
        sets.filter { set -> set.dateCreated?.let { date -> date in dateRange } == true }

      val label =
        dateRange.start
          .toLocalDateTime(timezone)
          .toJavaLocalDateTime()
          .toLocalDate()
          .format(dateFormatter)

      if (filteredSets.isEmpty()) {
        return@forEach
//        bestSetMaxWeightPoundsLifted.put(label, null)
//        bestSetMaxWeightKilogramsLifted.put(label, null)
//        bestSetEstimated1RMPounds.put(label, null)
//        bestSetEstimated1RMKilograms.put(label, null)
//        bestSetTotalVolumePounds.put(label, null)
//        bestSetTotalVolumeKilograms.put(label, null)
//        bestSetRepetitions.put(label, null)
      }

      bestSetMaxWeightPoundsLifted.put(label, filteredSets.maxOf { it.weightPounds ?: 0.0 })
      bestSetMaxWeightKilogramsLifted.put(label, filteredSets.maxOf { it.weightKilograms ?: 0.0 })
      bestSetEstimated1RMPounds.put(label, filteredSets.maxOf { it.estimated1RMPounds ?: 0.0 })
      bestSetEstimated1RMKilograms.put(
        label,
        filteredSets.maxOf { it.estimated1RMKilograms ?: 0.0 },
      )
      bestSetTotalVolumePounds.put(label, filteredSets.sumOf { it.setVolumeInPounds ?: 0.0 })
      bestSetTotalVolumeKilograms.put(label, filteredSets.sumOf { it.setVolumeInKilograms ?: 0.0 })
      bestSetRepetitions.put(label, filteredSets.maxOf { it.repetitions ?: 0 })
    }

    bestSetMaxWeightPoundsLifted =
      bestSetMaxWeightPoundsLifted.filterNot { it.value == 0.0 }.toMutableMap()
    bestSetMaxWeightKilogramsLifted =
      bestSetMaxWeightKilogramsLifted.filterNot { it.value == 0.0 }.toMutableMap()
    bestSetEstimated1RMPounds =
      bestSetEstimated1RMPounds.filterNot { it.value == 0.0 }.toMutableMap()
    bestSetEstimated1RMKilograms =
      bestSetEstimated1RMKilograms.filterNot { it.value == 0.0 }.toMutableMap()
    bestSetTotalVolumePounds = bestSetTotalVolumePounds.filterNot { it.value == 0.0 }.toMutableMap()
    bestSetTotalVolumeKilograms =
      bestSetTotalVolumeKilograms.filterNot { it.value == 0.0 }.toMutableMap()
    bestSetRepetitions = bestSetRepetitions.filterNot { it.value == 0 }.toMutableMap()

    _exerciseDetailsUiState.update {
      it.copy(
        bestSetEstimatedOneRepMaxPoundsChartData =
          ChartData(
            bestSetEstimated1RMPounds.keys.toList(),
            bestSetEstimated1RMPounds.values.toList(),
          ),
        bestSetEstimatedOneRepMaxKilogramsChartData =
          ChartData(
            bestSetEstimated1RMKilograms.keys.toList(),
            bestSetEstimated1RMKilograms.values.toList(),
          ),
        bestSetMaxWeightPoundsLiftedChartData =
          ChartData(
            bestSetMaxWeightPoundsLifted.keys.toList(),
            bestSetMaxWeightPoundsLifted.values.toList(),
          ),
        bestSetMaxWeightKilogramsLiftedChartData =
          ChartData(
            bestSetMaxWeightKilogramsLifted.keys.toList(),
            bestSetMaxWeightKilogramsLifted.values.toList(),
          ),
        bestSetTotalVolumePoundsChartData =
          ChartData(
            bestSetTotalVolumePounds.keys.toList(),
            bestSetTotalVolumePounds.values.toList(),
          ),
        bestSetTotalVolumeKilogramsChartData =
          ChartData(
            bestSetTotalVolumeKilograms.keys.toList(),
            bestSetTotalVolumeKilograms.values.toList(),
          ),
        bestSetRepetitionsChartData =
          ChartData(bestSetRepetitions.keys.toList(), bestSetRepetitions.values.toList()),
      )
    }
  }
}

data class BestPerformanceByRepetitions(
  var repetitions: Int,
  var repetitionsPerformed: Int?,
  var repetitionsInReserve: Double?,
  var ratePerceivedEffort: Double?,
  var weightPounds: Double?,
  var weightKilograms: Double?,
  var datePerformed: Instant?,
  var estimated1RMPounds: Double?,
  var estimated1RMKilograms: Double?,
)
