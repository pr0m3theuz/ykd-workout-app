@file:OptIn(ExperimentalUuidApi::class)

package com.example.myapplication.features.history.ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
import com.example.myapplication.common.ui.component.BottomNavigationBar
import com.example.myapplication.features.history.HistoryViewModel
import com.example.myapplication.features.workout.ui.WorkoutSummaryCard
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.androidx.compose.koinViewModel
import java.time.format.TextStyle
import java.util.Locale
import kotlin.uuid.ExperimentalUuidApi

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
  //    val selectedCategoriesMap by
  // exercisesViewModel.selectedCategories.collectAsStateWithLifecycle()
  //    val selectedCategoriesList = selectedCategoriesMap.keys.toList()

  fun filterWorkouts(search: String) {
    // TODO .setSearchExerciseName(search)
    // TODO .filterExercises()
  }

  //  var query by rememberSaveable { mutableStateOf(/* TODO .searchExerciseName */ ) }
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
        navigationIcon = {
          /*IconButton(
          onClick = {
            */
          /*TODO Navigate to Previous Screen */
          /*
              navController.navigateUp()
              Toast.makeText(context, "TODO: Navigate to Previous Screen", Toast.LENGTH_SHORT)
                .show()
            }
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
              tint = MaterialTheme.colorScheme.onSurface,
              contentDescription = "Go to the previous screen",
            )
          }*/
        },
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
              contentDescription = "Search Exercises",
            )
          }
          //          TODO ExercisesFilterMenu(/*modifier =
          // Modifier.align(Alignment.CenterVertically)*/ )
          //          TODO ExerciseOptionsDropdownMenu()
        },
      )
    },
    bottomBar = { BottomNavigationBar(navController = navController) },
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

        HistoryLazyColumn(navController = navController)
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
  historyViewModel: HistoryViewModel = koinViewModel(),
  navController: NavController,
) {

  val workouts by historyViewModel.workouts.collectAsState()

  val groupedWorkouts =
    workouts.groupBy {
      Instant.fromEpochSeconds(it.workoutEnded?.epochSeconds!!)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .month
    }

  Log.i("HistoryLazyColumn", "workout count: ${workouts.size}")

  if (workouts.isEmpty()) {
    NoWorkoutsPerformed()
  } else {
    LazyColumn(
      contentPadding = PaddingValues(16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      groupedWorkouts.forEach { (month, monthWorkouts) ->
        stickyHeader {
          Text(
            modifier = Modifier.padding(0.dp),
            text = month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
          )
        }

        // TODO Implement Search & Filtering of Workouts & Programs
        // items(count = workouts.size, key = { index: Int -> workouts[index].lazyListItemID }) {
        // index,
        items(monthWorkouts) { workout ->
          Box(
            modifier =
            Modifier.clickable(
              onClick = {
                navController.navigate("history?overviewWorkout=${workout.id.toHexString()}")
              },
              enabled = true,
              interactionSource = remember { MutableInteractionSource() },
              indication = LocalIndication.current,
            )
          ) {
            WorkoutSummaryCard(
              completedWorkout = workout,
              userDefaultWeightUnit = historyViewModel.userDefaultWeightUnit,
              getExercise = { exerciseID -> historyViewModel.getExerciseByExerciseID(exerciseID) },
            )
          }
        }
      }
    }
  }
}

