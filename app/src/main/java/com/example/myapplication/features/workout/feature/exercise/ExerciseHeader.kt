@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.example.myapplication.features.workout.feature.exercise

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonElevation
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.common.domain.model.ExerciseExerciseSets
import com.example.myapplication.common.domain.model.ExerciseNote
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.common.domain.model.averageWeight
import com.example.myapplication.common.domain.model.calculateTotalVolume
import com.example.myapplication.common.domain.model.totalRepetitions
import com.example.myapplication.features.ExerciseOptionsDropdownMenu
import com.example.myapplication.features.workout.WorkoutViewModel
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
fun ExerciseScreen(
  navController: NavController,
  workoutViewModel: WorkoutViewModel,
  _exerciseSets: ExerciseExerciseSets,
  weightUnit: String,
  exerciseWeightUnit: String?,
  userDefaultWeightUnit: String,
  removeExerciseSets: () -> Unit,
  addExerciseNote: () -> Unit,
  removeExerciseNote: (note: ExerciseNote) -> Unit,
  pinExerciseNote: (note: ExerciseNote, pinned: Boolean) -> Unit,
  updateExerciseWeightUnit: (weightUnit: String?) -> Unit,
  workoutComponents: List<WorkoutComponent> = listOf(),
  onCreateSuperset: (List<ExerciseExerciseSets>) -> Unit = {},
) {

  val exerciseSets by remember { mutableStateOf(_exerciseSets) }
  //  Log.i("ExerciseScreen", "$_exerciseSets")
  //  Log.i("ExerciseScreen", "$_exerciseNotes")

  //  val weightUnit = workoutViewModel.getWeightUnit(exerciseSets)
  //  val exerciseWeightUnit = exerciseSets.exercise?.weightUnit
  //  val userDefaultWeightUnit = workoutViewModel.userDefaultWeightUnit

  //  Log.i("ExerciseScreen", "_exerciseSets: $_exerciseSets".replace("\n",""))
  //  Log.i("ExerciseScreen", "exerciseSets: ${exerciseSets}")

  val (notesVisible, setNotesVisible) = remember { mutableStateOf(false) }

  ExerciseHeader(
    titleButton = { modifier: Modifier ->
      ExerciseTitleButton(
        onClick = {
          navController.navigate("exerciseDetails/${exerciseSets.exercise.id.toHexString()}")
        },
        modifier = modifier,
      ) {
        exerciseSets.exercise.name.let {
          Text(
            text = it,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
          )
        }
      }
    },
    exerciseOptionsMenu = {
      ExerciseOptionsDropdownMenu(
        workoutViewModel = workoutViewModel,
        exerciseSets = exerciseSets,
        addNotes = {
          setNotesVisible(true)
          addExerciseNote()
        },
        removeExerciseSets = { removeExerciseSets() },
        userDefaultWeightUnit = userDefaultWeightUnit,
        exerciseWeightUnit = exerciseWeightUnit,
        updateExerciseWeightUnit = { weightUnit -> updateExerciseWeightUnit(weightUnit) },
        navController = navController,
        workoutComponents = workoutComponents,
        onCreateSuperset = { selectedExercises -> onCreateSuperset(selectedExercises) },
      )
    },
    exerciseSetsDescription = {
      //      if (exerciseSets.description != null) {
      ExerciseSetDescription(exerciseSets = exerciseSets)
      //      }
    },
    exerciseNotesList = {
      ExerciseNotesList(
        notesVisible = notesVisible,
        exerciseSets = exerciseSets,
        _pinNote = { note, pin -> pinExerciseNote(note, pin) },
        removeNote = { note -> removeExerciseNote(note) },
      )
    },
    exerciseSetsWorkoutSummary = {
      ExerciseSetsWorkoutSummary(exerciseSets = exerciseSets, weightUnit = weightUnit)
    },
    setContentHeader = { SetContentHeader(weightUnit = weightUnit) },
  )
}

@Composable
fun ExerciseHeader(
  titleButton: @Composable (modifier: Modifier) -> Unit,
  exerciseOptionsMenu: @Composable () -> Unit,
  exerciseSetsDescription: @Composable () -> Unit,
  exerciseNotesList: @Composable () -> Unit,
  exerciseSetsWorkoutSummary: @Composable () -> Unit,
  setContentHeader: @Composable () -> Unit,
) {
  //  Log.i("ExerciseHeader", "$exerciseSets.notes")
  //  Log.i("ExerciseHeader", "$notes")

  Row(modifier = Modifier.fillMaxWidth()) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        titleButton(Modifier.weight(1.0f, fill = true))

        exerciseOptionsMenu()
      }
      exerciseSetsDescription()
      exerciseNotesList()
      exerciseSetsWorkoutSummary()
      setContentHeader()
    }
  }
}

@Composable
private fun ExerciseSetDescription(exerciseSets: ExerciseExerciseSets) {
  var visible by remember { mutableStateOf(false) }
  if (exerciseSets.description == null) {
    visible = false
  }
  //  Log.i("ExerciseSetDescription", "exerciseSets.description: ${exerciseSets.description}")
  exerciseSets.description?.let {
    Row(
      modifier = Modifier
        .clickable(onClick = { visible = !visible })
        .fillMaxWidth(),
      //        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Description",
        style = MaterialTheme.typography.bodyMediumEmphasized,
        modifier = Modifier.padding(horizontal = 8.dp),
      )
      Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
        Icon(
          //          modifier = Modifier.padding(8.dp).size(24.dp),
          imageVector = if (!visible) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
          contentDescription = if (!visible) "Expand" else "Collapse",
        )
      }
    }
    AnimatedVisibility(
      visible = visible,
      enter = expandVertically(animationSpec = tween(250)),
      exit = shrinkVertically(animationSpec = tween(250)),
    ) {
      Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)) {
        Text(text = AnnotatedString.fromHtml(it), style = MaterialTheme.typography.bodyMedium)
      }
    }
  }
}

@Composable
private fun ExerciseNotesList(
  notesVisible: Boolean,
  exerciseSets: ExerciseExerciseSets,
  _pinNote: (note: ExerciseNote, pin: Boolean) -> Unit,
  removeNote: (note: ExerciseNote) -> Unit,
) {
  //  Log.i("ExerciseNotesList", "${exerciseSets.notes.size}")
  var visible by remember { mutableStateOf(notesVisible) }

  if (exerciseSets.notes.isEmpty()) {
    visible = false
  }

  //  Log.i("ExerciseNotesList", "visible: $visible")

  if (exerciseSets.notes.isNotEmpty()) {
    Row(
      modifier =
        Modifier
          .clickable(onClick = { visible = !visible })
          .fillMaxWidth(),
      //        .height(40.dp)
//          .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Notes",
        style = MaterialTheme.typography.bodyMediumEmphasized,
        modifier = Modifier.padding(horizontal = 8.dp)
      )
      Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
        Icon(
          imageVector = if (!visible) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
          contentDescription = if (!visible) "Expand" else "Collapse",
        )
      }
    }

    AnimatedVisibility(
      visible = visible,
      enter = expandVertically(animationSpec = tween(250)),
      exit = shrinkVertically(animationSpec = tween(250)),
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        exerciseSets.notes.forEach { note ->
          Log.i("ExerciseNotesList", "Note: $note")
          SwipeToDismissItem(onDismissRequest = { removeNote(note) }) {
            ExerciseNotes(exerciseNote = note, _pinNote = { pin -> _pinNote(note, pin) })
          }
          Spacer(modifier = Modifier.height(4.dp))
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun SetContentHeaderPreview() {
  SetContentHeader(weightUnit = "kg")
}

@Composable
private fun SetContentHeader(weightUnit: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    //    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val textStyle = MaterialTheme.typography.bodyMedium

    Box(
      modifier = Modifier.width(48.dp),
      //      .weight(0.1f),
      contentAlignment = Alignment.Center,
    ) {
      Text("Set", style = textStyle)
    }

    Box(
      modifier =
        Modifier
          // .padding(4.dp)
          .width(110.dp),
      //        .weight(0.22f),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        "Previous Set (${weightUnit})",
        style = textStyle,
        maxLines = 1,
        autoSize = TextAutoSize.StepBased(maxFontSize = textStyle.fontSize),
      )
    }

    Box(
      modifier = Modifier
        .padding(PaddingValues(horizontal = 0.dp))
        .width(105.dp),
      //        .weight(0.20f),
      contentAlignment = Alignment.Center,
    ) {
      Text("Weight ($weightUnit)", style = textStyle)
    }

    Box(
      modifier = Modifier
        .padding(PaddingValues(horizontal = 0.dp))
        .width(65.dp),
      //        .weight(0.15f),
      contentAlignment = Alignment.Center,
    ) {
      Text("Reps", style = textStyle)
    }

    Box(
      modifier = Modifier
        .padding(0.dp)
        .width(60.dp),
      //      .weight(0.1f),
      contentAlignment = Alignment.Center,
    ) {
      Text("RIR", style = textStyle)
    }

    Spacer(Modifier.weight(0.1f))
  }
}

@Composable
private fun ExerciseSetsWorkoutSummary(
  //  workoutViewModel: WorkoutViewModel = koinViewModel(),
  exerciseSets: ExerciseExerciseSets,
  weightUnit: String,
) {
  // TODO Give option to show either Total Volume or Total Working Volume
  // TODO Update Values on user input, cause keyboard to
  val textStyle = MaterialTheme.typography.bodyMedium

  //  workoutViewModel.userDefaultWeightUnit

  //  Only necessary if values don't update on user input/recomposition is triggered
  //  val exercisesSets by workoutViewModel.exercisesSets.collectAsStateWithLifecycle()
  //  val exerciseSets = exercisesSets.first { it.exSetsUUID == _exerciseSets.exSetsUUID }

  //    val exerciseSets = _exerciseSets
  //  val weightUnit = workoutViewModel.getWeightUnit(exerciseSets)

  val totalVolume by remember {
    mutableDoubleStateOf(exerciseSets.calculateTotalVolume(weightUnit))
  }
  val totalRepetitions by remember { mutableIntStateOf(exerciseSets.totalRepetitions()) }
  val averageWeight by remember { mutableDoubleStateOf(exerciseSets.averageWeight(weightUnit)) }

  val df = DecimalFormat("#,###.##")
  df.roundingMode = RoundingMode.HALF_UP

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      // TODO get default value for weight
      text = "Volume: ${df.format(totalVolume)} $weightUnit",
      style = textStyle,
    )
    Text(text = "Total Reps: $totalRepetitions", style = textStyle)
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
  content: @Composable RowScope.() -> Unit,
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
              minHeight = ButtonDefaults.MinHeight,
            )
            .padding(contentPadding),
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically,
          content = content,
        )
      }
    }
  }
}
