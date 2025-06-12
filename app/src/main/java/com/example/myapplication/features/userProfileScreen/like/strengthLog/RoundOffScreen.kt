@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.content.Context
import android.content.res.Configuration
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.infrastructure.persistence.UserRepository
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.features.userProfileScreen.ProfileViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

// Mock UserRepository for Preview
private class MockUserRepository : UserRepository(context = null) {
    // Implement methods if they are called by ProfileViewModel constructor or during preview
    // For this case, ProfileViewModel doesn't seem to call them in constructor.
}

// Fake ViewModel for Preview
private class FakeProfileViewModel(context: Context) : ProfileViewModel(
    userRepository = MockUserRepository(), // Use the mock UserRepository
    context = context
) {
    override fun getRoundingPreference(): Flow<String> = flowOf("Round to nearest 2.0 lb") // Default for preview
    override suspend fun updateRoundingPreference(preference: String) {
        // No-op for preview
        println("Preview: Preference updated to $preference")
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RoundOffScreenPreview() {
    MyApplicationTheme {
        // Use LocalContext.current for the context in preview
        val context = LocalContext.current
        RoundOffScreen(
            onNavigateBack = {},
            bottomNavigationBar = {},
            profileViewModel = FakeProfileViewModel(context)
        )
    }
}

@Composable
fun RoundOffScreen(
    onNavigateBack: () -> Unit,
    bottomNavigationBar: @Composable () -> Unit,
    profileViewModel: ProfileViewModel,
) {
    val scope = rememberCoroutineScope()
    //TODO add KG equivalents
    val radioOptions =
        listOf(
            "Round to nearest 5.0 lb",
            "Round to nearest 2.0 lb",
            "Round to nearest 1.0 lb",
            "Round to nearest 0.5 lb",
        )

    val currentRoundingPreference by profileViewModel.getRoundingPreference()
        .collectAsState(initial = "Round to nearest 5.0 lb")
    // Ensure selectedOption is correctly initialized and updated
    val (selectedOption, onOptionSelected) = remember(currentRoundingPreference) {
        mutableStateOf(currentRoundingPreference)
    }


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

            radioOptions.forEach { text ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .selectable(
                            selected = (text == selectedOption),
                            onClick = {
                                onOptionSelected(text) // Update local state for immediate UI feedback
                                scope.launch {
                                    profileViewModel.updateRoundingPreference(text)
                                }
                            },
                            role = Role.RadioButton
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (text == selectedOption),
                        onClick = null // null recommended for accessibility with screen readers
                    )
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}