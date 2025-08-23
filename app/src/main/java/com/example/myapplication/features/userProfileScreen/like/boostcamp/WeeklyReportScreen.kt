@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.features.userProfileScreen.like.boostcamp

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.WorkoutAnalytics
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Data class to represent a stat item
data class Stat(val value: String, val label: String)

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun WeeklyReportPreview() {
  MyApplicationTheme {
    WeeklyReportScreenContent(
      onBackClick = {},
      WorkoutAnalytics(
        workoutCount = 5,
        totalDuration = 1000,
        totalVolumeInPounds = 30000.0,
        totalVolumeInKilograms = 15000.0,
      ),
      weeklyWorkoutStreak = 10,
      startDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
      endDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    )
  }
}

@Composable
fun WeeklyReportScreen(profileViewModel: ProfileViewModel, onBackClick: () -> Unit) {
  val user by profileViewModel.userProfile.collectAsState()
  val uiState by profileViewModel.analyticsUiState.collectAsState()

  profileViewModel.getWorkoutAnalyticsForPeriod(uiState.initStartDateWeek, uiState.initEndDateWeek)
  profileViewModel.getWorkoutStreakWeek()

  val weeklyWorkoutAnalytics = uiState.weeklyWorkoutAnalytics
  val weeklyWorkoutStreak = uiState.weeklyWorkoutStreak ?: 0

  WeeklyReportScreenContent(
    onBackClick = { onBackClick() },
    workoutAnalytics = weeklyWorkoutAnalytics,
    weeklyWorkoutStreak = weeklyWorkoutStreak,
    startDate = uiState.initStartDateWeek,
    endDate = uiState.initEndDateWeek,
    userMassUnit = user.defaultWeightUnit,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyReportScreenContent(
  onBackClick: () -> Unit,
  workoutAnalytics: WorkoutAnalytics?,
  weeklyWorkoutStreak: Int = 0,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  userMassUnit: MassUnit = MassUnit.POUNDS,
) {
  val startDateString = "${startDate.date.month.name.substring(0, 3)} ${startDate.date.day}"
  val endDateString = "${endDate.date.month.name.substring(0, 3)} ${endDate.date.day}"

  Text("$startDateString – $endDateString", style = MaterialTheme.typography.titleMedium)

  val currentLocale = Locale.current.platformLocale

  val weightLiftedValue =
    if (userMassUnit == MassUnit.POUNDS) {
      if (currentLocale.country == "US") {
        workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds?.div(2000)) } ?: 0.toString()
        //        workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds) } ?: 0.toString()
      } else {
        workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds?.div(2240)) }
        //        workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds) }
          ?: 0.toString()
      }
    } else {
      workoutAnalytics?.let { doubleFormatted(it.totalVolumeInKilograms?.div(1000)) }
      //      workoutAnalytics?.let { doubleFormatted(it.totalVolumeInKilograms?.div(1)) }
        ?: 0.toString()
    }

  //  val weightLiftedLabel =
  //    if (userMassUnit == MassUnit.POUNDS) {
  //      MassUnit.POUNDS.abbreviation
  //    } else {
  //      MassUnit.KILOGRAMS.abbreviation
  //    }

  val weightLiftedLabel =
    if (userMassUnit == MassUnit.POUNDS) {
      if (currentLocale.country == "US") {
        "US Tons Lifted"
      } else {
        "Imperial Tons Lifted"
      }
    } else {
      "Metric Tons Lifted"
    }

  val stats =
    listOf(
      Stat((workoutAnalytics?.workoutCount ?: 0).toString(), "Workouts"),
      Stat(
        workoutAnalytics?.let { doubleFormatted(it.totalDuration?.toDouble()) } ?: 0.toString(),
        "Minutes",
      ),
      Stat(weightLiftedValue, weightLiftedLabel),
      Stat(weeklyWorkoutStreak.toString(), "Week Streak"),
    )

  Scaffold(
    topBar = {
      CenterAlignedTopAppBar(
        title = {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              "Weekly Report",
              style = MaterialTheme.typography.titleLarge,
              color = MaterialTheme.colorScheme.onBackground,
            )
          }
        },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          //          IconButton(onClick = { /* Handle share/export action */ }) {
          //            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "Export
          // Report")
          //          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            //            containerColor = MaterialTheme.colorScheme.surfaceContainer
          ),
      )
    }
    //    containerColor = MaterialTheme.colorScheme.surfaceContainer,
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        "$startDateString – $endDateString",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(16.dp))

      // Stats Grid
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        items(stats) { stat -> StatCard(stat = stat) }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Muscle Engagement Card
      MuscleEngagementCard()

      Spacer(modifier = Modifier.weight(1f)) // Pushes the footer text to the bottom
    }
  }
}

@Composable
fun StatCard(stat: Stat, modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.aspectRatio(1f), // Makes the card a square
    shape = MaterialTheme.shapes.large,
    //    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      if (stat.label.contains("lbs") || stat.label.contains("kg")) {
        // Special case for the "lbs Lifted" text to include the unit
        Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = stat.value,
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
          )
          Text(
            text = "lbs",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(vertical = 6.dp),
          )
        }
        Text(
          text = "Lifted",
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      } else {
        Text(
          text = stat.value,
          style = MaterialTheme.typography.displayMedium,
          fontWeight = FontWeight.Bold,
        )
        Text(
          text = stat.label,
          textAlign = TextAlign.Center,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }
  }
}

@Composable
fun MuscleEngagementCard(modifier: Modifier = Modifier) {
  Card(
    modifier = modifier.fillMaxWidth()
    //    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = "Muscle Engagement Tracker",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
      )

      // Placeholder for the muscle anatomy images
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Image(
          imageVector = Icons.Default.Person, // Replace with your actual drawable
          contentDescription = "Front Muscle View",
          modifier = Modifier.height(150.dp),
          contentScale = ContentScale.Fit,
          colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        )
        Image(
          imageVector = Icons.Default.Person, // Replace with your actual drawable
          contentDescription = "Back Muscle View",
          modifier = Modifier.height(150.dp),
          contentScale = ContentScale.Fit,
          colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
        )
      }

      Text(
        text = "No data during this time period",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 16.dp),
      )
    }
  }
}
