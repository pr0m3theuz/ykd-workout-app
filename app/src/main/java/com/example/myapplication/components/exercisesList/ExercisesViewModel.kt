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
    viewModelScope.launch {
      println("ExercisesViewModel: collected exercises!")
      exercisesRepository.getExercises().collect { _exercises.value = it }

      Log.i("MuscleGroup", _muscleGroup.toString())

      /*exercisesRepository.getAllEquipment().collect { _equipment.value = it }


      exercisesRepository.getAllUtilities().collect { _utility.value = it }
      exercisesRepository.getAllForces().collect { _force.value = it }
      exercisesRepository.getAllMechanics().collect { _mechanic.value = it }*/
    }
  }

  val muscleGroups = _muscleGroup.value
  val equipments = _equipment.value
  val movementTypes = MovementType.values().sortedBy { it.name }

  val muscleGroupsChecked = muscleGroups.map { it to false }.toMutableStateMap()
  val equipmentsChecked = equipments.map { it to false }.toMutableStateMap()
  val movementTypesChecked = movementTypes.map { it to false }.toMutableStateMap()

  private val all = muscleGroupsChecked + movementTypesChecked + equipmentsChecked
  val selected = all.filterValues { it }

  //    TODO Implement
  //    private val _selectedCategories = MutableStateFlow(_selected)
  //    val selectedCategories: StateFlow<Map<Enum<*>, Boolean>>
  //        get() = _selectedCategories

  //    TODO Implement
  /*    fun onCategoryChange(category: Enum<*>) {
      if (muscleGroupsChecked.containsKey(category)) {
          muscleGroupsChecked[category as MuscleGroup] = false
      } else if (equipmentsChecked.containsKey(category)) {
          equipmentsChecked[category as Equipment] = false
      } else if (movementTypesChecked.containsKey(category)) {
          movementTypesChecked[category as MovementType] = false
      }
  }*/

  //    TODO Implement
  /*fun getPrintableName(category: Enum<*>): String {
      var printableName: String = ""

      if (muscleGroups.contains(category)) {
          muscleGroups.forEach {
              if (it.name == category.name) {
                  printableName = it.printableName
              }
          }
      } else if (equipmentsChecked.containsKey(category)) {
          equipments.forEach {
              if (it.name == category.name) {
                  printableName = it.printableName
              }
          }
      } else if (movementTypesChecked.containsKey(category)) {
          movementTypes.forEach {
              if (it.name == category.name) {
                  printableName = it.printableName
              }
          }
      }
      return printableName
  }*/

  fun filterExercises(search: String) {
    var searchExerciseName = search

    if (searchExerciseName.isEmpty()) {
      searchExerciseName = "%%"
    }

    //        TODO Implement Filter
    /*val selectedMuscleGroups = mutableListOf<String>()
    muscleGroupsChecked.filterValues { it }.keys.sortedBy { it.printableName }
        .forEach { selectedMuscleGroups.add(it.printableName) }
    val selectedEquipments = mutableListOf<String>()
    equipmentsChecked.filterValues { it }.keys.sortedBy { it.name }
        .forEach { selectedEquipments.add(it.printableName) }
    val selectedMovementTypes = mutableListOf<String>()
    movementTypesChecked.filterValues { it }.keys.sortedBy { it.name }
        .forEach { selectedMovementTypes.add(it.printableName) }

    if (selectedEquipments.isEmpty()) {
        selectedEquipments.add("%%")
    } else if (selectedMovementTypes.isEmpty()) {
        selectedMovementTypes.add("%%")
    } else if (selectedMuscleGroups.isEmpty()) {
        selectedMuscleGroups.add("%%")
    }*/

    //        TODO If _exercises is empty, display "No results" Message
    viewModelScope.launch {
      exercisesRepository
        .filterExercises(
          searchExerciseName
          //                selectedMuscleGroups.toList(),
          //                selectedEquipments.toList()
          //                movementTypes = selectedMovementTypes.toList()
        )
        .collect() {
          _exercises.value = it
          Log.i("repository.filterExercises", it.toString())
        }
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
      //            println("getExerciseDetails() called")
      viewModelScope.launch {
        println("getExerciseDetails() viewModelScope launched")
        exercisesRepository.getExerciseDetails(exercisePK = exerciseID.toLong()).collect() {
          _exercise.value = it
          //                    println("getExerciseDetails() query results it: $it")
          //                    println("getExerciseDetails() query results exercise:
          // ${_exercise.value}")
        }
      }
    }
    println("getExerciseDetails() exercise.first(): ${_exercise.value}")
    //        }
    //        return exercise.first()
  }

  fun getAllMuscleGroups() {
    viewModelScope.launch {
      exercisesRepository.getAllMuscleGroups().collect() { _muscleGroup.value = it }
    }
  }

  fun updateMuscleGroupsChecked(muscleGroup: MuscleGroup, checked: Boolean) {
    muscleGroupsChecked[muscleGroup] = checked
  }

  fun getAllEquipment() {
    viewModelScope.launch {
      exercisesRepository.getAllEquipment().collect() { _equipment.value = it }
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
}
