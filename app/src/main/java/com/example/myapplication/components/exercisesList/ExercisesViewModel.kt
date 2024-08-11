package com.example.myapplication.components.exercisesList

import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Equipment
import com.example.myapplication.data.model.Exercise
import com.example.myapplication.data.model.Force
import com.example.myapplication.data.model.Mechanic
import com.example.myapplication.data.model.MovementType
import com.example.myapplication.data.model.MuscleGroup
import com.example.myapplication.data.model.Utility
import com.example.myapplication.data.network.ExercisesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

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

  val movementTypes = MovementType.values().sortedBy { it.name }

  val movementTypesChecked = movementTypes.map { it to false }.toMutableStateMap()

  init {
    viewModelScope.launch {
      getEquipmentAndMuscleGroups()
      getAllExercises()
    }
  }

  private suspend fun getAllExercises() {
    //    Log.i("ExercisesViewModel", "getAllExercises")

    return coroutineScope {
      exercisesRepository.getAllExercises().collect() { _exercises.value = it }
    }
  }

  private suspend fun getEquipmentAndMuscleGroups() {
    //    Log.i("ExercisesViewModel", "getEquipmentAndMuscleGroups")
    return coroutineScope {
      val equipments = async { exercisesRepository.getAllEquipment() }
      val muscleGroups = async { exercisesRepository.getAllMuscleGroups() }
      init(equipments.await(), muscleGroups.await())
    }
  }

  private fun init(equipment: List<Equipment>, muscleGroup: List<MuscleGroup>) {
    //    Log.i("ExercisesViewModel", "init: _muscleGroupsChecked")

    _equipment.value = equipment
    if (_equipmentsChecked.value.isEmpty()) {
      _equipmentsChecked.value = equipment.map { it to false }.toMutableStateMap().toMutableMap()
    }

    _muscleGroup.value = muscleGroup
    if (_muscleGroupsChecked.value.isEmpty()) {
      _muscleGroupsChecked.value =
        muscleGroup.map { it to false }.toMutableStateMap().toMutableMap()
    }

    //    Log.i("ExercisesViewModel", "${_muscleGroupsChecked.value.keys.toList()}")
    //    Log.i("ExercisesViewModel", "${_equipmentsChecked.value.keys.toList()}")
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
      muscleGroupName = exercisesRepository.getMuscleGroupNameByID(muscleGroupID)
    }

    return muscleGroupName
  }

  fun updateMuscleGroupsChecked(muscleGroup: MuscleGroup, checked: Boolean) {
    _muscleGroupsChecked.value[muscleGroup] = checked
  }

  fun updateEquipmentsChecked(equipment: Equipment, checked: Boolean) {
    _equipmentsChecked.value[equipment] = checked
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
      .forEach { muscleGroup -> muscleGroup._id?.let { selectedMuscleGroups.add(it) } }

    val selectedEquipments = mutableListOf<String>()
    _equipmentsChecked.value
      .filterValues { it }
      .keys
      .sortedBy { it.name }
      .forEach { equipment -> equipment._id?.let { selectedEquipments.add(it) } }

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
        .forEach { muscleGroup -> muscleGroup._id?.let { selectedMuscleGroups.add(it) } }
    }

    if (selectedEquipments.isEmpty()) {
      _equipmentsChecked.value
        .filterValues { !it }
        .keys
        .sortedBy { it.name }
        .forEach { equipment -> equipment._id?.let { selectedEquipments.add(it) } }
    }

    /*
    if (selectedMovementTypes.size == movementTypesChecked.value.size) {
      selectedMovementTypes = mutableListOf("%")
    }
    */

    // TODO If _exercises is empty, display "No results" Message
    viewModelScope.launch {
      exercisesRepository
        .filterExercises(
          exercise = searchExerciseName,
          muscleGroupIDs = selectedMuscleGroups.toList(),
          equipmentIDs = selectedEquipments.toList(),
          //                movementTypes = selectedMovementTypes.toList()
        )
        .collect() { _exercises.value = it }
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
          exercisesRepository.getExerciseDetails(exerciseID = ObjectId(exerciseID).asObjectId())
      }
    }
  }
}
