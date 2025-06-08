@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.userProfileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.features.history.ui.HistoryLazyColumn
import com.example.myapplication.features.userProfileScreen.like.boostcamp.LifetimeStatsCard
import com.example.myapplication.features.userProfileScreen.like.boostcamp.WeeklyReportCard
import org.bson.types.ObjectId

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
  MyApplicationTheme {
    ProfileScreenV2(
      navigateToReplicatorScreen = {},
      bottomNavigationBar = {},
      navigateToAnalyticsScreen = {},
      navigateToSettingsScreen = {},
      navigateToHistoryScreen = {},
      navigateToWorkoutOverview = {}
    )
  }
}

@Composable
fun ProfileScreenV2(
//  profileViewModel: ProfileViewModel = koinViewModel(),
  bottomNavigationBar: @Composable () -> Unit,
  navigateToReplicatorScreen: () -> Unit,
  navigateToAnalyticsScreen: () -> Unit,
  navigateToSettingsScreen: () -> Unit,
  navigateToHistoryScreen: () -> Unit,
  navigateToWorkoutOverview: (workout: Workout) -> Unit,
) {

  // TODO REWRITE THIS
//  val user = profileViewModel.currentUser
//  val userEmail = user?.profileAsBsonDocument()!!["email"]?.asString()?.value ?: "anon@anon.anon"

//  val user = "Anonymous"
//  val userProfileManager by inject<UserProfileManager>(UserProfileManager::class.java)
//  val user = userProfileManager.userProfile.collectAsState().value
//  val userEmail = "TEMP@anon.anon"

  val user = UserProfile(ObjectId())

  Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
      TopAppBar(
        title = { Text(text = "Profile") },
        actions = {
          IconButton(onClick = {
            navigateToSettingsScreen()
          }) {
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
        verticalArrangement = Arrangement.spacedBy(0.dp)
      ) {
        Column(
          Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
        )
        {
          Image(
            painterResource(R.drawable.guy_fawkes_mask),
            modifier = Modifier
              .padding(8.dp)
              .size(200.dp)
              .clip(shape = CircleShape),
            contentDescription = "profile picture"
          )
          Text(
            text = user.name ?: "MISSING",
            fontSize = MaterialTheme.typography.titleLarge.fontSize
          )
        }

        // Tab Layout
        // Tab Layout
        var selectedTabIndex by remember { mutableStateOf(0) }
        val tabs = listOf("Activity", "Stats")

        SecondaryTabRow(selectedTabIndex)
        {
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

        when (selectedTabIndex) {
          0 -> ActivityContent(
            navigateToHistoryScreen = {
              navigateToHistoryScreen()
            },
            content = {
              HistoryLazyColumn(
//    modifier = Modifier.padding(horizontal = 16.dp),
                workouts = emptyList(),
                getExerciseByExerciseID = { exerciseID -> null },
                navigateToWorkoutOverview = {
                  navigateToWorkoutOverview(it)
                }
              )
            }
          )

          1 -> StatsContent(
            navigateToAnalyticsScreen = {
              navigateToAnalyticsScreen()
            }
          )
        }
      }
    }
  )
}

@Composable
private fun SectionTitle(title: String) {
  Text(
    text = title, fontStyle = MaterialTheme.typography.bodySmall.fontStyle,
    color = MaterialTheme.colorScheme.primary,
    modifier = Modifier.padding(horizontal = 16.dp)
  )
}

@Composable
private fun ActivityContent(
  navigateToHistoryScreen: () -> Unit,
  content: @Composable () -> Unit
) {
  // TODO if (!workouts.isEmpty()){} else {}
  Row(
    modifier = Modifier.padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.Start
  ) {
    TextButton(onClick = { navigateToHistoryScreen }) { Text("View All Workouts") }
  }
  content()
}

@Composable
private fun StatsContent(
  navigateToAnalyticsScreen: () -> Unit,
) {
  Row(
    modifier = Modifier.padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.Start
  )
  {
    TextButton(onClick = { navigateToAnalyticsScreen }) { Text("View All Stats") }
  }
  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    WeeklyReportCard()
    LifetimeStatsCard()
  }

}



