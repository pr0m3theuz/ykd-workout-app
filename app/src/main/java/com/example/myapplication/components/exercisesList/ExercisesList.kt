package com.example.myapplication.components.exercisesList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.components.SearchBar
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
    Column(
        modifier = Modifier.padding(horizontal = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Exercises",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            SearchBar()

            ExercisesFilterMenu()
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        contentPadding = PaddingValues(0.dp)
        ) {
            stickyHeader { /* TODO Search Function */ }
            items(count = exercises.size, key = { index: Int -> exercises[index].id }) { index ->
                val exercise: ExerciseModel = exercises[index]

                ExerciseItem(exercise = exercise)
            }
        }
    }
}
