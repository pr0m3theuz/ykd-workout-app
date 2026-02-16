@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.userProfileScreen

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.features.history.ui.HistoryLazyColumn
import com.example.myapplication.features.userProfileScreen.like.boostcamp.LifetimeStatsCard
import com.example.myapplication.features.userProfileScreen.like.boostcamp.WeeklyReportCard

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
  MyApplicationTheme {
    //    ProfileScreenV2(
    //      navigateToReplicatorScreen = {},
    //      bottomNavigationBar = {},
    //      navigateToAnalyticsScreen = {},
    //      navigateToSettingsScreen = {},
    //      navigateToHistoryScreen = {},
    //      navigateToWorkoutOverview = {}
    //    )
  }
}

@Composable
fun ProfileScreenV2(
  profileViewModel: ProfileViewModel,
  bottomNavigationBar: @Composable () -> Unit,
  navigateToReplicatorScreen: () -> Unit,
  navigateToAnalyticsScreen: () -> Unit,
  navigateToSettingsScreen: () -> Unit,
  navigateToHistoryScreen: () -> Unit,
  navigateToWorkoutOverview: (workout: Workout) -> Unit,
  navigateToWeeklyReport: () -> Unit,
) {
  // TODO REWRITE THIS
  val context = LocalContext.current
  val user by profileViewModel.userProfile.collectAsState()
  val uiState by profileViewModel.analyticsUiState.collectAsStateWithLifecycle()

  val image = remember { mutableStateOf<ImageBitmap?>(null) }
//   Log.i("ProfileScreenV2", "user.imageUri: ${user.imageUri}")
  user.imageUri?.toUri()?.apply {
    try {
      context.contentResolver.openFileDescriptor(this, "r").use { parcelFileDescriptor ->
        parcelFileDescriptor?.fileDescriptor?.let { fileDescriptor ->
          image.value = BitmapFactory.decodeFileDescriptor(fileDescriptor).asImageBitmap()
        }
      }
    } catch (e: Exception) {
//       Log.e("ProfileScreenV2", "Error loading image: ${e.message}")
    }
  }

  profileViewModel.getWorkoutAnalyticsForPeriod(uiState.initStartDateWeek, uiState.initEndDateWeek)
  profileViewModel.getWorkoutAnalyticsForLifetime()
  profileViewModel.getWorkoutStreakWeek()

  val weeklyWorkoutAnalytics = uiState.weeklyWorkoutAnalytics
  val lifetimeWorkoutAnalytics = uiState.lifetimeWorkoutAnalytics
  val weeklyWorkoutStreak = uiState.weeklyWorkoutStreak ?: 0
  //  val user = "Anonymous"
  //  val userProfileManager by inject<UserProfileManager>(UserProfileManager::class.java)
  //  val user = userProfileManager.userProfile.collectAsState().value
  //  val userEmail = "TEMP@anon.anon"

  //  val user = UserProfile(ObjectId())

  Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
      TopAppBar(
        title = { Text(text = "Profile") },
        actions = {
          IconButton(onClick = { navigateToSettingsScreen() }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
          }
        },
      )
    },
    bottomBar = { bottomNavigationBar() },
    content = { paddingValues ->
      Column(
        Modifier
          .fillMaxWidth()
          .padding(paddingValues),
        verticalArrangement = Arrangement.spacedBy(0.dp),
      ) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
          if (image.value != null) {
            Image(
              bitmap = image.value!!,
              modifier = Modifier
                .padding(8.dp)
                .size(200.dp)
                .clip(shape = CircleShape),
              contentDescription = "profile picture",
            )
          } else {
            Image(
              painterResource(R.drawable.guy_fawkes_mask), // Placeholder
              modifier = Modifier
                .padding(8.dp)
                .size(200.dp)
                .clip(shape = CircleShape),
              contentDescription = "profile picture",
            )
          }
          Text(
            text = user.name ?: "MISSING",
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
          )
        }

        // Tab Layout
        // Tab Layout
        var selectedTabIndex by remember { mutableStateOf(1) }
        val tabs = listOf("Activity", "Stats")

        SecondaryTabRow(selectedTabIndex) {
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

        when (selectedTabIndex) {
          0 ->
            ActivityContent(
              navigateToHistoryScreen = { navigateToHistoryScreen() },
              content = {
                HistoryLazyColumn(
                  //    modifier = Modifier.padding(horizontal = 16.dp),
                  workouts = emptyList(),
                  getExerciseByExerciseID = { exerciseID -> null },
                  navigateToWorkoutOverview = { navigateToWorkoutOverview(it) },
                )
              },
            )

          1 ->
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
              item {
                StatsContent(
                  navigateToAnalyticsScreen = { navigateToAnalyticsScreen() },
                  weeklyReportCard = {
                    WeeklyReportCard(
                      workoutAnalytics = weeklyWorkoutAnalytics,
                      startDate = uiState.initStartDateWeek,
                      endDate = uiState.initEndDateWeek,
                      userMassUnit = user.defaultWeightUnit,
                      navigateToWeeklyReport = { navigateToWeeklyReport() },
                    )
                  },
                  lifetimeStatsCard = {
                    LifetimeStatsCard(
                      workoutAnalytics = lifetimeWorkoutAnalytics,
                      weeklyWorkoutStreak = weeklyWorkoutStreak,
                      userMassUnit = user.defaultWeightUnit,
                    )
                  },
                )
              }
            }
        }
      }
    },
  )
}

@Composable
private fun SectionTitle(title: String) {
  Text(
    text = title,
    fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier.padding(horizontal = 16.dp),
  )
}

@Composable
private fun ActivityContent(navigateToHistoryScreen: () -> Unit, content: @Composable () -> Unit) {
  // TODO if (!workouts.isEmpty()){} else {}
  Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.Start) {
    TextButton(onClick = { navigateToHistoryScreen }) { Text("View All Workouts") }
  }
  content()
}

@Composable
private fun StatsContent(
  navigateToAnalyticsScreen: () -> Unit,
  weeklyReportCard: @Composable () -> Unit = {},
  lifetimeStatsCard: @Composable () -> Unit = {},
) {
  Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.Start) {
    TextButton(onClick = { navigateToAnalyticsScreen() }) { Text("View All Stats") }
  }
  Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
    weeklyReportCard()
    lifetimeStatsCard()
  }
}
