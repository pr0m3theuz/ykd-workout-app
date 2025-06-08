@file:OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)

package com.example.myapplication.features.userProfileScreen.like.boostcamp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(onNavigateToDashboardsInfo: () -> Unit) {
  var selectedTabIndex by remember { mutableStateOf(0) }
  val tabs = listOf("Muscle Engagement", "Workout Summary")

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Analytics") },
        actions = {
          /* TODO OutlinedButton(onClick = { *//* TODO Handle Exercises click *//* }) {
            Text("Exercises",
              style = MaterialTheme.typography.bodySmallEmphasized)
          }*/
        },
      )
    },
    // In a real app, you would have a proper BottomNavigationView here.
    // For this example, it's omitted as per the screenshot focus.
    bottomBar = {}
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      LifetimeStatsCard()
      WeeklyReportCard()

      // Tab Layout
      SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
              Text(
                text = title, style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
              )
            }
          )
        }
      }

      // Content based on selected tab
      when (selectedTabIndex) {
        0 -> MuscleTrackerContent()
        1 -> WorkoutSummaryContent() // Placeholder
        2 -> ExerciseAnalyticsContent(onNavigateToDashboardsInfo)
      }
    }
  }
}

@Composable
fun LifetimeStatsCard() {
  Card(
    modifier = Modifier.fillMaxWidth(),
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(" Lifetime Totals", style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        StatItem(value = "0", label = "Workouts")
        StatItem(value = "0", label = "Minutes")
        StatItem(value = "0", label = "lbs Lifted")
      }
      Spacer(Modifier.height(8.dp))
      Text(
        "0 Week Streak",
        modifier = Modifier.align(Alignment.CenterHorizontally),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
fun WeeklyReportCard(
  workoutCount: Int = 0,
  minutes: Int = 0,
  weightLifted: Int = 0,
  weightUnit: String = "lbs",
  navigateToWeeklyReport: () -> Unit = {}
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
  ) {

    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        val startDate = LocalDate.now().with(
          TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        ).atStartOfDay().toKotlinLocalDateTime()
        val endDate = LocalDate.now().with(
          TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)
        ).atStartOfDay().toKotlinLocalDateTime()

        val startDateString =
          "${startDate.date.month.name.substring(0, 3)} ${startDate.date.dayOfMonth}"
        val endDateString = "${endDate.date.month.name.substring(0, 3)} ${endDate.date.dayOfMonth}"

        Text(
          " $startDateString – $endDateString",
          style = MaterialTheme.typography.titleMedium
        )
        TextButton(onClick = { navigateToWeeklyReport() }) {
          Text("Weekly Report")
          Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "View weekly report"
          )
        }
      }
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        StatItem(value = workoutCount.toString(), label = "Workouts")
        StatItem(value = minutes.toString(), label = "Minutes")
        StatItem(value = weightLifted.toString(), label = "$weightUnit Lifted")
      }
    }
  }
}

@Composable
fun StatItem(value: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(
      text = value,
      style = MaterialTheme.typography.headlineMedium,
      fontWeight = FontWeight.Bold
    )
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun MuscleTrackerContent() {
  var showDatePicker by remember { mutableStateOf(false) }

  val startDate = LocalDate.now().with(
    TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
  ).atStartOfDay().toLocalDate()
  val endDate = LocalDate.now().with(
    TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)
  ).atStartOfDay().toLocalDate().minusDays(1)

  val state = rememberDateRangePickerState(
    initialSelectedStartDate = startDate,
    initialSelectedEndDate = endDate
  )

  if (showDatePicker) {
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text("OK")
        }
      },
      dismissButton = {
        TextButton(onClick = { showDatePicker = false }) {
          Text("Cancel")
        }
      }
    ) {
      DateRangePicker(state = state, modifier = Modifier.weight(1f))
    }
  }

  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
    )
    {
      Box(
        modifier = Modifier
          .padding(horizontal = 0.dp)
          .clickable(
            onClick = { showDatePicker = true },
          ),

        ) {
        val startDateString = "${
          state.getSelectedStartDate()?.toKotlinLocalDate()?.month?.name?.substring(0, 3)
        }" +
                " ${state.getSelectedStartDate()?.toKotlinLocalDate()?.dayOfMonth}"

        val endDateString = "${
          state.getSelectedEndDate()?.toKotlinLocalDate()?.month?.name?.substring(0, 3)
        }" +
                " ${state.getSelectedEndDate()?.toKotlinLocalDate()?.dayOfMonth}"

        TextButton(
          onClick = { showDatePicker = true },
          modifier = Modifier.padding(horizontal = 0.dp)
        ) { Text("$startDateString – $endDateString", style = MaterialTheme.typography.bodyMedium) }
      }

    }
    MuscleEngagementContent()
  }
}

@Composable
fun MuscleEngagementContent() {
  MuscleEngagementScreen(muscleDataList = emptyList())
}

@Composable
fun WorkoutSummaryContent() {
  // TODO Placeholder content for the second tab
//  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//    Text("Workout Summary Content", style = MaterialTheme.typography.titleLarge)
//  }
  val periods = listOf("Daily", "Weekly", "Monthly")
  var selectedPeriod by remember { mutableStateOf(periods.first()) }

  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
    periods.forEach { period ->
      FilterChip(
        selected = period == selectedPeriod,
        onClick = { selectedPeriod = period },
        label = { Text(period) }
      )
    }
  }

  // TODO Workout Frequency per Week Chart
  // TODO Total Training Volume per Week Chart
  // TODO Total Reps per Week Chart
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseAnalyticsContent(onNavigateToDashboardsInfo: () -> Unit) {
  val options = listOf(
    "Estimated 1RM using Brzycki Method",
    "Estimated 1RM using RPE",
    "Best Set",
    "Total Training Volume",
    "Max Consecutive Reps"
  )
  val newItems = setOf("Best Set", "Max Consecutive Reps")
  var expanded by remember { mutableStateOf(false) }
  var selectedOptionText by remember { mutableStateOf(options[0]) }

  Column(
    modifier = Modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      // Dropdown Menu
      ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.weight(1f)
      ) {
        OutlinedTextField(
          readOnly = true,
          textStyle = MaterialTheme.typography.bodySmall,
          value = selectedOptionText,
          onValueChange = {},
          trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
          colors = ExposedDropdownMenuDefaults.textFieldColors(),
          modifier = Modifier.menuAnchor(
            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
            enabled = true
          )
        )
        ExposedDropdownMenu(
          expanded = expanded,
          onDismissRequest = { expanded = false }
        ) {
          options.forEach { selectionOption ->
            DropdownMenuItem(
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(text = selectionOption)
                  if (newItems.contains(selectionOption)) {
                    Spacer(Modifier.width(8.dp))
                    NewTag()
                  }
                }
              },
              onClick = {
                selectedOptionText = selectionOption
                expanded = false
              }
            )
          }
        }
      }
      // Info Icon
      IconButton(onClick = onNavigateToDashboardsInfo) {
        Icon(Icons.Default.Info, contentDescription = "Information")
      }
    }

    // The content below the dropdown is also the upgrade banner
    // TODO
  }
}

@Composable
fun NewTag() {
  Text(
    text = "NEW",
    color = MaterialTheme.colorScheme.onError,
    style = MaterialTheme.typography.labelSmall,
    modifier = Modifier
      .background(
        color = MaterialTheme.colorScheme.error,
        shape = RoundedCornerShape(4.dp)
      )
      .padding(horizontal = 4.dp, vertical = 2.dp)
  )
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun AnalyticsScreenPreview() {
  MyApplicationTheme {
    AnalyticsScreen(onNavigateToDashboardsInfo = {})
  }
}