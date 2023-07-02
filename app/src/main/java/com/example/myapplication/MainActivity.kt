@file:OptIn(ExperimentalMaterialApi::class)

package com.example.myapplication

// import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.components.*
import com.example.myapplication.components.exerciseDetails.ExerciseDetailsScreen
import com.example.myapplication.components.exercisesList.ExercisesLazyColumn
import com.example.myapplication.components.exercisesList.ExercisesScreen
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.components.homeScreen.HomeScreen
import com.example.myapplication.components.programs.ProgramDetailsScreen
import com.example.myapplication.components.programs.ProgramsScreen
import com.example.myapplication.components.workout.WorkoutContent
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "workout") {
                    addExercisesGraph(navController)
                    addWorkoutGraph(navController)
                    addProgramsGraph(navController)
                    navigation(
                        "homeScreen",
                        route = "home"
                    ) {
                        composable("homeScreen") {
                            HomeScreen()
                        }
                    }
                }
//                Surface {
//                    WorkoutScreen()
//                    ExercisesScreen(exercisesViewModel = exercisesViewModel)
//                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}

private fun NavGraphBuilder.addWorkoutGraph(navController: NavController) {
    navigation("workoutContent", route = "workout") {
/*        composable("workoutScreen") {
            WorkoutScreen()
        }*/
        composable("workoutContent") {
            val viewModel = hiltViewModel<WorkoutViewModel>()
            WorkoutContent(
                navController = navController,
                workoutViewModel = viewModel,
            )
        }
        composable("exercisesList") {
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
        }
    }
}

private fun NavGraphBuilder.addExercisesGraph(navController: NavController) {
    navigation("exercisesList", route = "exercises") {
        composable("exercisesScreen") {
            ExercisesScreen()
        }

        composable("exercisesList") {
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
        }
    }
}

private fun NavGraphBuilder.addProgramsGraph(navController: NavController) {
    navigation("programsScreen", route = "programs") {
        composable("programsScreen") {
            ProgramsScreen(navController)
        }
        composable("programDetails") {
            ProgramDetailsScreen(navController)
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
