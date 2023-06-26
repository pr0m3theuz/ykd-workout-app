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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.components.*
import com.example.myapplication.components.exercisesList.ExercisesScreen
import com.example.myapplication.components.homeScreen.HomeScreen
import com.example.myapplication.components.workout.WorkoutScreen
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
                    navigation(
                        "exercisesScreen",
                        route = "exercises"
                    ) {
                        composable("exercisesScreen") {
                            ExercisesScreen()
                        }
                    }
                    navigation(
                        "workoutScreen",
                        route = "workout"
                    ) {
                        composable("workoutScreen") {
                            WorkoutScreen()
                        }
                    }
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
