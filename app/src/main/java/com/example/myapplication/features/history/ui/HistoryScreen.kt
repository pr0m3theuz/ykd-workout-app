@file:OptIn(
  ExperimentalUuidApi::class,
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalTime::class,
)

package com.example.myapplication.features.history.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.features.exercises.ui.NoHistoryTab
import com.example.myapplication.features.history.HistoryViewModel
import com.example.myapplication.features.workout.ui.WorkoutSummaryCard
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  historyViewModel: HistoryViewModel,
  bottomNavigationBar: @Composable () -> Unit,
  navigateToWorkoutOverview: (Workout) -> Unit = {},
) {
  val workouts by historyViewModel.workouts.collectAsState()
  val allPrograms by historyViewModel.programs.collectAsState()
  Log.i("HistoryScreen", "programs count: ${allPrograms.size}")
  val (searchBarVisibility, setSearchBarVisibility) = remember { mutableStateOf(false) }

  val programIdsList by remember { derivedStateOf { workouts.map { it.programID }.distinct() } }
  val relevantPrograms by remember {
    derivedStateOf { allPrograms.filter { programIdsList.contains(it.id) } }
  }

  var query by rememberSaveable { mutableStateOf("") }
  //  val programs by remember { mutableStateOf(relevantPrograms) }
  val dateRangePickerState = rememberDateRangePickerState()

  val levels = listOf("Beginner", "Intermediate", "Advanced")

  val falseList = List(relevantPrograms.size, { false })

  val programsMap = relevantPrograms.zip(falseList).toMap().toMutableMap()

  val authorsMap =
    relevantPrograms.mapNotNull { it.author }.distinct().zip(falseList).toMap().toMutableMap()

  val levelsMap = remember { levels.zip(List(levels.size, { false })).toMutableStateMap() }

  Log.i("HistoryScreen", "programsMap: ${programsMap.size}")
  Log.i("HistoryScreen", "authorsMap: $authorsMap")
  Log.i("HistoryScreen", "levelsMap: $levelsMap")

  val selectedPrograms = remember { mutableStateListOf<Program>() }
  val selectedAuthors = remember { mutableStateListOf<String>() }
  val selectedLevels = remember { mutableStateListOf<String>() }

  fun selectProgram(program: Program, checked: Boolean) {
    programsMap[program] = checked
    if (checked) {
      selectedPrograms.add(program)
    } else {
      selectedPrograms.remove(program)
    }
  }

  fun selectAuthor(author: String, checked: Boolean) {
    authorsMap[author] = checked
    if (checked) {
      selectedAuthors.add(author)
    } else {
      selectedAuthors.remove(author)
    }
  }

  fun selectLevel(level: String, checked: Boolean) {
    levelsMap[level] = checked
    if (checked) {
      selectedLevels.add(level)
    } else {
      selectedLevels.remove(level)
    }
  }

  val searchResults by remember {
    derivedStateOf {
      if (
        query.isEmpty() &&
        selectedPrograms.isEmpty() &&
        selectedAuthors.isEmpty() &&
        selectedLevels.isEmpty() &&
        dateRangePickerState.selectedStartDateMillis == null &&
        dateRangePickerState.selectedEndDateMillis == null
      ) {
        workouts
      } else {

        val filteredPrograms = relevantPrograms.filter { selectedPrograms.contains(it) }
        val filteredAuthors = relevantPrograms.filter { selectedAuthors.contains(it.author) }
        val filteredLevels = relevantPrograms.filter {
          // Log.i("HistoryScreen", "level: ${it.level}")
          selectedLevels.any { level -> it.level?.contains(level, ignoreCase = true) ?: false }
        }

        val filteredProgramsIDs =
          (filteredPrograms + filteredAuthors + filteredLevels).distinctBy { it.id }.map { it.id }

        val filteredWorkoutsName =
          if (query.isNotEmpty()) {
            workouts.filter { it.name.contains(query, ignoreCase = true) }
          } else emptyList()

        val filteredWorkoutsProgram = workouts.filter { filteredProgramsIDs.contains(it.programID) }

        val filteredWorkoutsDate =
          workouts.filter {
            if (
              dateRangePickerState.selectedEndDateMillis != null &&
              dateRangePickerState.selectedStartDateMillis != null &&
              it.workoutEnded != null
            ) {
              it.workoutEnded!! in
                      Instant.fromEpochMilliseconds(
                        dateRangePickerState.selectedStartDateMillis!!
                      )..Instant.fromEpochMilliseconds(dateRangePickerState.selectedEndDateMillis!!)
            } else {
              false
            }
          }

        val uniqueFilteredWorkouts =
          (filteredWorkoutsName + filteredWorkoutsProgram + filteredWorkoutsDate).distinctBy { it.id }
        return@derivedStateOf uniqueFilteredWorkouts
      }
    }
  }

  if (!searchBarVisibility) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = {
            Text(
              text = "History",
              color = MaterialTheme.colorScheme.onSurface,
              style = MaterialTheme.typography.titleLarge,
              textAlign = TextAlign.Center,
              //                    modifier = Modifier.align(Alignment.CenterVertically),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis,
            )
          },
          navigationIcon = {},
          actions = {
            IconButton(
              onClick = { setSearchBarVisibility(true) },
              enabled = true,
            ) {
              Icon(
                imageVector = Icons.Outlined.Search,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Search workouts",
              )
            }
            //          TODO ExercisesFilterMenu(/*modifier =
            // Modifier.align(Alignment.CenterVertically)*/ )
            //          TODO ExerciseOptionsDropdownMenu()
          },
        )
      },
      bottomBar = { bottomNavigationBar() },
      content = { contentPadding ->
        Column(Modifier.padding(contentPadding)) {
          if (workouts.isNotEmpty()) {

            HistoryLazyColumn(
              workouts = workouts,
              userDefaultWeightUnit = historyViewModel.userDefaultWeightUnit,
              getExerciseByExerciseID = { exerciseID ->
                historyViewModel.getExerciseByExerciseID(exerciseID)
              },
              navigateToWorkoutOverview = { navigateToWorkoutOverview(it) },
            )
          } else {
            NoHistoryTab(text = "Perform your first workout!")
          }
        }
      },
    )
  } else {
    HistorySearchBar(
      query = query,
      dateRangePickerState = dateRangePickerState,
      onQueryChange = { query = it },
      onSearch = {},
      searchResults = searchResults,
      programs = programsMap.toMap(),
      authors = authorsMap.toMap(),
      levels = levelsMap.toMap(),
      onResultClick = { navigateToWorkoutOverview(it) },
      onProgramSelected = { program, checked -> selectProgram(program, checked) },
      onAuthorSelected = { author, checked -> selectAuthor(author, checked) },
      onLevelSelected = { level, checked -> selectLevel(level, checked) },
      onDismissRequest = { setSearchBarVisibility(false) },
    )
  }
}

@Composable
fun NoWorkoutsPerformed() {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(top = 24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    //    LinearProgressIndicator(
    //      modifier = Modifier.semantics(mergeDescendants = true) {}.padding(10.dp),
    //    )
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text("No Workouts Performed!")
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryLazyColumn(
  workouts: List<Workout>,
  userDefaultWeightUnit: String = "lbs",
  getExerciseByExerciseID: (exerciseID: ObjectId?) -> Exercise?,
  navigateToWorkoutOverview: (Workout) -> Unit,
) {
  val defaultTimeZone = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()

  val groupedWorkouts =
    workouts.groupBy {
      val workoutEnded = it.workoutEnded?.toLocalDateTime(defaultTimeZone)
      YearMonth(workoutEnded!!.year, workoutEnded.month)
    }
  //      .mapValues { (_, workouts) ->
  //        workouts.groupBy { it.workoutEnded?.toLocalDateTime(defaultTimeZone)?.monthNumber }
  //      }

  Log.i("HistoryLazyColumn", "workout count: ${workouts.size}")

  if (workouts.isEmpty()) {
    NoWorkoutsPerformed()
  } else {
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      groupedWorkouts.forEach { (yearMonth, monthWorkouts) ->
        stickyHeader {
          Row(
            modifier = Modifier
              .background(MaterialTheme.colorScheme.background)
              .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Text(
              style = MaterialTheme.typography.titleMediumEmphasized,
              modifier = Modifier.padding(vertical = 16.dp),
              text = "${yearMonth.month} ${yearMonth.year}",
            )
          }
        }

        items(
          monthWorkouts.size,
          key = { index: Int -> monthWorkouts[index].lazyListItemID }) { index ->
          val workout = monthWorkouts[index]
          Box(
            modifier =
              Modifier.clickable(
                onClick = { navigateToWorkoutOverview(workout) },
                enabled = true,
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current,
              )
          ) {
            WorkoutSummaryCard(
              completedWorkout = workout,
              userDefaultWeightUnit = userDefaultWeightUnit,
              getExercise = { exerciseID -> getExerciseByExerciseID(exerciseID) },
            )
          }
        }
      }
    }
  }
}
