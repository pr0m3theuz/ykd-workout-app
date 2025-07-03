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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.infrastructure.replicator.ReplicatorService
import com.example.myapplication.common.infrastructure.replicator.ReplicatorServiceImpl
import com.example.myapplication.common.infrastructure.services.AuthenticationService
import com.example.myapplication.common.ui.component.BottomNavigationBar
import com.example.myapplication.common.ui.component.Screens
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.features.AddWarmUpSetsScreen
import com.example.myapplication.features.exercises.ui.ExerciseDetailsScreen
import com.example.myapplication.features.exercises.ui.ExerciseNavigableListDetailScreen
import com.example.myapplication.features.exercises.ui.ExercisesLazyColumn
import com.example.myapplication.features.history.HistoryViewModel
import com.example.myapplication.features.history.ui.EditWorkoutScreen
import com.example.myapplication.features.history.ui.HistoryScreen
import com.example.myapplication.features.history.ui.WorkoutOverview
import com.example.myapplication.features.history.ui.WorkoutOverviewContent
import com.example.myapplication.features.homeScreen.HomeScreen
import com.example.myapplication.features.importWorkoutProgram.ImportWorkoutProgramScreen
import com.example.myapplication.features.previewNextWorkout.PreviewNextWorkoutScreen
import com.example.myapplication.features.program.ui.ProgramDetailsScreen
import com.example.myapplication.features.program.ui.ProgramsScreen
import com.example.myapplication.features.syncData.ReplicatorViewModel
import com.example.myapplication.features.syncData.ui.ReplicatorConfigView
import com.example.myapplication.features.syncData.ui.ReplicatorView
import com.example.myapplication.features.userProfileScreen.ProfileScreen
import com.example.myapplication.features.userProfileScreen.ProfileScreenV2
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import com.example.myapplication.features.userProfileScreen.like.boostcamp.AnalyticsScreen
import com.example.myapplication.features.userProfileScreen.like.strengthLog.CalculatorsScreen
import com.example.myapplication.features.userProfileScreen.like.strengthLog.ChooseUnitsScreen
import com.example.myapplication.features.userProfileScreen.like.strengthLog.EditProfileScreen
import com.example.myapplication.features.userProfileScreen.like.strengthLog.RoundOffScreen
import com.example.myapplication.features.userProfileScreen.like.strengthLog.SettingsScreen
import com.example.myapplication.features.userProfileScreen.like.strengthLog.TimerSettingsScreen
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import org.bson.types.ObjectId
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.java.KoinJavaComponent.inject

class MainActivity : ComponentActivity() {

  private lateinit var crashlytics: FirebaseCrashlytics
  private val applicationScope =
    CoroutineScope(Dispatchers.Default) // Or SupervisorJob() + Dispatchers.Default

  lateinit var userProfileManager: UserProfileManager
  var replicatorService: ReplicatorService? = null // Make it nullable initially

  @OptIn(InternalCoroutinesApi::class)
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

    userProfileManager = inject<UserProfileManager>(UserProfileManager::class.java).value
    Log.d("MainActivity", "MainActivity.onCreate(): userProfileManager: $userProfileManager")
    val authService: AuthenticationService by inject()
    // TODO Add User Profile Manager here by injecting it
    //      val userProfileRepository: KeyValueRepository by inject()
//    val userProfileManager: UserProfileManager by inject()

    applicationScope.launch {
      userProfileManager.userProfile
        .filterNotNull() // Wait until a non-null profile is emitted
        .collect { profile ->
          println("MyApplication: UserProfile loaded, initializing ReplicatorServiceImpl.")
          if (replicatorService == null) { // Ensure it's initialized only once
            replicatorService = ReplicatorServiceImpl(
              userProfileManager = userProfileManager,
              databaseManager = get(),
            )
          }
        }
    }

//    val replicatorService: ReplicatorService by inject()

    setContent {

      val mainViewModel = getViewModel<MainViewModel>()
      replicatorService?.updateAuthentication(isReset = false)

      MyApplicationTheme {
        //        TestScreen()

        val navController = rememberNavController()
        // <<< --- ADD LOGGING FOR getCurrentBackStackEntryFlow() HERE --- >>>
        /*        LaunchedEffect(navController) {
                  navController.currentBackStackEntryFlow.onEach {
                     navBackStackEntry ->
                      Log.d(
                        "BackStackFlow",
                        "Current NavBackStackEntry: ${navBackStackEntry.destination.route}, ID: ${navBackStackEntry.id}"
                      )
                    }
                    .launchIn(this) // Launch collection in the LaunchedEffect's scope

                  // Optional: Log the entire back stack whenever the current entry changes
                  // This can be verbose but very helpful for debugging popUpTo issues.
                  navController.currentBackStack.collect { backStack ->
                    Log.d("BackStackFlow", "Current full back stack:")
                    backStack.forEachIndexed { index, entry ->
                      Log.d("BackStackFlow", "  [$index] Route: ${entry.destination.route}, ID: ${entry.id}, Args: ${entry.arguments}")
                    }
                  }
                }*/
        // <<< --- END OF LOGGING --- >>>


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
    "workoutScreen?workoutID={workoutID}/copyWorkoutFromPreviousWorkout={copyWorkoutFromPreviousWorkout}",
    route = "workout",
//    arguments = listOf(navArgument("workoutID") { type = NavType.StringType }),
  ) {
    composable("workoutScreen?workoutID={workoutID}/copyWorkoutFromPreviousWorkout={copyWorkoutFromPreviousWorkout}")
    { navBackStackEntry ->
//      Log.d("WorkoutNavGraph", "Navigated to Workout Screen")

      val workoutViewModel: WorkoutViewModel =
        navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)
      val loadPreviousWorkout =
        navBackStackEntry.arguments?.getBoolean("loadPreviousWorkout") ?: false
      val copyWorkoutFromPreviousWorkout =
        navBackStackEntry.arguments?.getBoolean("copyWorkoutFromPreviousWorkout") ?: false

//      Log.d("WorkoutNavGraph.WorkoutScreen", "WorkoutScreen.WorkoutViewModel: $workoutViewModel")

      val workoutID = navBackStackEntry.arguments?.getString("workoutID")

      LaunchedEffect(workoutID) {
        Log.d("WorkoutNavGraph", "LaunchedEffect launched!")
        Log.d("WorkoutNavGraph", "workoutID: $workoutID")
        if (!workoutID.isNullOrBlank() && !copyWorkoutFromPreviousWorkout) {
          Log.d("WorkoutScreen", "workoutID: $workoutID, calling getWorkoutByID()")
          workoutViewModel.getWorkoutByID(ObjectId(workoutID))
        } else if (!workoutID.isNullOrBlank() && copyWorkoutFromPreviousWorkout) {
          workoutViewModel.copyWorkoutFromPreviousWorkout(ObjectId(workoutID))
        }
      }

      WorkoutScreen(
        // TODO This is an anti-pattern
        // https://developer.android.com/guide/navigation/use-graph/navigate
        navController = navController,
        workoutViewModel = workoutViewModel,
        workoutID = workoutID,
        loadPreviousWorkout = loadPreviousWorkout,
        copyWorkoutFromPreviousWorkout = copyWorkoutFromPreviousWorkout,
        currentDestination = navController.currentBackStackEntry?.destination,
        onNavigateBottomBar = { route ->
          navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
          }
        }
      )
    }

    composable("previewWorkoutScreen") { navBackStackEntry ->
//      Log.d("WorkoutNavGraph", "Navigated to Preview Workout Screen")
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

    /* TODO not used
        composable("workoutScreen?workoutID={workoutID}?isCurrentWorkout={isCurrentWorkout}") { navBackStackEntry ->
    //      Log.d("WorkoutNavGraph", "Navigated to Workout Screen")

          val workoutViewModel: WorkoutViewModel =
            navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)

          val isCurrentWorkout = navBackStackEntry.arguments?.getBoolean("isCurrentWorkout") ?: false

          Log.d("WorkoutNavGraph.WorkoutScreen", "WorkoutScreen.WorkoutViewModel: $workoutViewModel")

          val workoutID = navBackStackEntry.arguments?.getString("workoutID")
          WorkoutScreen(
            // TODO This is an anti-pattern
            // https://developer.android.com/guide/navigation/use-graph/navigate
            navController = navController,
            workoutViewModel = workoutViewModel,
            workoutID = workoutID,
            loadPreviousWorkout = false,
            copyWorkoutFromPreviousWorkout = false
          )
        }*/

    composable("workoutSummary/{workoutID}/{workoutCount}") { navBackStackEntry ->
//      Log.d("WorkoutSummaryNavGraph", "Navigated to Workout Summary Screen")
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
          currentDestination = navController.currentBackStackEntry?.destination,
          onNavigateBottomBar = { route ->
            navController.navigate(route) {
              // Pop up to the start destination of the graph to
              // avoid building up a large stack of destinations
              // on the back stack as users select items
              popUpTo(navController.graph.findStartDestination().id) { saveState = true }
              // Avoid multiple copies of the same destination when
              // reselecting the same item
              launchSingleTop = true
              // Restore state when reselecting a previously selected item
              restoreState = true
            }
          }
        )
      }
    }

    composable(
      "exerciseDetails/{exerciseID}",
      arguments = listOf(navArgument("exerciseID") { type = NavType.StringType }),
    ) { navBackStackEntry ->
//      Log.d("WorkoutNavGraph", "Navigated to Exercise Details Screen")
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

//      Log.d("WorkoutNavGraph:AddExercise", "AddExercise.WorkoutViewModel: $workoutViewModel")

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

    composable("addWarmUpSets/{exerciseSetsID}") { navBackStackEntry ->
      val workoutViewModel: WorkoutViewModel =
        navBackStackEntry.sharedViewModel<WorkoutViewModel>(navController)

      val exerciseSetsID = navBackStackEntry.arguments?.getString("exerciseSetsID")
      Log.d("AddWarmUpSetsScreen", "exerciseSetsID: $exerciseSetsID")
      AddWarmUpSetsScreen(
        workoutViewModel = workoutViewModel,
        onNavigateBack = { navController.popBackStack() },
        exerciseSetsID = ObjectId(exerciseSetsID)
        // workoutViewModel can be koinViewModel() here if not passed from parent
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
          ExercisesLazyColumn(
            navigateToDetailPane = onClick,
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigateBottomBar = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            }
          )
        },
        exerciseDetailPane = { exerciseID: String, onClick: () -> Unit ->
          ExerciseDetailsScreen(navController, exerciseID, onClick)
        },
      )
    }

//    composable("exerciseChart") { FullScreenChart(navigateUp = { navController.navigateUp() }) }
  }
}

private fun NavGraphBuilder.addProgramsGraph(navController: NavController) {
  navigation("programsScreen", route = "programs") {
    composable("programsScreen") {
      ProgramsScreen(
        navController,
        currentDestination = navController.currentBackStackEntry?.destination,
        onNavigateBottomBar = { route ->
          navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
          }
        })
    }

    composable(
      "programDetails/{programID}/isProgramActive={isProgramActive}",
      arguments = listOf(navArgument("programID") { type = NavType.StringType }),
    ) { navBackStackEntry ->

      val programID = navBackStackEntry.arguments?.getString("programID")
      val isProgramActive = navBackStackEntry.arguments?.getBoolean("isProgramActive") ?: false

      requireNotNull(programID)

      // TODO This is an anti-pattern
      // https://developer.android.com/guide/navigation/use-graph/navigate
      ProgramDetailsScreen(
        navController,
        _programID = programID,
        isProgramActive = isProgramActive,
        currentDestination = navController.currentBackStackEntry?.destination,
        onNavigateBottomBar = { route ->
          navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
          }
        }
      )
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
          currentDestination = navController.currentBackStackEntry?.destination,
          onNavigateBottomBar = { route ->
            navController.navigate(route) {
              // Pop up to the start destination of the graph to
              // avoid building up a large stack of destinations
              // on the back stack as users select items
              popUpTo(navController.graph.findStartDestination().id) { saveState = true }
              // Avoid multiple copies of the same destination when
              // reselecting the same item
              launchSingleTop = true
              // Restore state when reselecting a previously selected item
              restoreState = true
            }
          }
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

      val historyViewModel: HistoryViewModel =
        navBackStackEntry.sharedViewModel<HistoryViewModel>(navController)

      HistoryScreen(
        historyViewModel,
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            })
        },
        navigateToWorkoutOverview = {
          navController.navigate("history?workoutOverview=${it.id.toHexString()}")
        }
      )
    }

    composable(route = "history?workoutOverview={workoutID}") { navBackStackEntry ->
      navBackStackEntry.arguments?.getString("workoutID")?.let {

        val historyViewModel: HistoryViewModel =
          navBackStackEntry.sharedViewModel<HistoryViewModel>(navController)

        WorkoutOverview(
          historyViewModel = historyViewModel,
          workoutID = it,
          popBackStack = { navController.popBackStack() },
          workoutOverviewContent = { workout, setOpenDeleteWorkoutDialog ->
            WorkoutOverviewContent(
              workout = workout,
              setOpenDeleteWorkoutDialog = { setOpenDeleteWorkoutDialog },
              performAgain = true,
              userDefaultWeightUnit = historyViewModel.userDefaultWeightUnit,
              workoutComponents = historyViewModel.workoutComponents.collectAsState().value,
              navigateUp = { navController.navigateUp() },
              navigateToEditWorkout = { workout -> navController.navigate("history?editWorkout=${workout.id.toHexString()}") },
              navigateToWorkout = { workout ->
                navController.navigate("workoutScreen?workoutID=${workout.id.toHexString()}/copyWorkoutFromPreviousWorkout=true") {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  // Avoid multiple copies of the same destination when
                  // reselecting the same item
                  launchSingleTop = true
                  // Restore state when reselecting a previously selected item
                  restoreState = true
                }
              }
            )
          }
        )
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
  navigation("profileScreenV2", route = Screens.Profile.baseRoute) {
    composable("profileScreen") { navBackStackEntry ->
      Log.d("ProfileNavGraph", "Navigated to User Profile Screen")
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      ProfileScreen(
//        navController = navController,
        navigateToReplicatorScreen = { navController.navigate("replicatorScreen") },
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            })
        },
      )
    }

    composable("profileScreenV2") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      ProfileScreenV2(
        profileViewModel = profileViewModel,
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            })
        },
        navigateToReplicatorScreen = {},
        navigateToAnalyticsScreen = { navController.navigate("analyticsScreen") },
        navigateToSettingsScreen = { navController.navigate("settingsScreen") },
        navigateToHistoryScreen = { navController.navigate(Screens.History.route) },
        navigateToWorkoutOverview = { workout -> "history?workoutOverview=${workout.id.toHexString()}" }
      )
    }

    composable("replicatorScreen") {
//      Log.d("ReplicatorNavGraph", "Navigated to Replicator Screen")
      ReplicatorView(
        viewModel = koinViewModel<ReplicatorViewModel>(),
        replicatorConfigNav = { navController.navigate("configScreen") },
        currentDestination = navController.currentBackStackEntry?.destination,
        onNavigateBottomBar = { route ->
          navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
          }
        }
      )
    }

    composable("configScreen") {
      ReplicatorConfigView(
        viewModel = koinViewModel<ReplicatorViewModel>(),
        navigateUp = { navController.popBackStack() },
      )
    }

    composable("settingsScreen") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      SettingsScreen(
//        profileViewModel = profileViewModel,
        onNavigate = { navController.navigate(it) },
        navigateToReplicatorScreen = { navController.navigate("replicatorScreen") },
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            })
        }
      )
    }

    composable("profileSettings") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      EditProfileScreen(
        profileViewModel = profileViewModel,
        onSaveChanges = { name, username, email, password ->
          profileViewModel.updateName(name)
          profileViewModel.updateUsername(username)
          profileViewModel.updateEmail(email)
          if (password.isNotBlank()) { // Only update password if something is entered
            profileViewModel.updatePassword(password)
          }
          // Persist changes
          profileViewModel.saveUserProfile()
        })
    }

    composable("timerSettings") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      TimerSettingsScreen(
        onNavigateBack = { navController.popBackStack() },
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            })
        }
      )
    }

    composable("calculatorsScreen") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      CalculatorsScreen(
        onNavigateBack = { navController.popBackStack() },
        onCalculatorSelected = { id -> println("Selected: $id") },
        onShowHelp = { id -> println("Help for: $id") },
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            })
        }
      )
    }

    composable("analyticsScreen") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      AnalyticsScreen(onNavigateToDashboardsInfo = {})
    }

    composable("unitsSettings") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)

      ChooseUnitsScreen(
        profileViewModel = profileViewModel,
        onNavigateBack = { navController.popBackStack() },
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            }
          )
        }
      )
    }

    composable("roundOffSettings") { navBackStackEntry ->
      val profileViewModel: ProfileViewModel =
        navBackStackEntry.sharedViewModel<ProfileViewModel>(navController)


      RoundOffScreen(
        profileViewModel = profileViewModel,
        onNavigateBack = { navController.popBackStack() },
        bottomNavigationBar = {
          BottomNavigationBar(
            currentDestination = navController.currentBackStackEntry?.destination,
            onNavigate = { route ->
              navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
              }
            }
          )
        }
      )
    }

  }
}

private fun NavGraphBuilder.addReplicatorGraph(navController: NavController) {
  navigation("replicatorScreen", route = Screens.Replicator.baseRoute) {
    composable("replicatorScreen") {
      Log.d("ReplicatorNavGraph", "Navigated to Replicator Screen")
      ReplicatorView(
        viewModel = koinViewModel<ReplicatorViewModel>(),
        replicatorConfigNav = { navController.navigate("configScreen") },
        currentDestination = navController.currentBackStackEntry?.destination,
        onNavigateBottomBar = { route ->
          navController.navigate(route) {
            // Pop up to the start destination of the graph to
            // avoid building up a large stack of destinations
            // on the back stack as users select items
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            // Avoid multiple copies of the same destination when
            // reselecting the same item
            launchSingleTop = true
            // Restore state when reselecting a previously selected item
            restoreState = true
          }
        }
      )
    }
  }

  composable("configScreen") {
    ReplicatorConfigView(
      viewModel = koinViewModel<ReplicatorViewModel>(),
      navigateUp = { navController.popBackStack() },
    )
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
