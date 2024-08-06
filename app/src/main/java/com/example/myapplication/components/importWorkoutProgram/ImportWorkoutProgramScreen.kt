package com.example.myapplication.components.importWorkoutProgram

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Workout
import io.realm.kotlin.types.RealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.koinViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

private const val successMessage = "Import successful!"

private const val failMessage = "Import failed!"

@OptIn(ExperimentalMaterial3Api::class)
// @Preview
@Composable
fun ImportWorkoutProgramScreen(navController: NavController) {
  val importWorkoutViewModel: ImportWorkoutViewModel = koinViewModel()
  val context = LocalContext.current
  var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
  var importStatus by remember { mutableStateOf("") }
  val coroutineScope = rememberCoroutineScope()

  var importedData by remember { mutableStateOf<Any?>(null) }

  val importOptions =
    listOf<Pair<String, Boolean>>(
      Pair("Program", false),
      Pair("Mesocycles", false),
      Pair("Microcycles", false),
      Pair("Workouts", false),
      Pair("Advanced Workouts", false),
    )

  val launcher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.GetContent(),
      onResult = { uri: Uri? -> selectedFileUri = uri },
    )

  val selectedOption = remember { mutableStateListOf<String>() }
  fun selectImportOption(option: String, isSelected: Boolean) {
    if (isSelected && selectedOption.size == 1) {
      selectedOption[0] = option
    } else if (isSelected && selectedOption.isEmpty()) {
      selectedOption.add(option)
    }
  }

  var importedProgram: Program? = null
  var importedPrograms: RealmList<Program>? = null
  var importedMesocycles: RealmList<Mesocycle>? = null
  var importedMicrocycles: RealmList<Microcycle>? = null
  var importedWorkouts: RealmList<Workout>? = null

  var openProgramPreview by remember { mutableStateOf(false) }

  if (openProgramPreview) {
    if (importedProgram != null) {
      val microcycles = importedProgram.mesocycles.flatMap { it.microcycles }

      ProgramPreviewScreen(
        program = importedProgram,
        mesocycles = importedProgram.mesocycles,
        microcycles = microcycles,
        navController = navController,
      )
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Import Workouts & Programs") }
        /*actions = {
        IconButton(onClick = { */
        /*TODO: Implement import logic */
        /* }) {
            Icon(Icons.Filled.Add, contentDescription = "Import")
          }
        }*/
      )
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      //      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Button(onClick = { launcher.launch("*/*") }) { // Adjust file type as needed
        Text("Select Workout Program to Import")
      }

      selectedFileUri?.let {
        Text("Selected file: ${it.path}", modifier = Modifier.padding(top = 8.dp))
      }

      if (selectedFileUri != null) {
        Button(
          onClick = {
            coroutineScope.launch {
              val result =
                importWorkoutProgram(context, selectedFileUri!!) { content ->
                  content?.let {
//                    importedData = importWorkoutViewModel.importWorkoutProgram(content)
                    println("Imported data: $importedData")
                  }
                } // Implement importWorkoutProgram function
              importStatus = if (result) successMessage else failMessage
            }
          }
        ) {
          Text("Import")
        }
      }

      @Suppress("UNCHECKED_CAST")
      when (importedData) {
        is Program -> {
          Text("Imported Program: ${(importedData as Program).name}")
          importedProgram = importedData as Program
        }

        is RealmList<*> -> {
          when (val firstItem = (importedData as RealmList<*>).firstOrNull()) {
            is Program -> {
              Text("Imported Program: ${firstItem.name ?: "No Program Name"}")
              importedPrograms = importedData as RealmList<Program>
            }

            is Mesocycle -> {
              Text("Imported Mesocycles: ${firstItem.name ?: "No Mesocycle Name"}")
              importedMesocycles = importedData as RealmList<Mesocycle>
            }

            is Microcycle -> {
              Text("Imported Microcycles: ${firstItem.name ?: "No Microcycle Name"}")
              importedMicrocycles = importedData as RealmList<Microcycle>
            }

            is Workout -> {
              Text("Imported Workouts: ${firstItem.name ?: "No Workout Name"}")
              importedWorkouts = importedData as RealmList<Workout>
            }

            null -> Text("Imported empty list")
            else -> Text("Imported unknown type")
          }
        }

        null -> {
          Text("No Data Imported!")
        }

        else -> {
          Text("Imported unknown type")
        }
      }

      Text(importStatus, modifier = Modifier.padding(top = 8.dp))

      if (importStatus == successMessage) {
        Button(onClick = { openProgramPreview = true }) { Text("Preview Program") }
        Button(onClick = { /*TODO*/ }) { Text("Create New Program") }
        Button(onClick = { /*TODO Add Dialog to add existing program and confirm*/ }) {
          Text("Add to Existing Program")
        }
      }
    }
  }
}

// Placeholder function for actual import logic
suspend fun importWorkoutProgram(
  context: Context,
  fileUri: Uri,
  processFile: (content: String?) -> Unit,
): Boolean {
  // TODO: Implement logic to read and parse the workout program file
  // and import the data into your app's database or data store.
  return withContext(Dispatchers.IO) {
    try {
      // Read the file content
      val content =
        context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
          BufferedReader(InputStreamReader(inputStream)).readText()
        }

      // Parse the content as needed
      processFile(content)

      // TODO: Save the workoutProgram data to your app's database or data store

      true // Import successful
    } catch (e: Exception) {
      // Handle exceptions (e.g., file not found, parsing errors)
      println("Import error: ${e.message}")
      false // Import failed
    }
  }
}
