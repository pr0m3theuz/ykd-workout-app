package com.example.myapplication.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.WorkoutViewModel
import com.example.myapplication.data.SetModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExerciseScreen(workoutViewModel: WorkoutViewModel = viewModel()) {
    exerciseLayout(
        workoutViewModel.exerciseSets,
        { workoutViewModel.addSet() },
        { id: Long -> workoutViewModel.remove(id) },
        { id: Long, newWeight: Double? -> workoutViewModel.updateSetWeight(id, newWeight) },
        { id: Long, newReps: Int? -> workoutViewModel.updateSetReps(id, newReps) },
        { id: Long, newRIR: Double? -> workoutViewModel.updateSetRIR(id, newRIR) },
        { id: Long, newRPE: Double? -> workoutViewModel.updateSetRPE(id, newRPE) },
        { id: Long, setComplete: Boolean -> workoutViewModel.setComplete(id, setComplete) }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun exerciseLayout(
    exerciseSets: List<SetModel>,
    addSet: () -> Unit,
    removeSet: (id: Long) -> Unit,
    updateSetWeight: (id: Long, newWeight: Double?) -> Unit,
    updateSetReps: (id: Long, newReps: Int?) -> Unit,
    updateSetRIR: (id: Long, newRIR: Double?) -> Unit,
    updateSetRPE: (id: Long, newRPE: Double?) -> Unit,
    setComplete: (id: Long, setComplete: Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Exercise Name"
                )

                exerciseOptionsDropdownMenu()
            }

            Spacer(Modifier.height(8.dp))

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
                { id: Long, setComplete: Boolean -> setComplete(id, setComplete) }
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
@RequiresApi(Build.VERSION_CODES.O)
val formatter = DateTimeFormatter.ofPattern("yyMMddhhmmAS")

@RequiresApi(Build.VERSION_CODES.O)
private fun initExerciseSets(): List<SetModel> {
    return List<SetModel>(6) { index ->
        SetModel(
            setNumber = index,
            exerciseWeight = 100.00,
            repetitions = 10
        )
    }
}
