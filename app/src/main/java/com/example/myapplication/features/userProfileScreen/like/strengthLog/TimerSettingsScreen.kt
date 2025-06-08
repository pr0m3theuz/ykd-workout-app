package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.common.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerSettingsScreen(
  onNavigateBack: () -> Unit,
  onNavigate: (String) -> Unit = {}, // For general navigation like "Notification Sound"
  onOpenTimePicker: () -> Unit = {},
  bottomNavigationBar: @Composable () -> Unit,
) {
  // State for switches
  var activateSetTimerEnabled by remember { mutableStateOf(true) }
  var restTimeDisplay by remember { mutableStateOf("02:00") } // Example time
  var startAutomaticallyEnabled by remember { mutableStateOf(true) }
  var countUpEnabled by remember { mutableStateOf(false) }
  var showProgressBarEnabled by remember { mutableStateOf(true) }
  val notificationSoundSelection by remember { mutableStateOf("System sound") }


  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Timer Settings") }, // As per screenshot
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
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(bottom = 16.dp) // Padding for the button at the end
    ) {
      item {
        TimerSwitchItem(
          title = "Activate set timer",
          checked = activateSetTimerEnabled,
          onCheckedChange = { activateSetTimerEnabled = it }
        )
      }

      item {
        TimerRestItem(
          title = "Rest",
          subtitle = "The time you would like to rest between sets.",
          time = restTimeDisplay,
          onClick = onOpenTimePicker // To open a time picker dialog
        )
      }

      item {
        TimerSwitchItem(
          title = "Start automatically",
          subtitle = "Start the timer automatically when you check a set.",
          checked = startAutomaticallyEnabled,
          onCheckedChange = { startAutomaticallyEnabled = it }
        )
      }

      item {
        TimerSwitchItem(
          title = "Count up",
          subtitle = "The timer will count up until you stop it or check a set.",
          checked = countUpEnabled,
          onCheckedChange = { countUpEnabled = it }
        )
      }

      item {
        TimerNavigationItem(
          title = "Notification sound",
          subtitle = notificationSoundSelection,
          onClick = { onNavigate("notification_sound_settings") }
        )
      }

//      item {
//        TimerInfoItem(
//          title = "About sound notifications",
//          content = "When you have your phone in silent mode, you won't receive sound notifications."
//        )
//      }

//      item {
//        TimerSwitchItem(
//          title = "Show progress bar",
//          subtitle = "The graphic fills up as your rest timer counts down.",
//          checked = showProgressBarEnabled,
//          onCheckedChange = { showProgressBarEnabled = it }
//        )
//      }
      /*
            item {
              TimerInfoItem(
                title = "Resting time for different exercises",
                content = "Set your preferred resting time for different exercises.",
                isHeaderStyle = true // Make it look more like a header for the button
              )
            }

            item {
              Button(
                onClick = onAddExercise,
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 24.dp, vertical = 24.dp),
              ) {
                Text("Add exercise", fontSize = 16.sp)
              }
            }*/
    }
  }
}

// Reusable composable for generic timer items (container)
@Composable
private fun TimerItemContainer(
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null,
  content: @Composable RowScope.() -> Unit
) {
  val itemModifier = modifier
    .fillMaxWidth()
    .clip(MaterialTheme.shapes.extraLarge)
//    .padding(horizontal = 16.dp, vertical = 16.dp)

  if (onClick != null) {
    Row(
      modifier = itemModifier.clickable(onClick = onClick),
      verticalAlignment = Alignment.CenterVertically,
      content = content
    )
  } else {
    Row(
      modifier = itemModifier,
      verticalAlignment = Alignment.CenterVertically,
      content = content
    )
  }
}

@Composable
private fun TimerSwitchItem(
  title: String,
  subtitle: String? = null,
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  TimerItemContainer(onClick = { onCheckedChange(!checked) }) {
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(24.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
      )
      subtitle?.let {
        Text(
          text = it,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp // Ensure readability for multi-line subtitles
        )
      }
    }
    Spacer(modifier = Modifier.width(16.dp))
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
    )
    Spacer(modifier = Modifier.width(24.dp))
  }
}

@Composable
private fun TimerRestItem(
  title: String,
  subtitle: String,
  time: String,
  onClick: () -> Unit
) {
  TimerItemContainer(onClick = onClick) {
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(24.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 18.sp
      )
    }
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = time,
      style = MaterialTheme.typography.bodyLarge,
      color = MaterialTheme.colorScheme.primary // Highlight the time
    )
    Spacer(modifier = Modifier.width(24.dp))
  }
}

@Composable
private fun TimerNavigationItem(
  title: String,
  subtitle: String? = null,
  onClick: () -> Unit
) {
  TimerItemContainer(onClick = onClick) {
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(24.dp)
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
      )
      subtitle?.let {
        Text(
          text = it,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    Spacer(modifier = Modifier.width(16.dp))
    Icon(
      imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
      contentDescription = "Navigate",
      modifier = Modifier.size(20.dp),
    )
    Spacer(modifier = Modifier.width(24.dp))
  }
}

@Composable
private fun TimerInfoItem(
  title: String,
  content: String,
  isHeaderStyle: Boolean = false // To optionally style it more like a sub-header
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(
        if (isHeaderStyle) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(
          alpha = 0.15f // Slightly different background for info blocks if not header
        )
      )
      .padding(horizontal = 16.dp, vertical = 12.dp)
  ) {
    Text(
      text = title,
      style = if (isHeaderStyle) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
      fontWeight = if (isHeaderStyle) FontWeight.Bold else FontWeight.Normal,
      color = if (isHeaderStyle) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(if (isHeaderStyle) 4.dp else 2.dp))
    Text(
      text = content,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      lineHeight = 18.sp
    )
  }
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun TimerSettingsScreenPreview() {
  MyApplicationTheme {
    TimerSettingsScreen(onNavigateBack = {}, bottomNavigationBar = {})
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun TimerSettingsScreenLightPreview() {
  MyApplicationTheme {
    TimerSettingsScreen(onNavigateBack = {}, bottomNavigationBar = {})
  }
}
