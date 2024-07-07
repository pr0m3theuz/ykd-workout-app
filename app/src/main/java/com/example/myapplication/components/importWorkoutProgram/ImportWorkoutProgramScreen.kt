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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

private const val successMessage = "Import successful!"

private const val failMessage = "Import failed!"

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ImportWorkoutProgramScreen() {
//  val importWorkoutViewModel: ImportWorkoutViewModel = koinViewModel()
  val context = LocalContext.current
  var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
  var importStatus by remember { mutableStateOf("") }
  val coroutineScope = rememberCoroutineScope()
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
      // We have two radio buttons and only one can be selected
      //      var state by remember { mutableStateOf(true) }
      // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior.
      // We also set a content description for this sample, but note that a RadioButton would
      // usually
      // be part of a higher level component, such as a raw with text, and that component would need
      // to provide an appropriate content description. See RadioGroupSample.

      /*importOptions.forEach { pair ->
        val text = pair.first
        val state = pair.second
        Row(
          Modifier.selectableGroup().fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Start,
        ) {
          RadioButton(
            selected = if (selectedOption.isNotEmpty()) selectedOption[0] == text else false,
            onClick = { selectImportOption(text, !state) },
            modifier = Modifier.semantics { contentDescription = "Localized Description" },
          )
          Text(text = text)
        }
      }*/

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
                importWorkoutProgram(
                  context,
                  selectedFileUri!!
                ) { /*content -> content?.let { importWorkoutViewModel.importWorkoutProgram(content) }*/ } // Implement importWorkoutProgram function
              importStatus = if (result) successMessage else failMessage
            }
          }
        ) {
          Text("Import")
        }
      }

      Text(importStatus, modifier = Modifier.padding(top = 8.dp))

      if (importStatus == successMessage) {
        Button(onClick = { /*TODO*/ }) { Text("Preview Program") }
        Button(onClick = { /*TODO*/ }) { Text("Create New Program") }
        Button(onClick = { /*TODO Add Dialog to add existing program and confirm*/ }) { Text("Add to Existing Program") }
      }
    }
  }
}

// Placeholder function for actual import logic
suspend fun importWorkoutProgram(
  context: Context,
  fileUri: Uri,
  processFile: (content: String?) -> Unit
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
