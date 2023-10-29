package com.example.myapplication.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.myapplication.data.model.MassUnit

@Composable
fun WeightUnitOptionsDropdownMenu(
  expandMenu: () -> Boolean,
  onClickArrowBack: () -> Unit,
  onDismiss: () -> Unit,
  userDefaultWeightUnit: String,
  exerciseWeightUnit: String?,
  updateExerciseWeightUnit: (String?) -> Unit,
) {
  val (defaultChecked, setDefaultChecked) = remember { mutableStateOf(true) }
  val (poundsChecked, setPoundsChecked) = remember { mutableStateOf(false) }
  val (kilogramsChecked, setKilogramsChecked) = remember { mutableStateOf(false) }

  fun updateWeightUnit() {
    when {
      defaultChecked -> updateExerciseWeightUnit(null)
      poundsChecked -> updateExerciseWeightUnit(MassUnit.POUNDS.abbreviation)
      kilogramsChecked -> updateExerciseWeightUnit(MassUnit.KILOGRAMS.abbreviation)
    }
  }

  fun onDefaultChecked() {
    setDefaultChecked(!defaultChecked)
    setPoundsChecked(false)
    setKilogramsChecked(false)
    updateWeightUnit()
  }

  fun onPoundsChecked() {
    setPoundsChecked(!poundsChecked)
    setDefaultChecked(false)
    setKilogramsChecked(false)
    updateWeightUnit()
  }

  fun onKilogramsChecked() {
    setKilogramsChecked(!kilogramsChecked)
    setDefaultChecked(false)
    setPoundsChecked(false)
    updateWeightUnit()
  }

  when (exerciseWeightUnit) {
    null -> setDefaultChecked(true)
    MassUnit.POUNDS.abbreviation -> setPoundsChecked(true)
    MassUnit.KILOGRAMS.abbreviation -> setKilogramsChecked(true)
  }

  val checkedColor = MaterialTheme.colorScheme.primary

  val uncheckedColor = MaterialTheme.colorScheme.outlineVariant

  val kilogramsCheckColor by
  animateColorAsState(
    if (kilogramsChecked) {
      checkedColor
    } else uncheckedColor,
    label = "kilogramsChecked"
  )
  val poundCheckColor by
  animateColorAsState(
    if (poundsChecked) checkedColor else uncheckedColor,
    label = "poundCheckColor"
  )

  val defaultCheckColor by
  animateColorAsState(
    if (defaultChecked) checkedColor else uncheckedColor,
    label = "poundCheckColor"
  )

  //    var expandedWeightOptionsDropdownMenu by remember { mutableStateOf( false ) }
  //    expandedWeightOptionsDropdownMenu = expandMenu()

  DropdownMenu(expanded = expandMenu(), onDismissRequest = { onDismiss() }) {
    DropdownMenuItem(
      text = {},
      leadingIcon = {
        // TODO Change IconToggleButton to Icon
        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Localized description")
      },
      onClick = {
        onDismiss()
        onClickArrowBack()
      }
    )
    DropdownMenuItem(
      // TODO Get from user perferences/settings
      text = { Text("Default ($userDefaultWeightUnit) ") },
      trailingIcon = {
        // TODO Change
        Icon(
          Icons.Filled.Check,
          contentDescription = "Localized description",
          tint = defaultCheckColor
        )
      },
      onClick = { onDefaultChecked() }
    )
    DropdownMenuItem(
      text = { Text("Metric (kg)") },
      trailingIcon = {
        Icon(
          Icons.Filled.Check,
          contentDescription = "Localized description",
          tint = kilogramsCheckColor
        )
      },
      // TODO Change weight units in exercises table
      onClick = { onKilogramsChecked() }
    )
    DropdownMenuItem(
      text = { Text("Imperial (lbs)") },
      trailingIcon = {
        Icon(
          Icons.Filled.Check,
          contentDescription = "Localized description",
          tint = poundCheckColor
        )
      },
      // TODO Change weight units in exercises table
      onClick = { onPoundsChecked() }
    )
  }
}
