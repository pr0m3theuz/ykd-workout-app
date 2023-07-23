package com.example.myapplication.components.exercisesList

import android.util.Log
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Equipment
import com.example.myapplication.data.ExerciseModel
import com.example.myapplication.data.ExercisesRepository
import com.example.myapplication.data.Force
import com.example.myapplication.data.Mechanic
import com.example.myapplication.data.MovementType
import com.example.myapplication.data.MuscleGroup
import com.example.myapplication.data.Utility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// TODO Implement Clone Exercise Feature

@HiltViewModel
class ExercisesViewModel
@Inject
constructor(
    private val exercisesRepository: ExercisesRepository,
) : ViewModel() {
    private val _exercises: MutableStateFlow<List<ExerciseModel>> = MutableStateFlow(emptyList())
    val exercises: StateFlow<List<ExerciseModel>>
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

  init {
    viewModelScope.launch { exercisesRepository.getExercises().collect { _exercises.value = it } }
  }

  val movementTypes = MovementType.values().sortedBy { it.name }
  fun getAllMuscleGroups() {
    viewModelScope.launch {
      exercisesRepository.getAllMuscleGroups().collect() { _muscleGroup.value = it }
    }

    if (_muscleGroupsChecked.value.isEmpty()) {
      _muscleGroupsChecked.value =
        _muscleGroup.value.map { it to false }.toMutableStateMap().toMutableMap()
    }
  }

  fun getAllEquipment() {
    viewModelScope.launch {
      exercisesRepository.getAllEquipment().collect() { _equipment.value = it }
    }
    if (_equipmentsChecked.value.isEmpty()) {
      _equipmentsChecked.value =
        _equipment.value.map { it to false }.toMutableStateMap().toMutableMap()
    }
  }

  fun getAllUtilities() {
    viewModelScope.launch {
      exercisesRepository.getAllUtilities().collect() { _utility.value = it }
    }
  }

  fun getAllForces() {
    viewModelScope.launch { exercisesRepository.getAllForces().collect() { _force.value = it } }
  }

  fun getAllMechanics() {
    viewModelScope.launch {
      exercisesRepository.getAllMechanics().collect() { _mechanic.value = it }
    }
  }

  fun getMuscleGroupNameByID(muscleGroupID: String): String? {
    var muscleGroupName: String? = null

    viewModelScope.launch {
      exercisesRepository.getMuscleGroupNameByID(muscleGroupID).collect() {
        muscleGroupName = it.firstOrNull()
      }
    }

    return muscleGroupName
  }

  private val _muscleGroupsChecked = MutableStateFlow(mutableMapOf<MuscleGroup, Boolean>())
  val muscleGroupsChecked
    get() = _muscleGroupsChecked.asStateFlow()

  private val _equipmentsChecked = MutableStateFlow(mutableMapOf<Equipment, Boolean>())
  val equipmentsChecked
    get() = _equipmentsChecked.asStateFlow()

  val movementTypesChecked = movementTypes.map { it to false }.toMutableStateMap()

  fun updateMuscleGroupsChecked(muscleGroup: MuscleGroup, checked: Boolean) {
    _muscleGroupsChecked.value[muscleGroup] = checked
    Log.i(
      "updateMuscleGroupsChecked",
      "${muscleGroup.name}: ${_muscleGroupsChecked.value[muscleGroup]}"
    )
    Log.i("updateMuscleGroupsChecked", "${_muscleGroupsChecked.value}")
  }

  fun getMuscleGroupsCheckedBoolean(muscleGroup: MuscleGroup): Boolean {
    return _muscleGroupsChecked.value[muscleGroup] == true
  }

  fun updateEquipmentsChecked(equipment: Equipment, checked: Boolean) {
    _equipmentsChecked.value[equipment] = checked
    Log.i("updateEquipmentsChecked", "${_equipmentsChecked.value.filterValues { it }.keys}")
  }

  private var _searchExerciseName = ""

  val searchExerciseName: String
    get() = _searchExerciseName

  fun setSearchExerciseName(value: String) {
    _searchExerciseName = value
  }

  fun filterExercises() {

    if (searchExerciseName.isEmpty()) {
      _searchExerciseName = "%"
    }

    var selectedMuscleGroups = mutableListOf<String>()
    _muscleGroupsChecked.value
      .filterValues { !it }
      .keys
      .sortedBy { it.name }
      .forEach { selectedMuscleGroups.add(it.id) }

    var selectedEquipments = mutableListOf<String>()
    _equipmentsChecked.value
      .filterValues { !it }
      .keys
      .sortedBy { it.name }
      .forEach { selectedEquipments.add(it.id) }

    var selectedMovementTypes = mutableListOf<String>()
    movementTypesChecked
      .filterValues { !it }
      .keys
      .sortedBy { it.name }
      .forEach { selectedMovementTypes.add(it.printableName) }

    if (selectedMuscleGroups.size == muscleGroupsChecked.value.size) {
      selectedMuscleGroups = mutableListOf("%")
    }

    if (selectedEquipments.size == equipmentsChecked.value.size) {
      selectedEquipments = mutableListOf("%")
    }

    /*
    if (selectedMovementTypes.size == movementTypesChecked.value.size) {
      selectedMovementTypes = mutableListOf("%")
    }
    */

    /*
        Log.i("muscleGroups", "${_muscleGroup.value}")
        Log.i("muscleGroups.map ", "${_muscleGroup.value.map { it to false }.toList()}")
        Log.i("muscleGroupsChecked", "${_muscleGroupsChecked.value
          .filterValues { it }.keys}")
        Log.i("selectedMuscleGroups", selectedMuscleGroups.toString())
        Log.i("selectedEquipments", selectedEquipments.toString())
        Log.i("selectedMovementTypes", selectedMovementTypes.toString())
    */

    // TODO If _exercises is empty, display "No results" Message
    viewModelScope.launch {
      exercisesRepository
        .filterExercises(
          exercise = searchExerciseName,
          muscleGroupIDs = selectedMuscleGroups.toList(),
          equipmentIDs = selectedEquipments.toList()
          //                movementTypes = selectedMovementTypes.toList()
        )
        .collect() { _exercises.value = it }
    }
  }

  // TODO Get Exercise Details
  private val _exercise: MutableStateFlow<List<ExerciseModel>> = MutableStateFlow(emptyList())

  val selectedExerciseDetails: StateFlow<List<ExerciseModel>>
    get() = _exercise.asStateFlow()

  /**
   * Retrieves the detailed information of an exercise from the repository.
   *
   * @param exerciseID The exercise for which to fetch the details.
   * @return The exercise model with detailed information.
   */
  fun getExerciseDetails(exerciseID: String?) {
    if (exerciseID != null) {
      viewModelScope.launch {
        exercisesRepository.getExerciseDetails(exercisePK = exerciseID.toLong()).collect() {
          _exercise.value = it
        }
      }
    }
  }
}
