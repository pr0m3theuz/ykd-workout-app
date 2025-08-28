package com.example.myapplication.features.workout.feature.addExercisesList

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterial3AdaptiveApi
@Composable
fun AddExerciseNavigableListDetailScreen(
  exerciseListPane: @Composable (onClick: (exerciseID: String) -> Unit) -> Unit,
  exerciseDetailPane: @Composable (exerciseID: String, onClick: () -> Unit) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Any>()

  // TODO add BackHandler to handle back navigation

  NavigableListDetailPaneScaffold(
    navigator = scaffoldNavigator,
    listPane = {
      AnimatedPane() {
        Surface(color = MaterialTheme.colorScheme.primary) {
          exerciseListPane() { exerciseID ->
            coroutineScope.launch {
              scaffoldNavigator.navigateTo(
                pane = ListDetailPaneScaffoldRole.Detail,
                contentKey = exerciseID,
              )
            }
          }
        }
      }
    },
    detailPane = {
      val exerciseID =
        scaffoldNavigator.currentDestination?.contentKey?.toString() ?: "Select an exercise"

      AnimatedPane() {
        Surface(color = MaterialTheme.colorScheme.secondary) {
          exerciseDetailPane(exerciseID) {
            coroutineScope.launch {
              scaffoldNavigator.navigateTo(pane = ListDetailPaneScaffoldRole.List)
            }
          }
        }
      }
    },
  )
}
