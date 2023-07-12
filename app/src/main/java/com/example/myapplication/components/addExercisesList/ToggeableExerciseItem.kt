package com.example.myapplication.components.addExercisesList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.data.ExerciseModel

@Composable
fun ToggleableExerciseItem(
    exercise: ExerciseModel,
    selectExercise: (_exercise: ExerciseModel, selected: Boolean) -> Unit,
    workoutViewModel: WorkoutViewModel = hiltViewModel()
) {

    val padding = 8.dp

    val (selected, onSelected) = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .toggleable(
                enabled = true,
                value = selected,
                onValueChange = {
                    onSelected(it)
                    selectExercise(exercise, it)
                }
            ),
//        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(padding))
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 50.dp)
                .background(
                    if (selected) {
                        MaterialTheme.colorScheme.surface
                    } else {
                        MaterialTheme.colorScheme.errorContainer
                    }
                )
        ) {
//            TODO Get Image from YouTube Thumbnail
            if (selected) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                    imageVector = Icons.Filled.CheckCircle,
                    tint = Color.Green,
                    contentDescription = "Selected"
                )
            } else {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(40.dp),
                    imageVector = Icons.Outlined.Error,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = "No exercise image available."
                )
            }

        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                // TODO Create data class that only take exerciseID, exercise name and MuscleGroup Name
                text = exercise.muscleGroupID,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(modifier = Modifier.width(padding))
    }
}
