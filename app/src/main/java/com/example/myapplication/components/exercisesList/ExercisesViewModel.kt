package com.example.myapplication.components.exercisesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.ExerciseModel
import com.example.myapplication.data.Repository
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
}
