package com.example.myapplication.components.exercisesList

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.ExerciseModel

@Composable
fun ExerciseItem(exercise: ExerciseModel, navController: NavController) {
    val context = LocalContext.current

    val padding = 8.dp

    Row(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
            .clickable(
                enabled = true,
                onClickLabel = null,
                onClick = {
                    navController.navigate("exerciseDetails/${exercise.primaryKey}")
                    Toast
                        .makeText(
                            context,
                            "TODO: Navigate to Exercise Details Screen",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            ), // TODO Create Exercise Details Screen
//        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(padding))
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 50.dp)
                .background(MaterialTheme.colorScheme.errorContainer)
        ) {
//            TODO Get Image from YouTube Thumbnail
            Icon(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(40.dp),
                imageVector = Icons.Outlined.Error,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = "No exercise image available."
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = exercise.muscleGroup,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(modifier = Modifier.width(padding))
    }
}
