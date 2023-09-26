@file:OptIn(ExperimentalMaterialApi::class, ExperimentalSerializationApi::class)

package com.example.myapplication

// import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.example.myapplication.components.homeScreen.HomeScreen
import com.example.myapplication.components.programs.ProgramDetailsScreen
import com.example.myapplication.components.programs.ProgramsScreen
import com.example.myapplication.components.workout.WorkoutScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      MyApplicationTheme {
        //        val viewModelStoreOwner: ViewModelStoreOwner
        //        val workoutViewModel: WorkoutViewModel = koinViewModel()
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.Programs.route) {
          addExercisesGraph(navController)
          addWorkoutGraph(
            navController, /*viewModelStoreOwner*/
          )
          addProgramsGraph(navController)
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

private fun NavGraphBuilder.addWorkoutGraph(
  navController: NavController,
  //  viewModel: WorkoutViewModel,
  //  viewModelStoreOwner: ViewModelStoreOwner
) {
  navigation("workoutScreen?workoutID={workoutID}", route = "workout?workoutID={workoutID}",
    arguments = listOf(navArgument("workoutID") { type = NavType.StringType })
  ) {
    composable("workoutScreen?workoutID={workoutID}") { navBackStackEntry ->
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      WorkoutScreen(
        navController = navController,
        workoutID = workoutID,
      )
    }

//    composable("workoutContent/{workoutID}") { navBackStackEntry ->
//      //      val workoutBackStackEntry = remember { navController.getBackStackEntry("workout") }
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

//    composable("workoutSummary/workoutID/workoutCount") { navBackStackEntry ->
//      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
//      val workoutsCount = navBackStackEntry.arguments?.getInt("workoutsCount")
//
//      if (workoutID != null && workoutsCount != null) {
//        WorkoutSummaryScreen(
//          navController = navController,
//          workoutID = workoutID,
//          workoutsCount = workoutsCount
//        )
//      }
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
//    }

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
