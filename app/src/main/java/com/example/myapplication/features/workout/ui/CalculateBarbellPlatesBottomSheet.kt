@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.features.workout.ui

import android.icu.util.LocaleData
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.common.util.doubleFormatted
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculateBarbellPlatesBottomSheet(
  bottomSheetState: SheetState,
  onDismissRequest: () -> Unit,
  modifier: Modifier = Modifier,
  initialTarget: String = "",
  initialBar: String = "",
  expandBottomSheet: () -> Unit = {},
  availablePlatesMetric: List<Float> =
    listOf(25f, 20f, 15f, 10f, 5f, 2.5f, 1.25f), // TODO replace with User Settings
  availablePlatesImperial: List<Float> =
    listOf(45f, 35f, 25f, 10f, 5f, 2.5f), // TODO replace with User Settings
  calculatePlates: (targetTotal: Float, bar: Float, plates: List<Float>) -> Map<Float, Int>,
) {
  val focusManager = LocalFocusManager.current

  val scope = rememberCoroutineScope()

  var targetText by rememberSaveable { mutableStateOf(initialTarget) }
  var barText by rememberSaveable { mutableStateOf(initialBar) }
  var useMetric by rememberSaveable { mutableStateOf(true) }
  var selectedPlates by rememberSaveable { mutableStateOf(availablePlatesMetric.toSet()) }

  var result by remember { mutableStateOf<Map<Float, Int>>(emptyMap()) }
  var error by remember { mutableStateOf<String?>(null) }

  val unitLabel = if (useMetric) "kg" else "lb"

  val targetHeight by
  animateDpAsState(
    targetValue = if (result.isNotEmpty()) 1000.dp else 800.dp,
    label = "SheetHeightAnimation",
  )

//  fun expandBottomSheet() {
//    scope.launch { bottomSheetState.expand() }
//  }

  ModalBottomSheet(
    sheetState = bottomSheetState,
    onDismissRequest = onDismissRequest,
    //    modifier = modifier.heightIn(min = targetHeight),
  ) {
    Column(modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp)) {
      Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
          "Calculate Barbell Plates",
          style = MaterialTheme.typography.titleLarge,
          modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onDismissRequest) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(Modifier.height(8.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Text("Unit:")
        FilterChip(
          selected = useMetric,
          onClick = {
            useMetric = true
            selectedPlates = availablePlatesMetric.toSet()
          },
          label = { Text("Metric (kg)") },
        )
        FilterChip(
          selected = !useMetric,
          onClick = {
            useMetric = false
            selectedPlates = availablePlatesImperial.toSet()
          },
          label = { Text("Imperial (lb)") },
        )
      }

      Spacer(Modifier.height(8.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = targetText,
          onValueChange = {
            targetText = it
            error = null
          },
          label = { Text("Target ($unitLabel)") },
          modifier = Modifier.weight(1f),
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        OutlinedTextField(
          value = barText,
          onValueChange = {
            barText = it
            error = null
          },
          label = { Text("Bar ($unitLabel)") },
          modifier = Modifier.width(120.dp),
          singleLine = true,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
      }

      AnimatedVisibility(visible = !error.isNullOrBlank(), enter = fadeIn(), exit = fadeOut()) {
        Text(text = error ?: "", color = MaterialTheme.colorScheme.error)
      }

      Spacer(Modifier.height(12.dp))

      Text("Available plates (per side)", style = MaterialTheme.typography.labelLarge)
      Spacer(Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(if (useMetric) availablePlatesMetric else availablePlatesImperial) { plate ->
          val selected = plate in selectedPlates
          FilterChip(
            selected = selected,
            onClick = {
              selectedPlates = if (selected) selectedPlates - plate else selectedPlates + plate
            },
            label = { Text("${plate} $unitLabel") },
          )
        }
      }

      Spacer(Modifier.height(16.dp))

      Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        TextButton(
          onClick = {
            targetText = ""
            barText = ""
            result = emptyMap()
            error = null
          }
        ) {
          Icon(Icons.Default.Refresh, contentDescription = "Clear")
          Spacer(Modifier.width(6.dp))
          Text("Clear")
        }

        Button(
          onClick = {
            focusManager.clearFocus(true)
            val target = targetText.toFloatOrNull()
            val bar = barText.toFloatOrNull()
            if (target == null || target <= 0f) {
              error = "Enter a valid target weight"
              result = emptyMap()
              return@Button
            }
            if (bar == null || bar <= 0f) {
              error = "Enter a valid bar weight"
              result = emptyMap()
              return@Button
            }
            if (target <= bar) {
              error = "Target must be greater than bar"
              result = emptyMap()
              return@Button
            }
            val platesList = selectedPlates.sortedDescending()
            if (platesList.isEmpty()) {
              error = "Select at least one plate type"
              result = emptyMap()
              return@Button
            }
            try {
              result = calculatePlates(target, bar, platesList)
              error = null
              expandBottomSheet()
            } catch (t: Throwable) {
              error = "Calculation failed: ${t.localizedMessage}"
              result = emptyMap()
            }
          }
        ) {
          Text("Calculate")
        }
      }

      Spacer(Modifier.height(16.dp))

      Text("Result", style = MaterialTheme.typography.labelLarge)
      Spacer(Modifier.height(8.dp))

      /*AnimatedVisibility(visible = result.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
        Column {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for ((plate, count) in result.entries.sortedByDescending { it.key }) {
              Card(
                modifier = Modifier
                  .padding(4.dp)
                  .size(84.dp),
                shape = RoundedCornerShape(8.dp)
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("${plate} $unitLabel")
                  Spacer(Modifier.height(6.dp))
                  Text("x $count")
                }
              }
            }
          }
          Spacer(Modifier.height(12.dp))
          Text("Total plates used: ${result.values.sum()}")
        }
      }*/

      AnimatedVisibility(visible = result.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          BarbellWithPlates(result = result, unitLabel = unitLabel)
          Spacer(Modifier.height(12.dp))
          Text("Total plates used: ${result.values.sum()}")
        }
      }
    }
  }
}

@Composable
fun BarbellWithPlates(result: Map<Float, Int>, unitLabel: String) {
  val baseHeight = 100.dp

  Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
    Box(
      modifier = Modifier
        .size(width = 50.dp, height = 12.dp)
        .background(Color.Gray)
    ) // left sleeve
    Box(
      modifier = Modifier
        .size(width = 8.dp, height = 16.dp)
        .background(Color.Gray)
    ) // left sleeve
    result.entries
      .sortedByDescending { it.key }
      .forEach { (plate, count) ->
        val proportion =
          if (unitLabel == "lb") {
            when (plate) {
              55f,
              45f -> 1f

              35f -> 400f / 450f
              25f -> 325f / 450f
              10f -> 228f / 450f
              5f -> 190f / 450f
              2.5f -> 160f / 450f
              1f -> 134f / 450f
              0.5f -> 112f / 450f
              0.25f -> 90f / 450f
              else -> 0f
            }
          } else {
            when (plate) {
              25f,
              20f -> 1f

              15f -> 400f / 450f
              10f -> 325f / 450f
              5f -> 228f / 450f
              2.5f -> 190f / 450f
              1.25f -> 160f / 450f
              0.5f -> 134f / 450f
              0.25f -> 112f / 450f
              else -> 0f
            }
          }

        val height = baseHeight * proportion

        repeat(count) {
          val color =
            if (unitLabel == "lb") {
              when (plate) {
                55f -> Color(0xFF005EB8) // Blue
                45f -> Color(0xFFE4002B) // Red
                35f -> Color(0xFFFECB00) // Yellow
                25f -> Color(0xFF00A34A) // Green
                10f -> Color.White
                5f -> Color.Black
                else -> Color.White
              }
            } else { // Metric (kg)
              when (plate) {
                25f -> Color(0xFFE4002B) // Red
                20f -> Color(0xFF005EB8) // Blue
                15f -> Color(0xFFFECB00) // Yellow
                10f -> Color(0xFF00A34A) // Green
                5.0f -> Color.White
                2.5f -> Color.Black
                else -> Color.White
              }
            }
          val textColor =
            if (unitLabel == "lb") {
              when (plate) {
                55f -> Color.White // Blue
                45f -> Color.White // Red
                35f -> Color.Black // Yellow
                25f -> Color.Black // Green
                10f -> Color.Black
                5f -> Color.White
                else -> Color.Black
              }
            } else { // Metric (kg)
              when (plate) {
                25f -> Color.White // Red
                20f -> Color.White // Blue
                15f -> Color.Black // Yellow
                10f -> Color.Black // Green
                5f -> Color.Black
                2.5f -> Color.White
                else -> Color.Black
              }
            }
          Box(contentAlignment = Alignment.Center) {
            Box(
              modifier =
                Modifier
                  .width(20.dp)
                  .height(height.plus(12.dp))
                  .clip(MaterialTheme.shapes.extraSmall)
                  .background(color = color)
                  .border(2.dp, Color.DarkGray)
              //                .padding(1.dp)
            )
            Text(
              doubleFormatted(plate.toDouble()),
              modifier = Modifier
                .vertical()
                .rotate(90f),
              style = MaterialTheme.typography.labelSmall,
              color = textColor,
            )
          }
        }
      }
    Box(modifier = Modifier
      .width(40.dp)
      .height(12.dp)
      .background(Color.Gray)) // bar middle
  }
}

fun Modifier.vertical() = layout { measurable, constraints ->
  val placeable = measurable.measure(constraints)
  layout(placeable.height, placeable.width) {
    placeable.place(
      x = -(placeable.width / 2 - placeable.height / 2),
      y = -(placeable.height / 2 - placeable.width / 2),
    )
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewCalculateBarbellPlatesBottomSheet() {
  MyApplicationTheme {
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState =
      rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    // App content
    Column(
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      Row(
        Modifier.toggleable(
          value = skipPartiallyExpanded,
          role = Role.Checkbox,
          onValueChange = { checked -> skipPartiallyExpanded = checked },
        )
      ) {
        Checkbox(checked = skipPartiallyExpanded, onCheckedChange = null)
        Spacer(Modifier.width(16.dp))
        Text("Skip partially expanded State")
      }
      Button(
        onClick = { openBottomSheet = !openBottomSheet },
        modifier = Modifier.align(Alignment.CenterHorizontally),
      ) {
        Text(text = "Show Bottom Sheet")
      }
    }
    if (openBottomSheet) {
      CalculateBarbellPlatesBottomSheet(
        bottomSheetState = bottomSheetState,
        expandBottomSheet = { scope.launch { bottomSheetState.expand() } },
        onDismissRequest = { openBottomSheet = !openBottomSheet },
        calculatePlates = { target, bar, plates ->
          val perSide = (target - bar) / 2
          val result = mutableMapOf<Float, Int>()
          var remaining = perSide
          for (p in plates) {
            val c = (remaining / p).toInt()
            if (c > 0) {
              result[p] = c
              remaining -= c * p
            }
          }
          result
        },
      )
    }
  }
}

sealed class PlateCalculatorUiState {
  object Idle : PlateCalculatorUiState()

  data class Success(val plates: Map<Double, Int>) : PlateCalculatorUiState()

  data class Error(val message: String) : PlateCalculatorUiState()
}

@RequiresApi(Build.VERSION_CODES.P)
@Composable
private fun PlateCalculatorContent(
  measurementSystem: LocaleData.MeasurementSystem,
  totalWeightInput: String,
  barbellWeightInput: String,
  uiState: PlateCalculatorUiState,
  onTotalWeightChanged: (String) -> Unit,
  onBarbellWeightChanged: (String) -> Unit,
  onToggleMeasurementSystem: () -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    // ... (existing Text and TextField components)

    // The toggle for Metric/Imperial
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Text("Metric (kg)")
      Switch(
        checked = measurementSystem == LocaleData.MeasurementSystem.US,
        onCheckedChange = { onToggleMeasurementSystem() },
      )
      Text("Imperial (lbs)")
    }

    Spacer(Modifier.height(16.dp))

    // Use AnimatedContent to animate between the different UI states.
    // This makes the transition from 'idle' to 'success' smooth and polished.
    AnimatedContent(
      targetState = uiState,
      transitionSpec = {
        fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
      },
    ) { targetState ->
      when (targetState) {
        is PlateCalculatorUiState.Idle -> {
          Text("Enter weights to calculate plates.")
        }

        is PlateCalculatorUiState.Success -> {
          Column {
            Text("Plates per side:")
            targetState.plates.forEach { (plateWeight, count) ->
              Text(
                "$count x ${plateWeight}${if (measurementSystem == LocaleData.MeasurementSystem.SI) "kg" else "lbs"}"
              )
            }
          }
        }

        is PlateCalculatorUiState.Error -> {
          Text("Error: ${targetState.message}", color = MaterialTheme.colorScheme.error)
        }
      }
    }
  }
}
/*

// We'll pass in mock data to the preview to see how the UI looks in different states.
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Preview(showBackground = true)
@Composable
private fun PlateCalculatorContentPreview() {
  val mockPlates = mapOf(25.0 to 2, 10.0 to 1)
  val successState = PlateCalculatorUiState.Success(mockPlates)

  // We can also test the imperial view by passing the Imperial object
  // and adjusting the text to match the lbs unit.

  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(onDismissRequest = {}, sheetState = sheetState) {
    PlateCalculatorContent(
      measurementSystem = LocaleData.MeasurementSystem.US,
      totalWeightInput = "100",
      barbellWeightInput = "20",
      uiState = successState,
      onTotalWeightChanged = {},
      onBarbellWeightChanged = {},
      onToggleMeasurementSystem = {},
    )
  }
}
*/
