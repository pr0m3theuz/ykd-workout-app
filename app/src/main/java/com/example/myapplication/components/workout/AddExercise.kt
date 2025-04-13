package com.example.myapplication.components.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddExercise(navController: NavController) {
  /*  val (openDialog, setOpenDialog) = remember { mutableStateOf(false) }

    val dialogProperties = DialogProperties(usePlatformDefaultWidth = false)

    if (openDialog) {
      // AddExercisesScreen(setOpenDialog = setOpenDialog, navController = navController)
      // TODO Replace Dialog
      // TODO Navigate to New Screen
      BasicAlertDialog(
        modifier = Modifier
          .fillMaxSize(),
        properties = dialogProperties,
        onDismissRequest = { setOpenDialog(false) },
      ) {
        AddExerciseNavigableListDetailScreen(
          exerciseListPane = { onClick ->
            AddExercisesScreen(
              setOpenDialog = setOpenDialog,
              navController = navController,
              navigateToDetailPane = onClick,
              exercisesViewModel = TODO(),
              workoutViewModel = TODO(),
            )
          },
          exerciseDetailPane = { exerciseID: String, onClick: () -> Unit ->
            ExerciseDetailsScreen(navController, exerciseID, onClick)
          },
        )
      }
    }*/

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    TextButton(
      onClick = {
//        setOpenDialog(true)
        navController.navigate("AddExercise")
      },
      modifier = Modifier.weight(1.0f, fill = true),
      shape = MaterialTheme.shapes.extraSmall,
    ) {
      Text("Add Exercise")
    }
  }
}
