package com.example.myapplication.features.exercises.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@ExperimentalMaterial3AdaptiveApi
@Composable
fun ExerciseNavigableListDetailScreen(
  navController: NavController,
  exerciseListPane: @Composable (onClick: (exerciseID: String) -> Unit) -> Unit,
  exerciseDetailPane: @Composable (exerciseID: String, onClick: () -> Unit) -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  val scaffoldNavigator = rememberListDetailPaneScaffoldNavigator<Any>()

  Surface(color = MaterialTheme.colorScheme.background) {
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
}
