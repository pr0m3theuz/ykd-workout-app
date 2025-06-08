@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.example.myapplication.features.program.ui.programEditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.common.ui.theme.MyApplicationTheme

// Data classes based on the JavaScript structure
data class Program(
  val id: Int,
  var name: String,
  var description: String,
  val mesocycles: List<Mesocycle>
)

data class Mesocycle(
  val id: Int,
  var name: String,
  var duration: String,
  val microcycles: List<Microcycle>
)

data class Microcycle(
  val id: Int,
  var name: String,
  val workouts: List<Workout>
)

data class Workout(
  val id: Int,
  var name: String,
  val workoutItems: List<WorkoutItemParent>
)

sealed class WorkoutItemParent {
  abstract val id: Int
}

data class ExerciseWorkoutItem(
  override val id: Int,
  val type: String = "exercise",
  val exercise: ExerciseDetails,
  val sets: List<SetDetails>
) : WorkoutItemParent()

data class SupersetWorkoutItem(
  override val id: Int,
  val type: String = "superset",
  var name: String,
  val exercises: List<SupersetExerciseDetail>
) : WorkoutItemParent()

data class ExerciseDetails(
  var name: String,
  var targetMuscle: String
)

data class SetDetails(
  val id: Int,
  var targetReps: String,
  var targetRIR: Int
)

data class SupersetExerciseDetail(
  val id: Int,
  val exercise: ExerciseDetails,
  val sets: List<SetDetails>
)

// Mock data
val initialProgramData = Program(
  id = 1,
  name = "The M.A.X. Muscle Plan",
  description = "Advanced muscle building program",
  mesocycles = listOf(
    Mesocycle(
      id = 1,
      name = "Phase 1: Foundation",
      duration = "4 weeks",
      microcycles = listOf(
        Microcycle(
          id = 1,
          name = "Week 1",
          workouts = listOf(
            Workout(
              id = 1,
              name = "Day 1 - Upper Body",
              workoutItems = listOf(
                ExerciseWorkoutItem(
                  id = 1,
                  exercise = ExerciseDetails(
                    name = "Dumbbell Bench Press",
                    targetMuscle = "Chest"
                  ),
                  sets = listOf(
                    SetDetails(id = 1, targetReps = "15-20", targetRIR = 4),
                    SetDetails(id = 2, targetReps = "15-20", targetRIR = 4),
                    SetDetails(id = 3, targetReps = "15-20", targetRIR = 4)
                  )
                ),
                SupersetWorkoutItem(
                  id = 2,
                  name = "Arm Superset",
                  exercises = listOf(
                    SupersetExerciseDetail(
                      id = 3,
                      exercise = ExerciseDetails(name = "Dumbbell Curl", targetMuscle = "Biceps"),
                      sets = listOf(
                        SetDetails(id = 4, targetReps = "12-15", targetRIR = 3),
                        SetDetails(id = 5, targetReps = "12-15", targetRIR = 3)
                      )
                    ),
                    SupersetExerciseDetail(
                      id = 4,
                      exercise = ExerciseDetails(
                        name = "Triceps Extension",
                        targetMuscle = "Triceps"
                      ),
                      sets = listOf(
                        SetDetails(id = 6, targetReps = "12-15", targetRIR = 3),
                        SetDetails(id = 7, targetReps = "12-15", targetRIR = 3)
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )
  )
)

enum class EditorView {
  PROGRAM, MESOCYCLES, MICROCYCLES, WORKOUTS, WORKOUT_ITEMS, EXERCISE_SETS
}

data class NavigationState(val view: EditorView, val item: Any? = null)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutProgramEditor() {
  var program by remember { mutableStateOf(initialProgramData) }
  var currentView by remember { mutableStateOf(EditorView.PROGRAM) }
  var navigationStack by remember { mutableStateOf(listOf<NavigationState>()) }
  var selectedItem by remember { mutableStateOf<Any?>(null) }
  var editMode by remember { mutableStateOf(false) }

  val navigateTo: (EditorView, Any?) -> Unit = { view, item ->
    navigationStack = navigationStack + NavigationState(currentView, selectedItem)
    currentView = view
    selectedItem = item
    editMode = false
  }

  val navigateBack: () -> Unit = {
    if (navigationStack.isNotEmpty()) {
      val previous = navigationStack.last()
      navigationStack = navigationStack.dropLast(1)
      currentView = previous.view
      selectedItem = previous.item
      editMode = false
    }
  }

  val title = remember(currentView, selectedItem, program) {
    when (currentView) {
      EditorView.PROGRAM -> program.name
      EditorView.MESOCYCLES -> "Mesocycles"
      EditorView.MICROCYCLES -> (selectedItem as? Mesocycle)?.name ?: "Microcycles"
      EditorView.WORKOUTS -> (selectedItem as? Microcycle)?.name ?: "Workouts"
      EditorView.WORKOUT_ITEMS -> (selectedItem as? Workout)?.name ?: "Workout"
      EditorView.EXERCISE_SETS -> (selectedItem as? ExerciseWorkoutItem)?.exercise?.name
        ?: "Exercise"
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold) },
        navigationIcon = {
          if (navigationStack.isNotEmpty()) {
            IconButton(onClick = navigateBack) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          }
        },
        actions = {
          IconButton(onClick = { editMode = !editMode }) {
            Icon(
              Icons.Filled.Edit,
              contentDescription = "Edit",
              tint = if (editMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
          }
          IconButton(onClick = { /* TODO: More options */ }) {
            Icon(Icons.Filled.MoreVert, contentDescription = "More options")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface,
          titleContentColor = MaterialTheme.colorScheme.onSurface
        )
      )
    },
    bottomBar = {
      EditorBottomNavigation(currentView) { /* TODO: Handle bottom nav clicks */ }
    },
    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
  ) { paddingValues ->
    Box(modifier = Modifier.padding(paddingValues)) {
      when (currentView) {
        EditorView.PROGRAM -> ProgramView(program, editMode, navigateTo)
        EditorView.MESOCYCLES -> MesocyclesView(program.mesocycles, editMode, navigateTo)
        EditorView.MICROCYCLES -> {
          val mesocycle = selectedItem as? Mesocycle
          if (mesocycle != null) {
            MicrocyclesView(mesocycle, editMode, navigateTo)
          } else {
            Text("Error: Mesocycle not selected", modifier = Modifier.padding(16.dp))
          }
        }

        EditorView.WORKOUTS -> {
          val microcycle = selectedItem as? Microcycle
          if (microcycle != null) {
            WorkoutsView(microcycle, editMode, navigateTo)
          } else {
            Text("Error: Microcycle not selected", modifier = Modifier.padding(16.dp))
          }
        }

        EditorView.WORKOUT_ITEMS -> {
          val workout = selectedItem as? Workout
          if (workout != null) {
            WorkoutItemsView(workout, editMode, navigateTo)
          } else {
            Text("Error: Workout not selected", modifier = Modifier.padding(16.dp))
          }
        }

        EditorView.EXERCISE_SETS -> {
          val exerciseItem = selectedItem as? ExerciseWorkoutItem
          if (exerciseItem != null) {
            ExerciseSetsView(exerciseItem, editMode)
          } else {
            Text("Error: Exercise not selected", modifier = Modifier.padding(16.dp))
          }
        }
      }
    }
  }
}

@Composable
fun EditorBottomNavigation(currentView: EditorView, onNavigate: (EditorView) -> Unit) {
  NavigationBar(
    containerColor = MaterialTheme.colorScheme.surface,
    contentColor = MaterialTheme.colorScheme.onSurface
  ) {
    NavigationBarItem(
      selected = false, // Assuming 'Editor' is the current context, not a navigable item here.
      onClick = { /* TODO: Navigate to Programs List */ },
      icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Programs") },
      label = { Text("Programs", fontSize = 11.sp) }
    )
    NavigationBarItem(
      selected = true, // Editor is always selected in this screen
      onClick = { /* Already here or navigate to Program root */
        if (currentView != EditorView.PROGRAM) onNavigate(EditorView.PROGRAM)
      },
      icon = { Icon(Icons.Filled.Edit, contentDescription = "Editor") },
      label = { Text("Editor", fontSize = 11.sp) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
      )
    )
    NavigationBarItem(
      selected = false,
      onClick = { /* TODO: Navigate to Analytics */ },
      icon = {
        Icon(
          Icons.Filled.Analytics,
          contentDescription = "Analytics"
        )
      }, // Changed from Target
      label = { Text("Analytics", fontSize = 11.sp) }
    )
  }
}


// Added Preview function
@Preview(showBackground = true, device = "id:pixel_5")
@Composable
fun WorkoutProgramEditorPreview() {
  // You might want to wrap this in your app's theme if you have one defined
  // For now, using a default MaterialTheme
  MyApplicationTheme { // Apply MaterialTheme for the preview
    WorkoutProgramEditor()
  }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Workout Items View (Edit Mode)")
@Composable
fun WorkoutItemsViewEditModePreview() {
  MaterialTheme {
    // Manually set up state for previewing a specific view
    val workoutForPreview =
      initialProgramData.mesocycles.first().microcycles.first().workouts.first()
    var currentView by remember { mutableStateOf(EditorView.WORKOUT_ITEMS) }
    var selectedItem by remember { mutableStateOf<Any?>(workoutForPreview) }
    var editMode by remember { mutableStateOf(true) } // Enable edit mode
    var navigationStack by remember {
      mutableStateOf(
        listOf(
          NavigationState(
            EditorView.WORKOUTS,
            initialProgramData.mesocycles.first().microcycles.first()
          )
        )
      )
    }


    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text((selectedItem as? Workout)?.name ?: "Workout") },
          navigationIcon = {
            IconButton(onClick = {}) { // Dummy onClick for preview
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          },
          actions = {
            IconButton(onClick = { editMode = !editMode }) {
              Icon(
                Icons.Filled.Edit,
                contentDescription = "Edit",
                tint = if (editMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
              )
            }
            IconButton(onClick = { /* TODO: More options */ }) {
              Icon(Icons.Filled.MoreVert, contentDescription = "More options")
            }
          }
        )
      },
      bottomBar = { EditorBottomNavigation(currentView) {} } // Dummy onNavigate
    ) { paddingValues ->
      Box(modifier = Modifier.padding(paddingValues)) {
        WorkoutItemsView(
          workout = workoutForPreview,
          editMode = editMode,
          navigateTo = { _, _ -> }) // Dummy navigateTo
      }
    }
  }
}

@Preview(showBackground = true, device = "id:pixel_5", name = "Exercise Sets View")
@Composable
fun ExerciseSetsViewPreview() {
  MaterialTheme {
    val exerciseItemForPreview =
      initialProgramData.mesocycles.first().microcycles.first().workouts.first().workoutItems.first { it is ExerciseWorkoutItem } as ExerciseWorkoutItem
    var currentView by remember { mutableStateOf(EditorView.EXERCISE_SETS) }
    var selectedItem by remember { mutableStateOf<Any?>(exerciseItemForPreview) }
    var editMode by remember { mutableStateOf(false) }
    var navigationStack by remember {
      mutableStateOf(
        listOf(
          NavigationState(
            EditorView.WORKOUT_ITEMS,
            initialProgramData.mesocycles.first().microcycles.first().workouts.first()
          )
        )
      )
    }


    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text((selectedItem as? ExerciseWorkoutItem)?.exercise?.name ?: "Exercise") },
          navigationIcon = {
            IconButton(onClick = {}) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          },
          actions = {
            IconButton(onClick = { editMode = !editMode }) {
              Icon(
                Icons.Filled.Edit,
                contentDescription = "Edit",
                tint = if (editMode) MaterialTheme.colorScheme.primary else LocalContentColor.current
              )
            }
            IconButton(onClick = { /* TODO: More options */ }) {
              Icon(Icons.Filled.MoreVert, contentDescription = "More options")
            }
          }
        )
      },
      bottomBar = { EditorBottomNavigation(currentView) {} }
    ) { paddingValues ->
      Box(modifier = Modifier.padding(paddingValues)) {
        ExerciseSetsView(exerciseItem = exerciseItemForPreview, editMode = editMode)
      }
    }
  }
}

