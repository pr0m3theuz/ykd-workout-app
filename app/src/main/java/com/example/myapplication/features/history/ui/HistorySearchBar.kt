@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.example.myapplication.features.history.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DateRangePickerState
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
import androidx.compose.material3.getSelectedEndDate
import androidx.compose.material3.getSelectedStartDate
import androidx.compose.material3.rememberDateRangePickerState
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
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.formatWorkoutDate
import com.example.myapplication.common.ui.component.AuthorPartialBottomSheet
import com.example.myapplication.common.ui.component.DateRangePickerDialog
import com.example.myapplication.common.ui.component.LevelsPartialBottomSheet
import com.example.myapplication.common.ui.component.ProgramPartialBottomSheet
import java.time.format.DateTimeFormatter
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun HistorySearchBar(
  modifier: Modifier = Modifier,
  dateRangePickerState: DateRangePickerState = rememberDateRangePickerState(),
  query: String,
  onQueryChange: (String) -> Unit,
  onSearch: (String) -> Unit,
  programs: Map<Program, Boolean>,
  authors: Map<String, Boolean>,
  levels: Map<String, Boolean>,
  searchResults: List<Workout>,
  onResultClick: (Workout) -> Unit,
  onProgramSelected: (Program, Boolean) -> Unit = { _, _ -> },
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

  var showDateBottomSheet by remember { mutableStateOf(false) }
  var showProgramsBottomSheet by remember { mutableStateOf(false) }
  var showAuthorsBottomSheet by remember { mutableStateOf(false) }
  var showLevelsBottomSheet by remember { mutableStateOf(false) }
  var showDateRangePickerDialog by remember { mutableStateOf(false) }

  val dateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val programsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val authorsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val levelsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

  // TODO Move to parent

  val dateFormatter =
    DateTimeFormatter.ofPattern("MM/dd/yy").withLocale(Locale.current.platformLocale)

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
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        val startDate = dateRangePickerState.getSelectedStartDate()
        val endDate = dateRangePickerState.getSelectedEndDate()

        item {
          FilterChip(
            selected = if (startDate != null && endDate != null) true else false,
            onClick = { showDateRangePickerDialog = true },
            label = {
              Text(
                text =
                  if (startDate != null && endDate != null) {
                    startDate.format(dateFormatter) + " - " + endDate.format(dateFormatter)
                  } else {
                    "Date"
                  }
              )
            },
          )
        }
        item {
          FilterChip(
            selected = programs.values.any { it },
            onClick = { showProgramsBottomSheet = true },
            label = { Text("Program") },
          )
        }
        item {
          FilterChip(
            selected = authors.values.any { it },
            onClick = { showAuthorsBottomSheet = true },
            label = { Text("Author") },
          )
        }
        item {
          FilterChip(
            selected = levels.values.any { it },
            onClick = { showLevelsBottomSheet = true },
            label = { Text("Levels") },
          )
        }
      }

      LazyColumn {
        items(count = searchResults.size) { index ->
          val workout = searchResults[index]
          ListItem(
            headlineContent = { Text(workout.name) },
            supportingContent = {
              Text(workout.formatWorkoutDate(), style = MaterialTheme.typography.bodySmall)
            },
            leadingContent = leadingContent,
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier =
              Modifier
                .clickable {
                  onResultClick(workout)
                  expanded = false
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
          )
          HorizontalDivider()
        }
      }

      if (showProgramsBottomSheet) {
        ProgramPartialBottomSheet(
          showBottomSheet = showProgramsBottomSheet,
          programsMap = programs,
          sheetState = programsSheetState,
          onProgramSelected = { program, checked -> onProgramSelected(program, checked) },
          onDismissRequest = { showProgramsBottomSheet = false },
        )
      }

      if (showAuthorsBottomSheet) {
        AuthorPartialBottomSheet(
          showBottomSheet = showAuthorsBottomSheet,
          authorsMap = authors,
          sheetState = authorsSheetState,
          onAuthorSelected = { author, checked -> onAuthorSelected(author, checked) },
          onDismissRequest = { showAuthorsBottomSheet = false },
        )
      }

      if (showLevelsBottomSheet) {
        LevelsPartialBottomSheet(
          showBottomSheet = showLevelsBottomSheet,
          levelsMap = levels,
          sheetState = levelsSheetState,
          onLevelSelected = { level, checked -> onLevelSelected(level, checked) },
          onDismissRequest = { showLevelsBottomSheet = false },
        )
      }

      if (showDateRangePickerDialog) {
        DateRangePickerDialog(
          state = dateRangePickerState,
          onDismissRequest = { showDateRangePickerDialog = false },
        )
      }
    }
  }
}
