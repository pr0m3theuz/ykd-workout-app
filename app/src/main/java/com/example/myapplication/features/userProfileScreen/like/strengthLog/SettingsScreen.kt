package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.ImportExport
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import org.koin.androidx.compose.koinViewModel

// Placeholder for your R.drawable.ic_flag_uk or similar
// import com.example.settingsScreen.R // If you have local drawables

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  profileViewModel: ProfileViewModel,
  onNavigate: (String) -> Unit = {},
  navigateToReplicatorScreen: () -> Unit = {},
  bottomNavigationBar: @Composable () -> Unit,
) {
  val user by profileViewModel.userProfile.collectAsState()

  // State for switches
  //  var darkModeEnabled by remember { mutableStateOf(true) }
  var shareDataEnabled by remember { mutableStateOf(false) }
  var selectedBottomNavItem by remember {
    mutableIntStateOf(4)
  } // Assuming settings is the 5th item (index 4)

  // New state variables for workout settings switches
  var showSummaryEnabled by remember { mutableStateOf(false) }
  var plateCalculatorKeyboardEnabled by remember {
    mutableStateOf(true)
  } // Defaulted based on screenshot
  var showCalorieCalcEnabled by remember { mutableStateOf(true) } // Defaulted based on screenshot
  var dontLockScreenEnabled by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(title = { Text("Settings", style = MaterialTheme.typography.titleLarge) })
    },
    bottomBar = { bottomNavigationBar() },
    //    containerColor = MaterialTheme.colorScheme.surface // Background for the LazyColumn area
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
      //        .background(MaterialTheme.colorScheme.surface) // Dark background
    ) {
      // Your account Section
      item { SettingsSectionTitle("Your account") }
      /*      item {
        SettingsIconItem(
          icon = Icons.Filled.Language,
          title = "Language",
          subtitle = "Tap to change",
          onClick = { onNavigate("language_settings") },
          trailingContent = {
            // Placeholder for flag icon - replace with your actual flag image
            // Image(painter = painterResource(id = R.drawable.ic_flag_uk), contentDescription = "UK Flag", modifier = Modifier.size(24.dp))
            Box(modifier = Modifier
              .size(24.dp)
              .background(Color.Gray, CircleShape)) // Placeholder
          }
        )
      }*/
      /*      item {
        SettingsSwitchItem(
          icon = Icons.Filled.BrightnessMedium, // Or DarkMode / LightMode
          title = "Dark mode",
          checked = darkModeEnabled,
          onCheckedChange = { darkModeEnabled = it }
        )
      }*/
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.PersonOutline,
          title = "Profile",
          onClick = { onNavigate("profileSettings") },
        )
      }

      /* item {
        SettingsNavigationItem(
          icon = Icons.Filled.ManageAccounts,
          title = "Account settings",
          onClick = { onNavigate("accountSettings") }
        )
      }*/

      // Premium Section
      /*      item { SettingsSectionTitle("Premium") }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.StarBorder,
          title = "Premium",
          subtitle = "Buy premium",
          onClick = { onNavigate("premium_buy") }
        )
      }
      item {
        SettingsNavigationItem(
          title = "Register purchase via wellness plan",
          onClick = { onNavigate("premium_wellness") }
        )
      }*/

      // Workout settings Section - Updated
      item { SettingsSectionTitle("Workout settings") }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.FitnessCenter, // Changed from Build for consistency with screenshot
          title = "Custom Exercises",
          subtitle = "Edit",
          onClick = { /* TODO onNavigate("exercisesSettings")*/ },
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.AccessibilityNew,
          title = "Muscle map",
          onClick = { /* TODO onNavigate("muscleMapSettings")*/ },
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.Timer,
          title = "Timer",
          onClick = { onNavigate("timerSettings") },
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.Straighten,
          title = "Choose units",
          subtitle = user.defaultWeightUnit.displayText,
          onClick = { onNavigate("unitsSettings") },
        )
      }
      item { // New item
        SettingsNavigationItem(
          icon = Icons.Filled.Loop, // Icon for "Round off"
          title = "Round off",
          subtitle =
            if (user.defaultWeightUnit == MassUnit.POUNDS) {
              "${user.roundingPreferencePounds} lbs"
            } else {
              "${user.roundingPreferenceKilograms} kg"
            },
          onClick = { onNavigate("roundOffSettings") },
        )
      }
      item { // New item
        SettingsNavigationItem(
          icon =
            ImageVector.vectorResource(
              R.drawable.weight_fill1_wght400_grad0_opsz24
            ), // Icon for "Round off"
          title = "Equipment Profiles",
          onClick = { onNavigate("equipmentProfileSettings") },
        )
      }
      /*      item { // New item
        SettingsSwitchItem(
          icon = Icons.Filled.BarChart, // Icon for "Show summary"
          title = "Show summary for each exercise block",
          subtitleAnnotated = buildAnnotatedString {
            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.tertiary)) { // Or your premium color
              append("Premium")
            }
          },
          checked = showSummaryEnabled,
          onCheckedChange = { showSummaryEnabled = it }
        )
      }*/
      /*      item { // New item
        SettingsSwitchItem(
          icon = Icons.Filled.Album, // Icon for "Plate calculator" (disc like)
          title = "Plate calculator",
          subtitle = "Show on keyboard",
          checked = plateCalculatorKeyboardEnabled,
          onCheckedChange = { plateCalculatorKeyboardEnabled = it }
        )
      }*/
      /*      item { // New item
        SettingsSwitchItem(
          // No icon in screenshot, so omitting it or using a generic one
          // icon = Icons.Filled.ToggleOn, (Example if an icon was desired)
          title = "Show calorie calculation",
          checked = showCalorieCalcEnabled,
          onCheckedChange = { showCalorieCalcEnabled = it }
        )
      }*/
      /*      item { // New item
        SettingsSwitchItem(
          icon = Icons.Filled.Smartphone, // Icon for "Don't lock screen"
          title = "Don't lock screen during active workout",
          checked = dontLockScreenEnabled,
          onCheckedChange = { dontLockScreenEnabled = it }
        )
      }*/

      // Tools Section - New
      item { SettingsSectionTitle("Tools") }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.Calculate,
          title = "Calculators",
          onClick = { onNavigate("calculatorsScreen") }, // Navigate to the CalculatorsScreen
        )
      }

      // Data sharing Section (from original settings)
      /*      item { SettingsSectionTitle("Data sharing") }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.Link, // Placeholder for Health Connect
          title = "Health Connect",
          subtitle = "Sync your data",
          onClick = { onNavigate("health_connect") }
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.CloudUpload,
          title = "Export your data",
          onClick = { onNavigate("export_data") }
        )
      }
      item {
        SettingsSwitchItem(
          icon = Icons.AutoMirrored.Filled.ReceiptLong, // Placeholder for document/sharing icon
          title = "Share my data anonymously",
          subtitle = "with scientific studies\nRead more",
          checked = shareDataEnabled,
          onCheckedChange = { shareDataEnabled = it },
          isMultilineSubtitle = true
        )
      }*/

      // Help & legal Section (from original settings)
      /*      item { SettingsSectionTitle("Help & legal") }
      item {
        SettingsIconItem(
          icon = Icons.Filled.RateReview, // Or ChatBubbleOutline
          title = "Review in Play Store",
          onClick = { onNavigate("review_playstore") */
      /* TODO: Open Play Store */
      /* },
          trailingIcon = Icons.AutoMirrored.Filled.OpenInNew
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.AutoMirrored.Filled.HelpOutline,
          title = "Feedback and support",
          onClick = { onNavigate("feedback_support") }
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.AutoMirrored.Filled.ListAlt,
          title = "Planned features",
          onClick = { onNavigate("planned_features") }
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.History,
          title = "Version history",
          onClick = { onNavigate("version_history") }
        )
      }
      item {
        SettingsIconItem(
          icon = Icons.Filled.Info,
          title = "Privacy policy",
          onClick = { onNavigate("privacy_policy") */
      /* TODO: Open link */
      /* },
          trailingIcon = Icons.AutoMirrored.Filled.OpenInNew
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.Gavel, // Or InfoOutline
          title = "Terms of Service",
          onClick = { onNavigate("terms_of_service") }
        )
      }
      item {
        SettingsNavigationItem(
          // No icon provided in screenshot for this one
          title = "Third-party software",
          onClick = { onNavigate("third_party_software") }
        )
      }*/

      item { SettingsSectionTitle("Sync") }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.SyncAlt,
          title = "Sync",
          onClick = { navigateToReplicatorScreen() }, // Navigate to the CalculatorsScreen
        )
      }
      item {
        SettingsNavigationItem(
          icon = Icons.Filled.ImportExport,
          title = "Import/Export Data",
          onClick = { onNavigate("importExportDataSettings") }, // Navigate to the CalculatorsScreen
        )
      }

      // Log out Button
      item { Spacer(modifier = Modifier.height(64.dp)) } // Extra space at the top
      item {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
          Button(
            onClick = { onNavigate("logout") /* TODO: Handle logout */ },
            modifier = Modifier
              .fillMaxWidth(0.66f)
              .padding(horizontal = 16.dp, vertical = 0.dp),
            colors =
              ButtonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer,
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer,
              ),
          ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Log out")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log out")
          }
        }
      }

      // Footer Information
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          SelectionContainer {
            Text(
              "Version: 2026.04.18-2bd64bdd", // Replace with actual app version
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          /*          Text(
            "Logged in with:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            "Anonymous", // Replace with actual user status
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(16.dp))
          // Placeholder for STRENGTHLOG logo
          Text(
            "STRENGTHLOG",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )

          Text(
            "app@strengthlog.com", // Replace with actual contact
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )*/
          Spacer(modifier = Modifier.height(24.dp)) // Extra space at the bottom
        }
      }
    }
  }
}

@Composable
private fun SettingsSectionTitle(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.titleSmall,
    //    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.primary, // Or onSurfaceVariant for less emphasis
    modifier =
      Modifier
        .fillMaxWidth()
        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 16.dp), // Adjusted padding
  )
}

@Composable
fun SettingsItemContainer(onClick: (() -> Unit)?, content: @Composable RowScope.() -> Unit) {
  val itemModifier = Modifier
    .fillMaxWidth()
    .clip(MaterialTheme.shapes.extraLarge)
  //    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
  //    .padding(horizontal = 16.dp, vertical = 24.dp)

  if (onClick != null) {
    Row(
      modifier = itemModifier.clickable(onClick = onClick),
      verticalAlignment = Alignment.CenterVertically,
      content = content,
    )
  } else {
    Row(modifier = itemModifier, verticalAlignment = Alignment.CenterVertically, content = content)
  }
}

@Composable
fun SettingsNavigationItem(
  icon: ImageVector? = null,
  title: String,
  subtitle: String? = null,
  onClick: () -> Unit,
) {
  SettingsIconItem(
    icon = icon,
    title = title,
    subtitle = subtitle,
    onClick = onClick,
    trailingIcon = Icons.AutoMirrored.Filled.ArrowForwardIos,
  )
}

@Composable
fun SettingsIconItem(
  icon: ImageVector? = null,
  title: String,
  subtitle: String? = null,
  subtitleAnnotated: AnnotatedString? = null, // For styled subtitles like "Premium"
  onClick: () -> Unit,
  trailingIcon: ImageVector? = null,
  trailingContent: (@Composable () -> Unit)? = null,
) {
  SettingsItemContainer(onClick = onClick) {
    Spacer(modifier = Modifier.width(24.dp))
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = title, // Content description for accessibility
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant, // Consistent icon tint
      )
      Spacer(modifier = Modifier.width(16.dp))
    } else {
      // Add padding to align text if no icon, ensuring consistent leading space
      Spacer(
        modifier = Modifier.width(16.dp + 24.dp)
      ) // was 24.dp + 16.dp, ensuring it's just the icon + padding
    }

    Column(modifier = Modifier
      .weight(1f)
      .padding(vertical = 16.dp)) {
      Text(text = title, style = MaterialTheme.typography.bodyLarge)
      subtitle?.let {
        Text(
          text = it,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
      subtitleAnnotated?.let {
        Text(
          text = it,
          style =
            MaterialTheme.typography.bodySmall, // Base style, specific styles in AnnotatedString
        )
      }
    }

    Spacer(modifier = Modifier.width(16.dp)) // Space before trailing icon/content

    if (trailingContent != null) {
      trailingContent()
    } else if (trailingIcon != null) {
      Icon(
        imageVector = trailingIcon,
        contentDescription = "Navigate", // Generic, consider making specific
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), // Subtler arrow
      )
    }
    Spacer(modifier = Modifier.width(24.dp))
  }
}

@Composable
fun SettingsSwitchItem(
  icon: ImageVector? = null,
  title: String,
  subtitle: String? = null,
  subtitleAnnotated: AnnotatedString? = null, // For styled subtitles
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  isMultilineSubtitle: Boolean = false,
) {
  SettingsItemContainer(onClick = { onCheckedChange(!checked) }) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = title, // Content description
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.onSurfaceVariant, // Consistent icon tint
      )
      Spacer(modifier = Modifier.width(16.dp))
    } else {
      // Add padding to align text if no icon
      Spacer(modifier = Modifier.width(16.dp + 24.dp))
    }

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
      subtitle?.let {
        Text(
          text = it,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = if (isMultilineSubtitle) 18.sp else TextUnit.Unspecified,
        )
      }
      subtitleAnnotated?.let {
        Text(
          text = it,
          style = MaterialTheme.typography.bodySmall, // Base style
          lineHeight = if (isMultilineSubtitle) 18.sp else TextUnit.Unspecified,
        )
      }
    }
    Spacer(modifier = Modifier.width(16.dp)) // Space before switch
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors =
        SwitchDefaults.colors(
          checkedThumbColor = MaterialTheme.colorScheme.primary,
          checkedTrackColor =
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f), // Slightly less opaque
          uncheckedThumbColor = MaterialTheme.colorScheme.outline,
          uncheckedTrackColor =
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), // Subtler track
        ),
    )
  }
}

// --- Preview ---
// Define a basic dark theme for preview purposes
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview_StrengthLog() {
  //  MaterialTheme(colorScheme = DarkColorSchemePreview) {
  //    SettingsScreen()
  //  }

  val profileViewModel = koinViewModel<ProfileViewModel>()

  MyApplicationTheme {
    SettingsScreen(
      profileViewModel = profileViewModel,
      onNavigate = {},
      navigateToReplicatorScreen = {},
      bottomNavigationBar = {})
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SettingsScreenLightPreview() {
  val profileViewModel = koinViewModel<ProfileViewModel>()

  MyApplicationTheme {
    SettingsScreen(
      profileViewModel,
      onNavigate = {},
      navigateToReplicatorScreen = {},
      bottomNavigationBar = {})
  }
}
