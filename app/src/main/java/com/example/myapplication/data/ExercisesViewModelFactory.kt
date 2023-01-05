package com.example.myapplication.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.components.exercisesList.ExercisesViewModel

class ExercisesViewModelFactory(private var repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExercisesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExercisesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
