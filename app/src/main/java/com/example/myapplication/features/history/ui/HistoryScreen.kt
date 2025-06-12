@file:OptIn(ExperimentalUuidApi::class, ExperimentalMaterial3ExpressiveApi::class)

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.myapplication.features.history.HistoryViewModel
import com.example.myapplication.features.workout.ui.WorkoutSummaryCard
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toJavaInstant
import org.bson.types.ObjectId
import kotlin.uuid.ExperimentalUuidApi

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
  historyViewModel: HistoryViewModel,
  bottomNavigationBar: @Composable () -> Unit,
  navigateToWorkoutOverview: (Workout) -> Unit = {},
) {
  //  var query by rememberSaveable { mutableStateOf(/* TODO .search History */ ) }
  var active by remember { mutableStateOf(false) }
  // val (searchBarVisibility, setSearchBarVisibility) = remember { mutableStateOf(false) } // SearchBar will be always visible

  val searchQuery by historyViewModel.searchQuery.collectAsState()

  // Debounce for search query
  LaunchedEffect(searchQuery) {
    kotlinx.coroutines.delay(300L) // Debounce time
    // The actual filtering is done via collecting the StateFlow in ViewModel,
    // so no explicit call needed here if updateFilters triggers recomposition that uses workouts flow.
  }

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
          FilterMenu(historyViewModel = historyViewModel)
          // Display active filters
          val currentStartDate by historyViewModel.startDate.collectAsState()
          val currentEndDate by historyViewModel.endDate.collectAsState()
          val currentQuery by historyViewModel.searchQuery.collectAsState()
          DisplayActiveFilters(
            startDateEpochSeconds = currentStartDate,
            endDateEpochSeconds = currentEndDate,
            query = currentQuery
          )
        },
      )
    },
    bottomBar = { bottomNavigationBar() },
    content = { contentPadding ->
      Column(Modifier.padding(contentPadding)) {
        SearchBar(
          inputField = {
            SearchBarDefaults.InputField(
              query = searchQuery ?: "",
              onQueryChange = { historyViewModel.updateFilters(newQuery = it) },
              onSearch = { active = false },
              expanded = active,
              onExpandedChange = { active = false }, // Keep this to allow dismissing search by clicking outside
              enabled = true,
              placeholder = {
                Text("Search History", color = MaterialTheme.colorScheme.onSecondaryContainer)
              },
              leadingIcon = {
                Icon(
                  Icons.Default.Search,
                  contentDescription = "Search",
                  tint = MaterialTheme.colorScheme.onSecondaryContainer,
                )
              },
              trailingIcon = {
                if (searchQuery?.isNotEmpty() == true) {
                  IconButton(onClick = { historyViewModel.updateFilters(newQuery = "") }) {
                    Icon(
                      Icons.Default.Clear,
                      contentDescription = "Clear search",
                      tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                  }
                }
              },
              interactionSource = remember { MutableInteractionSource() },
            )
          },
          expanded = active,
          onExpandedChange = { active = false }, // This controls the expanded state of the search results area
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
          windowInsets = WindowInsets(0.dp),
        ) {}

        val workouts by historyViewModel.workouts.collectAsState()

        HistoryLazyColumn(
          workouts = workouts,
          userDefaultWeightUnit = historyViewModel.userDefaultWeightUnit,
          getExerciseByExerciseID = { exerciseID ->
            historyViewModel.getExerciseByExerciseID(
              exerciseID
            )
          },
          navigateToWorkoutOverview = { navigateToWorkoutOverview(it) }
        )
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
      Text(
        "No Workouts Performed!",
      )
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
  val groupedWorkouts =
    workouts.groupBy {
      java.time.YearMonth.from(
        Instant.fromEpochSeconds(it.workoutEnded?.epochSeconds!!)
          .toJavaInstant()
      )
    }

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
        items(monthWorkouts) { workout ->
          Box(
            modifier =
              Modifier.clickable(
                onClick = {
                  navigateToWorkoutOverview(workout)
                },
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

@Composable
fun DisplayActiveFilters(startDateEpochSeconds: Long?, endDateEpochSeconds: Long?, query: String?) {
  val dateFormat = remember { java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()) }
  var filterText = ""

  if (query?.isNotEmpty() == true) {
    filterText += "Query: \"$query\""
  }

  if (startDateEpochSeconds != null && endDateEpochSeconds != null) {
    val startDate = Instant.fromEpochSeconds(startDateEpochSeconds).toLocalDateTime(TimeZone.currentSystemDefault()).date
    val endDate = Instant.fromEpochSeconds(endDateEpochSeconds).toLocalDateTime(TimeZone.currentSystemDefault()).date
    val startDateString = dateFormat.format(java.util.Date(startDateEpochSeconds * 1000))
    val endDateString = dateFormat.format(java.util.Date(endDateEpochSeconds * 1000))

    if (filterText.isNotEmpty()) filterText += "; "
    filterText += "Dates: $startDateString - $endDateString"
  } else if (startDateEpochSeconds != null) {
    // Handle case with only start date if necessary, though current logic sets both or none for date ranges
    val startDateString = dateFormat.format(java.util.Date(startDateEpochSeconds * 1000))
    if (filterText.isNotEmpty()) filterText += "; "
    filterText += "From: $startDateString"
  } else if (endDateEpochSeconds != null) {
    // Handle case with only end date if necessary
    val endDateString = dateFormat.format(java.util.Date(endDateEpochSeconds * 1000))
    if (filterText.isNotEmpty()) filterText += "; "
    filterText += "To: $endDateString"
  }

  if (filterText.isNotEmpty()) {
    Text(
      text = filterText,
      style = MaterialTheme.typography.labelSmall,
      modifier = Modifier.padding(horizontal = 4.dp),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}


@Composable
fun FilterMenu(historyViewModel: HistoryViewModel) {
  var expanded by remember { mutableStateOf(false) }
  var showDateRangePicker by remember { mutableStateOf(false) }
  val dateRangePickerState = rememberDateRangePickerState()

  Box {
    IconButton(onClick = { expanded = true }) {
      Icon(
        imageVector = Icons.Default.FilterList,
        contentDescription = "Filter Workouts",
        tint = MaterialTheme.colorScheme.onSurface,
      )
    }
    DropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      // TODO: Add filter options here
      DropdownMenuItem(text = { Text("All (No Filter)") }, onClick = {
        historyViewModel.updateFilters(newQuery = historyViewModel.searchQuery.value, newStartDate = null, newEndDate = null)
        expanded = false
      })
      DropdownMenuItem(text = { Text("Last 7 Days") }, onClick = {
        val now = Clock.System.now()
        val startOfDay = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val endOfToday = Instant.parse("${startOfDay}T23:59:59.999Z") // End of current day
        val sevenDaysAgo = now.minus(7, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        historyViewModel.updateFilters(
          newQuery = historyViewModel.searchQuery.value,
          newStartDate = sevenDaysAgo.epochSeconds,
          newEndDate = endOfToday.epochSeconds
        )
        expanded = false
      })
      DropdownMenuItem(text = { Text("Last 30 Days") }, onClick = {
        val now = Clock.System.now()
        val startOfDay = now.toLocalDateTime(TimeZone.currentSystemDefault()).date
        val endOfToday = Instant.parse("${startOfDay}T23:59:59.999Z")
        val thirtyDaysAgo = now.minus(30, DateTimeUnit.DAY, TimeZone.currentSystemDefault())
        historyViewModel.updateFilters(
          newQuery = historyViewModel.searchQuery.value,
          newStartDate = thirtyDaysAgo.epochSeconds,
          newEndDate = endOfToday.epochSeconds
        )
        expanded = false
      })
      DropdownMenuItem(text = { Text("This Month") }, onClick = {
        val now = Clock.System.now()
        val currentDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())
        val firstDayOfMonthInstant = Instant.parse("${currentDateTime.year}-${String.format("%02d", currentDateTime.monthNumber)}-01T00:00:00Z")
        val endOfToday = Instant.parse("${now.toLocalDateTime(TimeZone.currentSystemDefault()).date}T23:59:59.999Z")
        historyViewModel.updateFilters(
          newQuery = historyViewModel.searchQuery.value,
          newStartDate = firstDayOfMonthInstant.epochSeconds,
          newEndDate = endOfToday.epochSeconds
        )
        expanded = false
      })
      DropdownMenuItem(text = { Text("Custom Range...") }, onClick = {
        showDateRangePicker = true
        expanded = false
      })
    }

    if (showDateRangePicker) {
      DatePickerDialog(
        onDismissRequest = { showDateRangePicker = false },
        confirmButton = {
          TextButton(
            onClick = {
              showDateRangePicker = false
              val startDateMillis = dateRangePickerState.selectedStartDateMillis
              val endDateMillis = dateRangePickerState.selectedEndDateMillis
              if (startDateMillis != null && endDateMillis != null) {
                historyViewModel.updateFilters(
                  newQuery = historyViewModel.searchQuery.value,
                  newStartDate = startDateMillis / 1000, // Convert to seconds
                  newEndDate = endDateMillis / 1000 // Convert to seconds
                )
              }
            }
          ) { Text("OK") }
        },
        dismissButton = {
          TextButton(
            onClick = { showDateRangePicker = false }
          ) { Text("Cancel") }
        }
      ) {
        DateRangePicker(state = dateRangePickerState, modifier = Modifier.padding(16.dp))
      }
    }
  }
}