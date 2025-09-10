package com.example.myapplication.common.ui.component

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
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
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizableSearchBar(
  modifier: Modifier = Modifier,
  query: String,
  onQueryChange: (String) -> Unit,
  onSearch: (String) -> Unit,
  searchResults: List<String>,
  onResultClick: (String) -> Unit,
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
  var expanded by rememberSaveable { mutableStateOf(false) }

  var showBottomSheet by remember { mutableStateOf(false) }
  var showDateRangePickerDialog by remember { mutableStateOf(false) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

  val filterChipList = listOf("Date", "Muscle Groups", "Equipment", "Arthur", "Level")

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
          onExpandedChange = { expanded = it },
          placeholder = placeholder,
          leadingIcon = leadingIcon,
          trailingIcon = trailingIcon,
        )
      },
      expanded = expanded,
      onExpandedChange = { expanded = it },
    ) {
      // Show search results in a lazy column for better performance
      LazyRow(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {

        filterChipList.forEach {
          item {
            FilterChip(
              selected = false,
              onClick = {
                showBottomSheet = true
              },
              label = { Text(it) },
            )
          }
        }
      }

      LazyColumn {
        items(count = searchResults.size) { index ->
          val resultText = searchResults[index]
          ListItem(
            headlineContent = { Text(resultText) },
            supportingContent = supportingContent?.let { { it(resultText) } },
            leadingContent = leadingContent,
            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
            modifier =
              Modifier
                .clickable {
                  onResultClick(resultText)
                  expanded = false
                }
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
          )
        }
      }

      if (showBottomSheet) {
        PartialBottomSheet(
          showBottomSheet = showBottomSheet,
          sheetState = sheetState,
          onDismissRequest = { showBottomSheet = false },
          openDateRangePickerDialog = { showDateRangePickerDialog = true },
        )
      }

      if (showDateRangePickerDialog) {
        DateRangePickerDialog(
          onDismissRequest = { showDateRangePickerDialog = false },
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartialBottomSheet(
  showBottomSheet: Boolean,
  sheetState: SheetState,
  onDismissRequest: () -> Unit,
  openDateRangePickerDialog: () -> Unit = {},
) {
  val dateOptions =
    listOf(
      "Any time",
      "Older than a week",
      "Older than a month",
      "Older than 6 months",
      "Older than a year",
    )
  val (selectedOption, onOptionSelected) = remember { mutableStateOf(dateOptions[0]) }
  //  Column(
  //    modifier = Modifier.fillMaxWidth(),
  //    horizontalAlignment = Alignment.CenterHorizontally,
  //  ) {
  ModalBottomSheet(
    modifier = Modifier.fillMaxHeight(),
    sheetState = sheetState,
    onDismissRequest = { onDismissRequest },
  ) {
    Row {
      Text(
        "Date",
        modifier = Modifier.padding(16.dp),
        style = MaterialTheme.typography.titleSmall
      )
    }

    Column(modifier = Modifier.selectableGroup()) {
      dateOptions.forEach { text ->
        Row(
          Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
              selected = (text == selectedOption),
              onClick = { onOptionSelected(text) },
              role = Role.RadioButton,
            )
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
        ) {
          RadioButton(
            selected = (text == selectedOption),
            onClick = null, // null recommended for accessibility with screen readers
          )
          Text(
            style = MaterialTheme.typography.bodyLarge,
            text = text,
            modifier = Modifier.padding(start = 16.dp),
          )
        }
      }
    }

    TextButton(
      onClick = { openDateRangePickerDialog() },
    ) {
      Text("Custom range", style = MaterialTheme.typography.bodyLarge)
    }

  }
  //  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
  onDismissRequest: () -> Unit,
) {
  val state = rememberDateRangePickerState()
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
          positionProvider =
            TooltipDefaults.rememberTooltipPositionProvider(),
          tooltip = { PlainTooltip { Text("Close") } },
          state = rememberTooltipState(),
        ) {
          IconButton(onClick = { onDismissRequest() }) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
          }
        }
        TextButton(
          onClick = {
            snackScope.launch {
              val range = state.selectedStartDateMillis!!..state.selectedEndDateMillis!!
              snackState.showSnackbar("Saved range (timestamps): $range")
            }
          },
          enabled = state.selectedEndDateMillis != null,
        ) {
          Text(text = "Save")
        }
      }
      DateRangePicker(
        state = state,
        modifier = Modifier.fillMaxWidth(),
      )
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
  val searchResults by remember {
    derivedStateOf {
      if (query.isEmpty()) {
        initList
      } else {
        initList.filter { it.contains(query, ignoreCase = true) }
      }
    }
  }

  MyApplicationTheme {
    Scaffold() { paddingValues ->
      Column(
        modifier = Modifier
          .padding(paddingValues)
          .fillMaxSize()
      ) {
        CustomizableSearchBar(
          query = query,
          onQueryChange = { query = it },
          onSearch = {},
          searchResults = searchResults,
          onResultClick = { selectedItem = it },
        )
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp)
          ) {
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
