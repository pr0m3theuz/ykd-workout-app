@file:OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)

package com.example.myapplication.features.exercises.ui

import android.content.Intent
import android.icu.util.LocaleData
import android.icu.util.ULocale
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.Equipment
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.ExerciseSets
import com.example.myapplication.common.domain.model.Force
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Mechanic
import com.example.myapplication.common.domain.model.MuscleExerciseWithMuscle
import com.example.myapplication.common.domain.model.MuscleGroup
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.domain.model.Superset
import com.example.myapplication.common.domain.model.Utility
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.formatWorkoutDate
import com.example.myapplication.common.domain.model.setSummaryRIR
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.exercises.ExerciseDetailsUiState
import com.example.myapplication.features.exercises.ExercisesViewModel
import com.example.myapplication.features.userProfileScreen.like.boostcamp.ChartType
import com.example.myapplication.features.userProfileScreen.like.boostcamp.WebViewChart
import com.example.myapplication.features.userProfileScreen.like.boostcamp.generateRandomExerciseVolumeData
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
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

  val exerciseUiState by exercisesViewModel.uiState.collectAsStateWithLifecycle()
  val exerciseDetailsUiState by
  exercisesViewModel.exerciseDetailsUiState.collectAsStateWithLifecycle()

  val defaultTimeZone = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()

  fun navigateToFullscreenChartScreen() {
    navController.navigate("exerciseChart")
  }

  if (exerciseID.contentEquals("Select an exercise")) {
    NoExerciseDetailContent(exerciseID)
  } else {
    exercisesViewModel.getExerciseDetails(exerciseID)

    exerciseDetailsUiState.selectedExerciseDetails?.let { it ->
      val completedSetsByExercise = exerciseDetailsUiState.exerciseHistory

      ExerciseDetailsLayout(
        exercise = it,
        tabState = tabState,
        setTabState = { state -> exercisesViewModel.setExerciseDetailsLayoutTabState(state) },
        navigateToListPane = onClick,
        aboutTab = {
          AboutTab(
            exercise = it,
            utilityList = exerciseDetailsUiState.utility,
            equipmentList = exerciseDetailsUiState.equipment,
            forceList = exerciseDetailsUiState.force,
            mechanicList = exerciseDetailsUiState.mechanic,
            muscleGroupList = exerciseDetailsUiState.muscleGroup,
            muscleExerciseList = exerciseDetailsUiState.musclesExerciseList,
          )
        },
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
                  // TODO navigate to workout screen or workout overview screen
                  navigateToWorkoutScreen = {
                    navController.navigate("history?editWorkout=${workout.id}")
                  },
                )
              },
            )
          }
            ?: NoHistoryTab(text = "Perform your first set!") // TODO Replace with No History Content
        },
        chartTab = {
          ChartTab(
            exerciseDetailsUiState = exerciseDetailsUiState,
            navigateToFullscreenChartScreen = { navigateToFullscreenChartScreen() },
          )
        },
        recordsTab = { RecordsTab(exerciseDetailsUiState) },
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
  exercise: Exercise,
  tabState: Int = 0,
  setTabState: (Int) -> Unit = {},
  navigateToListPane: () -> Unit,
  aboutTab: @Composable () -> Unit = {},
  historyTab: @Composable () -> Unit = {},
  chartTab: @Composable () -> Unit = {},
  recordsTab: @Composable () -> Unit = {},
) {
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
            style = MaterialTheme.typography.titleLarge,
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
        .padding(16.dp)
        .fillMaxWidth()) {
        when (tabState) {
          0 -> aboutTab()
          1 -> historyTab()
          2 -> chartTab()
          3 -> recordsTab()
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
fun AboutTab(
  exercise: Exercise,
  utilityList: List<Utility>,
  equipmentList: List<Equipment>,
  forceList: List<Force>,
  mechanicList: List<Mechanic>,
  muscleGroupList: List<MuscleGroup>,
  muscleExerciseList: List<MuscleExerciseWithMuscle>,
) {
  val exerciseName: String = exercise.name.replace(" ", "+")
  val youtubeSearchQuery = "https://www.youtube.com/results?search_query=$exerciseName"

  val intent = Intent(Intent.ACTION_VIEW, youtubeSearchQuery.toUri())

  val context = LocalContext.current

  val onSurface = MaterialTheme.colorScheme.onSecondaryContainer

  val buttonColors =
    ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

  // TODO Replace with a LazyColumn
  Column(modifier = Modifier.fillMaxWidth()) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
      Icon(
        imageVector = Icons.Outlined.BrokenImage,
        contentDescription = "Missing Image",
        tint = MaterialTheme.colorScheme.primary,
      )
    }

    Spacer(Modifier.height(8.dp))

    Button(onClick = { startActivity(context, intent, null) }, colors = buttonColors) {
      Text(text = "Search Exercise on Youtube", color = onSurface)
    }

    Spacer(Modifier.height(8.dp))

    Text(
      text = "Exercise Description",
      modifier = Modifier.padding(vertical = 8.dp),
      color = onSurface,
      style = MaterialTheme.typography.titleMedium,
    )
    if (equipmentList.isNotEmpty()) {
      exercise.equipmentID?.let { equipmentID ->
        val name = equipmentList.first { it.id == equipmentID }.name
        Row {
          Text(text = "Equipment: ", color = onSurface, style = MaterialTheme.typography.titleSmall)
          Text(text = name, color = onSurface, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }

    exercise.primaryMuscleID?.let { muscle ->
      // val name = muscleGroupList.first { it.id == muscle }.exrxName

      Row {
        Text(
          text = "Primary Muscle: ",
          color = onSurface,
          style = MaterialTheme.typography.titleSmall,
        )
        Text(text = muscle, color = onSurface, style = MaterialTheme.typography.bodyMedium)
      }
    }


    if (muscleGroupList.isNotEmpty()) {
      exercise.muscleGroupID?.let { muscle ->
        val name = muscleGroupList.first { it.id == muscle }.exrxName
        Row {
          Text(
            text = "Muscle Group: ",
            color = onSurface,
            style = MaterialTheme.typography.titleSmall,
          )
          Text(text = name, color = onSurface, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }

    if (utilityList.isNotEmpty()) {
      exercise.utilityID?.let { utility ->
        val name = utilityList.first { it.id == utility }.name

        Row {
          Text(text = "Utility: ", color = onSurface, style = MaterialTheme.typography.titleSmall)
          Text(text = name, color = onSurface, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }

    if (mechanicList.isNotEmpty()) {
      exercise.mechanicsID?.let { mechanics ->
        val name = mechanicList.first { it.id == mechanics }.name

        Row {
          Text(text = "Mechanics: ", color = onSurface, style = MaterialTheme.typography.titleSmall)
          Text(text = name, color = onSurface, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }

    if (forceList.isNotEmpty()) {
      exercise.force?.let { force ->
        val name = forceList.first { it.id == force }.name
        Row {
          Text(text = "Force: ", color = onSurface, style = MaterialTheme.typography.titleSmall)
          Text(text = name, color = onSurface, style = MaterialTheme.typography.bodyMedium)
        }
      }
    }

    exercise.functionID?.let {
      Row {
        Text(text = "Function: ", color = onSurface, style = MaterialTheme.typography.titleSmall)
        Text(text = it, color = onSurface, style = MaterialTheme.typography.bodyMedium)
      }
    }

    exercise.intensityID?.let {
      Row {
        Text(text = "Intensity: ", color = onSurface, style = MaterialTheme.typography.titleSmall)
        Text(text = it, color = onSurface, style = MaterialTheme.typography.bodyMedium)
      }
    }

    if (muscleExerciseList.isNotEmpty()) {
      Text(
        text = "Target Muscles: Role",
        color = onSurface,
        style = MaterialTheme.typography.titleSmall,
        modifier = Modifier.padding(vertical = 8.dp),
      )
      muscleExerciseList.forEach { muscleExercise ->
        Row {
          Text(
            text =
              "${muscleExercise.muscle.name}: ${muscleExercise.muscleExercise.roleFunction.replaceFirstChar { it.titlecase() }}",
            color = onSurface,
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }

    Row(modifier = Modifier.padding(vertical = 8.dp)) {
      Text(
        text = "Repetition Capacity: ",
        color = onSurface,
        style = MaterialTheme.typography.titleSmall,
      )
      Text(
        text = exercise.repCapacity.name,
        color = onSurface,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
  }
}

@Preview
@Composable
fun NoHistoryTab(text: String = "Perform your first set!") {
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
      text = text,
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
  LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
  navigateToWorkoutScreen: () -> Unit = {},
) {
  val cardColors =
    CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
  val subtitle = MaterialTheme.colorScheme.onSurfaceVariant
  val onSurface = MaterialTheme.colorScheme.onSurface

  // TODO Add notes & descriptions

  Card(
    onClick = { navigateToWorkoutScreen() },
    modifier = Modifier.fillMaxWidth(),
    // colors = cardColors,
    //            .padding(8.dp)
    //    border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outlineVariant),
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
        // TODO Simplify
        Text(
          text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
              val measurmentSystem = LocaleData.getMeasurementSystem(ULocale.getDefault())
              when (measurmentSystem) {
                LocaleData.MeasurementSystem.US -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInPounds)} lbs"
                }

                LocaleData.MeasurementSystem.UK -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInKilograms)} kg"
                }

                LocaleData.MeasurementSystem.SI -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInKilograms)} kg"
                }

                else -> {
                  "Total Volume: ${doubleFormatted(it.totalVolumeInPounds)} lbs"
                }
              }
            } else {
              // Default
              "Total Volume: ${doubleFormatted(it.totalVolumeInPounds)} lbs"
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
fun PlaceholderChart() {
  val boxModifier = Modifier
    .fillMaxWidth()
    .aspectRatio(16f / 9f)
    .padding(bottom = 16.dp)

  val workoutFrequencyChartData = generateRandomExerciseVolumeData()

  val gifEnabledLoader =
    ImageLoader.Builder(LocalContext.current)
      .components {
        if (SDK_INT >= 28) {
          add(ImageDecoderDecoder.Factory())
        } else {
          add(GifDecoder.Factory())
        }
      }
      .build()

  Row(modifier = boxModifier) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      //      WebViewChart(
      //        workoutFrequencyChartData,
      //        ChartType.VOLUME,
      //        yMaxAllowed = workoutFrequencyChartData.values.maxBy { it.toInt() }.toInt(),
      //      )

      val gifs =
        listOf(
          "https://media0.giphy.com/media/v1.Y2lkPTc5MGI3NjExcWVzcW1nbXU4ZDZ0b3d3M3g4M2l2NjNzam8xcHo5YXFhZWk5Ym4yeSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/G2tytrXJjiZjO/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExeTVrdmY0dmo0NDd3NXc0NnJmcXBkZ3AzdjRzOHFxMGh5MjN1Z2p3ayZlcD12MV9naWZzX3NlYXJjaCZjdD1n/l1J9FwSQMezfQrPJ6/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExeTVrdmY0dmo0NDd3NXc0NnJmcXBkZ3AzdjRzOHFxMGh5MjN1Z2p3ayZlcD12MV9naWZzX3NlYXJjaCZjdD1n/3ohhwkOPJalh5ar1cI/giphy.gif",
          "https://media4.giphy.com/media/v1.Y2lkPTc5MGI3NjExcHNmYjAwb252bDJ4MXQ2MjltZ2ZvOTc2b2VlYXZmM2kxZXNsM2RrNyZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/g01ZnwAUvutuK8GIQn/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3Yng2ZTV0d25pN2VjMDR3aHVuaHBqbHdpazhya3Vuc2RyNzl3ZHh2NSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/3vjUhay98FYRRWfiNZ/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExNjNyNTB5bmk2OHZ3ZDFpMzloYzltdnNsOHdyMWx0bnp4czJhdTJseSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/9PgxlC8MA5Nq69Vh4W/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExZTc5aWM4MmE4aGk2YmFhd3AyaG0xdmpocnI3M2RtbXRldDYzYTlwdiZlcD12MV9naWZzX3NlYXJjaCZjdD1n/UqZ4imFIoljlr5O2sM/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExMmR5ZHFmdXI2a2FqZ3I2dTJ3Nmg0bmc5cG14dXpwYW1ldzk0dGhpZCZlcD12MV9naWZzX3NlYXJjaCZjdD1n/9PgxlC8MA5Nq69Vh4W/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3bnF2aGx6eXVvaXdiZnBnOXF3M3gwdjAxcWh1Z3lnbjRkeW1mMnRxMCZlcD12MV9naWZzX3JlbGF0ZWQmY3Q9Zw/GHpqx8F4t9ciI/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPTc5MGI3NjExdWM1dGdxOGpxZWxmZnFoeWVjMWRobW1naWtvZTU0aXl4eGV2M2dhMSZlcD12MV9naWZzX3NlYXJjaCZjdD1n/J9UXcyVW6OYN2fH0Sr/giphy.gif",
          "https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExdTdtaWt5cGl2eDdmdzNoY2M3YTVzbGxua25pMXJyNW5odTZtZ2NkYiZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/L6LPsHa8bqMfTerzX1/giphy.gif",
          "https://media3.giphy.com/media/v1.Y2lkPTc5MGI3NjExcHZqZmxuNm9mN2wxNm96b3p4bjFwc2F6MHhzMDQ3M28xaWR2cGlxMSZlcD12MV9pbnRlcm5hbF9naWZfYnlfaWQmY3Q9Zw/NnKbYrjEWde7XZxcgJ/giphy.gif",
          "https://media.giphy.com/media/v1.Y2lkPWVjZjA1ZTQ3eWNzcnh1YWxrOHlpb3lqemlnemtjODV4dnF2dmVmOXlkNHVsdWp1dSZlcD12MV9naWZzX3JlbGF0ZWQmY3Q9Zw/YsOQGFLy6l6aIqZ0ru/giphy.gif",
        )

      AsyncImage(
        model = gifs.random(),
        imageLoader = gifEnabledLoader,
        contentDescription = null,
        alpha = 1f,
        modifier = Modifier.fillMaxWidth(),
        //        contentScale = ContentScale.,
      )

      Text(
        "No Data Available!",
        color = Color.White,
        modifier = Modifier
          .align(Alignment.TopCenter)
          .padding(vertical = 8.dp),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold,
      )
    }
  }
}

@Composable
fun ChartTab(
  exerciseDetailsUiState: ExerciseDetailsUiState,
  navigateToFullscreenChartScreen: () -> Unit,
) {
  val state = rememberScrollState()

  val boxModifier = Modifier
    .fillMaxWidth()
    .aspectRatio(1f / 1f)
  val textModifier = Modifier.padding(vertical = 8.dp)

  Column(modifier = Modifier.verticalScroll(state)) {
    Text(
      text =
        "Best Set (Estimated 1RM) (${exerciseDetailsUiState.userDefaultWeightUnit.abbreviation})",
      modifier = textModifier,
    )
    when (exerciseDetailsUiState.userDefaultWeightUnit) {
      MassUnit.POUNDS -> exerciseDetailsUiState.bestSetEstimatedOneRepMaxPoundsChartData
      MassUnit.KILOGRAMS -> exerciseDetailsUiState.bestSetEstimatedOneRepMaxKilogramsChartData
    }?.let { chartData ->
      if (chartData.values.isNotEmpty()) {
        Row(modifier = boxModifier) {
          WebViewChart(
            chartData,
            ChartType.VOLUME,
            yMaxAllowed = chartData.values.maxBy { it.toInt() }.toInt().times(1.2),
          )
        }
      } else {
        PlaceholderChart()
      }
    } ?: PlaceholderChart()

    Text(
      text =
        "Best Set (Max Weight Lifted) (${exerciseDetailsUiState.userDefaultWeightUnit.abbreviation})",
      modifier = textModifier,
    )
    when (exerciseDetailsUiState.userDefaultWeightUnit) {
      MassUnit.POUNDS -> exerciseDetailsUiState.bestSetMaxWeightPoundsLiftedChartData
      MassUnit.KILOGRAMS -> exerciseDetailsUiState.bestSetMaxWeightKilogramsLiftedChartData
    }?.let { chartData ->
      if (chartData.values.isNotEmpty()) {
        Row(modifier = boxModifier) {
          WebViewChart(
            chartData,
            ChartType.VOLUME,
            yMaxAllowed = chartData.values.maxBy { it.toInt() }.toInt().times(1.2),
          )
        }
      } else {
        PlaceholderChart()
      }
    } ?: PlaceholderChart()

    Text(
      text = "Total Volume (${exerciseDetailsUiState.userDefaultWeightUnit.abbreviation})",
      modifier = textModifier,
    )

    when (exerciseDetailsUiState.userDefaultWeightUnit) {
      MassUnit.POUNDS -> exerciseDetailsUiState.bestSetTotalVolumePoundsChartData
      MassUnit.KILOGRAMS -> exerciseDetailsUiState.bestSetTotalVolumeKilogramsChartData
    }?.let { chartData ->
      if (chartData.values.isNotEmpty()) {
        Row(modifier = boxModifier) {
          WebViewChart(
            chartData,
            ChartType.VOLUME,
            yMaxAllowed = chartData.values.maxBy { it.toInt() }.toInt().times(1.2),
          )
        }
      } else {
        PlaceholderChart()
      }
    } ?: PlaceholderChart()

    Text(text = "Best Set (Repetitions)", modifier = textModifier)
    exerciseDetailsUiState.bestSetRepetitionsChartData?.let { chartData ->
      if (chartData.values.isNotEmpty()) {
        Row(modifier = boxModifier) {
          WebViewChart(
            chartData,
            ChartType.VOLUME,
            yMaxAllowed = chartData.values.maxBy { it.toInt() }.toInt().plus(5),
          )
        }
      } else {
        PlaceholderChart()
      }
    } ?: PlaceholderChart()
  }
}

// @Preview(showBackground = true)
@Composable
fun RecordsTab(uiState: ExerciseDetailsUiState) {
  val exerciseRecords = uiState.exerciseRecords

  val rowPadding = 4.dp

  val onSurface = MaterialTheme.colorScheme.onSurface
  val subtitle = MaterialTheme.colorScheme.onSurfaceVariant

  val buttonColors =
    ButtonDefaults.buttonColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )

  LazyColumn(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(rowPadding)) {
    item {
      Row {
        Text("Personal Records", color = onSurface, style = MaterialTheme.typography.titleMedium)
      }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Estimated 1RM", color = onSurface, style = MaterialTheme.typography.bodyMedium)
        // TODO Add Kilograms
        Text(
          "${
            exerciseRecords?.bestPerformanceByRepetitions?.firstOrNull()
              ?.let { doubleFormatted(it.estimated1RMPounds) }?.ifEmpty { "-" }
          } lbs",
          color = onSurface,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Max Volume", color = onSurface, style = MaterialTheme.typography.bodyMedium)
        // TODO Add Kilograms
        Text(
          "${exerciseRecords?.maxVolumePounds?.let { doubleFormatted(it) } ?: "-"} lbs",
          color = onSurface,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Max Weight", color = onSurface, style = MaterialTheme.typography.bodyMedium)
        // TODO Add Kilograms
        Text(
          "${exerciseRecords?.maxWeightLiftedPounds?.let { doubleFormatted(it) } ?: "-"} lbs",
          color = onSurface,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = { /* TODO Navigate Records History */ }, colors = buttonColors) {
          Text(
            text = "View Records History",
            color = onSurface,
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
          "Reps",
          color = onSurface,
          modifier = Modifier.weight(1f),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
        )
        Text(
          "Best Performance",
          color = onSurface,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
        )
        Text(
          "Estimated RM",
          color = onSurface,
          modifier = Modifier.weight(1f),
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
        )
      }
    }

    // TODO Refoctor to List
    // TODO handle missing data, i.e. where there is no actual performance data
    exerciseRecords?.bestPerformanceByRepetitions?.forEach { set ->
      item {
        Row(
          Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Text(
            text = "${set?.repetitions}",
            color = onSurface,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
          )

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
          ) {
            Text(
              text =
                "${set?.weightPounds?.let { doubleFormatted(it) } ?: "-"} lbs (${set?.repetitionsPerformed ?: "-"} Reps)",
              color = onSurface,
              textAlign = TextAlign.Center,
              style = MaterialTheme.typography.bodyMedium,
            )

            val dateFormatter =
              DateTimeFormatter.ofPattern("MMMM d yyyy").withLocale(Locale.current.platformLocale)

            val timezone = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()
            val dateCreated =
              set
                ?.datePerformed
                ?.toLocalDateTime(timezone)
                ?.toJavaLocalDateTime()
                ?.format(dateFormatter)

            Text(
              text = dateCreated ?: "-",
              color = subtitle,
              style = MaterialTheme.typography.bodySmall,
              textAlign = TextAlign.Center,
            )
          }

          Text(
            text = "${set?.estimated1RMPounds?.let { doubleFormatted(it) } ?: "-"} lbs",
            color = onSurface,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }
    }

    item {
      Row(Modifier.padding(vertical = 16.dp)) {
        Text(
          text =
            "Estimated Rep Maxes are calculated using Data Driven Strength's Modernized Load/RIR " +
                    "tables if Rate of Perceived Effort (RPE) or Repetitions In Reserve (RIR) were recorded for the set and uses the Repetition Capacity of the exercise. " +
                    "1RM Percentage selected for Rep Max calculation uses RPE is 10 and RIR is 0." +
                    "Alternatively, Brzyckyi's formuala is used to calculate estimated rep maxes if RPE or RIR were not recorded. " +
                    "Actual Rep Maxes show your best performance for each rep.",
          color = subtitle,
          style = MaterialTheme.typography.bodySmall,
        )
      }
    }

    item {
      Row {
        Text("Lifetime Stats", color = onSurface, style = MaterialTheme.typography.titleMedium)
      }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Total Reps", color = onSurface, style = MaterialTheme.typography.bodyMedium)
        Text(
          text =
            "${exerciseRecords?.totalReps?.let { doubleFormatted(it.toDouble()) } ?: "-"} reps",
          color = onSurface,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }

    item {
      Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("Total Volume", color = onSurface, style = MaterialTheme.typography.bodyMedium)
        // TODO Add Kilograms
        Text(
          "${exerciseRecords?.totalVolumePounds?.let { doubleFormatted(it) } ?: "-"} lbs",
          color = onSurface,
          style = MaterialTheme.typography.bodyMedium,
        )
      }
    }
  }
}
