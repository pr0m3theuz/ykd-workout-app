package com.example.myapplication.components.programs

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Workout
import org.koin.androidx.compose.koinViewModel
import org.mongodb.kbson.ObjectId

@Composable
fun ProgramDetailsScreen(
  navController: NavController,
  programsViewModel: ProgramsViewModel = koinViewModel(),
  programID: String
) {

  programsViewModel.getProgramById(ObjectId(programID).asObjectId())
  programsViewModel.getMicrocyclesByProgram(ObjectId(programID).asObjectId())

  val program by programsViewModel.selectedProgram.collectAsStateWithLifecycle(null)
//  val programMicrocycles =
//    programsViewModel.activeProgramMicrocycles
  //  program?.let { programsViewModel.getProgramData() }

  val programMicrocycles by
  programsViewModel.selectedProgramMicrocycles.collectAsStateWithLifecycle()
  //  val mesocycles by programsViewModel.get

  program?.let { ProgramDetailsLayout(navController, programsViewModel, it, programMicrocycles) }
}

// TODO Add animatedVisibility Function to expand more/less buttons
@OptIn(ExperimentalMaterial3Api::class)
// @Preview
@Composable
fun ProgramDetailsLayout(
  navController: NavController,
  programsViewModel: ProgramsViewModel, /*= viewModel()*/
  program: Program,
  microcycles: List<Microcycle>,
) {
  val padding8 = 8.dp
  val titleMedium = MaterialTheme.typography.titleLarge
  val onSurface = MaterialTheme.colorScheme.onSurface

  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val titles = listOf("Overview", "Mesocycles", "Workouts")

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
          IconButton(
            onClick = {
              navController.navigateUp()
              Toast.makeText(context, "TODO: Navigate to Previous Screen", Toast.LENGTH_SHORT)
                .show()
            },
          ) {
            Icon(
              imageVector = Icons.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen"
            )
          }
        },
        actions = { ProgramDetailsOptionsDropdownMenu() }
      )
    },
    bottomBar = { BottomNavigationBar(navController = navController) }
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
          tint = MaterialTheme.colorScheme.primary
        )
      }

      TabRow(
        selectedTabIndex = selectedTabIndex,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
      ) {
        titles.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
              androidx.compose.material.Text(
                text = title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
            }
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
              programsViewModel = programsViewModel
            )

          1 -> MesocyclesTab()
          2 -> WorkoutsTab()
        }
      }
    }
  }
}

@Composable
fun ProgramDetailsOptionsDropdownMenu() {
  var expanded by remember { mutableStateOf(false) }

  val onSurface = MaterialTheme.colorScheme.onSurface

  Box {
    IconButton(onClick = { expanded = true }) {
      Icon(
        Icons.Outlined.MoreVert,
        contentDescription = "Program Options and Settings",
        tint = MaterialTheme.colorScheme.onSurface
      )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
        text = { Text("Edit Program", color = onSurface) },
        onClick = { /* TODO Navigate to Create Exercise Screen*/ }
      )
    }
  }
}

@Composable
fun OverviewTab(
  program: Program,
  microcycles: List<Microcycle>,
  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
    CoachsCard(program = program)
    ProgramOverviewCard(program = program)
    ProgramDescriptionCard(program = program)
    WorkoutOverviewCard(microcycles = microcycles, programsViewModel = programsViewModel)
  }
}

@Composable
fun MesocyclesTab() {
  //  TODO
}

// @Preview
@Composable
fun WorkoutsTab() {
  MicrocycleCard()
}

@Composable
fun CoachsCard(program: Program) {
  val padding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val bodySmall = MaterialTheme.typography.bodySmall

  Card() {
    Column(modifier = Modifier
      .fillMaxWidth()
      .padding(padding)) {
      Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
          Text(text = "Coach Overview", style = titleSmall)
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column() { Text(text = program.author ?: "Coach Name", style = titleSmall) }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
      ) {
        Column() { Text(text = "Description", style = bodySmall) }
      }
    }
  }
}

@Composable
fun ProgramOverviewCard(program: Program) {
  val padding = 12.dp
  val spacedByPadding = 4.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall

  Card() {
    Column(modifier = Modifier
      .fillMaxWidth()
      .padding(padding)) {
      Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
          Text(text = "Program Overview", style = titleSmall)
        }
      }
      //      Body
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column() {
          Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text(text = "Level", style = labelMedium) }
            Column(modifier = Modifier.weight(1f)) {
              Text(text = program.level ?: "Description", style = bodySmall)
            }
          }
          Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { Text(text = "Equipment", style = labelMedium) }
            Column(modifier = Modifier.weight(1f)) {
              Text(text = program.programEquipment ?: "Description", style = bodySmall)
            }
          }
          Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Days Per Week", style = labelMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(text = program.daysPerWeek ?: "Description", style = bodySmall)
            }
          }
          Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
              Text(text = "Program Length", style = labelMedium)
            }
            Column(modifier = Modifier.weight(1f)) {
              Text(text = program.programLength ?: "Description", style = bodySmall)
            }
          }
        }
      }
    }
  }
}

@Composable
fun ProgramDescriptionCard(program: Program) {
  val padding = 12.dp
  val spacedByPadding = 4.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall

  val (expanded, onClick) = remember { mutableStateOf(false) }

  //  TODO Add Expand/Hide Text Button
  Card() {
    Column(modifier = Modifier
      .fillMaxWidth()
      .padding(padding)) {
      Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
          Text(text = "Program Description", style = titleSmall)
        }
      }
      Row(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.animateContentSize(animationSpec = tween(100))) {
          if (expanded) {
            Text(
              text = program.description ?: "Description, Use Markdown Potentially",
              style = bodySmall,
            )
          } else {
            Text(
              text = program.description ?: "Description, Use Markdown Potentially",
              style = bodySmall,
              maxLines = 20,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }
      //      TODO Hide if not needed
      TextButton(
        onClick = { onClick(!expanded) },
        contentPadding = PaddingValues(start = 0.dp, bottom = 0.dp)
      ) {
        Text(
          text = if (expanded) "Show less" else "Show more",
          style = MaterialTheme.typography.labelMedium
        )
      }
    }
  }
}

@Composable
fun WorkoutOverviewCard(
  microcycles: List<Microcycle>,
  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  val padding = 12.dp
  val spacedByPadding = 4.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall

  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val scrollState = rememberScrollState()
  val titles =
    if (microcycles.isNotEmpty()) {
      List(microcycles.size) { "Week ${it.plus(1)}" }
    } else {
      List(1) { "Week 1" }
    }

  if (microcycles.isNotEmpty()) {
    val workouts = microcycles[selectedTabIndex].workouts

    Card() {
      Column(modifier = Modifier
        .fillMaxWidth()
        .padding(padding)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(text = "Workout Overview", style = titleSmall)
        }

        ScrollableTabRow(
          selectedTabIndex = selectedTabIndex,
          edgePadding = 0.dp,
          containerColor = Color.Transparent
        ) {
          titles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTabIndex == index,
              onClick = { selectedTabIndex = index },
              text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
            )
          }
        }

        Row(
          Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .horizontalScroll(scrollState),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          if (!workouts.isEmpty()) {
            workouts.forEachIndexed { index, workout ->
              Log.i("WorkoutOverviewCard", workout.toString())
              WorkoutDayCard(index, workout, programsViewModel)
            }
          }
        }
      }
    }
  }
}

@Composable
fun WorkoutDayCard(
  index: Int,
  workout: Workout,
  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  val padding = 12.dp
  val spacedByPadding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall
  val cardColors = CardDefaults.cardColors()

  val exercisesSets = workout.exerciseSets

  Log.i("WorkoutDayCard", exercisesSets.toString())

  ElevatedCard() {
    Column(modifier = Modifier
      .width(300.dp)
      .padding(padding)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
      ) {
        Text(text = "Day ${index.plus(1)}", style = labelMedium)
      }
      if (!exercisesSets.isEmpty()) {
        exercisesSets.forEachIndexed { index, exerciseSet ->
          ExerciseOverview(
            index = index,
            //            exerciseID = entry.key,
            exerciseSets = exerciseSet
            //            programsViewModel = programsViewModel
          )
          Spacer(modifier = Modifier.height(spacedByPadding))
        }
      }
    }
  }
}

@Composable
fun ExerciseOverview(
  index: Int,
  //  exerciseID: Long,
  exerciseSets: ExerciseSets,
  //  programsViewModel: ProgramsViewModel
) {
  val labelMedium = MaterialTheme.typography.labelMedium

  val exercise = exerciseSets.exercise

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 4.dp),
  ) {
    if (exercise != null) {
      Text(text = "${index.plus(1)}. ${exercise.name}", style = labelMedium)
    }
  }
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
    Text(text = "Sets", style = labelMedium, modifier = Modifier.weight(1f))
    Text(
      text = "Target Reps",
      style = labelMedium,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.Center
    )
    Text(
      text = "Intensity",
      style = labelMedium,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.Center
    )
  }
  exerciseSets.sets.forEach { set ->
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Text(
        text = set.setNumber.toString(),
        style = labelMedium,
        modifier = Modifier
          .weight(1f)
          .padding(start = 8.dp)
      )
      Text(
        text = set.targetRepetitions ?: "N/A",
        style = labelMedium,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center
      )
      Text(
        text = set.targetRepetitionsInReserve.toString(),
        style = labelMedium,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center
      )
    }
  }
}

@Composable
fun MicrocycleCard() {
  val padding = 12.dp
  val spacedByPadding = 4.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall

  val cardColors = CardDefaults.cardColors()

  Card() {
    Column(modifier = Modifier
      .fillMaxWidth()
      .padding(padding)) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Week # - Mesocycle #/Name", style = titleMedium)

        IconButton(onClick = { /*TODO*/ }) {
          Icon(
            imageVector = Icons.Outlined.ExpandMore,
            contentDescription = "Show workouts for this microcycle"
          )
        }
      }
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          //                    .padding(bottom = 8.dp),
          //                horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Checkbox(
            checked = false,
            onCheckedChange = {},
            colors = CheckboxDefaults.colors(),
          )
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "Day #", style = titleMedium)

              Box(
                contentAlignment = Alignment.Center,
                modifier =
                Modifier
                  .padding(horizontal = 8.dp)
                  .height(22.dp)
                  .background(
                    color = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = ShapeDefaults.ExtraSmall
                  )
              ) {
                Text(
                  text = "Date Completed",
                  style = bodySmall,
                  modifier = Modifier.padding(horizontal = 4.dp)
                )
              }
            }
            Text(text = "# Exercises", style = bodySmall)
          }

          // TODO if completed = true "View" else "Start"
          Button(onClick = { /*TODO*/ }) { Text(text = "Start") }
        }
      }
    }
  }
}
