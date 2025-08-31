package com.example.myapplication.common.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex

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
