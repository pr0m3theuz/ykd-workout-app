package com.example.myapplication.common.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.myapplication.common.domain.model.Equipment
import com.example.myapplication.common.domain.model.MovementType
import com.example.myapplication.common.domain.model.MuscleGroup
import com.example.myapplication.common.domain.model.Program


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
  if (showBottomSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = {
//          showSheet = false
          onDismissRequest()
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
//  var showSheet by remember { mutableStateOf(showBottomSheet) }
  if (showBottomSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier
          .fillMaxHeight()
          .zIndex(2f),
        sheetState = sheetState,
        onDismissRequest = {
//          showSheet = false
          onDismissRequest()
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
  if (showBottomSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier
          .fillMaxHeight()
          .zIndex(2f),
        sheetState = sheetState,
        onDismissRequest = {
//          showSheet = false
          onDismissRequest()
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
fun EquipmentPartialBottomSheet(
  showBottomSheet: Boolean,
  equipmentMap: Map<Equipment, Boolean>,
  sheetState: SheetState,
  onEquipmentSelected: (Equipment, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit,
) {
  if (showBottomSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier
          .fillMaxHeight()
          .zIndex(2f),
        sheetState = sheetState,
        onDismissRequest = {
//          showSheet = false
          onDismissRequest()
        },
      ) {
        Row {
          Text(
            "Equipment",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall,
          )
        }

        LazyColumn(modifier = Modifier.selectableGroup()) {
          equipmentMap.filter { (_, value) -> value }
            .toSortedMap(compareBy<Equipment> { it.name }).keys.forEach { equipment ->
            item {
              val selected = equipmentMap[equipment] ?: false
              val (checked, onCheckedChange) = remember { mutableStateOf(selected) }
              Row(
                Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .selectable(
                    selected = selected,
                    onClick = {
                      onCheckedChange(!checked)
                      onEquipmentSelected(equipment, !selected)
                    },
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
                  text = equipment.name,
                  modifier = Modifier.padding(start = 16.dp),
                )
              }

            }
          }
          equipmentMap.filter { (_, value) -> !value }
            .toSortedMap(compareBy<Equipment> { it.name }).keys.forEach { equipment ->
            item {
              val selected = equipmentMap[equipment] ?: false
              val (checked, onCheckedChange) = remember { mutableStateOf(selected) }
              Row(
                Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .selectable(
                    selected = selected,
                    onClick = {
                      onCheckedChange(!checked)
                      onEquipmentSelected(equipment, !selected)
                    },
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
                  text = equipment.name,
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
fun MuscleGroupsPartialBottomSheet(
  showBottomSheet: Boolean,
  muscleGoupsMap: Map<MuscleGroup, Boolean>,
  sheetState: SheetState,
  onMuscleGroupSelected: (MuscleGroup, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit,
) {
  if (showBottomSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier
          .fillMaxHeight()
          .zIndex(2f),
        sheetState = sheetState,
        onDismissRequest = {
//          showSheet = false
          onDismissRequest()
        },
      ) {
        Row {
          Text(
            "Muscle Groups",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall,
          )
        }

        LazyColumn(modifier = Modifier.selectableGroup()) {
          muscleGoupsMap.filter { (_, value) -> value }
            .toSortedMap(compareBy<MuscleGroup> { it.name }).keys.forEach { muscleGroup ->
            item {
              val selected = muscleGoupsMap[muscleGroup] ?: false
              val (checked, onCheckedChange) = remember { mutableStateOf(selected) }
              Row(
                Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .selectable(
                    selected = selected,
                    onClick = {
                      onCheckedChange(!checked)
                      onMuscleGroupSelected(muscleGroup, !selected)
                    },
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
                  text = muscleGroup.name,
                  modifier = Modifier.padding(start = 16.dp),
                )
              }
            }
          }
          muscleGoupsMap.filter { (_, value) -> !value }
            .toSortedMap(compareBy<MuscleGroup> { it.name }).keys.forEach { muscleGroup ->
            item {
              val selected = muscleGoupsMap[muscleGroup] ?: false
              val (checked, onCheckedChange) = remember { mutableStateOf(selected) }
              Row(
                Modifier
                  .fillMaxWidth()
                  .height(56.dp)
                  .selectable(
                    selected = selected,
                    onClick = {
                      onCheckedChange(!checked)
                      onMuscleGroupSelected(muscleGroup, !selected)
                    },
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
                  text = muscleGroup.name,
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
fun MovementTypePartialBottomSheet(
  showBottomSheet: Boolean,
  movementTypesMap: Map<MovementType, Boolean>,
  sheetState: SheetState,
  onMovementTypeSelected: (MovementType, Boolean) -> Unit = { _, _ -> },
  onDismissRequest: () -> Unit,
) {
  if (showBottomSheet) {
    Column {
      ModalBottomSheet(
        modifier = Modifier
          .fillMaxHeight()
          .zIndex(2f),
        sheetState = sheetState,
        onDismissRequest = {
//          showSheet = false
          onDismissRequest()
        },
      ) {
        Row {
          Text(
            "Movement Type",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleSmall,
          )
        }

        Column(modifier = Modifier.selectableGroup()) {
          movementTypesMap.toSortedMap(compareBy<MovementType> { it.name }).keys.forEach { movementType ->
            val selected = movementTypesMap[movementType] ?: false
            val (checked, onCheckedChange) = remember { mutableStateOf(selected) }
            Row(
              Modifier
                .fillMaxWidth()
                .height(56.dp)
                .selectable(
                  selected = selected,
                  onClick = {
                    onCheckedChange(!checked)
                    onMovementTypeSelected(movementType, !selected)
                  },
                  role = Role.Checkbox,
                )
                .padding(horizontal = 16.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Checkbox(
                checked = checked,
                onCheckedChange = null, // null recommended for accessibility with screen readers
              )
              Text(
                style = MaterialTheme.typography.bodyMedium,
                text = movementType.name,
                modifier = Modifier.padding(start = 16.dp),
              )
            }
          }
        }
      }
    }
  }
}
