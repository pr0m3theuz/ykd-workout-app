package com.example.myapplication.features.userProfileScreen.like.strengthLog

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.ui.theme.MyApplicationTheme

// Data class for each calculator item
data class CalculatorItemData(
  val id: String,
  val title: String,
  val icon: ImageVector? = null // Optional icon
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorsScreen(
  onNavigateBack: () -> Unit,
  onCalculatorSelected: (String) -> Unit, // Passes calculator ID
  onShowHelp: (String) -> Unit, // Passes calculator ID for help
  bottomNavigationBar: @Composable () -> Unit,
) {
  var selectedBottomNavItem by remember { mutableIntStateOf(4) } // Assuming settings/calculators is 5th item

  val calculatorItems = listOf(
    CalculatorItemData(
      "1rm",
      "1RM/RPE/RIR calculator",
      Icons.Filled.FitnessCenter
    ), // Placeholder icon
    CalculatorItemData("plate", "Plate calculator", Icons.Filled.ChangeCircle), // Placeholder icon
    CalculatorItemData("warmup", "Warm-up for max attempt", Icons.AutoMirrored.Filled.TrendingUp),
//    CalculatorItemData("attempt_selection", "Attempt selection for powerlifting meets", Icons.Filled.MoreHoriz),
//    CalculatorItemData("ipf_wilks", "IPF/Wilks calculator", Icons.Filled.EmojiEvents), // Placeholder icon
//    CalculatorItemData("sinclair", "Sinclair calculator", Icons.Filled.FormatListNumbered), // Placeholder icon
//    CalculatorItemData("calories", "Daily caloric expense", Icons.Filled.LocalFireDepartment), // Placeholder icon
//    CalculatorItemData("protein", "Protein calculator", Icons.Filled.BubbleChart), // Placeholder icon
//    CalculatorItemData("bodyfat", "Body fat percentage & fat-free mass", Icons.Filled.AccessibilityNew)
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Calculators", fontWeight = FontWeight.Bold) },
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
    bottomBar = { bottomNavigationBar },
    containerColor = MaterialTheme.colorScheme.surface // Background for the LazyColumn area
  ) { paddingValues ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues),
      contentPadding = PaddingValues(vertical = 8.dp) // Add some padding around the list
    ) {
      items(calculatorItems) { calcItem ->
        CalculatorListItem(
          itemData = calcItem,
          onClick = { onCalculatorSelected(calcItem.id) },
          onHelpClick = { onShowHelp(calcItem.id) }
        )
        // No explicit Divider, relying on item background and padding for separation
      }
    }
  }
}

@Composable
private fun CalculatorListItem(
  itemData: CalculatorItemData,
  onClick: () -> Unit,
  onHelpClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(MaterialTheme.shapes.extraLarge)
      .clickable(onClick = onClick)
      .padding(24.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (itemData.icon != null) {
      Icon(
        imageVector = itemData.icon,
        contentDescription = null, // Decorative
        modifier = Modifier.size(24.dp),
        tint = MaterialTheme.colorScheme.primary // Icon color
      )
      Spacer(modifier = Modifier.width(16.dp))
    } else {
      // If no icon, add padding to align text with items that do have icons
      Spacer(modifier = Modifier.width(24.dp + 16.dp))
    }

    Text(
      text = itemData.title,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f)
    )

    Spacer(modifier = Modifier.width(8.dp))

    IconButton(
      onClick = onHelpClick,
      modifier = Modifier.size(36.dp) // Slightly larger touch target for icon button
    ) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
        contentDescription = "Help for ${itemData.title}",
        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), // Subtler help icon
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun CalculatorsScreenPreview() {
  MyApplicationTheme {
    CalculatorsScreen(
      onNavigateBack = {},
      onCalculatorSelected = { id -> println("Selected: $id") },
      onShowHelp = { id -> println("Help for: $id") },
      bottomNavigationBar = {}
    )
  }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun CalculatorsScreenLightPreview() {
  MyApplicationTheme {
    CalculatorsScreen(
      onNavigateBack = {},
      onCalculatorSelected = { id -> println("Selected: $id") },
      onShowHelp = { id -> println("Help for: $id") },
      bottomNavigationBar = {}
    )
  }
}
