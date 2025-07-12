@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package com.example.myapplication.features.exercises.ui

import android.content.Intent
import android.icu.util.LocaleData
import android.icu.util.ULocale
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.TabRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BrokenImage
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.formatWorkoutDate
import com.example.myapplication.common.domain.model.setSummaryRIR
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.exercises.ExercisesViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinTimeZone
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@Composable
fun ExerciseDetailsScreen(
  navController: NavController,
  exerciseID: String,
  onClick: () -> Unit,
  exercisesViewModel: ExercisesViewModel = koinViewModel(),
) {
  val tabState by exercisesViewModel.exerciseDetailsLayoutTabState.collectAsStateWithLifecycle()

  val defaultTimeZone = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()

  if (exerciseID.contentEquals("Select an exercise")) {
    NoExerciseDetailContent(exerciseID)
  } else {
    exercisesViewModel.getExerciseDetails(exerciseID)
    val exercise by exercisesViewModel.selectedExerciseDetails.collectAsStateWithLifecycle()

    exercise?.let { it ->
      val completedSetsByExercise = exercisesViewModel.getCompletedSetsByExercise(it)
      ExerciseDetailsLayout(
        navController,
        exercise = it,
        tabState = tabState,
        setTabState = { state -> exercisesViewModel.setExerciseDetailsLayoutTabState(state) },
        navigateToListPane = onClick,
        historyTab = {
          completedSetsByExercise?.let { (programs, workoutSetsMap) ->
            HistoryTab(
              programs = programs,
              workoutSetsMap = workoutSetsMap.toList(),
              workoutCard = { workout, sets, program ->
                WorkoutCard(
                  workout = workout,
                  sets = sets,
                  program = program,
                  defaultTimeZone = defaultTimeZone,
                )
              },
            )
          } ?: NoHistoryTab() // TODO Replace with No History Content
        },
      )
    }
  }
}

@Composable
private fun NoExerciseDetailContent(exerciseID: String) {
  Scaffold { contentPadding ->
    Column(
      Modifier
        .padding(contentPadding)
        .fillMaxWidth()
        .fillMaxHeight(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Image(
        painterResource(R.drawable.select_exercise),
        modifier = Modifier.scale(0.75f),
        contentDescription = null,
      )
      Text(
        text = "Select an exercise",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
      )
      Text(
        text = "To see more details about an exercise.",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyLarge,
      )
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ExerciseDetailsLayout(
  navController: NavController,
  exercise: Exercise,
  tabState: Int = 0,
  setTabState: (Int) -> Unit = {},
  navigateToListPane: () -> Unit,
  historyTab: @Composable () -> Unit = {},
) {
  //    val exercise = _exercise.firstOrNull()

  fun navigateToFullscreenChartScreen() {
    navController.navigate("exerciseChart")
  }

  val context = LocalContext.current
  val titles = listOf("About", "History", "Charts", "Records")
  val onSurface = MaterialTheme.colorScheme.onSurface

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = exercise.name,
            maxLines = 2,
            overflow = TextOverflow.Visible,
            color = onSurface,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
          )
        },
        navigationIcon = {
          //          IconButton(
          //            onClick = {
          ////              navController.navigateUp()
          //              navigateToListPane()
          //              /*TODO Navigate to Previous Screen */
          //              Toast.makeText(context, "TODO: Navigate to Previous Screen",
          // Toast.LENGTH_SHORT)
          //                .show()
          //            }
          //          ) {
          //            Icon(
          //              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
          //              tint = MaterialTheme.colorScheme.onSurface,
          //              contentDescription = "Go to the previous screen",
          //            )
          //          }
        },
        actions = { ExerciseDetailsOptionsDropdownMenu() },
      )
    }
  ) { contentPadding ->
    Column(Modifier.padding(contentPadding)) {
      TabRow(
        selectedTabIndex = tabState,
        backgroundColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
      ) {
        titles.forEachIndexed { index, title ->
          Tab(
            selected = tabState == index,
            onClick = { setTabState(index) },
            text = { Text(text = title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
          )
        }
      }

      Column(Modifier
        .padding(8.dp)
        .fillMaxWidth()) {
        when (tabState) {
          0 -> AboutTab(exercise.name)
          1 -> historyTab()
          2 -> ChartTab(navigateToFullscreenChartScreen = { navigateToFullscreenChartScreen() })
          3 -> RecordsTab()
        }
      }
    }
  }
}

@Composable
fun ExerciseDetailsOptionsDropdownMenu() {
  var expanded by remember { mutableStateOf(false) }

  val onSurface = MaterialTheme.colorScheme.onSurface

  Box {
    IconButton(onClick = { expanded = true }) {
      Icon(
        Icons.Outlined.MoreVert,
        contentDescription = "Exercise Options and Settings",
        tint = MaterialTheme.colorScheme.onSurface,
      )
    }
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      //      if (exercise.ownerID == user.id.toHexString()))
      DropdownMenuItem(
        text = { Text("Edit Exercise", color = onSurface) },
        onClick = { /* TODO Navigate to Create Exercise Screen*/ },
      )

      DropdownMenuItem(
        text = { Text("Clone Exercise", color = onSurface) },
        onClick = { /* TODO Clone Exercise */ },
      )
      DropdownMenuItem(
        text = { Text("Delete Exercise", color = onSurface) },
        onClick = { /* TODO Delete Exercise */ },
      )
    }
  }
}

// @Preview
@Composable
fun AboutTab(_exerciseName: String) {
  val exerciseName: String = _exerciseName.replace(" ", "+")
  val youtubeSearchQuery = "https://www.youtube.com/results?search_query=$exerciseName"

  val intent = Intent(Intent.ACTION_VIEW, youtubeSearchQuery.toUri())

  val context = LocalContext.current

  val onSurface = MaterialTheme.colorScheme.onSecondaryContainer

  val buttonColors =
    ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
    Row() {
      Icon(
        imageVector = Icons.Outlined.BrokenImage,
        contentDescription = "Missing Image",
        tint = MaterialTheme.colorScheme.primary,
      )
    }
    Button(onClick = { startActivity(context, intent, null) }, colors = buttonColors) {
      Text(text = "Search Exercise on Youtube", color = onSurface)
    }
  }
}

@Preview
@Composable
fun NoHistoryTab() {
  Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
    Spacer(modifier = Modifier.size(32.dp))
    Box(modifier = Modifier.size(300.dp)) {
      Image(
        painterResource(R.drawable.one_punch_man_ok),
        contentDescription = "One Punch Man",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.FillWidth,
      )

      //      AsyncImage(
      //        contentScale = ContentScale.FillWidth,
      //        alignment = Alignment.Center,
      //        model = "https://assets.stickpng.com/images/58582c39f034562c58220601.png",
      //        contentDescription = null,
      //        //        colorFilter = ColorFilter.tint(Color.LightGray, BlendMode.Modulate),
      //      )
    }
    //    Spacer(modifier = Modifier.size(24.dp))
    Text(
      //      modifier = Modifier.padding(16.dp),
      text = "Perform your first set!",
      color = MaterialTheme.colorScheme.onSurface,
      //      fontWeight = FontWeight.Bold,
    )
  }
}

@Composable
fun HistoryTab(
  workoutSetsMap: List<Pair<Workout, List<Set>>> = listOf(),
  programs: List<Program> = listOf(),
  workoutCard: @Composable (workout: Workout, sets: List<Set>, program: Program?) -> Unit =
    { _, _, _ ->
    },
) {
  //    TODO Write query to get Workout Data for an Exercise, return a List
  //    TODO Change to LazyColumn
  LazyColumn {
    items(
      workoutSetsMap.count(),
      key = { index -> workoutSetsMap[index].first.lazyListItemID }) { index ->
      val workout = workoutSetsMap[index].first

      workoutCard(
        workout,
        workoutSetsMap[index].second,
        programs.firstOrNull { it.id == workout.programID },
      )
    }
  }
}

@Composable
private fun WorkoutCard(
  workout: Workout,
  sets: List<Set>,
  program: Program?,
  defaultTimeZone: TimeZone,
) {
  val cardColors =
    CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  val subtitle = MaterialTheme.colorScheme.onSurfaceVariant
  val onSurface = MaterialTheme.colorScheme.onSurface

  Card(
    // colors = cardColors,
    modifier = Modifier.fillMaxWidth(),
    //            .padding(8.dp)
    border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outlineVariant),
  ) {
    Column(Modifier.padding(16.dp)) {
      Text(text = workout.name, style = MaterialTheme.typography.titleMedium, color = onSurface)
      program?.let { prog ->
        prog.name?.let { text ->
          Text(text = text, style = MaterialTheme.typography.bodyMedium, color = subtitle)
        }

        prog.mesocycles
          .firstOrNull { it.id == workout.mesocycleID }
          ?.let { mesocycle ->
            mesocycle.name?.let { text ->
              Text(text = text, style = MaterialTheme.typography.bodyMedium, color = subtitle)
            }
            mesocycle.microcycles
              .firstOrNull { it.id == workout.microcycleID }
              ?.let { microcycle ->
                microcycle.name?.let { text ->
                  Text(text = text, style = MaterialTheme.typography.bodyMedium, color = subtitle)
                }
              }
          }
      }

      Text(
        text = workout.formatWorkoutDate(),
        style = MaterialTheme.typography.bodyMedium,
        color = subtitle,
      )

      val exerciseSets =
        workout.workoutItems.firstOrNull {
          when (it) {
            is ExerciseSets -> it.id == sets.first().exerciseSetsID
            is Superset ->
              it.exercisesSets.firstOrNull { it.id == sets.first().exerciseSetsID } != null
          }
        } as ExerciseSets?

      exerciseSets?.let {
        // TODO move to MainActivity and prefer the user default
        Text(
          text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              val measurmentSystem = LocaleData.getMeasurementSystem(ULocale.getDefault())
              when (measurmentSystem) {
                LocaleData.MeasurementSystem.US -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInPounds)}"
                }

                LocaleData.MeasurementSystem.UK -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInKilograms)}"
                }

                LocaleData.MeasurementSystem.SI -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInKilograms)}"
                }

                else -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInPounds)}"
                }
              }
            } else {
              // Default
              "Total Volume: ${doubleFormatted(it.totalVolumeInPounds)}"
            },
          color = onSurface,
          style = MaterialTheme.typography.bodyMedium,
        )
      }

      Row(Modifier.padding(vertical = 8.dp)) {
        Text(
          text = "Sets performed",
          style = MaterialTheme.typography.titleSmall,
          color = onSurface,
        )
      }

      sets.forEach { set ->
        // TODO use user.intensity and user's preferred mass units
        Row() {
          Text(
            text =
              "${
                if (set.setTypeID == SetType.normalSet) {
                  set.setNumber
                } else {
                  set.setTypeID.abbreviation
                }
              }.   ${set.setSummaryRIR(MassUnit.POUNDS.abbreviation)}",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            color = onSurface,
          )
        }
      }
    }
  }
}

@Composable
fun ChartTab(navigateToFullscreenChartScreen: () -> Unit) {
  val state = rememberScrollState()
  Column(modifier = Modifier.verticalScroll(state)) {
    Text(text = "Chart Name")
    Row() { ChartWrapper(navigateToFullscreenChartScreen = {}) }
    Text(text = "Estimated 1RM")
    Row() { ChartWrapper(navigateToFullscreenChartScreen = {}) }
    Text(text = "Volume")
    Row() { ChartWrapper(navigateToFullscreenChartScreen = {}) }
  }
}

@Preview(showBackground = true)
@Composable
fun RecordsTab() {
  val rowPadding = 4.dp

  val onSurface = MaterialTheme.colorScheme.onSurface
  val subtitle = MaterialTheme.colorScheme.onSurfaceVariant

  val buttonColors =
    ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

  Column(Modifier
    .padding(4.dp)
    .fillMaxWidth()) {
    Row(Modifier.padding(rowPadding)) {
      Text("Personal Records", color = onSurface, style = MaterialTheme.typography.titleMedium)
    }
    Row(
      Modifier
        .padding(rowPadding)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text("Estimated 1RM", color = onSurface)
      Text("13 lbs", color = onSurface)
    }
    Row(
      Modifier
        .padding(rowPadding)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text("Max Volume", color = onSurface)
      Text("1,300 lbs", color = onSurface)
    }
    Row(
      Modifier
        .padding(rowPadding)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text("Max Weight", color = onSurface)
      Text("13 lbs", color = onSurface)
    }
    Row(Modifier
      .padding(rowPadding)
      .fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
      Button(onClick = { /* TODO Navigate Records History */ }, colors = buttonColors) {
        Text(text = "View Records History", color = onSurface)
      }
    }
    Row(
      Modifier
        .padding(rowPadding)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text("Reps", color = onSurface, modifier = Modifier.weight(1f))
      Text(
        "Best Performance",
        color = onSurface,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
      )
      Text(
        "Estimated 1RM",
        color = onSurface,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
      )
    }

    // TODO Refoctor to List
    Row(
      Modifier
        .padding(rowPadding)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(text = "1", color = onSurface, modifier = Modifier.weight(1f))

      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
        Text(text = "85 lbs (# of Reps)", color = onSurface, textAlign = TextAlign.Center)
        Text(
          text = "Date",
          color = subtitle,
          style = MaterialTheme.typography.bodySmall,
          textAlign = TextAlign.Center,
        )
      }

      Text(
        text = "1RM",
        color = onSurface,
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Center,
      )
    }

    Row(Modifier.padding(rowPadding)) {
      Text(
        text = "Estimate RMs are generated using ...",
        color = subtitle,
        style = MaterialTheme.typography.bodySmall,
      )
    }
    Row(Modifier.padding(rowPadding)) {
      Text("Lifetime Stats", color = onSurface, style = MaterialTheme.typography.titleMedium)
    }
    Row(
      Modifier
        .padding(rowPadding)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text("Total Reps", color = onSurface)
      Text(text = "1,305", color = onSurface)
    }
    Row(
      Modifier
        .padding(rowPadding)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text("Total Volume", color = onSurface)
      Text("13,405 lbs", color = onSurface)
    }
  }
}
