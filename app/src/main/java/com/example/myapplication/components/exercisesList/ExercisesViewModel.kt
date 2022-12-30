package com.example.myapplication.components.exercisesList

import androidx.compose.runtime.toMutableStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

public class ExercisesViewModel(repository: Repository) : ViewModel() {

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
}
