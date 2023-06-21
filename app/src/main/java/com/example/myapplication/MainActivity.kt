@file:OptIn(ExperimentalMaterialApi::class)

package com.example.myapplication

// import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.myapplication.components.*
import com.example.myapplication.components.exercisesList.ExercisesScreen
import com.example.myapplication.components.exercisesList.ExercisesViewModel
import com.example.myapplication.data.ExercisesViewModelFactory
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val exercisesViewModel: ExercisesViewModel by viewModels {
        ExercisesViewModelFactory((application as NameHereApplication).repository)
    }

//    private val exercisesViewModel: ExercisesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface {
//                    WorkoutScreen()
                    ExercisesScreen(exercisesViewModel = exercisesViewModel)
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
