package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.ExerciseModel

@Composable
fun ExerciseItem(exercise: ExerciseModel) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(width = 50.dp, height = 50.dp)
                .background(Color.Gray)
        ) {
//            TODO Get Image from YouTube Thumbnail
        }

        Column {
            Text(text = exercise.name, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = exercise.bodyPart, color = Color.Gray, fontSize = 14.sp)
        }
    }
}
