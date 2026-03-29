package com.example.myapplication.features.workout.feature.workoutDialogs

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.RepCapacity

// STEP 1: CORE FILTERING LOGIC (Enum, Data Class, and Manager)
// =================================================================

class FilterManager(private val allData: List<ModernizedLoadRIR>) {

  // Holds the current active filters. Null means no filter is applied.
  data class ActiveFilters(
    var repCapacity: RepCapacity? = null,
    var rir: Double? = null,
    var rpe: Double? = null,
    var repsPerformed: Int? = null,
    var relativeLoadPercent: Double? = null
  )

  // Represents the available options for each dropdown and the final filtered data
  data class DropdownOptions(
    val repCapacityOptions: List<RepCapacity>,
    val rirOptions: List<Double>,
    val rpeOptions: List<Double>,
    val repsPerformedOptions: List<Int>,
    val relativeLoadPercentOptions: List<Double>,
    val filteredData: List<ModernizedLoadRIR>
  )

  fun applyFilters(newFilters: ActiveFilters): DropdownOptions {
    // First, filter the final data based on all active filters
    val filteredData = allData.filter { item ->
      (newFilters.repCapacity == null || item.repCapacity == newFilters.repCapacity) &&
              (newFilters.rir == null || item.rir == newFilters.rir) &&
              (newFilters.rpe == null || item.rpe == newFilters.rpe) &&
              (newFilters.repsPerformed == null || item.repsPerformed == newFilters.repsPerformed) &&
              (newFilters.relativeLoadPercent == null || item.relativeLoadPercent == newFilters.relativeLoadPercent)
    }

    // Now, for each dropdown, find the available options from the filtered data
    val repCapacityOptions = allData.filter { item ->
      (newFilters.rir == null || item.rir == newFilters.rir) &&
              (newFilters.rpe == null || item.rpe == newFilters.rpe) &&
              (newFilters.repsPerformed == null || item.repsPerformed == newFilters.repsPerformed) &&
              (newFilters.relativeLoadPercent == null || item.relativeLoadPercent == newFilters.relativeLoadPercent)
    }.map { it.repCapacity }.distinct().sorted()

    val rirOptions = allData.filter { item ->
      (newFilters.repCapacity == null || item.repCapacity == newFilters.repCapacity) &&
              (newFilters.rpe == null || item.rpe == newFilters.rpe) &&
              (newFilters.repsPerformed == null || item.repsPerformed == newFilters.repsPerformed) &&
              (newFilters.relativeLoadPercent == null || item.relativeLoadPercent == newFilters.relativeLoadPercent)
    }.map { it.rir }.distinct().sorted()

    val rpeOptions = allData.filter { item ->
      (newFilters.repCapacity == null || item.repCapacity == newFilters.repCapacity) &&
              (newFilters.rir == null || item.rir == newFilters.rir) &&
              (newFilters.repsPerformed == null || item.repsPerformed == newFilters.repsPerformed) &&
              (newFilters.relativeLoadPercent == null || item.relativeLoadPercent == newFilters.relativeLoadPercent)
    }.map { it.rpe }.distinct().sorted()

    val repsPerformedOptions = allData.filter { item ->
      (newFilters.repCapacity == null || item.repCapacity == newFilters.repCapacity) &&
              (newFilters.rir == null || item.rir == newFilters.rir) &&
              (newFilters.rpe == null || item.rpe == newFilters.rpe) &&
              (newFilters.relativeLoadPercent == null || item.relativeLoadPercent == newFilters.relativeLoadPercent)
    }.map { it.repsPerformed }.distinct().sorted()

    val relativeLoadPercentOptions = allData.filter { item ->
      (newFilters.repCapacity == null || item.repCapacity == newFilters.repCapacity) &&
              (newFilters.rir == null || item.rir == newFilters.rir) &&
              (newFilters.rpe == null || item.rpe == newFilters.rpe) &&
              (newFilters.repsPerformed == null || item.repsPerformed == newFilters.repsPerformed)
    }.map { it.relativeLoadPercent }.distinct().sorted()
// 

    Log.i("FilterManager", "repCapacityOptions: $repCapacityOptions")
//     Log.i("FilterManager", "rirOptions: $rirOptions")
//     Log.i("FilterManager", "rpeOptions: $rpeOptions")
//     Log.i("FilterManager", "repsPerformedOptions: $repsPerformedOptions")
//     Log.i("FilterManager", "relativeLoadPercentOptions: $relativeLoadPercentOptions")

    return DropdownOptions(
      repCapacityOptions,
      rirOptions,
      rpeOptions,
      repsPerformedOptions,
      relativeLoadPercentOptions,
      filteredData
    )
  }

  fun getInitialOptions(): DropdownOptions {
    // Initially, no filters are applied
    return applyFilters(ActiveFilters())
  }
}


// STEP 2: REUSABLE UI COMPONENTS
// =================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> FilteringDropDown(
  label: String,
  options: List<T>,
  selectedValue: T?,
  onValueSelected: (T?) -> Unit
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { expanded = !expanded },
    modifier = Modifier.padding(vertical = 4.dp)
  ) {
    TextField(
      modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
      readOnly = true,
      value = selectedValue?.toString() ?: "All",
      onValueChange = {},
      label = { Text(label) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
      colors = ExposedDropdownMenuDefaults.textFieldColors(),
    )
    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false },
    ) {
      // Add an option to clear the filter
      DropdownMenuItem(
        text = { Text("All") },
        onClick = {
          onValueSelected(null)
          expanded = false
        }
      )
      options.forEach { selectionOption ->
        DropdownMenuItem(
          text = { Text(selectionOption.toString()) },
          onClick = {
            onValueSelected(selectionOption)
            expanded = false
          }
        )
      }
    }
  }
}

@Composable
fun ResultItem(item: ModernizedLoadRIR) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(Modifier.padding(16.dp)) {
      Text(
        "Capacity: ${item.repCapacity}, RPE: ${item.rpe}, RIR: ${item.rir}",
        style = MaterialTheme.typography.bodyLarge
      )
      Text(
        "Performed ${item.repsPerformed} reps at ${item.relativeLoadPercent}%",
        style = MaterialTheme.typography.bodyMedium
      )
    }
  }
}


// STEP 3: SCREEN IMPLEMENTATION & PREVIEW
// =================================================================

@Composable
fun WorkoutFilterScreen(allData: List<ModernizedLoadRIR>) {

  // Instantiate the manager with the full dataset. `remember` ensures it survives recompositions.
  val filterManager = remember { FilterManager(allData) }

  // State for the currently selected filter values.
  var activeFilters by remember { mutableStateOf(FilterManager.ActiveFilters()) }

  // State for the available options in each dropdown and the final filtered list.
  // This is derived from the activeFilters.
  var dropdownOptions by remember { mutableStateOf(filterManager.getInitialOptions()) }

  // This is the core logic update.
  // Whenever a filter changes, this function is called to get new options.
  val updateFilters = { newFilters: FilterManager.ActiveFilters ->
    activeFilters = newFilters
    dropdownOptions = filterManager.applyFilters(newFilters)
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Text(
      "Workout Filters",
      style = MaterialTheme.typography.headlineSmall,
      modifier = Modifier.padding(bottom = 8.dp)
    )

    // --- Dropdown Menus ---
    FilteringDropDown(
      label = "Rep Capacity",
      options = dropdownOptions.repCapacityOptions,
      selectedValue = activeFilters.repCapacity,
      onValueSelected = { selected -> updateFilters(activeFilters.copy(repCapacity = selected)) }
    )

    FilteringDropDown(
      label = "Reps in Reserve (RIR)",
      options = dropdownOptions.rirOptions,
      selectedValue = activeFilters.rir,
      onValueSelected = { selected -> updateFilters(activeFilters.copy(rir = selected)) }
    )

    FilteringDropDown(
      label = "Rating of Perceived Exertion (RPE)",
      options = dropdownOptions.rpeOptions,
      selectedValue = activeFilters.rpe,
      onValueSelected = { selected -> updateFilters(activeFilters.copy(rpe = selected)) }
    )

    FilteringDropDown(
      label = "Reps Performed",
      options = dropdownOptions.repsPerformedOptions,
      selectedValue = activeFilters.repsPerformed,
      onValueSelected = { selected -> updateFilters(activeFilters.copy(repsPerformed = selected)) }
    )

    FilteringDropDown(
      label = "Relative Load %",
      options = dropdownOptions.relativeLoadPercentOptions,
      selectedValue = activeFilters.relativeLoadPercent,
      onValueSelected = { selected -> updateFilters(activeFilters.copy(relativeLoadPercent = selected)) }
    )

    Divider(modifier = Modifier.padding(vertical = 16.dp))

    // --- Results ---
    Text(
      "Results: ${dropdownOptions.filteredData.size}",
      style = MaterialTheme.typography.titleMedium
    )
    Spacer(modifier = Modifier.height(8.dp))

    LazyColumn {
      items(dropdownOptions.filteredData) { item ->
        ResultItem(item = item)
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun WorkoutFilterScreenPreview() {
  val sampleData = listOf(
    ModernizedLoadRIR(RepCapacity.High, 1.0, 9.0, 5, 92.3),
    ModernizedLoadRIR(RepCapacity.High, 2.0, 8.0, 8, 85.5),
    ModernizedLoadRIR(RepCapacity.Average, 2.0, 8.0, 8, 82.2),
    ModernizedLoadRIR(RepCapacity.Average, 3.0, 7.0, 10, 78.9),
    ModernizedLoadRIR(RepCapacity.Average, 1.0, 9.0, 5, 89.1),
    ModernizedLoadRIR(RepCapacity.Low, 4.0, 6.0, 12, 75.0),
    ModernizedLoadRIR(RepCapacity.Low, 2.0, 8.0, 8, 79.0),
    ModernizedLoadRIR(RepCapacity.High, 3.0, 7.0, 10, 82.2),
    ModernizedLoadRIR(RepCapacity.Average, 2.0, 8.0, 6, 82.2) // Same RPE/RIR but different reps
  )

  MaterialTheme {
    Surface {
      WorkoutFilterScreen(allData = sampleData)
    }
  }
}