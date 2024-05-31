@file:OptIn(ExperimentalMaterialApi::class, ExperimentalSerializationApi::class)

package com.example.myapplication

// import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.components.Screens
import com.example.myapplication.components.exerciseDetails.ExerciseDetailsScreen
import com.example.myapplication.components.exercisesList.ExercisesLazyColumn
import com.example.myapplication.components.exercisesList.ExercisesScreen
import com.example.myapplication.components.history.EditWorkoutScreen
import com.example.myapplication.components.history.HistoryScreen
import com.example.myapplication.components.history.WorkoutOverview
import com.example.myapplication.components.homeScreen.HomeScreen
import com.example.myapplication.components.programs.ProgramDetailsScreen
import com.example.myapplication.components.programs.ProgramsScreen
import com.example.myapplication.components.workout.WorkoutScreen
import com.example.myapplication.components.workout.WorkoutSummaryScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.newrelic.agent.android.NewRelic
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : ComponentActivity() {

  //  private lateinit var firebaseAnalytics: FirebaseAnalytics

  @RequiresApi(Build.VERSION_CODES.S)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    //    firebaseAnalytics = Firebase.analytics

    // New Relic
    NewRelic.withApplicationToken("eu01xx7700dce7ae53e2b20b7cc382fcba147c93ae-NRMA")
      .start(this.applicationContext)

    setContent {
      MyApplicationTheme {
        //        val viewModelStoreOwner: ViewModelStoreOwner
        //        val workoutViewModel: WorkoutViewModel = koinViewModel()
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.Programs.route) {
          addExercisesGraph(navController)
          addWorkoutGraph(navController /*viewModelStoreOwner*/)
          addWorkoutSummaryGraph(navController)
          addProgramsGraph(navController)
          addHistoryGraph(navController)
          navigation("homeScreen", route = "home") { composable("homeScreen") { HomeScreen() } }
        }
      }
    }
  }

  companion object {
    fun newIntent(context: Context): Intent {
      return Intent(context, MainActivity::class.java)
    }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun NavGraphBuilder.addWorkoutGraph(
  navController: NavController
  //  viewModel: WorkoutViewModel,
  //  viewModelStoreOwner: ViewModelStoreOwner
) {
  navigation(
    "workoutScreen?workoutID={workoutID}",
    route = "workout?workoutID={workoutID}",
    arguments = listOf(navArgument("workoutID") { type = NavType.StringType }),
  ) {
    composable("workoutScreen?workoutID={workoutID}") { navBackStackEntry ->
      Log.i("WorkoutNavGraph", "Navigated to Workout Screen")
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      WorkoutScreen(
        // TODO This is an anti-pattern
        // https://developer.android.com/guide/navigation/use-graph/navigate
        navController = navController,
        workoutID = workoutID,
      )
    }

    composable("workoutSummary/{workoutID}/{workoutCount}") { navBackStackEntry ->
      Log.i("WorkoutSummaryNavGraph", "Navigated to Workout Summary Screen")
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      val workoutsCount = navBackStackEntry.arguments?.getInt("workoutsCount")

      if (workoutID != null && workoutsCount != null) {
        WorkoutSummaryScreen(
          // TODO This is an anti-pattern
          // https://developer.android.com/guide/navigation/use-graph/navigate
          navController = navController,
          workoutID = workoutID,
          workoutsCount = workoutsCount,
        )
      }
    }

    composable(
      "exerciseDetails/{exerciseID}",
      arguments = listOf(navArgument("exerciseID") { type = NavType.StringType }),
    ) { navBackStackEntry ->
      Log.i("WorkoutNavGraph", "Navigated to Exercise Details Screen")
      navBackStackEntry.arguments?.getString("exerciseID")?.let {
        ExerciseDetailsScreen(
          // TODO This is an anti-pattern
          // https://developer.android.com/guide/navigation/use-graph/navigate
          navController,
          exerciseID = it,
        )
      }
    }
  }
}

private fun NavGraphBuilder.addExercisesGraph(navController: NavController) {
  navigation("exercisesList", route = "exercises") {
    composable("exercisesScreen") { ExercisesScreen() }

    composable("exercisesList") {
      ExercisesLazyColumn(
        // TODO This is an anti-pattern
        // https://developer.android.com/guide/navigation/use-graph/navigate
        navController = navController
      )
    }
    composable(
      "exerciseDetails/{exerciseID}",
      arguments = listOf(navArgument("exerciseID") { type = NavType.StringType }),
    ) { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("exerciseID")?.let {
        ExerciseDetailsScreen(
          // TODO This is an anti-pattern
          // https://developer.android.com/guide/navigation/use-graph/navigate
          navController,
          exerciseID = it,
        )
      }
    }
  }
}

private fun NavGraphBuilder.addProgramsGraph(navController: NavController) {
  navigation("programsScreen", route = "programs") {
    composable("programsScreen") { ProgramsScreen(navController) }

    composable(
      "programDetails/{programID}",
      arguments = listOf(navArgument("programID") { type = NavType.StringType }),
    ) { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("programID")?.let {
        ProgramDetailsScreen(
          // TODO This is an anti-pattern
          // https://developer.android.com/guide/navigation/use-graph/navigate
          navController,
          programID = it,
        )
      }
    }
    /*composable("exercisesList") {
        val viewModel = hiltViewModel<ExercisesViewModel>()
        ExercisesLazyColumn(
            navController = navController,
            exercisesViewModel = viewModel,
        )
    }
    composable(
        "exerciseDetails/{exerciseID}",
        arguments = listOf(navArgument("exerciseID") { type = NavType.StringType })
    ) { navBackStackEntry ->
        val viewModel = hiltViewModel<ExercisesViewModel>()
        navBackStackEntry.arguments?.getString("exerciseID")?.let {
            ExerciseDetailsScreen(
                navController,
                exercisePK = it,
                exercisesViewModel = viewModel,
            )
        }
    }*/
  }
}

private fun NavGraphBuilder.addWorkoutSummaryGraph(navController: NavController) {
  navigation("workoutSummary/workoutID/workoutCount", route = "workoutSummary") {
    composable("workoutSummary/workoutID/workoutCount") { navBackStackEntry ->
      Log.i("WorkoutSummaryNavGraph", "Navigated to Workout Summary Screen")
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      val workoutsCount = navBackStackEntry.arguments?.getInt("workoutsCount")

      if (workoutID != null && workoutsCount != null) {
        WorkoutSummaryScreen(
          navController = navController,
          workoutID = workoutID,
          workoutsCount = workoutsCount,
        )
      }
    }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun NavGraphBuilder.addHistoryGraph(navController: NavController) {
  navigation("historyScreen", route = "history") {
    composable("historyScreen") { navBackStackEntry ->
      Log.i("HistoryNavGraph", "Navigated to History Screen")
      HistoryScreen(navController = navController)
    }

    composable(route = "history?overviewWorkout={workoutID}") { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("workoutID")?.let {
        WorkoutOverview(workoutID = it, navController = navController)
      }
    }

    composable(route = "history?editWorkout={workoutID}") { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("workoutID")?.let {
        EditWorkoutScreen(workoutID = it, navController = navController)
      }
    }
  }
}
