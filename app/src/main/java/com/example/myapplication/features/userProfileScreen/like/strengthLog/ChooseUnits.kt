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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import kotlinx.coroutines.launch

@Preview
@Composable
private fun Preview() {
//  ChooseUnitsScreen(onNavigateBack = {}, bottomNavigationBar = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseUnitsScreen(
  profileViewModel: ProfileViewModel,
  onNavigateBack: () -> Unit,
  bottomNavigationBar: @Composable () -> Unit,
) {
  val scope = rememberCoroutineScope()

  val currentUser by profileViewModel.userProfile.collectAsState()
//   Log.i("ChooseUnitsScreen", "currentUser: $currentUser")

  val radioOptions = MassUnit.entries.sortedBy { it.displayText }
  val (selectedOption, onOptionSelected) = remember { mutableStateOf(currentUser.defaultWeightUnit) }
// 
  Log.i("ChooseUnitsScreen", "selectedOption: $selectedOption")

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Choose Units") }, // As per screenshot
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
        Text("This will affect units everywhere applicable.")
      }
      radioOptions.forEach { units ->
        Row(
          modifier = Modifier
            .padding(16.dp)
            .selectable(
              selected = (units == selectedOption),
              onClick = {
                onOptionSelected(units)
                scope.launch {
                  profileViewModel.updateDefaultWeightUnit(units)
                }
              },
              role = Role.RadioButton,
            ),
          verticalAlignment = Alignment.CenterVertically
        ) {
          RadioButton(
            selected = (units == selectedOption),
            onClick = null // null recommended for accessibility with screen readers
          )
          Text(
            text = units.displayText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
          )
        }
      }
    }
  }
}
