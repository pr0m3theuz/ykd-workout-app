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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.replicator.ReplicatorService
import com.example.myapplication.common.infrastructure.services.AuthenticationService
import com.example.myapplication.common.ui.component.Screens
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.features.exercises.ui.ExerciseDetailsScreen
import com.example.myapplication.features.exercises.ui.ExerciseNavigableListDetailScreen
import com.example.myapplication.features.exercises.ui.ExercisesLazyColumn
import com.example.myapplication.features.exercises.ui.FullScreenChart
import com.example.myapplication.features.history.ui.EditWorkoutScreen
import com.example.myapplication.features.history.ui.HistoryScreen
import com.example.myapplication.features.history.ui.WorkoutOverview
import com.example.myapplication.features.homeScreen.HomeScreen
import com.example.myapplication.features.importWorkoutProgram.ImportWorkoutProgramScreen
import com.example.myapplication.features.previewNextWorkout.PreviewNextWorkoutScreen
import com.example.myapplication.features.program.ui.ProgramDetailsScreen
import com.example.myapplication.features.program.ui.ProgramsScreen
import com.example.myapplication.features.syncData.ReplicatorViewModel
import com.example.myapplication.features.syncData.ui.ReplicatorConfigView
import com.example.myapplication.features.syncData.ui.ReplicatorView
import com.example.myapplication.features.userProfileScreen.ProfileScreen
import com.example.myapplication.features.workout.WorkoutViewModel
import com.example.myapplication.features.workout.feature.addExercisesList.AddExerciseNavigableListDetailScreen
import com.example.myapplication.features.workout.feature.addExercisesList.AddExercisesScreen
import com.example.myapplication.features.workout.feature.previewWorkout.WorkoutPreviewScreen
import com.example.myapplication.features.workout.ui.WorkoutScreen
import com.example.myapplication.features.workout.ui.WorkoutSummaryScreen
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.crashlytics.setCustomKeys
import com.newrelic.agent.android.NewRelic
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel

class MainActivity : ComponentActivity() {

  private lateinit var crashlytics: FirebaseCrashlytics

  @RequiresApi(Build.VERSION_CODES.S)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

//    enableEdgeToEdge()

    WindowCompat.getInsetsController(window, window.decorView)
      .isAppearanceLightStatusBars = true

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

      val authService: AuthenticationService by inject()
      // TODO Add User Profile Manager here by injecting it
      //      val userProfileRepository: KeyValueRepository by inject()
      val userProfileManager: UserProfileManager by inject()
      val replicatorService: ReplicatorService by inject()
      val mainViewModel = getViewModel<MainViewModel>()

      userProfileManager.updateUserProfile(
        UserProfile(
          username = "user",
          name = "user",
          email = "user",
          password = "password",
          authToken = "TOKEN"
        )
      )
//      authService.authenticatedUser(username = uname, password = pwd)

      replicatorService.updateAuthentication(isReset = false)

      MyApplicationTheme {
        //        TestScreen()

        val navController = rememberNavController()

        MainView(mainViewModel.startDatabase, mainViewModel.closeDatabase)

        NavHost(navController = navController, startDestination = Screens.Replicator.route) {
          addExercisesGraph(navController)
          addWorkoutGraph(navController)
          addWorkoutSummaryGraph(navController)
          addProgramsGraph(navController)
          addHistoryGraph(navController)
          addProfileGraph(navController)
          addReplicatorGraph(navController)
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

// Helper function to get and remember the parent graph entry
@Composable
fun NavBackStackEntry.rememberParentEntry(
  navController: NavController,
  graphRoute: String
): NavBackStackEntry {
  // NavBackStackEntry is the owner of the composable, remember based on it
  return remember(this) {
    navController.getBackStackEntry(graphRoute)
  }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(navController: NavController):
        T {
  val navGraphRoute = destination.parent?.route ?: return koinViewModel()
  val parentEntry = remember(this) { navController.getBackStackEntry(navGraphRoute) }
  return koinViewModel(viewModelStoreOwner = parentEntry)
}


// TODO rewrite shared viewmodel & consider using koin scope
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@RequiresApi(Build.VERSION_CODES.S)
private fun NavGraphBuilder.addWorkoutGraph(
  navController: NavController
  //  viewModel: WorkoutViewModel,
  //  viewModelStoreOwner: ViewModelStoreOwner
) {
  navigation(
    "workoutScreen?workoutID={workoutID}",
    route = "workout",
//    arguments = listOf(navArgument("workoutID") { type = NavType.StringType }),
  ) {
    composable("workoutScreen?workoutID={workoutID}") { navBackStackEntry ->
      Log.d("WorkoutNavGraph", "Navigated to Workout Screen")

      val workoutViewModel: WorkoutViewModel =
        navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)

      Log.d("WorkoutNavGraph.WorkoutScreen", "WorkoutScreen.WorkoutViewModel: $workoutViewModel")

      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      WorkoutScreen(
        // TODO This is an anti-pattern
        // https://developer.android.com/guide/navigation/use-graph/navigate
        navController = navController,
        workoutViewModel = workoutViewModel,
        workoutID = workoutID,
      )
    }

    composable("previewWorkoutScreen") { navBackStackEntry ->
      Log.d("WorkoutNavGraph", "Navigated to Preview Workout Screen")
      val workoutViewModel: WorkoutViewModel =
        navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)

      WorkoutPreviewScreen(
        viewModel = workoutViewModel,
        onBack = { navController.popBackStack() },
        onAccept = {
          workoutViewModel.acceptProgressions()
          navController.navigate("workoutScreen?workoutID=${it.id}")
        },
        onReject = {
          workoutViewModel.rejectProgressions()
          navController.navigate("workoutScreen?workoutID=${it.id}")
        }
      )
    }

    composable("workoutScreen?workoutID={workoutID}?isCurrentWorkout={isCurrentWorkout}") { navBackStackEntry ->
      Log.d("WorkoutNavGraph", "Navigated to Workout Screen")

      val workoutViewModel: WorkoutViewModel =
        navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)

      Log.d("WorkoutNavGraph.WorkoutScreen", "WorkoutScreen.WorkoutViewModel: $workoutViewModel")

      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      WorkoutScreen(
        // TODO This is an anti-pattern
        // https://developer.android.com/guide/navigation/use-graph/navigate
        navController = navController,
        workoutViewModel = workoutViewModel,
        workoutID = workoutID,
      )
    }

    composable("workoutSummary/{workoutID}/{workoutCount}") { navBackStackEntry ->
      Log.d("WorkoutSummaryNavGraph", "Navigated to Workout Summary Screen")
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      val workoutsCount = navBackStackEntry.arguments?.getInt("workoutsCount")

//      val workoutViewModel: WorkoutViewModel = koinViewModel(
//        viewModelStoreOwner = navBackStackEntry.rememberParentEntry(navController, Screens.Workout.baseRoute)
//      )

      val workoutViewModel: WorkoutViewModel =
        navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)

      //      navController.currentBackStackEntry?.savedStateHandle?.get<Workout>(key =
      // WORKOUT_DETAILS)

      if (workoutID != null && workoutsCount != null) {
        WorkoutSummaryScreen(
          // TODO This is an anti-pattern
          // https://developer.android.com/guide/navigation/use-graph/navigate
          navController = navController,
          workoutID = workoutID,
          workoutsCount = workoutsCount,
          workoutViewModel = workoutViewModel,
        )
      }
    }

    composable(
      "exerciseDetails/{exerciseID}",
      arguments = listOf(navArgument("exerciseID") { type = NavType.StringType }),
    ) { navBackStackEntry ->
      Log.d("WorkoutNavGraph", "Navigated to Exercise Details Screen")
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

    composable("AddExercise") { navBackStackEntry ->
      val workoutViewModel: WorkoutViewModel =
        navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)

      Log.d("WorkoutNavGraph:AddExercise", "AddExercise.WorkoutViewModel: $workoutViewModel")

      AddExerciseNavigableListDetailScreen(
        exerciseListPane = { onClick ->
          AddExercisesScreen(
            setOpenDialog = { /*TODO*/ },
            navController = navController,
            navigateToDetailPane = onClick,
            workoutViewModel = workoutViewModel,
          )
        },
        exerciseDetailPane = { exerciseID: String, onClick: () -> Unit ->
          ExerciseDetailsScreen(navController, exerciseID, onClick)
        },
      )
    }

    composable("previewNextWorkout/{workoutID}") { navBackStackEntry ->
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      //      val workoutID = "66cb82f4e1a3eb29e7c411e6"

      if (workoutID != null) {
        PreviewNextWorkoutScreen(navController = navController, workoutID = workoutID)
      }
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
          _programID = it,
        )
      }
    }

    composable("importWorkoutProgram") { ImportWorkoutProgramScreen(navController) }
  }
}

// TODO Does this get used?
@RequiresApi(Build.VERSION_CODES.S)
private fun NavGraphBuilder.addWorkoutSummaryGraph(navController: NavController) {
  navigation(startDestination = "workoutSummary/workoutID/workoutCount", route = "workoutSummary") {
    composable("workoutSummary/workoutID/workoutCount") { navBackStackEntry ->
      Log.d("WorkoutSummaryNavGraph", "Navigated to Workout Summary Screen")

      val workoutViewModel: WorkoutViewModel = koinViewModel(
        viewModelStoreOwner = navBackStackEntry.rememberParentEntry(
          navController,
          Screens.Workout.baseRoute
        )
      )

      // TODO to keep or to not keep?
      val workoutID = navBackStackEntry.arguments?.getString("workoutID")
      val workoutsCount = navBackStackEntry.arguments?.getInt("workoutsCount")

      if (workoutID != null && workoutsCount != null) {
        WorkoutSummaryScreen(
          navController = navController,
          workoutID = workoutID,
          workoutsCount = workoutsCount,
          workoutViewModel = workoutViewModel,
        )
      }
    }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun NavGraphBuilder.addHistoryGraph(navController: NavController) {
  navigation("historyScreen", route = Screens.History.baseRoute) {
    composable("historyScreen") { navBackStackEntry ->
      Log.d("HistoryNavGraph", "Navigated to History Screen")
      HistoryScreen(navController = navController)
    }

    composable(route = "history?overviewWorkout={workoutID}") { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("workoutID")?.let {
        WorkoutOverview(workoutID = it, navController = navController, performAgain = true)
      }
    }

    composable(route = "history?editWorkout={workoutID}") { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("workoutID")?.let {
        EditWorkoutScreen(workoutID = it, navController = navController, editable = true)
      }
    }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
private fun NavGraphBuilder.addProfileGraph(navController: NavController) {
  navigation("profileScreen", route = Screens.Profile.baseRoute) {
    composable("profileScreen") { navBackStackEntry ->
      Log.d("ProfileNavGraph", "Navigated to User Profile Screen")
      ProfileScreen(navController = navController,
        navigateToReplicatorScreen = { navController.navigate("replicatorScreen") })
    }

    composable("replicatorScreen") {
//      Log.d("ReplicatorNavGraph", "Navigated to Replicator Screen")
      ReplicatorView(
        navController,
        viewModel = koinViewModel<ReplicatorViewModel>(),
        replicatorConfigNav = { navController.navigate("configScreen") },
      )
    }

    composable("configScreen") {
      ReplicatorConfigView(
        viewModel = koinViewModel<ReplicatorViewModel>(),
        navigateUp = { navController.popBackStack() },
      )
    }

  }
}

//val scaffoldState: ScaffoldState = rememberScaffoldState()

private fun NavGraphBuilder.addReplicatorGraph(navController: NavController) {
  navigation("replicatorScreen", route = Screens.Replicator.baseRoute) {
    composable("replicatorScreen") {
      Log.d("ReplicatorNavGraph", "Navigated to Replicator Screen")
      ReplicatorView(
        navController,
        viewModel = koinViewModel<ReplicatorViewModel>(),
//        openDrawer = TODO(),
        replicatorConfigNav = { navController.navigate("configScreen") },
//        scaffoldState = TODO()
      )
    }

    composable("configScreen") {
      ReplicatorConfigView(
        viewModel = koinViewModel<ReplicatorViewModel>(),
        navigateUp = { navController.popBackStack() },
//        scaffoldState = TODO()
      )
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
      val provider =
        GoogleFont.Provider(
          providerAuthority = "com.google.android.gms.fonts",
          providerPackage = "com.google.android.gms",
          certificates = R.array.com_google_android_gms_fonts_certs,
        )

      val fontName = GoogleFont("East Sea Dokdo")

      val fontFamily = FontFamily(Font(googleFont = fontName, fontProvider = provider))

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(250.dp), contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          contentScale = ContentScale.FillWidth,
          alignment = Alignment.TopCenter,
          model = "https://i.pinimg.com/originals/b0/61/b6/b061b61f7fe227a9dacd5d5dd67390d3.jpg",
          contentDescription = null,
          colorFilter = ColorFilter.tint(Color.LightGray, BlendMode.Modulate),
        )
        val fontSize = MaterialTheme.typography.headlineLarge.fontSize.times(4)
        Text(
          text = "RAIDER",
          style =
          TextStyle(
            color = Color.White,
            fontSize = MaterialTheme.typography.headlineLarge.fontSize.times(4),
            fontWeight = FontWeight.Bold,
            fontFamily = fontFamily,
          ),
        )
      }

      //      Button(
      //        onClick = {
      //          throw RuntimeException("Test Crash") // Force a crash
      //        }
      //      ) {
      //        Text(text = "Test Crash")
      //      }
    }
  }
}
