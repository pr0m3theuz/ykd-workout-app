package com.example.myapplication.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

@Composable
fun WeightUnitOptionsDropdownMenu(
  expandMenu: () -> Boolean,
  onClickArrowBack: () -> Unit,
  onDismiss: () -> Unit
) {
  var poundsChecked by remember { mutableStateOf(true) }
  var kilogramsChecked by remember { mutableStateOf(false) }
  val kilogramsCheckColor by
  animateColorAsState(if (kilogramsChecked) Color(0xFFEC407A) else Color(0xFFB0BEC5))
  val poundCheckColor by
  animateColorAsState(if (poundsChecked) Color(0xFFEC407A) else Color(0xFFB0BEC5))

  // TODO add arg to take false so that if false, stay false
  fun checkPounds() {
    poundsChecked = !poundsChecked
  }

  fun checkKilograms() {
    kilogramsChecked = !kilogramsChecked
  }

  //    var expandedWeightOptionsDropdownMenu by remember { mutableStateOf( false ) }
  //    expandedWeightOptionsDropdownMenu = expandMenu()

  DropdownMenu(expanded = expandMenu(), onDismissRequest = { onDismiss() }) {
    DropdownMenuItem(
      text = {},
      leadingIcon = {
        // TODO Change IconToggleButton to Icon
        Icon(Icons.Outlined.ArrowBack, contentDescription = "Localized description")
      },
      onClick = {
        onDismiss()
        onClickArrowBack()
      }
    )
    DropdownMenuItem(
      //            TODO Get from user perferences/settings
      text = { Text("Default (???) ") },
      trailingIcon = {
        // TODO Change
        Icon(
          Icons.Filled.Check,
          contentDescription = "Localized description",
          tint = Color(0xFF000000)
        )
      },
      // TODO Change to User's Defaults, use default in weight units table
      onClick = {}
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
      onClick = {
        checkPounds()
        checkKilograms()
      }
    )
    DropdownMenuItem(
      text = { Text("US/Imperial (lbs)") },
      trailingIcon = {
        Icon(
          Icons.Filled.Check,
          contentDescription = "Localized description",
          tint = poundCheckColor
        )
      },
      // TODO Change weight units in exercises table
      onClick = {
        checkPounds()
        checkKilograms()
      }
    )
  }
}
