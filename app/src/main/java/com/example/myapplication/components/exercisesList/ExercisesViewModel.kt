package com.example.myapplication.components.exercisesList

import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
public class ExercisesViewModel @Inject constructor(private var repository: Repository) :
    ViewModel() {

    private val _exercises: MutableStateFlow<List<ExerciseModel>> = MutableStateFlow(emptyList())
    val exercises: StateFlow<List<ExerciseModel>>
        get() = _exercises.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getExercises().collect {
                _exercises.value = it
            }
        }
    }

    val muscleGroups = MuscleGroup.values().sortedBy { it.name }

    val equipments = Equipment.values().sortedBy { it.name }

    val movementTypes = MovementType.values().sortedBy { it.name }

    val muscleGroupsChecked = muscleGroups.map { it to false }.toMutableStateMap()
    val equipmentsChecked = equipments.map { it to false }.toMutableStateMap()
    val movementTypesChecked = movementTypes.map { it to false }.toMutableStateMap()

    private val all = muscleGroupsChecked + movementTypesChecked + equipmentsChecked
    val selected = all.filterValues { it }

//    private val _selectedCategories = MutableStateFlow(_selected)
//    val selectedCategories: StateFlow<Map<Enum<*>, Boolean>>
//        get() = _selectedCategories

    fun onCategoryChange(category: Enum<*>) {
        if (muscleGroupsChecked.containsKey(category)) {
            muscleGroupsChecked[category as MuscleGroup] = false
        } else if (equipmentsChecked.containsKey(category)) {
            equipmentsChecked[category as Equipment] = false
        } else if (movementTypesChecked.containsKey(category)) {
            movementTypesChecked[category as MovementType] = false
        }
    }

    fun getPrintableName(category: Enum<*>): String {
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
    }

    fun filterExercises(searchExerciseName: String = "%%") {
        val selectedMuscleGroups = mutableListOf<String>()
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
        }

        viewModelScope.launch {
            repository.filterExercises(
                searchExerciseName,
                selectedMuscleGroups.toList(),
                selectedEquipments.toList()
//                movementTypes = selectedMovementTypes.toList()
            )
        }
    }
}
