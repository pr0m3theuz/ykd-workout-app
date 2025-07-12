package com.example.myapplication.features.exercises

import android.util.Log
import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.common.domain.model.Equipment
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.Force
import com.example.myapplication.common.domain.model.Mechanic
import com.example.myapplication.common.domain.model.MovementType
import com.example.myapplication.common.domain.model.MuscleGroup
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.Utility
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.infrastructure.persistence.ExercisesRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.bson.types.ObjectId

// TODO Implement Clone Exercise Feature

class ExercisesViewModel constructor(private val exercisesRepository: ExercisesRepository) :
  ViewModel() {

  private var _expand: MutableStateFlow<Boolean> = MutableStateFlow(false)
  val expand: StateFlow<Boolean>
    get() = _expand.asStateFlow()

  fun setExpand(value: Boolean) {
    _expand.value = value
  }

  private var _exerciseDetailsLayoutTabState: MutableStateFlow<Int> = MutableStateFlow(0)
  val exerciseDetailsLayoutTabState: StateFlow<Int>
    get() = _exerciseDetailsLayoutTabState.asStateFlow()

  fun setExerciseDetailsLayoutTabState(value: Int) {
    _exerciseDetailsLayoutTabState.value = value
  }

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
      getEquipment()
    }

    viewModelScope.launch {
//      Log.i("ExercisesViewModel", "init: getMuscleGroups")
      getMuscleGroups()
    }
  }

  private suspend fun getAllExercises() {
    //    Log.i("ExercisesViewModel", "getAllExercises")

    return coroutineScope {
      exercisesRepository.getAllExercises().collect { list ->
        _exercises.value = list.sortedBy { exercise -> exercise.name }
//        Log.i("ExercisesViewModel.getAllExercises()", list.toString())
      }
    }
  }

  private suspend fun getEquipment() {
//    Log.i("ExercisesViewModel", "getEquipment")
    return coroutineScope {
      exercisesRepository.getAllEquipment().collect {
        _equipment.value = it
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

  private suspend fun getMuscleGroups() {
//    Log.i("ExercisesViewModel", "getMuscleGroups")
    return coroutineScope {
      exercisesRepository.getAllMuscleGroups().collect {
        _muscleGroup.value = it
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

  fun getAllUtilities() {
    viewModelScope.launch {
      exercisesRepository.getAllUtilities()
    }
  }

  fun getAllForces() {
    viewModelScope.launch { exercisesRepository.getAllForces() }
  }

  fun getAllMechanics() {
    viewModelScope.launch {
      exercisesRepository.getAllMechanics()
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
      }"
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

    Log.i("ExercisesViewModel", "filterExercises().selectedMuscleGroups: $selectedMuscleGroups")
    Log.i("ExercisesViewModel", "filterExercises().selectedEquipments: $selectedEquipments")
//    Log.i("ExercisesViewModel", "filterExercises().selectedMovementTypes: $selectedMovementTypes")
    Log.i("ExercisesViewModel", "filterExercises()._searchExerciseName: $_searchExerciseName")

    // TODO If _exercises is empty, display "No results" Message
    viewModelScope.launch {
      exercisesRepository
        .filterExercises(
          exercise = searchExerciseName,
          muscleGroupIDs = selectedMuscleGroups.toList(),
          equipmentIDs = selectedEquipments.toList(),
          //                movementTypes = selectedMovementTypes.toList()
        )
        .collect() { _exercises.value = it.sortedBy { exercise -> exercise.name } }
    }
  }

  // TODO Get Exercise Details
  private var _exercise: MutableStateFlow<Exercise?> = MutableStateFlow(null)

  val selectedExerciseDetails: StateFlow<Exercise?>
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
        _exercise.value =
          exercisesRepository.getExerciseDetails(exerciseID = ObjectId(exerciseID))
      }
    }
  }

  fun getCompletedSetsByExercise(exercise: Exercise): Pair<List<Program>, Map<Workout, List<Set>>>? {
    return exercisesRepository.getCompletedSetsByExercise(exercise)
  }
}
