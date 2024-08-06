package com.example.myapplication.components.programs

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.ExpandLess
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
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myapplication.components.BottomNavigationBar
import com.example.myapplication.data.model.ExerciseSets
import com.example.myapplication.data.model.Mesocycle
import com.example.myapplication.data.model.Microcycle
import com.example.myapplication.data.model.Program
import com.example.myapplication.data.model.Workout
import com.example.myapplication.data.model.formatWorkoutDate
import com.example.myapplication.utils.doubleFormatted
import org.koin.androidx.compose.koinViewModel
import org.mongodb.kbson.ObjectId

@Composable
fun ProgramDetailsScreen(
  navController: NavController,
  programsViewModel: ProgramsViewModel = koinViewModel(),
  programID: String
) {

  programsViewModel.getProgramById(ObjectId(programID).asObjectId())
  programsViewModel.getMesocyclesByProgram(ObjectId(programID).asObjectId())
  programsViewModel.getMicrocyclesByProgram(ObjectId(programID).asObjectId())

  val program by programsViewModel.selectedProgram.collectAsStateWithLifecycle(null)

  val programMesocycles by programsViewModel.selectedProgramMesocycles.collectAsStateWithLifecycle()

  val programMicrocycles by
    programsViewModel.selectedProgramMicrocycles.collectAsStateWithLifecycle()
  //  val mesocycles by programsViewModel.get

  program?.let {
    ProgramDetailsLayout(
      navController,
      programsViewModel,
      it,
      programMesocycles,
      programMicrocycles
    )
  }
}

// TODO Add animatedVisibility Function to expand more/less buttons
// TODO Add Floating Action Button to Start Program
@OptIn(ExperimentalMaterial3Api::class)
// @Preview
@Composable
fun ProgramDetailsLayout(
  navController: NavController,
  programsViewModel: ProgramsViewModel, /*= viewModel()*/
  program: Program,
  mesocycles: List<Mesocycle>,
  microcycles: List<Microcycle>,
) {
  val padding8 = 8.dp
  val titleMedium = MaterialTheme.typography.titleLarge
  val onSurface = MaterialTheme.colorScheme.onSurface

  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var selectedTabIndex by remember { mutableIntStateOf(0) }
  val titles = listOf("Overview", "Mesocycles", "Workouts")

  val programActive = programsViewModel.isProgramActive(program.uuid)
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
          IconButton(
            onClick = { navController.navigateUp() },
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen"
            )
          }
        },
        actions = { ProgramDetailsOptionsDropdownMenu() }
      )
    },
    bottomBar = { BottomNavigationBar(navController = navController) },
    floatingActionButton = {
      if (!programActive || startButtonPressed) {
        ExtendedFloatingActionButton(
          onClick = { /*TODO*/
            if (!startButtonPressed) {
              programsViewModel.startNewProgram(program = program)

              //                    Toast.makeText(context, "TODO Implement Start Program",
              // Toast.LENGTH_SHORT)
              //                        .show()
              setStartButtonPressed(true)
            }
          },
        ) {
          Text(text = "Start Program")
        }
      }
    }
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
            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) }
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
//              programsViewModel = programsViewModel
            )

          1 -> MesocyclesTab(activeProgram = program.active, mesocycles = mesocycles)
          2 ->
            WorkoutsTab(
              activeProgram = program.active,
              navController = navController,
              microcycles = microcycles,
            )
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
        onClick = { /* TODO Navigate to Create Edit Program Screen*/ }
      )
    }
  }
}

@Composable
fun OverviewTab(
  program: Program,
  microcycles: List<Microcycle>,
//  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  //  Log.i("OverviewTab", "microcycles: $microcycles")
  Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    CoachsCard(program = program)
    ProgramOverviewCard(program = program)
    ProgramDescriptionCard(program = program)
    WorkoutOverviewCard(microcycles = microcycles /*programsViewModel = programsViewModel*/)
    Spacer(modifier = Modifier.height(60.dp))
  }
}

@Composable
fun MesocyclesTab(
  activeProgram: Boolean,
  mesocycles: List<Mesocycle>,
) {
  //  TODO
  Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    MesocycleCard(activeProgram = activeProgram, mesocycles = mesocycles)
    Spacer(modifier = Modifier.height(60.dp))
  }
}

// @Preview
@Composable
fun WorkoutsTab(
  activeProgram: Boolean,
  navController: NavController,
  microcycles: List<Microcycle>,
) {
  Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    MicrocycleCard(
      activeProgram = activeProgram,
      navController = navController,
      microcycles = microcycles
    )
    Spacer(modifier = Modifier.height(60.dp))
  }
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
  val bodySmall = MaterialTheme.typography.bodyMedium

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
//  programsViewModel: ProgramsViewModel = koinViewModel()
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
    //    Log.i("WorkoutOverviewCard", Json.encodeToString(workouts))
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
          if (workouts.isNotEmpty()) {
            workouts.sortedBy { it.day }.forEachIndexed { index, workout ->
              //              Log.i("WorkoutOverviewCard", workout.toString())
              WorkoutDayCard(index, workout /*programsViewModel*/)
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
//  programsViewModel: ProgramsViewModel = koinViewModel()
) {
  val padding = 12.dp
  val spacedByPadding = 12.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall
  val cardColors = CardDefaults.cardColors()

  val exercisesSets = workout.exercisesSets
//  Log.i("WorkoutDayCard", "Workout: ${ Json.encodeToString(workout) }")
//  Log.i("WorkoutDayCard ExercisesSets", "${ Json.encodeToString(exercisesSets[0]) }")

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
      if (exercisesSets.isNotEmpty()) {
        exercisesSets
          .sortedBy { it.exerciseOrder }
          .forEachIndexed { index, exerciseSets ->
            ExerciseOverview(
              index = index,
              //            exerciseID = entry.key,
              exerciseSets = exerciseSets,
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
  programsViewModel: ProgramsViewModel = koinViewModel(),
) {
  val labelMedium = MaterialTheme.typography.labelMedium

  val exercise = exerciseSets.exerciseID
  val exerciseName = programsViewModel.getExerciseNameByID(exerciseID = exercise)
//  Log.i("ExerciseOverview", "exerciseSets: ${Json.encodeToString(exerciseSets)}")
//  Log.i("ExerciseOverview", "exercise ID: $exercise")
//  Log.i("ExerciseOverview", "exercise name: $exerciseName")

  exerciseName?.let {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 4.dp),
    ) {
      if (exercise != null) {
        Text(text = "${index.plus(1)}. $exerciseName", style = labelMedium)
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
        text = "Target RIR",
        style = labelMedium,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center
      )
    }
    //  Log.i("ExerciseOverview", Json.encodeToString(exerciseSets.sets))
    exerciseSets.sets.sortedBy { it.setNumber }.forEach { set ->
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
          text = doubleFormatted(set.targetRepetitionsInReserve),
          style = labelMedium,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center
        )
      }
    }
  }
}

@Composable
fun MicrocycleCard(
  activeProgram: Boolean,
  navController: NavController,
  microcycles: List<Microcycle>,
) {

  val context = LocalContext.current
  val padding = 12.dp
  val spacedByPadding = 4.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall

  val cardColors = CardDefaults.cardColors()

  fun getExpandCollapseIcon(visible: Boolean): ImageVector {
    return if (visible) {
      Icons.Outlined.ExpandLess
    } else {
      Icons.Outlined.ExpandMore
    }
  }

  if (microcycles.isNotEmpty()) {
    microcycles.forEachIndexed { index, microcycle ->
      val (workoutVisible, setWorkoutVisible) =
        rememberSaveable { mutableStateOf(!activeProgram && index == 0) }

      //      TODO Figure out a way to show the most recent microcycle with completed workouts

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
            Text(
              text = "Week ${index.plus(1)} - ${microcycle.name}",
              style = titleMedium,
              //              overflow = TextOverflow.Ellipsis,
              softWrap = true,
              modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { setWorkoutVisible(!workoutVisible) }) {
              Icon(
                imageVector = getExpandCollapseIcon(workoutVisible),
                contentDescription = "Show workouts"
              )
            }
          }

          AnimatedVisibility(
            visible = workoutVisible,
            enter = expandVertically(animationSpec = tween(500)),
            exit = shrinkVertically(animationSpec = tween(500))
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              val (expanded, onClick) = remember { mutableStateOf(false) }
              val (showShowMoreButton, unhideShowMoreButton) = remember { mutableStateOf(false) }

              Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.animateContentSize(animationSpec = tween(100))) {
                  if (expanded) {
                    Text(
                      text = microcycle.description ?: "Description, Use Markdown Potentially",
                      style = bodySmall,
                    )
                  } else {
                    Text(
                      text = microcycle.description ?: "Description, Use Markdown Potentially",
                      style = bodySmall,
                      maxLines = 20,
                      overflow = TextOverflow.Ellipsis,
                      onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                          unhideShowMoreButton(true)
                        }
                      }
                    )
                  }
                }
              }

              if (showShowMoreButton) {
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

              microcycle.workouts.sortedBy { it.day }.forEach { workout ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  //                    .padding(bottom = 8.dp),
                  //                horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Checkbox(
                    checked = workout.workoutEnded != null,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(),
                  )
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = workout.name ?: "Workout",
                        style = titleMedium,
                        softWrap = true,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                    //                  TODO Hide if workout not completed in Active Program
                    if (workout.workoutEnded != null) {
                      Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                        Modifier
                          .padding(horizontal = 0.dp)
                          .height(22.dp)
                          .background(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            shape = ShapeDefaults.ExtraSmall
                          )
                      ) {
                        if (workout.formatWorkoutDate().isNotEmpty()) {
                          Text(
                            text = workout.formatWorkoutDate(),
                            style = bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp)
                          )
                        }
                      }
                    }

                    Text(text = "${workout.exercisesSets.size} Exercises", style = bodySmall)
                  }

                  val workoutRoute = "workout?workoutID=${workout._id.toHexString()}"
                  if (workout.workoutEnded == null) {
                    Button(
                      enabled = true, //activeProgram,
                      onClick = {
                        navController.navigate(workoutRoute) {
                          popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                          }
                          // Avoid multiple copies of the same destination when
                          // reselecting the same item
                          launchSingleTop = true
                          // Restore state when reselecting a previously selected item
                          restoreState = true
                        }
                      }
                    ) {
                      Text(text = "Start")
                    }
                  } else {
                    OutlinedButton(
                      enabled = activeProgram,
                      onClick = {
                        // TODO Create EditWorkoutScreen
                        // TODO Implement Navigate to Edit Workout
                        //                        navController.navigate(workoutRoute)
                        Toast.makeText(
                          context,
                          "TODO: Implement View/Edit Workout",
                          Toast.LENGTH_SHORT
                        )
                          .show()
                      }
                    ) {
                      Text(text = "View")
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun MesocycleCard(
  activeProgram: Boolean,
  mesocycles: List<Mesocycle>,
) {

  val context = LocalContext.current
  val padding = 12.dp
  val spacedByPadding = 4.dp
  val titleMedium = MaterialTheme.typography.titleMedium
  val titleSmall = MaterialTheme.typography.titleSmall
  val labelMedium = MaterialTheme.typography.labelMedium
  val bodySmall = MaterialTheme.typography.bodySmall

  val cardColors = CardDefaults.cardColors()

  fun getExpandCollapseIcon(visible: Boolean): ImageVector {
    return if (visible) {
      Icons.Outlined.ExpandLess
    } else {
      Icons.Outlined.ExpandMore
    }
  }

  if (mesocycles.isNotEmpty()) {
    mesocycles.forEachIndexed { index, mesocycle ->
      val (workoutVisible, setWorkoutVisible) =
        rememberSaveable { mutableStateOf(!activeProgram && index == 0) }

      //      TODO Figure out a way to show the most recent microcycle with completed workouts

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
            Text(
              text = "Mesocycle ${index.plus(1)} - ${mesocycle.name}",
              style = titleMedium,
              //              overflow = TextOverflow.Ellipsis,
              softWrap = true,
              modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { setWorkoutVisible(!workoutVisible) }) {
              Icon(
                imageVector = getExpandCollapseIcon(workoutVisible),
                contentDescription = "Show mesocycle"
              )
            }
          }

          AnimatedVisibility(
            visible = workoutVisible,
            enter = expandVertically(animationSpec = tween(500)),
            exit = shrinkVertically(animationSpec = tween(500))
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
              val (expanded, onClick) = remember { mutableStateOf(false) }
              val (showShowMoreButton, unhideShowMoreButton) = remember { mutableStateOf(false) }

              Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                  Text(text = "Mesocycle Description", style = titleSmall)
                }
              }

              Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.animateContentSize(animationSpec = tween(100))) {
                  if (expanded) {
                    Text(
                      text = mesocycle.description ?: "Description, Use Markdown Potentially",
                      style = bodySmall,
                    )
                  } else {
                    Text(
                      text = mesocycle.description ?: "Description, Use Markdown Potentially",
                      style = bodySmall,
                      maxLines = 20,
                      overflow = TextOverflow.Ellipsis,
                      onTextLayout = { textLayoutResult ->
                        if (textLayoutResult.hasVisualOverflow) {
                          unhideShowMoreButton(true)
                        }
                      }
                    )
                  }
                }
              }

              if (showShowMoreButton) {
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

              mesocycle.microcycles.forEach { microcycle ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  //                    .padding(bottom = 8.dp),
                  //                horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  var completedWorkouts: Int = 0

                  microcycle.workouts.forEach { workout ->
                    if (workout.workoutEnded != null) {
                      completedWorkouts++
                    }
                  }
                  Log.i(
                    "${microcycle.name} completedWorkouts == microcycle.workouts.size",
                    "${completedWorkouts == microcycle.workouts.size}"
                  )
                  Checkbox(
                    checked = completedWorkouts == microcycle.workouts.size,
                    onCheckedChange = {},
                    colors = CheckboxDefaults.colors(),
                  )
                  Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "${microcycle.name}",
                        style = titleMedium,
                        softWrap = true,
                        modifier = Modifier.weight(1f),
                        overflow = TextOverflow.Ellipsis
                      )
                    }
                    //                  TODO Hide if workout not completed in Active Program
                    if (completedWorkouts == microcycle.workouts.size) {
                      Box(
                        contentAlignment = Alignment.Center,
                        modifier =
                        Modifier
                          .padding(horizontal = 0.dp)
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

                    Text(text = "${microcycle.workouts.size} Workouts", style = bodySmall)
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
