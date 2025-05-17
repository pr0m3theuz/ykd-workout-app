@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.workout.feature.previewWorkout

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.WorkoutItem
import com.example.myapplication.common.domain.model.convertPoundToKg
import com.example.myapplication.common.domain.model.setSummaryRIR
import com.example.myapplication.common.domain.model.setTargetSummaryRIR
import com.example.myapplication.common.util.ProgressionOutcome
import com.example.myapplication.common.util.newLazyListItemID
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

//import com.example.rphypertrophy.* // Core model imports

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutPreviewScreen(
  viewModel: WorkoutPreviewViewModel,
  onAccept: (Workout) -> Unit,
  onReject: (Workout) -> Unit,
  onBack: () -> Unit
) {
  val previewItems by remember { derivedStateOf { viewModel.previewItems.toList() } } // Observe list changes
  val overallSuccess by viewModel.overallProgressionSuccess
  val message by viewModel.progressionMessage

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Workout Progression Preview") },
        navigationIcon = {
          // IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") } // Add back navigation if needed
        }
      )
    },
    bottomBar = {
      PreviewBottomBar(
        overallSuccess = overallSuccess,
        message = message,
        onAccept = { },
        onReject = { }
      )
    }
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .padding(paddingValues)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(vertical = 16.dp)
    ) {
      items(previewItems, key = { getItemId(it) }) { item ->
        when (item) {
          is PreviewExerciseSets -> ExercisePreviewItem(item)
          is PreviewSuperset -> SupersetPreviewItem(item)
          // Handle other WorkoutItem types if necessary
        }
        Spacer(modifier = Modifier.height(12.dp))
      }
    }
  }
}

// Helper to get a stable key for LazyColumn items
fun getItemId(item: Any): Long {
  return when (item) {
    is PreviewExerciseSets -> item.originalItem.lazyListItemID
    is PreviewSuperset -> item.originalItem.lazyListItemID
    is WorkoutItem -> {
      when (item) {
        is ExerciseSets -> item.lazyListItemID
        is Superset -> item.lazyListItemID
      }
    }

    else -> newLazyListItemID()
  }
}


@Composable
fun ExercisePreviewItem(preview: PreviewExerciseSets) {
  Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "Exercise",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f)
        )
        ProgressionStatusIcon(outcome = preview.overallOutcome)
      }
      when (preview.overallOutcome) {
        ProgressionOutcome.PROGRESSED -> {
          Text(
            "Progression Applied",
            style = MaterialTheme.typography.labelSmall,
            fontStyle = FontStyle.Italic
          )
        }

        ProgressionOutcome.FAILED -> {
          Text(
            "Progression Failed",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall
          )
        }

        else -> {
          // Keep the nested if for clarity if the condition is complex,
          // or inline if preferred for brevity and the condition is simple enough.
          if (preview.previewSets.none { it.wasModified }) {
            Text(
              "No changes applied",
              style = MaterialTheme.typography.labelSmall,
              fontStyle = FontStyle.Italic
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(8.dp))
      preview.previewSets.forEach { setPreview ->
        SetPreviewRow(setPreview)
      }
    }
  }
}

@Composable
fun SupersetPreviewItem(preview: PreviewSuperset) {
  Card(
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
        alpha = 0.7f
      )
    ) // Slightly different bg
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Text(
        text = "Superset",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
      )
      preview.previewExerciseSets.forEachIndexed { index, exercisePreview ->
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${index + 1}. Exercise",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
          )
          ProgressionStatusIcon(outcome = exercisePreview.overallOutcome)
        }
        if (exercisePreview.overallOutcome == ProgressionOutcome.FAILED) {
          Text(
            "Progression Failed",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall
          )
        }
        if (exercisePreview.overallOutcome == ProgressionOutcome.UNCHANGED && exercisePreview.previewSets.none { it.wasModified }) {
          Text(
            "No changes applied",
            style = MaterialTheme.typography.labelSmall,
            fontStyle = FontStyle.Italic
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        exercisePreview.previewSets.forEach { setPreview ->
          SetPreviewRow(setPreview)
        }
        if (index < preview.previewExerciseSets.size - 1) {
          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        }
      }
    }
  }
}

@Composable
private fun SetTypeIcon(
  setType: SetType,
  setIndex: String
) {
  var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }

  val iconText = when (setType) {
    SetType.normalSet -> setIndex
    SetType.warmUpSet -> "W"
    SetType.dropSet -> "D"
    SetType.myoRepSet -> "M"
    SetType.myoRepMatchSet -> "MM"
    SetType.giantSet -> "G"
    SetType.amrapSet -> "A"
    SetType.failureSet -> "F"
    else -> {
      setIndex
    }
  }

  val buttonBackgroundColor =
    when (setType) {
      SetType.warmUpSet -> MaterialTheme.colorScheme.tertiary
      SetType.amrapSet -> MaterialTheme.colorScheme.error
      SetType.failureSet -> MaterialTheme.colorScheme.error
      else -> MaterialTheme.colorScheme.primary
    }

  val iconColor: Color =
    when (setType) {
      SetType.warmUpSet -> MaterialTheme.colorScheme.onSecondary
      SetType.amrapSet -> MaterialTheme.colorScheme.onError
      SetType.failureSet -> MaterialTheme.colorScheme.onError
      else -> MaterialTheme.colorScheme.onPrimary
    }

  Text(
    modifier = Modifier.padding(end = 8.dp),
    text = iconText,
    fontSize = 14.sp,
    fontWeight = FontWeight.SemiBold,
    color = buttonBackgroundColor,
    textAlign = TextAlign.Center
  )
}

@Composable
fun SetPreviewRow(setPreview: PreviewSet) {
  val previousSet = setPreview.previousSet
  val originalSet = setPreview.originalSet
  val progressedSet = setPreview.progressedSet
  val wasModified = setPreview.wasModified
  val color = when {
    setPreview.outcome == ProgressionOutcome.FAILED -> MaterialTheme.colorScheme.error
    wasModified -> MaterialTheme.colorScheme.primary
    else -> LocalContentColor.current // Default text color
  }

  Row(
    modifier = Modifier.padding(vertical = 2.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Original Value (Strikethrough if modified)
    if (setPreview.outcome != ProgressionOutcome.FAILED) {
      SetTypeIcon(
        setType = originalSet.setTypeID,
        setIndex = originalSet.setNumber.toString()
      )
      // TODO Show progress from previous workout or change from template
      Text(
        text = previousSet?.setSummaryRIR("lbs") ?: "No previous set was found. -> ".plus(
          progressedSet.setTargetSummaryRIR("lbs")
        ),
        fontSize = 14.sp,
        textDecoration = if (wasModified) TextDecoration.LineThrough else TextDecoration.None,
        color = if (wasModified) LocalContentColor.current.copy(alpha = 0.6f) else LocalContentColor.current
      )

      Spacer(modifier = Modifier.width(8.dp))
      // Progressed Value
      Text(
        text = "-> ".plus(progressedSet.setTargetSummaryRIR("lbs")),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = color
      )
    } else {
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "(There was an error in progression logic.)",
        fontSize = 12.sp,
        fontStyle = FontStyle.Italic,
        color = color
      )
    }
    // Add RPE/Type indication if needed
    Spacer(modifier = Modifier.weight(1f))
  }
}

@Composable
fun ProgressionStatusIcon(outcome: ProgressionOutcome) {
  when (outcome) {
    ProgressionOutcome.PROGRESSED -> Icon(
      Icons.Filled.CheckCircle,
      "Progressed",
      tint = Color(0xFF008000)
    ) // Dark Green
    ProgressionOutcome.FAILED -> Icon(
      Icons.Filled.Error,
      "Failed",
      tint = MaterialTheme.colorScheme.error
    )

    else -> Icon(
      Icons.Filled.RadioButtonUnchecked,
      "Unchanged/Pending",
      tint = Color.Gray
    ) // Or hide it
  }
}


@Composable
fun PreviewBottomBar(
  overallSuccess: Boolean,
  message: String,
  onAccept: () -> Unit,
  onReject: () -> Unit
) {
  Surface(tonalElevation = 4.dp) { // Use Surface for elevation
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = message,
        color = if (overallSuccess) Color.Black else MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold
      )
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Button(
          onClick = onAccept,
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Greenish
        ) {
          Text("Accept Progressions")
        }
        OutlinedButton(
          onClick = onReject,
          colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
          border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = SolidColor(MaterialTheme.colorScheme.error) // Temporary fix if needed
          )

        ) {
          Text("Reject / Use Original")
        }
      }
    }
  }
}

// Add a Preview for easy visualization in Android Studio
@OptIn(ExperimentalUuidApi::class)
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun WorkoutPreviewScreenPreview() {
  // Create dummy data for preview
  val dummyViewModel = WorkoutPreviewViewModel() // Assume this is your ViewModel

  val json = Json {
    ignoreUnknownKeys = true // Recommended for flexibility
    isLenient = true         // If JSON might be slightly malformed
    prettyPrint = false      // Not needed for deserialization test
  }

  val file = LocalContext.current.assets.open("Workout_cb_exported_completed.json").bufferedReader()
    .readLines()

  val workouts = file.mapNotNull map@{
    var workout: Workout? = null
    try {
      workout = json.decodeFromString<Workout>(it)
    } catch (e: Exception) {
      println("Error processing document ID : ${e.message}")
      println(it)
      e.printStackTrace()
    }
    return@map workout
  }

  val previousWorkout = workouts.first()
  val currentWorkout = workouts.last()
  dummyViewModel.originalWorkout = currentWorkout

  previousWorkout.workoutItems.forEach {
    when (it) {
      is ExerciseSets -> {
        it.sets.forEach { set ->
          set.setCompleted = true
          set.repetitions = set.targetRepetitions ?: set.targetMaximumRepetitions ?: 8
          set.weightPounds = 100.0
          set.weightKilograms = set.convertPoundToKg()
          set.incrementWeightPounds = 5.0
          set.incrementWeightKilograms = 5.0.convertPoundToKg()
        }
      }

      is Superset -> {
        it.exercisesSets.forEach {
          it.sets.forEach { set ->
            set.repetitions = set.targetRepetitions ?: set.targetMaximumRepetitions ?: 8
            set.weightPounds = 100.0
            set.weightKilograms = set.convertPoundToKg()
            set.incrementWeightPounds = 5.0
            set.incrementWeightKilograms = 5.0.convertPoundToKg()
          }
        }
      }
    }
  }


// --- Define ProgressionResults (Simulating what your progression logic would output) ---

// Assign to the viewModel if it had a direct field for it,
// otherwise this map is used directly in setupPreview
// dummyViewModel.progressionResults = progressionResultsMap // If viewModel stores this directly

// Call the setup logic which internally uses createWorkoutPreview
// We pass the map directly to simulate the ViewModel's internal state after progression logic runs.
// For the preview, we are essentially bypassing the `runActualProgressionLogic`
// and providing its output directly.

  dummyViewModel.setupPreview(
    workoutToPrepare = currentWorkout,
    previousWorkout = previousWorkout
  )

//  dummyViewModel.previewItems.clear()
//  dummyViewModel.previewItems.addAll(
//    createWorkoutPreview(
//      dummyViewModel.originalWorkout,
//      progressionResultsMap
//    )
//  )

// Manually set overall success and message for the preview based on our dummy results
  val failures = dummyViewModel.previewItems.count {
    (it is PreviewExerciseSets && it.overallOutcome == ProgressionOutcome.FAILED) ||
            (it is PreviewSuperset && it.previewExerciseSets.any { p -> p.overallOutcome == ProgressionOutcome.FAILED })
  }
  val successes = dummyViewModel.previewItems.count {
    (it is PreviewExerciseSets && it.overallOutcome == ProgressionOutcome.PROGRESSED) ||
            (it is PreviewSuperset && it.previewExerciseSets.any { p -> p.overallOutcome == ProgressionOutcome.PROGRESSED })
  }
  dummyViewModel.overallProgressionSuccess.value = failures == 0
  dummyViewModel.progressionMessage.value = when {
    failures > 0 -> "Progression failed for $failures exercise(s)."
    successes > 0 -> "Progression applied successfully to $successes exercise(s)."
    else -> "No progressions applied."
  }


  // Use your app's theme
  WorkoutPreviewScreen(
    viewModel = dummyViewModel,
    onAccept = {},
    onReject = {},
    onBack = {}
  )
}