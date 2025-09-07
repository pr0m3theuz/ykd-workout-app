@file:OptIn(ExperimentalTime::class)

package com.example.myapplication.features.userProfileScreen.like.boostcamp

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Muscle
import com.example.myapplication.common.domain.model.MuscleFractionalSets
import com.example.myapplication.common.domain.model.WorkoutAnalytics
import com.example.myapplication.common.ui.theme.IBMColorBlindPalette
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.floor
import kotlin.math.min
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

  profileViewModel.getMuscleEngagementData(
    startDate = uiState.initStartDateWeek,
    endDate = uiState.initEndDateWeek,
  )

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
    muscleEngagementCard = {
      MuscleEngagementCard(
        content = { MuscleEngagementContent(muscleEngagementData = uiState.muscleEngagementData) }
      )
    },
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
  muscleEngagementCard: @Composable () -> Unit = {},
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
    LazyColumn(
      modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      //        .verticalScroll(rememberScrollState())
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      item {
        Text(
          "$startDateString – $endDateString",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Stats Grid
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            StatCard(stat = stats[0], modifier = Modifier.weight(0.5f))
            StatCard(stat = stats[1], modifier = Modifier.weight(0.5f))
          }
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
          ) {
            StatCard(stat = stats[2], modifier = Modifier.weight(0.5f))
            StatCard(stat = stats[3], modifier = Modifier.weight(0.5f))
          }
        }
        Spacer(modifier = Modifier.height(16.dp))
      }

      // Muscle Engagement Card
      item {
        muscleEngagementCard()
        Spacer(modifier = Modifier.height(16.dp))
      }

      //      Spacer(modifier = Modifier.weight(1f)) // Pushes the footer text to the bottom
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
fun MuscleEngagementCard(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
  Card(
    modifier = modifier.fillMaxSize()
    //    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
  ) {
    Column(
      modifier = Modifier.padding(vertical = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = "Muscle Engagement",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
      )

      content()
    }
  }
}

@Composable
private fun MuscleEngagementContent(
  muscleEngagementData: List<Pair<Muscle, List<MuscleFractionalSets>>> = emptyList(),
  muscleDataList: List<MuscleData> = emptyList(),
) {
  val riveMuscleList = rememberSaveable { riveMuscleListSource }

  Column(
    modifier = Modifier.fillMaxSize(),
    //      .background(MaterialTheme.colorScheme.surface)
    verticalArrangement = Arrangement.spacedBy(0.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // --- Static Header Content ---
    if (muscleDataList.isNotEmpty()) {
      // --- List Header ---
      MuscleListHeader()
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

      // --- Muscle Data List ---
      muscleDataList.forEach { muscleData ->
        MuscleListItem(data = muscleData)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
      }
    } else if (muscleEngagementData.isNotEmpty()) {

      val muscleFractionalSetsList =
        muscleEngagementData
          .flatMap { it.second }
          .groupBy { it.muscle }
          .map { (muscle, data) -> muscle to data.sumOf { it.fractionalSets } }

      val maxFractionalSets = muscleFractionalSetsList.maxOf { it.second }

      if (muscleFractionalSetsList.isNotEmpty()) {
        riveMuscleList.forEach { muscleData ->
          val muscleFractionalSetsList =
            muscleFractionalSetsList.filter { (muscle, _) ->
              muscleData.correspondingMuscles.contains(muscle.id)
            }

          val maxFractionalSetsForGroup =
            if (muscleFractionalSetsList.isNotEmpty()) muscleFractionalSetsList.maxOf { it.second }
            else 0.0

          val fraction = maxFractionalSetsForGroup.div(maxFractionalSets).toFloat()

          muscleData.color =
            if (maxFractionalSetsForGroup == 0.0) {
              MaterialTheme.colorScheme.outlineVariant.toArgb()
            } else {
              val numberOfColors = IBMColorBlindPalette.colors.size - 1
              val startColor = floor(fraction * numberOfColors).toInt().coerceIn(0, numberOfColors)
              lerp(
                IBMColorBlindPalette.colors[min(0, startColor)],
                IBMColorBlindPalette.colors[min(startColor + 1, numberOfColors)],
                fraction,
              )
                .toArgb()
            }
        }
      }
      // --- Rive Animation ---
      MuscleEngagementRiveAnimation(riveMuscleDataList = riveMuscleList)

      // --- List Header ---
      Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        MuscleListHeader()
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        muscleEngagementData.forEach { muscleData ->
          //        Log.d("MuscleEngagementScreen", "maxFractionalSets: $maxFractionalSets")

          MuscleFractionalSetsItem(data = muscleData, maxFractionalSets = maxFractionalSets)
          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 2.dp),
          )
        }
      }
    } else {
      Text("No data available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}
