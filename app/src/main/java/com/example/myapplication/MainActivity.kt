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
import com.example.myapplication.components.history.HistoryScreen
import com.example.myapplication.components.homeScreen.HomeScreen
import com.example.myapplication.components.programs.ProgramDetailsScreen
import com.example.myapplication.components.programs.ProgramsScreen
import com.example.myapplication.components.workout.WorkoutScreen
import com.example.myapplication.components.workout.WorkoutSummaryScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : ComponentActivity() {

  @RequiresApi(Build.VERSION_CODES.S)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      MyApplicationTheme {
        //        val viewModelStoreOwner: ViewModelStoreOwner
        //        val workoutViewModel: WorkoutViewModel = koinViewModel()
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.History.route) {
          addExercisesGraph(navController)
          addWorkoutGraph(
            navController, /*viewModelStoreOwner*/
          )
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
  navController: NavController,
  //  viewModel: WorkoutViewModel,
  //  viewModelStoreOwner: ViewModelStoreOwner
) {
  navigation(
    "workoutScreen?workoutID={workoutID}",
    route = "workout?workoutID={workoutID}",
    arguments = listOf(navArgument("workoutID") { type = NavType.StringType })
  ) {
    composable("workoutScreen?workoutID={workoutID}") { navBackStackEntry ->
      Log.i("WorkoutNavGraph", "Navigated to Workout Screen")
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      WorkoutScreen(
        navController = navController,
        workoutID = workoutID,
      )
    }

    //    composable("workoutContent/{workoutID}") { navBackStackEntry ->
    //      //      val workoutBackStackEntry = remember {
    // navController.getBackStackEntry("workout") }
    //      //      val workoutViewModel: WorkoutViewModel = koinViewModel()
    //      //      CompositionLocalProvider(LocalViewModelStoreOwner provides navBackStackEntry) {
    //      //        Log.i("WorkoutContentNavGraph", "workoutViewModel:
    //      // ${getViewModel<WorkoutViewModel>()}")
    //      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
    //      WorkoutContent(
    //        navController = navController,
    ////        workoutName = "Workout"
    //        //        workoutViewModel = viewModel
    //        //          workoutViewModel = getViewModel<WorkoutViewModel>()
    //      )
    //      //      }
    //    }

    composable("workoutSummary/{workoutID}/{workoutCount}") { navBackStackEntry ->
      Log.i("WorkoutSummaryNavGraph", "Navigated to Workout Summary Screen")
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      val workoutsCount = navBackStackEntry.arguments?.getInt("workoutsCount")

      if (workoutID != null && workoutsCount != null) {
        WorkoutSummaryScreen(
          navController = navController,
          workoutID = workoutID,
          workoutsCount = workoutsCount
        )
      }
      //      CompositionLocalProvider(LocalViewModelStoreOwner provides navBackStackEntry) {
      //        Log.i("WorkoutSummary", "workoutViewModel: ${getViewModel<WorkoutViewModel>()}")
      //      WorkoutSummaryScreen(
      //        navController = navController,
      //        workoutViewModel = viewModel
      //          workoutViewModel = getViewModel<WorkoutViewModel>()
      //        completedWorkout = completedWorkout,
      //        workoutsCount = workoutsCount
      //      )
      //      }
    }

    composable(
      "exerciseDetails/{exerciseID}",
      arguments = listOf(navArgument("exerciseID") { type = NavType.StringType })
    ) { navBackStackEntry ->
      Log.i("WorkoutNavGraph", "Navigated to Exercise Details Screen")
      navBackStackEntry.arguments?.getString("exerciseID")?.let {
        ExerciseDetailsScreen(
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
        navController = navController,
      )
    }
    composable(
      "exerciseDetails/{exerciseID}",
      arguments = listOf(navArgument("exerciseID") { type = NavType.StringType })
    ) { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("exerciseID")?.let {
        ExerciseDetailsScreen(
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
      arguments = listOf(navArgument("programID") { type = NavType.StringType })
    ) { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("programID")?.let {
        ProgramDetailsScreen(
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
          workoutsCount = workoutsCount
        )
      }
      //      CompositionLocalProvider(LocalViewModelStoreOwner provides navBackStackEntry) {
      //        Log.i("WorkoutSummary", "workoutViewModel: ${getViewModel<WorkoutViewModel>()}")
      //      WorkoutSummaryScreen(
      //        navController = navController,
      //        workoutViewModel = viewModel
      //          workoutViewModel = getViewModel<WorkoutViewModel>()
      //        completedWorkout = completedWorkout,
      //        workoutsCount = workoutsCount
      //      )
      //      }
    }
  }
}

private fun NavGraphBuilder.addHistoryGraph(navController: NavController) {
  navigation("historyScreen", route = "history") {
    composable("historyScreen") { navBackStackEntry ->
      Log.i("HistoryNavGraph", "Navigated to History Screen")

      HistoryScreen(
        navController = navController,
      )
      //      CompositionLocalProvider(LocalViewModelStoreOwner provides navBackStackEntry) {
      //        Log.i("WorkoutSummary", "workoutViewModel: ${getViewModel<WorkoutViewModel>()}")
      //      WorkoutSummaryScreen(
      //        navController = navController,
      //        workoutViewModel = viewModel
      //          workoutViewModel = getViewModel<WorkoutViewModel>()
      //        completedWorkout = completedWorkout,
      //        workoutsCount = workoutsCount
      //      )
      //      }
    }
  }
}
