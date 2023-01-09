@file:OptIn(ExperimentalMaterialApi::class)

package com.example.myapplication

// import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.myapplication.components.*
import com.example.myapplication.components.workout.WorkoutScreen
import com.example.myapplication.data.WorkoutModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

//    private val exercisesViewModel: ExercisesViewModel by viewModels {
//        ExercisesViewModelFactory((application as NameHereApplication).repository)
//    }

//    private val exercisesViewModel: ExercisesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface {
                    WorkoutScreen(workout = WorkoutModel(name = "Workout Name"))
//                    ExercisesScreen(exercisesViewModel = exercisesViewModel)
                }
            }
        }
    }
}
