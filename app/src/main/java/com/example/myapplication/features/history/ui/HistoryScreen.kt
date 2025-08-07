@file:OptIn(
  ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalTime::class
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.features.exercises.ui.NoHistoryTab
import com.example.myapplication.features.history.HistoryViewModel
import com.example.myapplication.features.workout.ui.WorkoutSummaryCard
import kotlinx.datetime.YearMonth
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import org.bson.types.ObjectId
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  historyViewModel: HistoryViewModel,
  bottomNavigationBar: @Composable () -> Unit,
  navigateToWorkoutOverview: (Workout) -> Unit = {},
) {
  // TODO Implement Search & Filtering of Workouts & Programs
  fun filterWorkouts(search: String) {
    // TODO .setSearchExerciseName(search)
    // TODO .filterExercises()
  }


  val workouts by historyViewModel.workouts.collectAsState()

  //  var query by rememberSaveable { mutableStateOf(/* TODO .search History */ ) }
  var active by remember { mutableStateOf(false) }
  val (searchBarVisibility, setSearchBarVisibility) = remember { mutableStateOf(false) }

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
          val iconColors = IconButtonDefaults.iconButtonColors()
          IconButton(
            onClick = { setSearchBarVisibility(!searchBarVisibility) },
            enabled = true,
            colors = iconColors,
          ) {
            Icon(
              imageVector = Icons.Outlined.Search,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Search Workouts",
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
        SearchBar(
          inputField = {
            SearchBarDefaults.InputField(
              query = "TODO", /* TODO query */
              onQueryChange = {
                //            query = it
                //         TODO filterExercises(it)
              },
              onSearch = { active = false },
              expanded = active,
              onExpandedChange = { active = false },
              enabled = true,
              placeholder = {
                Text("Search History", color = MaterialTheme.colorScheme.onSecondaryContainer)
              },
              leadingIcon = {
                Icon(
                  Icons.Default.Search,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
              },
              trailingIcon = null,
              interactionSource = null,
            )
          },
          expanded = active,
          onExpandedChange = { active = false },
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
          windowInsets = WindowInsets(0.dp),
        ) {}


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
              text = "${yearMonth.month} ${yearMonth.year}"
            )
          }
        }

        // TODO Implement Search & Filtering of Workouts & Programs
        // items(count = workouts.size, key = { index: Int -> workouts[index].lazyListItemID }) {
        // index,


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
