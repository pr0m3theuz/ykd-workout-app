package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ExerciseModel

@Composable
fun ExerciseItem(exercise: ExerciseModel) {
    Row {
        Box() {
//            TODO Get Image from YouTube Thumbnail
        }

        Column {
            Text(text = exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = exercise.bodyPart, color = Color.Gray, fontSize = 14.sp)
        }
    }
}
