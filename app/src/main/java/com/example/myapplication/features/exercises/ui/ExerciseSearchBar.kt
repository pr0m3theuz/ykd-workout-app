@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.exercises.ui

import android.util.Log
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
import androidx.compose.material3.Icon
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
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.domain.model.Equipment
import com.example.myapplication.common.domain.model.Exercise
import com.example.myapplication.common.domain.model.MovementType
import com.example.myapplication.common.domain.model.MuscleGroup
import com.example.myapplication.common.ui.component.EquipmentPartialBottomSheet
import com.example.myapplication.common.ui.component.MovementTypePartialBottomSheet
import com.example.myapplication.common.ui.component.MuscleGroupsPartialBottomSheet

@Composable
fun ExerciseSearchBar(
  modifier: Modifier = Modifier,
  query: String,
  onQueryChange: (String) -> Unit,
  onSearch: (String) -> Unit,
  muscleGroups: Map<MuscleGroup, Boolean>,
  equipment: Map<Equipment, Boolean>,
  movementTypes: Map<MovementType, Boolean>,
  searchResults: List<Exercise>,
//  onResultClick: (Exercise) -> Unit,
  onMuscleGroupsSelected: (MuscleGroup, Boolean) -> Unit = { _, _ -> },
  onEquipmentSelected: (Equipment, Boolean) -> Unit = { _, _ -> },
  onMovementTypeSelected: (MovementType, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit = {},
  // Customization options
  placeholder: @Composable () -> Unit = { Text("Search") },
  leadingIcon: @Composable (() -> Unit)? = {
    Icon(Icons.Default.Search, contentDescription = "Search")
  },
  trailingIcon: @Composable (() -> Unit)? = null,
  supportingContent: (@Composable (String) -> Unit)? = null,
  leadingContent: (@Composable () -> Unit)? = null,
  selectedFiltersLazyRow: (@Composable () -> Unit),
  exerciseItem: @Composable (exercise: Exercise) -> Unit,
  exercisesLazyList: @Composable () -> Unit,
) {
  var expanded by rememberSaveable { mutableStateOf(true) }

  var showEquipmentBottomSheet by remember { mutableStateOf(false) }
  var showMuscleGroupsBottomSheet by remember { mutableStateOf(false) }
  var showMovementTypeBottomSheet by remember { mutableStateOf(false) }

  val equipmentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val muscleGroupsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val movementTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

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
      selectedFiltersLazyRow()

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        FilterChip(
          selected = muscleGroups.values.any { it },
          onClick = {
            showMuscleGroupsBottomSheet = true
          },
          label = { Text("Muscle Groups") },
        )
        FilterChip(
          selected = equipment.values.any { it },
          onClick = {
            showEquipmentBottomSheet = true
          },
          label = { Text("Equipment") },
        )
        FilterChip(
          selected = movementTypes.values.any { it },
          onClick = {
            showMovementTypeBottomSheet = true
          },
          label = { Text("Movement Types") },
        )
      }

      exercisesLazyList()

/*      LazyColumn {
        items(
          count = searchResults.size,
          key = { index: Int -> searchResults[index].lazyListItemID }) { index ->
          val exercise = searchResults[index]
          exerciseItem(exercise)
        }
      }*/
    }


    Log.d("ExerciseSearchBar", "showEquipmentBottomSheet: $showEquipmentBottomSheet")
    if (showEquipmentBottomSheet) {
      EquipmentPartialBottomSheet(
        showBottomSheet = showEquipmentBottomSheet,
        equipmentMap = equipment,
        sheetState = equipmentSheetState,
        onEquipmentSelected = { equipment, selected -> onEquipmentSelected(equipment, selected) },
        onDismissRequest = {
          showEquipmentBottomSheet = false
        },
      )
    }

    Log.d("ExerciseSearchBar", "showMuscleGroupsBottomSheet: $showMuscleGroupsBottomSheet")
    if (showMuscleGroupsBottomSheet) {
      MuscleGroupsPartialBottomSheet(
        showBottomSheet = showMuscleGroupsBottomSheet,
        muscleGoupsMap = muscleGroups,
        sheetState = muscleGroupsSheetState,
        onMuscleGroupSelected = { muscleGroup, checked ->
          onMuscleGroupsSelected(
            muscleGroup,
            checked
          )
        },
        onDismissRequest = {
          showMuscleGroupsBottomSheet = false
        },
      )
    }

    Log.d("ExerciseSearchBar", "showMovementTypeBottomSheet: $showMovementTypeBottomSheet")
    if (showMovementTypeBottomSheet) {
      MovementTypePartialBottomSheet(
        showBottomSheet = showMovementTypeBottomSheet,
        movementTypesMap = movementTypes,
        sheetState = movementTypeSheetState,
        onMovementTypeSelected = { movementType, checked ->
          onMovementTypeSelected(
            movementType,
            checked
          )
        },
        onDismissRequest = {
          showMovementTypeBottomSheet = false
        },
      )
    }
  }
}
