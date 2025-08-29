@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.features.userProfileScreen.ProfileViewModel

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
//  MyApplicationTheme { EditProfileScreen() }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun EditProfileScreenPreviewDarkMode() {
//  MyApplicationTheme { EditProfileScreen() }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
  profileViewModel: ProfileViewModel,
  onNavigateBack: () -> Unit = {},
  onSaveChanges: (name: String, username: String, email: String, password: String) -> Unit =
    { _, _, _, _ ->
    },
) {

  val pickMedia =
    rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
      // Callback is invoked after the user selects a media item or closes the
      // photo picker.
      if (uri != null) {
        Log.d("PhotoPicker", "Selected URI: $uri")
        // TODO save the selected image URI or image bitmap to app data
        //   Potentially: profileViewModel.updateProfilePicture(uri)
      } else {
        Log.d("PhotoPicker", "No media selected")
      }
    }

  fun selectImage() {
    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
  }

  val userProfileState by profileViewModel.userProfile.collectAsState()

  var name by remember { mutableStateOf("") }
  var username by remember { mutableStateOf("") }
  var email by remember { mutableStateOf("") }
  var password by remember { mutableStateOf("") } // For entering new password or changing

  LaunchedEffect(userProfileState) {
    userProfileState.let { profile ->
      name = profile.name ?: ""
      username = profile.username ?: ""
      email = profile.email ?: ""
      // Password field is for entry, so we don't prefill it from the profile's current password
      // password = profile.password ?: "" // Typically, do not pre-fill password
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Edit Profile") },
        navigationIcon = {},
        actions = {
          TextButton(
            onClick = {
              onSaveChanges(name, username, email, password)
              onNavigateBack()
              // TODO: Add navigation back or success message (e.g., using a Snackbar)
            }
          ) {
            Text("Save")
          }
        },
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .padding(padding)
        .padding(16.dp)
        .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Box {
          // TODO: Load actual profile picture, e.g., from userProfileState?.profilePictureUri
          if (true) {
            Image(
              painterResource(R.drawable.guy_fawkes_mask), // Placeholder
              modifier = Modifier
                .padding(8.dp)
                .size(200.dp)
                .clip(shape = CircleShape),
              contentDescription = "profile picture",
            )
          } else {
            Icon(
              imageVector = Icons.Filled.AccountCircle,
              tint = MaterialTheme.colorScheme.outlineVariant,
              modifier =
                Modifier
                  //                .padding(8.dp)
                  .size(200.dp)
                  .clip(shape = CircleShape),
              //                .background(color = MaterialTheme.colorScheme.outlineVariant)
              contentDescription = "profile picture",
            )
          }
          IconButton(
            onClick = { selectImage() },
            modifier =
              Modifier
                .align(Alignment.BottomEnd)
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
          ) {
            Icon(
              imageVector =
                if (true) {
                  Icons.Filled.Edit
                } else {
                  Icons.Filled.AddPhotoAlternate
                },
              contentDescription = "Edit profile picture",
              tint = MaterialTheme.colorScheme.onPrimary,
            )
          }
        }
      }
      Row {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Name") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
      Row {
        OutlinedTextField(
          value = username,
          onValueChange = { username = it },
          label = { Text("Username") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
      Row {
        OutlinedTextField(
          value = email,
          onValueChange = { email = it },
          label = { Text("Email") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
      Row {
        OutlinedTextField(
          value = password,
          onValueChange = { password = it },
          label = { Text("New Password") },
          modifier = Modifier.fillMaxWidth(),
        )
      }
    }
  }
}
