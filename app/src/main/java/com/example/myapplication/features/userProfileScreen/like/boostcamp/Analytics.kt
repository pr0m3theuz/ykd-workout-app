@file:OptIn(
  ExperimentalMaterial3ExpressiveApi::class,
  ExperimentalMaterial3Api::class,
  ExperimentalTime::class,
)

package com.example.myapplication.features.userProfileScreen.like.boostcamp

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.Muscle
import com.example.myapplication.common.domain.model.MuscleFractionalSets
import com.example.myapplication.common.domain.model.TotalWorkoutVolume
import com.example.myapplication.common.domain.model.WorkoutAnalytics
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.common.util.doubleFormatted
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toKotlinLocalDate
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toKotlinTimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.TimeZone
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.time.toDuration

@Composable
fun AnalyticsScreen(
  profileViewModel: ProfileViewModel,
  onNavigateToDashboardsInfo: () -> Unit,
  onNavigateToWeeklyReport: () -> Unit,
) {
  val user by profileViewModel.userProfile.collectAsState()

  val startDate =
    LocalDate.now()
      .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
      .atStartOfDay()
      .toKotlinLocalDateTime()

  val endDate =
    LocalDate.now()
      .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
      .atStartOfDay()
      .minusNanos(1)
      .toKotlinLocalDateTime()

  val weeklyWorkoutAnalytics =
    profileViewModel.getWorkoutAnalyticsForPeriod(startDate = startDate, endDate = endDate)

  val lifetimeWorkoutAnalytics = profileViewModel.getWorkoutAnalyticsForLifetime()

  val weeklyWorkoutStreak = profileViewModel.getWorkoutStreakWeek() ?: 0

  val totalTrainingVolumeData = profileViewModel.getTotalVolumeData()

  val muscleEngagementData =
    profileViewModel.getMuscleEngagementData(startDate = startDate, endDate = endDate)

  Log.i("AnalyticsScreen", "muscleEngagementData: $muscleEngagementData")

  AnalyticsScreenContent(
    onNavigateToDashboardsInfo = { onNavigateToDashboardsInfo() },
    weeklyReportCard = {
      WeeklyReportCard(
        workoutAnalytics = weeklyWorkoutAnalytics,
        startDate = startDate,
        endDate = endDate,
        userMassUnit = user.defaultWeightUnit,
        navigateToWeeklyReport = { onNavigateToWeeklyReport() },
      )
    },
    lifetimeStatsCard = {
      LifetimeStatsCard(
        workoutAnalytics = lifetimeWorkoutAnalytics,
        weeklyWorkoutStreak = weeklyWorkoutStreak,
        userMassUnit = user.defaultWeightUnit,
      )
    },
    muscleTrackerContent = {
      MuscleTrackerContent(
        startDate = startDate,
        endDate = endDate,
        muscleEngagementData = muscleEngagementData
      )
    },
    totalTrainingVolumeData = totalTrainingVolumeData,
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreenContent(
  onNavigateToDashboardsInfo: () -> Unit,
  weeklyReportCard: @Composable () -> Unit = {},
  lifetimeStatsCard: @Composable () -> Unit = {},
  muscleTrackerContent: @Composable () -> Unit = {},
  totalTrainingVolumeData: List<TotalWorkoutVolume>,
) {
  var selectedTabIndex by remember { mutableStateOf(0) }
  val tabs = listOf("Muscle Engagement", "Workout Summary")

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Analytics") },
        actions = {
          /* TODO OutlinedButton(onClick = { */
          /* TODO Handle Exercises click */
          /* }) {
            Text("Exercises",
              style = MaterialTheme.typography.bodySmallEmphasized)
          }*/
        },
      )
    },
    // In a real app, you would have a proper BottomNavigationView here.
    // For this example, it's omitted as per the screenshot focus.
    bottomBar = {},
  ) { paddingValues ->
    val scrollState = rememberScrollState()

    Column(
      modifier =
        Modifier
          .padding(paddingValues)
          .fillMaxSize()
          .padding(16.dp)
          .verticalScroll(state = scrollState, flingBehavior = ScrollableDefaults.flingBehavior()),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      weeklyReportCard()
      lifetimeStatsCard()

      // Tab Layout
      SecondaryTabRow(selectedTabIndex = selectedTabIndex) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTabIndex == index,
            onClick = { selectedTabIndex = index },
            text = {
              Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
              )
            },
          )
        }
      }

      // Content based on selected tab
      when (selectedTabIndex) {
        0 -> muscleTrackerContent()
        1 -> WorkoutSummaryContent(totalTrainingVolumeData = totalTrainingVolumeData) // Placeholder
        2 -> ExerciseAnalyticsContent(onNavigateToDashboardsInfo)
      }

      Spacer(Modifier.height(16.dp))
    }
  }
}

@Composable
fun LifetimeStatsCard(
  workoutAnalytics: WorkoutAnalytics?,
  weeklyWorkoutStreak: Int = 0,
  userMassUnit: MassUnit = MassUnit.POUNDS,
) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text("Lifetime Totals", style = MaterialTheme.typography.titleMedium)
      Spacer(Modifier.height(16.dp))
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        val boxModifier = Modifier.weight(1f)
        Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
          StatItem(value = (workoutAnalytics?.workoutCount ?: 0).toString(), label = "Workouts")
        }
        Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
          StatItem(value = (workoutAnalytics?.totalDuration ?: 0).toString(), label = "Minutes")
        }

        val currentLocale = Locale.current.platformLocale

        val weightLiftedValue =
          if (userMassUnit == MassUnit.POUNDS) {
            if (currentLocale.country == "US") {
              workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds?.div(2000)) }
                ?: 0.toString()
            } else {
              workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds?.div(2240)) }
                ?: 0.toString()
            }
          } else {
            workoutAnalytics?.let { doubleFormatted(it.totalVolumeInKilograms?.div(1000)) }
              ?: 0.toString()
          }

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

        Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
          StatItem(value = weightLiftedValue, label = weightLiftedLabel)
        }
      }
    }
    //    Spacer(Modifier.height(8.dp))
    Text(
      "$weeklyWorkoutStreak Week Streak",
      modifier = Modifier
        .align(Alignment.CenterHorizontally)
        .padding(bottom = 16.dp),
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    //    Spacer(Modifier.height(16.dp))
  }
}

@Composable
fun WeeklyReportCard(
  workoutAnalytics: WorkoutAnalytics?,
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  userMassUnit: MassUnit,
  navigateToWeeklyReport: () -> Unit = {},
) {
  Card(modifier = Modifier.fillMaxWidth()) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        val startDateString = "${startDate.date.month.name.substring(0, 3)} ${startDate.date.day}"
        val endDateString = "${endDate.date.month.name.substring(0, 3)} ${endDate.date.day}"

        Text("$startDateString – $endDateString", style = MaterialTheme.typography.titleMedium)

        TextButton(onClick = { navigateToWeeklyReport() }) {
          Text("Weekly Report")
          Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "View weekly report",
          )
        }
      }

      Row(modifier = Modifier.fillMaxWidth()) {
        val boxModifier = Modifier.weight(1f)
        Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
          StatItem(value = (workoutAnalytics?.workoutCount ?: 0).toString(), label = "Workouts")
        }
        Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
          StatItem(value = (workoutAnalytics?.totalDuration ?: 0).toString(), label = "Minutes")
        }

        val currentLocale = Locale.current.platformLocale

        val weightLiftedValue =
          if (userMassUnit == MassUnit.POUNDS) {
            if (currentLocale.country == "US") {
              workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds?.div(2000)) }
                ?: 0.toString()
            } else {
              workoutAnalytics?.let { doubleFormatted(it.totalVolumeInPounds?.div(2240)) }
                ?: 0.toString()
            }
          } else {
            workoutAnalytics?.let { doubleFormatted(it.totalVolumeInKilograms?.div(1000)) }
              ?: 0.toString()
          }

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

        Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
          StatItem(value = weightLiftedValue, label = weightLiftedLabel)
        }
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
      fontWeight = FontWeight.Bold,
    )
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun MuscleTrackerContent(
  startDate: LocalDateTime,
  endDate: LocalDateTime,
  muscleEngagementData: List<Pair<Muscle, List<MuscleFractionalSets>>>
) {
  var showDatePicker by remember { mutableStateOf(false) }

  val state =
    rememberDateRangePickerState(
      initialSelectedStartDate = startDate.toJavaLocalDateTime().toLocalDate(),
      initialSelectedEndDate = endDate.toJavaLocalDateTime().toLocalDate(),
    )

  if (showDatePicker) {
    DatePickerDialog(
      onDismissRequest = { showDatePicker = false },
      confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } },
      dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
    ) {
      DateRangePicker(state = state, modifier = Modifier.weight(1f))
    }
  }

  //  Column(modifier = Modifier.fillMaxSize()) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    Box(
      modifier = Modifier
        .padding(horizontal = 0.dp)
        .clickable(onClick = { showDatePicker = true })
    ) {
      val startDateString =
        "${
          state.getSelectedStartDate()?.toKotlinLocalDate()?.month?.name?.substring(0, 3)
        }" +
                " ${state.getSelectedStartDate()?.toKotlinLocalDate()?.day}"

      val endDateString =
        "${
          state.getSelectedEndDate()?.toKotlinLocalDate()?.month?.name?.substring(0, 3)
        }" +
                " ${state.getSelectedEndDate()?.toKotlinLocalDate()?.day}"

      TextButton(
        onClick = { showDatePicker = true },
        modifier = Modifier.padding(horizontal = 0.dp),
      ) {
        Text("$startDateString – $endDateString", style = MaterialTheme.typography.bodyMedium)
      }
    }
  }
  MuscleEngagementContent(muscleEngagementData = muscleEngagementData)
  //  }
}

@Composable
fun MuscleEngagementContent(muscleEngagementData: List<Pair<Muscle, List<MuscleFractionalSets>>>) {
  MuscleEngagementScreen(muscleEngagementData = muscleEngagementData)
}

enum class Period(val displayName: String, val value: Duration, val sequenceLimit: Int) {
  DAILY("Daily", 1.toDuration(DurationUnit.DAYS), 30),
  WEEKLY("Weekly", 7.toDuration(DurationUnit.DAYS), 30),
  MONTHLY("Monthly", 1.toDuration(DurationUnit.DAYS), 30),
}

@Composable
fun WorkoutSummaryContent(
  totalTrainingVolumeData: List<TotalWorkoutVolume>,
  userMassUnit: MassUnit = MassUnit.POUNDS,
  workoutFrequencyChart: @Composable () -> Unit = {},
  totalTrainingVolumeChart: @Composable () -> Unit = {},
) {
  // TODO Placeholder content for the second tab
  //  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
  //    Text("Workout Summary Content", style = MaterialTheme.typography.titleLarge)
  //  }

  var selectedPeriod by remember { mutableStateOf(Period.DAILY) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Period.entries.forEach { period ->
        FilterChip(
          selected = period == selectedPeriod,
          onClick = { selectedPeriod = period },
          label = { Text(period.displayName, style = MaterialTheme.typography.labelMedium) },
        )
      }
    }

    // TODO Add DatePicker Dialog & generateSequence limit is based on the selected period & the
    //  number of periods in the date range

    var dateSequenceLimit by remember { mutableStateOf(selectedPeriod.sequenceLimit) }

    if (totalTrainingVolumeData.isNotEmpty()) {
      val timezone = TimeZone.getDefault().toZoneId().toKotlinTimeZone()

      val startDateDaily =
        LocalDate.now().atStartOfDay().toKotlinLocalDateTime().toInstant(timezone)

      val endDateDaily =
        LocalDate.now()
          .plusDays(1)
          .atStartOfDay()
          .minusNanos(1)
          .toKotlinLocalDateTime()
          .toInstant(timezone)

      val startDateWeekly =
        LocalDate.now()
          .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
          .atStartOfDay()
          .toKotlinLocalDateTime()
          .toInstant(timezone)

      val endDateWeekly =
        LocalDate.now()
          .with(TemporalAdjusters.next(DayOfWeek.MONDAY))
          .atStartOfDay()
          .minusNanos(1)
          .toKotlinLocalDateTime()
          .toInstant(timezone)

      val startDateMonthly =
        LocalDate.now()
          .with(TemporalAdjusters.firstDayOfMonth())
          .atStartOfDay()
          .toKotlinLocalDateTime()
          .toInstant(timezone)

      val endDateMonthly =
        LocalDate.now()
          .with(TemporalAdjusters.lastDayOfMonth())
          .atStartOfDay()
          .minusNanos(1)
          .toKotlinLocalDateTime()
          .toInstant(timezone)

      // TODO Add type if needed

      val workoutEndedDateTimes = totalTrainingVolumeData.map { it.workoutEnded }

      val dateSequence =
        generateSequence(
          seed =
            when (selectedPeriod) {
              Period.DAILY -> startDateDaily..endDateDaily
              Period.WEEKLY -> startDateWeekly..endDateWeekly
              Period.MONTHLY -> startDateMonthly..endDateMonthly
            }
        ) { currentRange ->
          when (selectedPeriod) {
            Period.DAILY ->
              currentRange.start.minus(selectedPeriod.value)..currentRange.endInclusive.minus(
                selectedPeriod.value
              )

            Period.WEEKLY ->
              currentRange.start.minus(selectedPeriod.value)..currentRange.endInclusive.minus(
                selectedPeriod.value
              )

            Period.MONTHLY -> {
              currentRange.start
                .toLocalDateTime(timezone)
                .toJavaLocalDateTime()
                .minus(1, ChronoUnit.MONTHS)
                .toKotlinLocalDateTime()
                .toInstant(timezone)..currentRange.endInclusive
                .toLocalDateTime(timezone)
                .toJavaLocalDateTime()
                .minus(1, ChronoUnit.MONTHS)
                .toKotlinLocalDateTime()
                .toInstant(timezone)
            }
          }
        }
          .take(dateSequenceLimit)

      val workoutFrequencyData: Map<ClosedRange<Instant>, Int> =
        dateSequence.associateWith { dateRange -> workoutEndedDateTimes.count { it in dateRange } }

      val labels =
        when (selectedPeriod) {
          Period.DAILY -> {
            workoutFrequencyData.keys.mapNotNull {
              val dateFormatter =
                DateTimeFormatter.ofPattern("MMM d").withLocale(Locale.current.platformLocale)

                it.start.toLocalDateTime(timezone).toJavaLocalDateTime().format(dateFormatter)
            }
          }

          Period.WEEKLY -> {
            workoutFrequencyData.keys.mapNotNull {
              val dateFormatter =
                DateTimeFormatter.ofPattern("MMM d yyyy").withLocale(Locale.current.platformLocale)


              it.start.toLocalDateTime(timezone).toJavaLocalDateTime().format(dateFormatter)

            }
          }

          Period.MONTHLY -> {
            workoutFrequencyData.keys.mapNotNull {
              val dateFormatter =
                DateTimeFormatter.ofPattern("MMM yyyy").withLocale(Locale.current.platformLocale)

                it.start.toLocalDateTime(timezone).toJavaLocalDateTime().format(dateFormatter)

            }
          }
        }

      val valuesReversed = workoutFrequencyData.values.toList().asReversed()

      val firstNonZeroIndex = valuesReversed.indexOfFirst { it != 0 }.minus(1).coerceAtLeast(0)

      val labelsReversed = labels.asReversed().subList(firstNonZeroIndex, labels.size)

      val workoutFrequencyChartData: ChartData =
        ChartData(labelsReversed, valuesReversed.subList(firstNonZeroIndex, valuesReversed.size))

      val totalTrainingVolumePoundsData: Map<ClosedRange<Instant>, Double> =
        dateSequence.associateWith { dateRange ->
          totalTrainingVolumeData.sumOf {
            if (it.workoutEnded in dateRange) it.totalVolumeInPounds else 0.0
          }
        }

      val totalTrainingVolumeKilogramsData: Map<ClosedRange<Instant>, Double> =
        dateSequence.associateWith { dateRange ->
          totalTrainingVolumeData.sumOf {
            if (it.workoutEnded in dateRange) it.totalVolumeInKilograms else 0.0
          }
        }

      val totalTrainingVolumeKilogramsChartData: ChartData =
        ChartData(
          labelsReversed,
          totalTrainingVolumeKilogramsData.values
            .toList()
            .asReversed()
            .subList(firstNonZeroIndex, valuesReversed.size),
        )

      val totalTrainingVolumePoundsChartData: ChartData =
        ChartData(
          labelsReversed,
          totalTrainingVolumePoundsData.values
            .toList()
            .asReversed()
            .subList(firstNonZeroIndex, valuesReversed.size),
        )

      val boxModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f / 1f)
      Text(text = "Workout Frequency", style = MaterialTheme.typography.titleMedium)

      Row(modifier = boxModifier) {
        WebViewChart(
          workoutFrequencyChartData,
          ChartType.FREQUENCY,
          yMaxAllowed = workoutFrequencyData.values.toList().max().times(1.1),
//          period = selectedPeriod,
        )
      }

      Text(
        text = "Total Training Volume (${userMassUnit.abbreviation})",
        style = MaterialTheme.typography.titleSmall,
      )
      Row(modifier = boxModifier) {
        if (userMassUnit == MassUnit.POUNDS) {
          WebViewChart(
            totalTrainingVolumePoundsChartData,
            ChartType.FREQUENCY,
            yMaxAllowed = totalTrainingVolumePoundsData.values.toList().max().times(1.1),
//            period = selectedPeriod,
          )
        } else {
          WebViewChart(
            totalTrainingVolumeKilogramsChartData,
            ChartType.VOLUME,
            yMaxAllowed = totalTrainingVolumeKilogramsData.values.toList().max().times(1.1),
//            period = selectedPeriod,
          )
        }
      }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseAnalyticsContent(onNavigateToDashboardsInfo: () -> Unit) {
  val options =
    listOf(
      "Estimated 1RM using Brzycki Method",
      "Estimated 1RM using RPE",
      "Best Set",
      "Total Training Volume",
      "Max Consecutive Reps",
    )
  val newItems = setOf("Best Set", "Max Consecutive Reps")
  var expanded by remember { mutableStateOf(false) }
  var selectedOptionText by remember { mutableStateOf(options[0]) }

  //  Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp))
  // {
  Row(verticalAlignment = Alignment.CenterVertically) {
    // Dropdown Menu
    ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = { expanded = !expanded },
      modifier = Modifier.weight(1f),
    ) {
      OutlinedTextField(
        readOnly = true,
        textStyle = MaterialTheme.typography.bodySmall,
        value = selectedOptionText,
        onValueChange = {},
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        colors = ExposedDropdownMenuDefaults.textFieldColors(),
        modifier =
          Modifier.menuAnchor(
            type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
            enabled = true,
          ),
      )
      ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
            },
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
  //  }
}

@Composable
fun NewTag() {
  Text(
    text = "NEW",
    color = MaterialTheme.colorScheme.onError,
    style = MaterialTheme.typography.labelSmall,
    modifier =
      Modifier
        .background(color = MaterialTheme.colorScheme.error, shape = RoundedCornerShape(4.dp))
        .padding(horizontal = 4.dp, vertical = 2.dp),
  )
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun AnalyticsScreenPreview() {
  MyApplicationTheme {
    AnalyticsScreenContent(onNavigateToDashboardsInfo = {}, totalTrainingVolumeData = emptyList())
  }
}
