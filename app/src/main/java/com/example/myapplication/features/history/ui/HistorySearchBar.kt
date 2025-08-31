@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)

package com.example.myapplication.features.history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import com.example.myapplication.common.domain.model.Program
import com.example.myapplication.common.domain.model.Workout
import com.example.myapplication.common.domain.model.formatWorkoutDate
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
          expanded = expanded,
          onExpandedChange = {
            expanded = it
            onDismissRequest
          },
          placeholder = placeholder,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
        )
      },
      expanded = expanded,
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
          selected = false,
          onClick = { showDateRangePickerDialog = true },
          label = { Text("Date") }, // TODO change to be selected date range
        )

        FilterChip(
          selected = false,
          onClick = { showProgramsBottomSheet = true },
          label = { Text("Program") },
        )

        FilterChip(
          selected = false,
          onClick = { showAuthorsBottomSheet = true },
          label = { Text("Author") },
        )
        FilterChip(
          selected = false,
          onClick = { showLevelsBottomSheet = true },
          label = { Text("Levels") },
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramPartialBottomSheet(
  showBottomSheet: Boolean = false,
  programsMap: Map<Program, Boolean>,
  sheetState: SheetState,
  onProgramSelected: (Program, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit,
) {
  var showSheet by remember { mutableStateOf(showBottomSheet) }
  if (showSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = {
          showSheet = false
          onDismissRequest
        },
      ) {
        Row {
          Text(
            "Program",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall,
          )
        }

        Column(modifier = Modifier.selectableGroup()) {
          programsMap.keys.forEach { program ->
            val selected = programsMap[program] ?: false
            program.name?.let {
              Row(
                Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .selectable(
                    selected = selected,
                    onClick = { onProgramSelected(program, !selected) },
                    role = Role.Checkbox,
                  )
                  .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
              ) {
                Checkbox(
                  checked = selected,
                  onCheckedChange = null, // null recommended for accessibility with screen readers
                )
                Text(
                  style = MaterialTheme.typography.bodyMedium,
                  text = it,
                  modifier = Modifier.padding(start = 16.dp),
                )
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorPartialBottomSheet(
  showBottomSheet: Boolean = false,
  authorsMap: Map<String, Boolean>,
  sheetState: SheetState,
  onAuthorSelected: (String, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit,
) {
  var showSheet by remember { mutableStateOf(showBottomSheet) }
  if (showSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = {
          showSheet = false
          onDismissRequest
        },
      ) {
        Row {
          Text(
            "Author",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall,
          )
        }

        Column(modifier = Modifier.selectableGroup()) {
          authorsMap.keys.forEach { author ->
            val selected = authorsMap[author] ?: false
            Row(
              Modifier
                .fillMaxWidth()
                .height(56.dp)
                .selectable(
                  selected = selected,
                  onClick = { onAuthorSelected(author, !selected) },
                  role = Role.Checkbox,
                )
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = selected,
                onCheckedChange = null, // null recommended for accessibility with screen readers
              )
              Text(
                style = MaterialTheme.typography.bodyMedium,
                text = author,
                modifier = Modifier.padding(start = 16.dp),
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelsPartialBottomSheet(
  showBottomSheet: Boolean,
  levelsMap: Map<String, Boolean>,
  sheetState: SheetState,
  onLevelSelected: (String, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit,
) {
  var showSheet by remember { mutableStateOf(showBottomSheet) }
  if (showSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = {
          showSheet = false
          onDismissRequest
        },
      ) {
        Row {
          Text(
            "Level",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall,
          )
        }

        Column(modifier = Modifier.selectableGroup()) {
          levelsMap.keys.forEach { level ->
            val selected = levelsMap[level] ?: false
            Row(
              Modifier
                .fillMaxWidth()
                .height(56.dp)
                .selectable(
                  selected = selected,
                  onClick = { onLevelSelected(level, !selected) },
                  role = Role.Checkbox,
                )
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = selected,
                onCheckedChange = null, // null recommended for accessibility with screen readers
              )
              Text(
                style = MaterialTheme.typography.bodyMedium,
                text = level,
                modifier = Modifier.padding(start = 16.dp),
              )
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
  state: DateRangePickerState = rememberDateRangePickerState(),
  onDismissRequest: () -> Unit,
) {
  //  val state = rememberDateRangePickerState()
  val snackState = remember { SnackbarHostState() }
  val snackScope = rememberCoroutineScope()
  SnackbarHost(hostState = snackState, Modifier.zIndex(1f))
  Dialog(
    onDismissRequest = { onDismissRequest() },
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top) {
      Row(
        modifier =
          Modifier
            .fillMaxWidth()
            .background(DatePickerDefaults.colors().containerColor)
            .padding(start = 12.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        TooltipBox(
          positionProvider = TooltipDefaults.rememberTooltipPositionProvider(),
          tooltip = { PlainTooltip { Text("Close") } },
          state = rememberTooltipState(),
        ) {
          IconButton(onClick = { onDismissRequest() }) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
          }
        }
        TextButton(
          onClick = { onDismissRequest() },
          enabled = state.selectedEndDateMillis != null,
        ) {
          Text(text = "Save")
        }
      }
      DateRangePicker(state = state, modifier = Modifier.fillMaxWidth())
    }
  }
}

@Preview(showBackground = true)
@Composable
fun CustomizableSearchBarPreview() {
  val initList =
    listOf(
      "Apples",
      "Açaí",
      "Acerola Cherries",
      "African Breadfruit",
      "Akebi Fruit",
      "Alpine Strawberry",
      "African Mango",
      "Amanatsu Oranges",
      "Ambarella",
      "Amaou Strawberries",
      "Ambrosia Apples",
      "Ambrosia Melon",
      "Amla",
      "Apricots",
      "Annatto",
      "Annona",
      "Asian Pears",
      "Arava Melon",
      "Asam Kumbang",
      "Avocado",
      "Banana",
      "Cherry",
      "Date",
      "Grape",
      "Lemon",
      "Mango",
      "Orange",
      "Peach",
      "Pear",
    )

  var query by rememberSaveable { mutableStateOf("") }
  var selectedItem by rememberSaveable { mutableStateOf("") }
  val programs by remember { mutableStateOf(listOf<Program>()) }
  val workouts by remember { mutableStateOf(listOf<Workout>()) }
  val dateRangePickerState = rememberDateRangePickerState()

  // TODO Move to parent
  val levels = listOf("Beginner/Novice", "Intermediate", "Advanced")
  val authors = programs.map { it.author }.distinct()

  val selectedPrograms = remember { mutableStateListOf<Program>() }
  val selectedAuthors = remember { mutableStateListOf<String>() }
  val selectedLevels = remember { mutableStateListOf<String>() }

  val searchResults by remember {
    derivedStateOf {
      if (query.isEmpty()) {
        workouts
      } else {
        val filteredProgramsIDs =
          programs
            .filter { selectedPrograms.contains(it) }
            .filter { selectedAuthors.contains(it.author) }
            .filter {
              it.level?.split(",")?.any { level -> selectedLevels.contains(level) } ?: true
            }
            .map { it.id }

        workouts
          .filter { it.name.contains(query, ignoreCase = true) }
          .filter { filteredProgramsIDs.contains(it.programID) }
          .filter {
            if (
              dateRangePickerState.selectedEndDateMillis != null &&
              dateRangePickerState.selectedStartDateMillis != null &&
              it.workoutEnded != null
            ) {
              it.workoutEnded!! in
                      Instant.fromEpochMilliseconds(
                        dateRangePickerState.selectedStartDateMillis!!
                      )..Instant.fromEpochMilliseconds(dateRangePickerState.selectedEndDateMillis!!)
            } else {
              true
            }
          }
      }
    }
  }

  MyApplicationTheme {
    Scaffold() { paddingValues ->
      Column(modifier = Modifier
        .padding(paddingValues)
        .fillMaxSize()) {
        HistorySearchBar(
          query = query,
          onQueryChange = { query = it },
          onSearch = {},
          searchResults = searchResults,
          programs = mapOf(),
          authors = mapOf(),
          levels = mapOf(),
          onResultClick = { _ -> },
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
          Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            if (selectedItem.isNotEmpty()) {
              Text(text = "Select fruit: $selectedItem")
            } else {
              Text(text = "No fruit selected!")
            }
          }
        }
      }
    }
  }
}
