package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.myapplication.components.exercise.ExerciseScreen
import com.example.myapplication.data.ExerciseSetModel

@Composable
fun ExerciseSetLazyColumn(
    exercisesSets: List<ExerciseSetModel>,
    startRestTimer: (exerciseSet: ExerciseSetModel, setID: Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
        //  verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(count = exercisesSets.size, key = { index -> exercisesSets[index].id }) { index ->

            ExerciseScreen(
                exerciseSet = exercisesSets[index],
                startRestTimer = { setID -> startRestTimer(exercisesSets[index], setID) }
            )
        }
    }
}
