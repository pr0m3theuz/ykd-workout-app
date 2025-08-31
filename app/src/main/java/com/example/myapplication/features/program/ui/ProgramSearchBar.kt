@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.example.myapplication.features.program.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.ui.component.AuthorPartialBottomSheet
import com.example.myapplication.common.ui.component.LevelsPartialBottomSheet
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun ProgramSearchBar(
  modifier: Modifier = Modifier,
  query: String,
  onQueryChange: (String) -> Unit,
  onSearch: (String) -> Unit,
  authors: Map<String, Boolean>,
  levels: Map<String, Boolean>,
  searchResults: List<Program>,
  onResultClick: (Program) -> Unit,
  onAuthorSelected: (String, Boolean) -> Unit = { _, _ -> },
  onLevelSelected: (String, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit = {},
  // Customization options
  placeholder: @Composable () -> Unit = { Text("Search") },
  leadingIcon: @Composable (() -> Unit)? = {
    Icon(Icons.Default.Search, contentDescription = "Search")
  },
  trailingIcon: @Composable (() -> Unit)? = null,
  supportingContent: (@Composable (String) -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
) {
  // Track expanded state of search bar
  var expanded by rememberSaveable { mutableStateOf(true) }

  var showAuthorsBottomSheet by remember { mutableStateOf(false) }
  var showLevelsBottomSheet by remember { mutableStateOf(false) }

  val authorsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val levelsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

  Box(
    modifier
      .fillMaxWidth()
      .wrapContentHeight(Alignment.Top)
      .semantics { isTraversalGroup = true }
  ) {
    SearchBar(
      modifier = Modifier
        .align(Alignment.TopCenter)
        .semantics { traversalIndex = 0f },
      inputField = {
        // Customizable input field implementation
        SearchBarDefaults.InputField(
          query = query,
          onQueryChange = onQueryChange,
          onSearch = {
            onSearch(query)
            expanded = false
          },
          expanded = true,
          onExpandedChange = {
            expanded = it
            onDismissRequest
          },
          placeholder = placeholder,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
        )
      },
      expanded = true,
      onExpandedChange = {
        expanded = it
        onDismissRequest()
      },
    ) {
      // Show search results in a lazy column for better performance
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        FilterChip(
          selected = authors.values.any { it },
          onClick = {
            showAuthorsBottomSheet = true
          },
          label = { Text("Author") },
        )
        FilterChip(
          selected = levels.values.any { it },
          onClick = {
            showLevelsBottomSheet = true
          },
          label = { Text("Levels") },
        )
      }

      LazyColumn {
        items(count = searchResults.size) { index ->
          val program = searchResults[index]
          program.name?.let { programName ->
            ListItem(
              headlineContent = { Text(programName) },
              supportingContent = {
                program.author?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
              },
              leadingContent = leadingContent,
              colors = ListItemDefaults.colors(containerColor = Color.Transparent),
              modifier =
                Modifier
                  .clickable {
                    onResultClick(program)
                    expanded = false
                  }
                  .fillMaxWidth()
                  .padding(horizontal = 16.dp, vertical = 4.dp),
            )
            HorizontalDivider()
          }
        }
      }

      Log.d("ProgramSearchBar", "showAuthorsBottomSheet: $showAuthorsBottomSheet")
      if (showAuthorsBottomSheet) {
        AuthorPartialBottomSheet(
          showBottomSheet = showAuthorsBottomSheet,
          authorsMap = authors,
          sheetState = authorsSheetState,
          onAuthorSelected = { author, selected -> onAuthorSelected(author, selected) },
          onDismissRequest = {
            showAuthorsBottomSheet = false
          },
        )
      }

      Log.d("ProgramSearchBar", "showLevelsBottomSheet: $showLevelsBottomSheet")
      if (showLevelsBottomSheet) {
        LevelsPartialBottomSheet(
          showBottomSheet = showLevelsBottomSheet,
          levelsMap = levels,
          sheetState = levelsSheetState,
          onLevelSelected = { level, checked -> onLevelSelected(level, checked) },
          onDismissRequest = {
            showLevelsBottomSheet = false
          },
        )
      }
    }
  }
}
