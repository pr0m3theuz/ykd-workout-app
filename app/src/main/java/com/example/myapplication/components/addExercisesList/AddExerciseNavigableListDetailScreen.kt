package com.example.myapplication.components.addExercisesList

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@ExperimentalMaterial3AdaptiveApi
@Composable
fun AddExerciseNavigableListDetailScreen(
  navController: NavController,
  exerciseListPane: @Composable (onClick: (exerciseID: String) -> Unit) -> Unit,
  exerciseDetailPane: @Composable (exerciseID: String, onClick: () -> Unit) -> Unit,
) {
  val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Any>()

  Surface(color = MaterialTheme.colorScheme.background) {
    NavigableListDetailPaneScaffold(
      navigator = scaffoldNavigator,
      listPane = {
        AnimatedPane() {
          Surface(color = MaterialTheme.colorScheme.primary) {
            exerciseListPane() { exerciseID ->
              scaffoldNavigator.navigateTo(
                pane = ListDetailPaneScaffoldRole.Detail,
                content = exerciseID,
              )
            }
          }
        }
      },
      detailPane = {
        val exerciseID =
          scaffoldNavigator.currentDestination?.content?.toString() ?: "Select an exercise"

        AnimatedPane() {
          Surface(color = MaterialTheme.colorScheme.secondary) {
            exerciseDetailPane(exerciseID) {
              scaffoldNavigator.navigateTo(pane = ListDetailPaneScaffoldRole.List)
            }
          }
        }
      },
    )
  }
}
