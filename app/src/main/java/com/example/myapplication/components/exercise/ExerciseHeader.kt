package com.example.myapplication.components.exercise

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.components.ExerciseOptionsDropdownMenu
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.data.model.ExerciseNote
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.averageWeight
import com.example.myapplication.data.model.totalRepetitions
import com.example.myapplication.data.model.totalVolume
import org.koin.androidx.compose.koinViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun ExerciseScreen(
    navController: NavController,
    workoutViewModel: WorkoutViewModel = koinViewModel(),
    _exerciseSets: ExerciseSets,
) {
    val exerciseSet by remember { mutableStateOf(_exerciseSets) }
    //  Log.i("ExerciseScreen", "$_exerciseSets")
    //  Log.i("ExerciseScreen", "$_exerciseNotes")

    ExerciseHeader(
        navController = navController,
        exerciseSets = exerciseSet,
        removeExercise = { exerciseSets: ExerciseSets ->
            workoutViewModel.removeExerciseSets(exerciseSets)
        },
        //    addNote = { exerciseSets: ExerciseSets -> workoutViewModel.addExerciseNote(exerciseSets)
        // },
        //    removeNote = { exerciseSets: ExerciseSets, note: ExerciseNote ->
        //      workoutViewModel.removeExerciseNote(exerciseSets, note)
        //    }
    )
}

@Composable
fun ExerciseHeader(
    navController: NavController,
    exerciseSets: ExerciseSets,
    removeExercise: (exerciseSets: ExerciseSets) -> Unit,
    //  addNote: (exerciseSets: ExerciseSets) -> Unit,
    //  removeNote: (exerciseSets: ExerciseSets, note: ExerciseNote) -> Unit,
    workoutViewModel: WorkoutViewModel = viewModel()
) {
    //    TODO: Create a button that toggle the notes
    val (notesVisible, setNotesVisible) = remember { mutableStateOf(false) }

    //    TODO fun updateExerciseNotes() updates notes when workout is completed

    fun removeNote(exerciseSets: ExerciseSets, note: ExerciseNote) {
        workoutViewModel.removeExerciseNote(exerciseSets, note)
        //    notes = workoutViewModel.getExerciseNotesByExercise(exerciseSets)
    }

    fun addNote(exerciseSets: ExerciseSets) {
        workoutViewModel.addExerciseNote(exerciseSets)
        //    notes = workoutViewModel.getExerciseNotesByExercise(exerciseSets)
    }

    fun pinNote(note: ExerciseNote, pin: Boolean) {
        Log.i("ExerciseHeader.pinNote", "pin: $pin")
        workoutViewModel.pinExerciseNote(exerciseSets, note, pin)
    }

    Log.i("ExerciseHeader", "$exerciseSets.notes")
    //  Log.i("ExerciseHeader", "$notes")

    Row(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseTitleButton(
                    onClick = { navController.navigate("exerciseDetails/${exerciseSets.exercise?._id?.toHexString()}") },
                    modifier = Modifier.weight(1.0f, fill = true)
                ) {
                    exerciseSets.exercise?.name?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.titleSmall,
                        )
                    }
                }

                ExerciseOptionsDropdownMenu(
                    exerciseSets = exerciseSets,
                    addNotes = {
                        setNotesVisible(true)
                        addNote(exerciseSets)
                    },
                    removeExercise = { removeExercise(exerciseSets) }
                )
            }

            Log.i("ExerciseHeader", "$notesVisible")
            ExerciseNotesList(
                notesVisible = notesVisible,
                exerciseSets = exerciseSets,
                _pinNote = { note, pin -> pinNote(note, pin) },
                removeNote = { exerciseSets, note -> removeNote(exerciseSets, note) },
            )

            ExerciseSetsWorkoutSummary(_exerciseSets = exerciseSets)

            SetContentHeader()
        }
    }
}

@Composable
private fun ExerciseNotesList(
    notesVisible: Boolean,
    exerciseSets: ExerciseSets,
    _pinNote: (note: ExerciseNote, pin: Boolean) -> Unit,
    removeNote: (exerciseSets: ExerciseSets, note: ExerciseNote) -> Unit
) {
    Log.i("ExerciseNotesList", "${exerciseSets.notes.size}")
    var visible by remember { mutableStateOf(notesVisible) }

    if (exerciseSets.notes.isEmpty()) {
        visible = false
    }

    Log.i("ExerciseNotesList", "visible: $visible")

    AnimatedVisibility(
        visible = notesVisible,
        enter = expandVertically(animationSpec = tween(250)),
        exit = shrinkVertically(animationSpec = tween(250))
    ) {}

    exerciseSets.notes.forEach { note ->
        Log.i("ExerciseNotesList", "Note: $note")
        SwipeToDismissItem(onDismissRequest = { removeNote(exerciseSets, note) }) {
            ExerciseNotes(
                exerciseNote = note,
                _pinNote = { pin -> _pinNote(note, pin) },
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun SetContentHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val textStyle = MaterialTheme.typography.bodySmall

        Box(
            modifier = Modifier
                .padding(0.dp)
                .width(48.dp), contentAlignment = Alignment.Center
        ) {
            Text("Set", style = textStyle)
        }

        Box(
            modifier =
            Modifier
                //                        .padding(4.dp)
                .width(110.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Previous", style = textStyle)
        }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(64.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Weight", style = textStyle)
        }

        Box(
            modifier = Modifier
                .padding(PaddingValues(horizontal = 4.dp))
                .width(76.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Reps", style = textStyle)
        }

        Box(
            modifier = Modifier
                .padding(0.dp)
                .width(40.dp), contentAlignment = Alignment.Center
        ) {
            Text("RIR", style = textStyle)
        }

        Spacer(
            Modifier
                .width(100.dp)
                .padding(PaddingValues(horizontal = 4.dp))
        )
    }
}

@Composable
private fun ExerciseSetsWorkoutSummary(
    workoutViewModel: WorkoutViewModel = koinViewModel(),
    _exerciseSets: ExerciseSets,
) {
    // TODO Give option to show either Total Volume or Total Working Volume
    // TODO Update Values on user input, cause keyboard to
    val textStyle = MaterialTheme.typography.bodySmall

    val exercisesSets by workoutViewModel.exercisesSets.collectAsStateWithLifecycle()
    val exerciseSets = exercisesSets.first { it.exSetsUUID == _exerciseSets.exSetsUUID }

    //    val exerciseSets = _exerciseSets

    val totalVolume by remember { mutableDoubleStateOf(exerciseSets.totalVolume()) }
    val totalRepetitions by remember { mutableIntStateOf(exerciseSets.totalRepetitions()) }
    val averageWeight by remember { mutableDoubleStateOf(exerciseSets.averageWeight()) }

    val df = DecimalFormat("#,###.##")
    df.roundingMode = RoundingMode.HALF_UP

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            //      TODO get default value for weight
            text = "Volume: ${df.format(totalVolume)}${exerciseSets.exercise?.weightUnit}",
            style = textStyle
        )
        Text(text = "Reps: $totalRepetitions", style = textStyle)
        Text(text = "Avg Weight: ${df.format(averageWeight)}", style = textStyle)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExerciseTitleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    elevation: ButtonElevation? = null,
    shape: Shape = androidx.compose.material.MaterialTheme.shapes.small,
    border: BorderStroke? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    content: @Composable RowScope.() -> Unit
) {
    val contentColor by colors.contentColor(enabled)
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = colors.backgroundColor(enabled).value,
        contentColor = contentColor.copy(alpha = 1f),
        border = border,
        elevation = elevation?.elevation(enabled, interactionSource)?.value ?: 0.dp,
        interactionSource = interactionSource,
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(value = androidx.compose.material.MaterialTheme.typography.button) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minWidth = ButtonDefaults.MinWidth,
                            minHeight = ButtonDefaults.MinHeight
                        )
                        .padding(contentPadding),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    content = content
                )
            }
        }
    }
}
