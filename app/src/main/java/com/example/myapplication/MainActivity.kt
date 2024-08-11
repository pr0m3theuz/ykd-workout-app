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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.myapplication.components.Screens
import com.example.myapplication.components.addExercisesList.AddExerciseNavigableListDetailScreen
import com.example.myapplication.components.addExercisesList.AddExercisesScreen
import com.example.myapplication.components.exerciseDetails.ExerciseDetailsScreen
import com.example.myapplication.components.exerciseDetails.FullScreenChart
import com.example.myapplication.components.exercisesList.ExerciseNavigableListDetailScreen
import com.example.myapplication.components.exercisesList.ExercisesLazyColumn
import com.example.myapplication.components.history.EditWorkoutScreen
import com.example.myapplication.components.history.HistoryScreen
import com.example.myapplication.components.history.WorkoutOverview
import com.example.myapplication.components.homeScreen.HomeScreen
import com.example.myapplication.components.importWorkoutProgram.ImportWorkoutProgramScreen
import com.example.myapplication.components.programs.ProgramDetailsScreen
import com.example.myapplication.components.programs.ProgramsScreen
import com.example.myapplication.components.workout.WorkoutScreen
import com.example.myapplication.components.workout.WorkoutSummaryScreen
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.newrelic.agent.android.NewRelic
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : ComponentActivity() {

  private lateinit var crashlytics: FirebaseCrashlytics

  @RequiresApi(Build.VERSION_CODES.S)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    crashlytics = Firebase.crashlytics
    crashlytics.log("onCreate")

    // Log the onCreate event, this will also be printed in logcat
    crashlytics.log("onCreate")

    // Add some custom values and identifiers to be included in crash reports
    crashlytics.setCustomKeys {
      key("MeaningOfLife", 42)
      key("LastUIAction", "Test value")
    }
    crashlytics.setUserId("123456789")

    // Report a non-fatal exception, for demonstration purposes
    crashlytics.recordException(Exception("Non-fatal exception: something went wrong!"))

    // New Relic
    NewRelic.withApplicationToken("eu01xx7700dce7ae53e2b20b7cc382fcba147c93ae-NRMA")
      .withCrashReportingEnabled(true)
      .withLoggingEnabled(true)
      .start(this.applicationContext)

    setContent {
      MyApplicationTheme {
        TestScreen()

        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = Screens.Programs.route) {
          addExercisesGraph(navController)
          addWorkoutGraph(navController)
          addWorkoutSummaryGraph(navController)
          addProgramsGraph(navController)
          addHistoryGraph(navController)
          navigation("homeScreen", route = "home") { composable("homeScreen") { HomeScreen() } }
        }
      }
    }

    //    crashlytics.log("Activity created")
  }

  companion object {
    fun newIntent(context: Context): Intent {
      return Intent(context, MainActivity::class.java)
    }
  }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
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

      //      navController.currentBackStackEntry?.savedStateHandle?.get<Workout>(key =
      // WORKOUT_DETAILS)

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
          onClick = {},
          exerciseID = it,
        )
      }
    }

    composable("AddExercise") {
      AddExerciseNavigableListDetailScreen(
        navController = navController,
        exerciseListPane = { onClick ->
          AddExercisesScreen(
            setOpenDialog = { /*TODO*/ },
            navController = navController,
            navigateToDetailPane = onClick,
          )
        },
        exerciseDetailPane = { exerciseID: String, onClick: () -> Unit ->
          ExerciseDetailsScreen(navController, exerciseID, onClick)
        },
      )
    }
  }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
private fun NavGraphBuilder.addExercisesGraph(navController: NavController) {
  navigation("exercisesList", route = "exercises") {
    //    composable("exercisesScreen") { ExercisesScreen() }

    composable("exercisesList") {
      ExerciseNavigableListDetailScreen(
        navController = navController,
        exerciseListPane = { onClick ->
          ExercisesLazyColumn(navController = navController, navigateToDetailPane = onClick)
        },
        exerciseDetailPane = { exerciseID: String, onClick: () -> Unit ->
          ExerciseDetailsScreen(navController, exerciseID, onClick)
        },
      )
    }

    composable("exerciseChart") { FullScreenChart(navigateUp = { navController.navigateUp() }) }
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

    composable("importWorkoutProgram") { ImportWorkoutProgramScreen(navController) }
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

@Composable
private fun TestScreen() {
  Scaffold() { contentPadding ->
    Column(
      Modifier
        .padding(contentPadding)
        .fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      AsyncImage(
        model = "https://i.pinimg.com/736x/de/6f/da/de6fda600cc48c291a6823e8a09bf94b.jpg",
        contentDescription = null,
      )

      Button(
        onClick = {
          throw RuntimeException("Test Crash") // Force a crash
        }
      ) {
        Text(text = "Test Crash")
      }
    }
  }
}
