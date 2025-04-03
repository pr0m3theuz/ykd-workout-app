@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.components.exercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLink
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.SupersetExercisesSets
import com.example.myapplication.data.model.WorkoutComponent
import com.example.myapplication.ui.theme.IBMColorBlindPalette
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun OldCreateSupersetScreen(
  workoutComponents: List<WorkoutComponent> = listOf(),
) {
  fun onEditSuperset(superset: SupersetExercisesSets) {}
  fun onDeleteSuperset(supersetID: ObjectId) {}

  val superset = SupersetExercisesSets()

  val (selected, onSelected) = remember { mutableStateOf(false) }

  val selectedExercises = remember { mutableSetOf<ExerciseExerciseSets>() }

  fun selectExercise(exercise: ExerciseExerciseSets, isSelected: Boolean) {
    if (isSelected) {
      selectedExercises.add(exercise)
    } else {
      selectedExercises.remove(exercise)
    }
  }

  val selectedSupersetExercises = remember { mutableSetOf<ExerciseExerciseSets>() }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("${selectedExercises.size} Selected") },
        actions = {
          IconButton(onClick = {}, enabled = selectedExercises.size > 0,
            content = {
              when {
                selectedExercises.size == 0 -> Icon(Icons.Default.Link, contentDescription = "Link")
                selectedExercises.size > 1 -> Icon(
                  Icons.Default.AddLink,
                  contentDescription = "Create Superset"
                )
              }
            }
          )
        }
      )
    },
  ) { contentPadding ->
    Column(modifier = Modifier.padding(contentPadding)) {
      workoutComponents.forEach { component ->
        when (component) {
          is ExerciseExerciseSets -> {
            Row(
              modifier = Modifier
                .clickable(
                  onClick = {
                    selectExercise(component, !selected)
                  }
                )
                .background(color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
            ) { Text(component.exercise.name) }
          }

          is SupersetExercisesSets -> {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
              shape = RoundedCornerShape(0f),
              colors = CardColors(
                containerColor = IBMColorBlindPalette.random(),
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.tertiaryContainer,
              )
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(start = 3.dp)
                  .clip(RoundedCornerShape(0f))
                  .background(MaterialTheme.colorScheme.surface),
                verticalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                component.exercisesSets.forEach { exerciseSets: ExerciseExerciseSets ->
                  Row(
                    modifier = Modifier
                      .clickable(
                        onClick = {
                          selectExercise(exerciseSets, !selected)
                        }
                      )
                      .background(color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface)
                  ) { Text(exerciseSets.exercise.name) }
                }
              }
            }
          }
        }
      }
    }
  }
}


@OptIn(
  ExperimentalMaterial3Api::class,
  ExperimentalUuidApi::class
) // Added ExperimentalUuidApi if needed
@Preview(showBackground = true)
@Composable
fun CreateSupersetScreen(
  onDismiss: () -> Unit = {},
  workoutComponents: List<WorkoutComponent> = listOf(),
  // Add callbacks for actual actions
  onCreateSuperset: (List<ExerciseExerciseSets>) -> Unit = {},
  onShowMessage: (String) -> Unit = {} // Example: Callback for showing Snackbar/Toast
) {
  // State for selection - Consider moving selection logic to ViewModel if complex
  val selectedExercises =
    remember { mutableStateMapOf<Uuid, ExerciseExerciseSets>() } // Use Map for easier toggle

  fun selectExercise(exercise: ExerciseExerciseSets, select: Boolean) {
    if (select) {
      selectedExercises[exercise.exSetsUUID] = exercise
    } else {
      selectedExercises.remove(exercise.exSetsUUID)
    }
  }

  // TODO Check if selected ExerciseExerciseSet already part of a existing Superset. if true, use LinkOff icon

  var removeSuperset by remember { mutableStateOf(false) }

  BasicAlertDialog(
    onDismissRequest = {
      // Dismiss the dialog when the user clicks outside the dialog or on the back
      // button. If you want to disable that functionality, simply use an empty
      // onDismissRequest.
      onDismiss()
    },
    modifier = Modifier
      .fillMaxSize()
      .imePadding(),
    properties = DialogProperties(usePlatformDefaultWidth = false),

    ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("${selectedExercises.size} Selected") },
          actions = {
            IconButton(
              onClick = {
                // --- Check added here ---
                val selectedIds = selectedExercises.keys
                var alreadyInSuperset = false
                var parentSupersetName: String? = null

                // Iterate through existing supersets to check for containment
                for (component in workoutComponents) {
                  if (component is SupersetExercisesSets) {
                    val supersetIds = component.exercisesSets.map { it.exSetsUUID }.toSet()
                    // Check if ALL selected IDs are contained within THIS superset's IDs
                    if (selectedIds.any { supersetIds.contains(it) }) {
                      removeSuperset = true
                      alreadyInSuperset = true
                      parentSupersetName = component.name ?: "Superset" // Optional: get name
                      break // Found a containing superset, no need to check others
                    }
                  }
                }

                if (alreadyInSuperset) {
                  // Handle case where selected items are already part of a superset
                  onShowMessage("Selected exercises are already together in '${parentSupersetName}'.")
                  // Optionally clear selection or disable further action
                  // selectedExercises.clear()
                } else if (selectedExercises.size > 1) {
                  // Proceed with creating a new superset
                  onCreateSuperset(selectedExercises.values.toList())
                  onShowMessage("Creating superset...") // Example feedback
                  selectedExercises.clear() // Clear selection after action
                } else {
                  // Handle cases with 0 or 1 selection if needed (though button might be disabled)
                  onShowMessage("Select at least two exercises to create a superset.")
                }
              },
              // Enable button only if more than one exercise is selected
              enabled = selectedExercises.size > 1
            ) {
              // Icon logic remains similar, based on selection size
              when {
                selectedExercises.isEmpty() -> Icon(
                  Icons.Default.Link,
                  contentDescription = "Select Exercises"
                )

                selectedExercises.size == 1 -> Icon(
                  Icons.Default.Link,
                  contentDescription = "Select More Exercises"
                )

                selectedExercises.size > 1 -> Icon(
                  Icons.Default.AddLink,
                  contentDescription = "Create Superset"
                )

                removeSuperset -> Icon(
                  Icons.Default.LinkOff,
                  contentDescription = "Remove Superset"
                )
              }
            }
          }
        )
      },
    ) { contentPadding ->
      // Using LazyColumn for potentially long lists
      LazyColumn(
        modifier = Modifier.padding(contentPadding),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp) // Spacing between items
      ) {
        items(
          count = workoutComponents.size,
          // Provide a stable key if possible (using component's UUID or ID)
          key = { index ->
            when (val component = workoutComponents[index]) {
              is ExerciseExerciseSets -> component.exSetsUUID // Use stable UUID
              is SupersetExercisesSets -> component.uuid // Use stable UUID
            }
          }
        ) { index ->
          val component = workoutComponents[index]
          // Determine if the current component (or its inner items) is selected
          val isSelected = when (component) {
            is ExerciseExerciseSets -> selectedExercises.containsKey(component.exSetsUUID)
            // For supersets, you might want different selection behavior (e.g., select whole superset?)
            // Current logic only selects individual exercises *within* the superset display
            is SupersetExercisesSets -> false // Superset card itself isn't directly selectable here
          }

          val supersetCardBackgroundColor: Color by remember { mutableStateOf(IBMColorBlindPalette.random()) }

          when (component) {
            is ExerciseExerciseSets -> {
              // Standalone Exercise Row
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    selectExercise(component, !selectedExercises.containsKey(component.exSetsUUID))
                  }
                  .background(
                    color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surface
                  )
                  .padding(horizontal = 8.dp, vertical = 12.dp) // Added padding
              ) {
                Text(component.exercise.name)
              }
            }

            is SupersetExercisesSets -> {
              // Superset Card containing Exercise Rows
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp), // Slightly rounded corners
                colors = CardDefaults.cardColors( // Use CardDefaults
                  containerColor = supersetCardBackgroundColor // Consider a stable color mapping
                )
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp) // Padding around inner content
//                  .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 4.dp, bottomEnd = 4.dp)) // Clip inner part if needed
                    .background(MaterialTheme.colorScheme.surface) // Background for inner rows
                ) {
//                Text( // Display Superset Name (optional)
//                  text = component.name ?: "Superset",
//                  style = MaterialTheme.typography.titleMedium,
//                  modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 4.dp)
//                )

                  component.exercisesSets.forEach { exerciseSets ->
                    val isInnerSelected = selectedExercises.containsKey(exerciseSets.exSetsUUID)
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                          selectExercise(exerciseSets, !isInnerSelected)
                        }
                        .background(
                          color = if (isInnerSelected) MaterialTheme.colorScheme.secondaryContainer
                          else MaterialTheme.colorScheme.surface
                        )
                        .padding(8.dp)
                    ) {
                      Text(exerciseSets.exercise.name)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }


}