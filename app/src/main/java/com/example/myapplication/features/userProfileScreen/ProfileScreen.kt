package com.example.myapplication.features.userProfileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.UserProfileManager
import com.example.myapplication.common.ui.component.BottomNavigationBar
import org.koin.androidx.compose.koinViewModel
import org.koin.java.KoinJavaComponent.inject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  navController: NavController,
  navigateToReplicatorScreen: () -> Unit,
  profileViewModel: ProfileViewModel = koinViewModel(),
) {

  // TODO REWRITE THIS
//  val user = profileViewModel.currentUser
//  val userEmail = user?.profileAsBsonDocument()!!["email"]?.asString()?.value ?: "anon@anon.anon"

//  val user = "Anonymous"
  val userProfileManager by inject<UserProfileManager>(UserProfileManager::class.java)
  val user = userProfileManager.userProfile.collectAsState().value
//  val userEmail = "TEMP@anon.anon"

  Scaffold(
    modifier = Modifier.imePadding(),
    topBar = {
      TopAppBar(
        title = { Text(text = "Profile") },
        actions = {
          IconButton(onClick = { navController.navigate("replicatorScreen") }) {
            Icon(imageVector = Icons.Default.Settings, contentDescription = null)
          }
        },
      )
    },
    bottomBar = { BottomNavigationBar(navController = navController) },
    content = { paddingValues ->
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
//          .padding(horizontal = 16.dp)
      ) {
        item {
          Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
            Image(
              painterResource(R.drawable.guy_fawkes_mask),
              modifier = Modifier
                .padding(8.dp)
                .size(48.dp)
                .clip(shape = CircleShape),
              contentDescription = "profile picture"
            )
//          Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null)
            Text(
              text = user.name ?: "MISSING",
              fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
          }
        }

        item {
          Column(
            Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            Text(text = "Profile")
            Text(text = user.id.toHexString() ?: "MISSING")
          }
          HorizontalDivider()
        }

        item {
          Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            SectionTitle("Units")
            ListItem(
              headlineContent = { Text(text = "Weight Unit") },
              supportingContent = { Text(text = "Kilograms") },
            )
          }
          HorizontalDivider()
        }

        item {
          Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
          ) {
            SectionTitle("General")
            ListItem(
              headlineContent = { Text(text = "Language") },
              supportingContent = { Text(text = "English") })
          }
          HorizontalDivider()
        }

        item {
          Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            SectionTitle("Rest Timer")
            ListItem(
              headlineContent = {
                Text(text = "Default Values") // TODO onClick open Dialog
              },
            )
            ListItem(
              headlineContent = { Text("Auto start timer") } // TODO add switch
            )

            ListItem(
              headlineContent = {
                Text(text = "Vibrate on completion") // TODO add switch
              },
            )
            ListItem(
              headlineContent = {
                Text(text = "Sound on completion") // TODO add switch
              },
            )
            // TODO this not a feature
            ListItem(
              headlineContent = { Text(text = "Update Rest Timer for all templates") } // TODO onClick open dialog
            )
          }
          HorizontalDivider()
        }

        item {
          Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            SectionTitle("Advanced")
            // TODO add switch
            ListItem(
              headlineContent = { Text(text = "Update Template") },
              supportingContent = { Text(text = "TODO") },
            )
            // TODO implement formula/feature
            ListItem(
              headlineContent = { Text(text = "xRM Formula") },
              supportingContent = { Text(text = "TODO") },
            )
          }
          HorizontalDivider()
        }

        item {
          Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
//            verticalArrangement = Arrangement.spacedBy(16.dp)
          ) {
            SectionTitle("Display")
            ListItem(
              headlineContent = { Text(text = "Theme") },
              supportingContent = { Text(text = "System Default") },
            ) // TODO implement formula/feature

            ListItem(
              headlineContent = { Text(text = "Start of the Week") },
              supportingContent = { Text(text = "TODO Monday") },
            ) // TODO implement formula/feature
          }
          HorizontalDivider()
        }

        item {
          Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
          ) {
            SectionTitle("Sync")
            ListItem(headlineContent = { Text(text = "Sync Settings") },
              supportingContent = { Text(text = "Sync Server Settings") },
              modifier = Modifier.clickable(
                onClickLabel = "Sync Server Settings",
                onClick = { navigateToReplicatorScreen() }
              ))
          }
          HorizontalDivider()
        }

        item {
          Row(
            Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "v0.0.1-alpha",
              fontStyle = MaterialTheme.typography.titleSmall.fontStyle,
              color = MaterialTheme.colorScheme.outline
            )
          }
        }
      }
    },
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


@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
//  SettingsComponent()
}

@Composable
fun ThemeDialog() {
  // TODO https://developer.android.com/develop/ui/views/theming/darktheme
  Dialog(onDismissRequest = { }) {
    Card() {
      Column() {
        Text(text = "Choose Theme", fontStyle = MaterialTheme.typography.displayMedium.fontStyle)
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("Light")
        }
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("Dark")
        }
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("System Default")
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
          onClick = {},
          modifier = Modifier.align(Alignment.End)
        ) {
          Text("Cancel")
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun WeightUnitDialog() {
  // TODO Update User Settings
  Dialog(onDismissRequest = { }) {
    Card() {
      Column() {
        Text(text = "Weight", fontStyle = MaterialTheme.typography.displayMedium.fontStyle)
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("Kilograms")
        }
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("Pounds")
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
          onClick = {},
          modifier = Modifier.align(Alignment.End)
        ) {
          Text("Cancel")
        }
      }
    }
  }
}


@Composable
fun StartOfTheWeekDialog() {
  // TODO Update User Settings
  Dialog(onDismissRequest = { }) {
    Card() {
      Column() {
        Text(
          text = "Start of the Week",
          fontStyle = MaterialTheme.typography.displayMedium.fontStyle
        )
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("Saturday")
        }
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("Sunday")
        }
        Row {
          RadioButton(
            selected = TODO(),
            onClick = TODO(),
            modifier = TODO(),
            enabled = TODO(),
            colors = TODO(),
            interactionSource = TODO()
          )
          Text("Monday")
        }
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(
          onClick = {},
          modifier = Modifier.align(Alignment.End)
        ) {
          Text("Cancel")
        }
      }
    }
  }
}