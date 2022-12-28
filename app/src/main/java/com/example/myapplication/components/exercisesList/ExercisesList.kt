package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.data.ExerciseModel

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ExercisesScreen(
    exercisesViewModel: ExercisesViewModel
) {
    val exercises by exercisesViewModel.exercises.collectAsStateWithLifecycle(initialValue = emptyList())
    Surface {
        ExercisesLazyColumn(exercises = exercises)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExercisesLazyColumn(exercises: List<ExerciseModel>) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        stickyHeader { /* TODO Search Function */ }
        items(count = exercises.size, key = { index: Int -> exercises[index].id }) { index ->
            val exercise: ExerciseModel = exercises[index]

            ExerciseItem(exercise = exercise)
        }
    }
}
