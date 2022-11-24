package com.example.myapplication.components.exercise

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ExerciseViewModel
import com.example.myapplication.components.ExerciseOptionsDropdownMenu
import com.example.myapplication.components.ExerciseSetLazyColumn
import com.example.myapplication.data.ExerciseModel
import com.example.myapplication.data.SetModel
import com.example.myapplication.data.SetTypeModel
import java.time.format.DateTimeFormatter

@Composable
fun ExerciseScreen(exerciseViewModel: ExerciseViewModel = viewModel()) {
    ExerciseLayout(
        exercise = ExerciseModel(name = "Exercise Name"),
        exerciseViewModel.exerciseSets,
        { exerciseViewModel.addSet() },
        { id: Long -> exerciseViewModel.remove(id) },
        { id: Long, newWeight: Double? -> exerciseViewModel.updateSetWeight(id, newWeight) },
        { id: Long, newReps: Int? -> exerciseViewModel.updateSetReps(id, newReps) },
        { id: Long, newRIR: Double? -> exerciseViewModel.updateSetRIR(id, newRIR) },
        { id: Long, newRPE: Double? -> exerciseViewModel.updateSetRPE(id, newRPE) },
        { id: Long, setComplete: Boolean -> exerciseViewModel.setComplete(id, setComplete) },
        { id: Long, setType: SetTypeModel -> exerciseViewModel.updateSetType(id, setType) }
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExerciseLayout(
    exercise: ExerciseModel,
    exerciseSets: List<SetModel>,
    addSet: () -> Unit,
    removeSet: (id: Long) -> Unit,
    updateSetWeight: (id: Long, newWeight: Double?) -> Unit,
    updateSetReps: (id: Long, newReps: Int?) -> Unit,
    updateSetRIR: (id: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (id: Long, newRPE: Double?) -> Unit,
    setComplete: (id: Long, setComplete: Boolean) -> Unit,
    updateSetType: (id: Long, setType: SetTypeModel) -> Unit
) {
    var notesVisible by remember { mutableStateOf(exercise.notesPinned) }

//    TODO fun updateExerciseNotes() updates notes when workout is completed

    Row(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO Change to a button that takes the user to a new screen with more info about the exercise
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Exercise Name"
                )

                ExerciseOptionsDropdownMenu(addNotes = { notesVisible = true })
            }

            AnimatedVisibility(
                visible = notesVisible,
                enter = slideInHorizontally(animationSpec = tween(250)),
                exit = slideOutHorizontally(animationSpec = tween(250))
            ) {
// TODO Add swipe to dismiss functionality and maybe wrap in a LazyColumn
                ExerciseNotes(exercise = exercise)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp)
                        .width(48.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Text("SET")
                }

                Box(
                    modifier = Modifier
//                        .padding(4.dp)
                        .width(128.dp),
                    contentAlignment = Alignment.Center
                ) { Text("PREVIOUS") }

                Box(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 4.dp))
                        .width(72.dp),
                    contentAlignment = Alignment.Center
                ) { Text("WEIGHT") }

                Box(
                    modifier = Modifier
                        .padding(PaddingValues(horizontal = 4.dp))
                        .width(60.dp),
                    contentAlignment = Alignment.Center
                ) { Text("REPS") }

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(40.dp),
                    contentAlignment = Alignment.Center
                ) { Text("RIR") }

                Spacer(
                    Modifier
                        .width(40.dp)
                        .padding(PaddingValues(horizontal = 4.dp))
                )
            }

            ExerciseSetLazyColumn(
                exerciseSets,
                { id: Long -> removeSet(id) },
                { id: Long, newWeight: Double? -> updateSetWeight(id, newWeight) },
                { id: Long, newReps: Int? -> updateSetReps(id, newReps) },
                { id: Long, newRIR: Double? -> updateSetWeight(id, newRIR) },
                { id: Long, newRPE: Double? -> updateSetWeight(id, newRPE) },
                { id: Long, setComplete: Boolean -> setComplete(id, setComplete) },
                { id: Long, setType: SetTypeModel -> updateSetType(id, setType) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { addSet() }) {
                    Text("Add Set")
                }
            }
        }
    }
}

// TODO Remove
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

private fun initExerciseSets(): List<SetModel> {
    return List(6) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10
        )
    }
}
