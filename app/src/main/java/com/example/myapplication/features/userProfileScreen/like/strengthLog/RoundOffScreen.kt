@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import kotlinx.coroutines.launch

enum class RoundingOption(val displayText: String, val imperial: Double, val metric: Double?) {
  ROUND_TO_NEAREST_10_LB("Round to nearest 10 lb / 5 kg", 10.0, 5.0),
  ROUND_TO_NEAREST_5_LB("Round to nearest 5 lb / 2.5 kg", 5.0, 2.5),
  ROUND_TO_NEAREST_2_5_LB("Round to nearest 2.5 lb / 1.25 kg", 2.5, 1.25),
  ROUND_TO_NEAREST_2_LB("Round to nearest 2.0 lb", 2.0, null),
  ROUND_TO_NEAREST_1_5_LB("Round to nearest 1.5 lb", 1.5, null),
  ROUND_TO_NEAREST_1_LB("Round to nearest 1 lb / 0.5 kg", 1.0, 0.5),
  ROUND_TO_NEAREST_0_5_LB("Round to nearest 0.5 lb", 0.5, null);

  companion object {
    fun fromImperial(value: Double): RoundingOption? {
      return entries.find { it.imperial == value }
    }

    fun nearestMetric(ordinal: Int): Double {
      return when (ordinal) {
        0 -> 5.0
        1 -> 2.5
        2 -> 1.25
        3 -> 1.25
        4 -> 0.5
        5 -> 0.5
        6 -> 0.5
        else -> 0.5
      }
    }
  }
}

@Composable
fun RoundOffScreen(
  profileViewModel: ProfileViewModel,
  onNavigateBack: () -> Unit,
  bottomNavigationBar: @Composable () -> Unit,
) {
  val scope = rememberCoroutineScope()
  val radioOptions = RoundingOption.entries

  val currentUser by profileViewModel.userProfile.collectAsState()
//   Log.i("RoundOffScreen", "currentUser: $currentUser")

  val currentRoundingPreference by profileViewModel.getRoundingPreference().collectAsState(
    RoundingOption.fromImperial(currentUser.roundingPreferencePounds)
  )

  // Find the RoundingOption that matches the currentRoundingPreference
  val (selectedOption, onOptionSelected) = remember(currentRoundingPreference) {
    mutableStateOf(currentRoundingPreference ?: RoundingOption.ROUND_TO_NEAREST_5_LB)
  }
// 
  Log.i("RoundOffScreen", "selectedOption: $selectedOption lbs")

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Round Off") }, // As per screenshot
        navigationIcon = {
          IconButton(onClick = onNavigateBack) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Back"
            )
          }
        },
      )
    },
    bottomBar = { bottomNavigationBar() },
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
//      verticalArrangement = Arrangement.spacedBy(16.dp) // Padding for the button at the end
    ) {
      Row(modifier = Modifier.padding(16.dp)) {
        Text("Choose how calculated weights are rounded off.")
      }

      radioOptions.forEachIndexed { index, option ->
//        Log.i("RoundOffScreen", "option $index: $option")
        Row(
          modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .selectable(
              selected = (option == selectedOption),
              onClick = {
                onOptionSelected(option) // Update local state for immediate UI feedback
                scope.launch {
                  profileViewModel.updateRoundingPreference(option)
                }
              },
              role = Role.RadioButton
            ),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RadioButton(
            selected = (option == selectedOption),
            onClick = null // null recommended for accessibility with screen readers
          )
          Text(
            text = option.displayText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
          )
        }
      }
    }
  }
}