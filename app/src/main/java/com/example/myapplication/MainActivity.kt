@file:OptIn(ExperimentalMaterialApi::class)

package com.example.myapplication

// import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.*
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface {
                    WorkoutScreen()
                }
            }
        }
    }
}

// TODO Takes a Workout Object
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutScreen() {
    Row {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(modifier = Modifier.padding(8.dp), text = "Workout Title")

                workoutOptionsDropdownMenu()
            }
            Text(modifier = Modifier.padding(8.dp), text = "Workout Duration")
            Text(modifier = Modifier.padding(8.dp), text = "Workout Notes")

            ExerciseScreen()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* TODO Do something! */ }) {
                    Text("Add Exercise")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { /* TODO Do something! */ }) {
                    Text("Cancel Workout", color = Color.Red)
                }
            }
        }
    }
}
