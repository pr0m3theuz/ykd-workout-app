package com.example.myapplication.components.importWorkoutProgram

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.components.programs.MesocyclesTab
import com.example.myapplication.components.programs.OverviewTab
import com.example.myapplication.components.programs.ProgramDetailsOptionsDropdownMenu
import com.example.myapplication.components.programs.WorkoutsTab
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program

// TODO Add animatedVisibility Function to expand more/less buttons
// TODO Add Floating Action Button to Start Program
@OptIn(ExperimentalMaterial3Api::class)
// @Preview
@Composable
fun ProgramPreviewScreen(
  program: Program,
  mesocycles: List<Mesocycle>,
  microcycles: List<Microcycle>,
  navController: NavController,
) {
  val padding8 = 8.dp
  val titleMedium = MaterialTheme.typography.titleLarge
  val onSurface = MaterialTheme.colorScheme.onSurface

  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val titles = listOf("Overview", "Mesocycles", "Workouts")

  //  Log.i("ProgramDetailsLayout", "program mesocycles: ${program.mesocycles.size}")
  //  Log.i("ProgramDetailsLayout", "programActive: $programActive")
  //  Log.i("ProgramDetailsLayout", "mesocycles: ${program.mesocycles}")
  //  Log.i("ProgramDetailsLayout", "microcycles:
  // ${Json.encodeToString(program.mesocycles[0].microcycles[0])}")

  val (startButtonPressed, setStartButtonPressed) = rememberSaveable { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = program.name ?: "New Program",
            maxLines = 2,
            overflow = TextOverflow.Visible,
            color = onSurface,
            style = titleMedium,
            textAlign = TextAlign.Center,
          )
        },
        navigationIcon = {
          IconButton(onClick = {}) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen",
            )
          }
        },
        actions = { ProgramDetailsOptionsDropdownMenu() },
      )
    },
    bottomBar = { /*BottomNavigationBar(navController = navController)*/ },
    floatingActionButton = {
//      ExtendedFloatingActionButton(onClick = { /*TODO*/ }) { Text(text = "Start Program") }
    },
  ) { contentPadding ->
    Column(
      Modifier
        .padding(contentPadding)
        .fillMaxWidth()
        .fillMaxHeight()
        .verticalScroll(scrollState)
    ) {
      Row(
        Modifier
          .fillMaxWidth()
          .padding(padding8), horizontalArrangement = Arrangement.Center) {
        Icon(
          imageVector = Icons.Outlined.BrokenImage,
          contentDescription = "Missing Image",
          tint = MaterialTheme.colorScheme.primary,
        )
      }

      TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
      ) {
        titles.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
          )
        }
      }

      Column(
        Modifier
          .padding(12.dp)
          .fillMaxWidth()) {
        when (selectedTabIndex) {
          0 ->
            OverviewTab(
              program = program,
              microcycles = microcycles,
              // programsViewModel = programsViewModel,
            )

          1 -> MesocyclesTab(activeProgram = program.isActive, mesocycles = mesocycles)
          2 ->
            WorkoutsTab(
              activeProgram = program.isActive,
              navController = navController,
              microcycles = microcycles,
            )
        }
      }
    }
  }
}
