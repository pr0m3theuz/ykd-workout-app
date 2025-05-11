package com.example.myapplication.features.workout.feature.workoutDialogs

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.example.myapplication.common.ui.theme.White
import com.example.myapplication.common.util.ModernizedLoadRIR
import com.example.myapplication.common.util.RepCapacity
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.roundToInt

fun getFormattedPercentageText(percentage: Double?): String {
  val df = DecimalFormat("#.##")
  df.roundingMode = RoundingMode.HALF_UP

  if (percentage != null) {
    val reformattedPercentage = df.format(percentage.times(100.0)).toDouble()

    return if (reformattedPercentage.minus(reformattedPercentage.roundToInt()) == 0.0) {
      reformattedPercentage.roundToInt().toString()
    } else {
      reformattedPercentage.toString()
    }
  } else {
    return ""
  }

}

fun calculatePercentage(percentage: String): Pair<String, Double> {
  val formattedPercentage = percentage.replace("%", "").take(6)
  val percentageText = formattedPercentage.takeIf { formattedPercentage.isNotBlank() } ?: ""
  val workingSetPercentage = formattedPercentage.toDoubleOrNull()?.div(100.0) ?: 0.0
  return Pair(percentageText, workingSetPercentage)
}

/**
 * Calculates and returns a formatted string representing new set results based on the given
 * parameters.
 *
 * @param weight The working weight for the set. Can be null.
 * @param percentage The percentage to apply to the working weight. Can be null.
 * @param repetitionsText The text representing the repetitions for the set.
 * @return A formatted string representing the new set results.
 *
 * from ChatGPT
 */
fun calculateNewSetResults(
  weight: Double?, percentage: Double?, repetitionsText: String?, rirText: String?, rpeText: String?
): String {
  // Constants for formatting the string
  val workingWeightXRepetitions = "%s x %s"
  val workingWeightXRepetitionsRIR = "%s x %s @ %s RIR"
  val workingWeightXRepetitionsRPE = "%s x %s @ %s RPE"

  // Calculate new working weight and round to nearest integer
  val newWorkingWeight = percentage?.let { weight?.times(it) }
  val roundedNewWorkingWeight = weight?.roundToInt()

  // Format new working weight as double with 2 decimal places and round to nearest integer
  val formattedNewWorkingWeight = "%.2f".format(newWorkingWeight).toDoubleOrNull() ?: 0.0
  val roundedFormattedNewWorkingWeight =
    formattedNewWorkingWeight.roundToInt() // TODO Round to nearest smallest increment

  // Choose appropriate values for formatting the string based on calculated results
  return when {
    newWorkingWeight?.minus(roundedNewWorkingWeight!!) == 0.0 -> {
      when {
        rirText != null -> {
          workingWeightXRepetitionsRIR.format(roundedNewWorkingWeight, repetitionsText, rirText)
        }

        rpeText != null -> {
          workingWeightXRepetitionsRPE.format(roundedNewWorkingWeight, repetitionsText, rpeText)
        }

        else -> workingWeightXRepetitions.format(roundedNewWorkingWeight, repetitionsText)
      }
    }

    formattedNewWorkingWeight.minus(roundedFormattedNewWorkingWeight) == 0.0 -> {
      workingWeightXRepetitions.format(roundedFormattedNewWorkingWeight, repetitionsText)
      when {
        rirText != null -> {
          workingWeightXRepetitionsRIR.format(
            roundedFormattedNewWorkingWeight,
            repetitionsText,
            rirText
          )
        }

        rpeText != null -> {
          workingWeightXRepetitionsRPE.format(
            roundedFormattedNewWorkingWeight,
            repetitionsText,
            rpeText
          )
        }

        else -> workingWeightXRepetitions.format(roundedFormattedNewWorkingWeight, repetitionsText)
      }
    }

    else -> {
      when {
        rirText != null -> {
          workingWeightXRepetitionsRIR.format(formattedNewWorkingWeight, repetitionsText, rirText)
        }

        rpeText != null -> {
          workingWeightXRepetitionsRPE.format(formattedNewWorkingWeight, repetitionsText, rpeText)
        }

        else -> workingWeightXRepetitions.format(formattedNewWorkingWeight, repetitionsText)
      }
    }
  }
}

@Composable
fun RoundedBox(modifier: Modifier, content: @Composable () -> Unit) {
  Box(modifier = modifier, propagateMinConstraints = true, contentAlignment = Alignment.Center) {
    content()
  }
}

@Composable
fun Textfield(text: String, onValueChange: (String) -> Unit) {
  BasicTextField(
    modifier = Modifier.padding(PaddingValues(4.dp)),
    textStyle =
      TextStyle(
        fontSize = 4.em,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground,
      ),
    //                placeholder = { Text(warmUpSet.exerciseWeight.toString())},
    keyboardOptions =
      KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
    value = text,
    onValueChange = { onValueChange(it) },
  )
}

@Composable
fun SetTypeButton(setTypeText: String, setTypeColor: Color, onClick: () -> Unit) {
  Box(modifier = Modifier.wrapContentSize(Alignment.Center), propagateMinConstraints = true) {
    Button(
      modifier = Modifier
        .padding(8.dp)
        .size(32.dp),
      shape = RoundedCornerShape(size = 8.dp),
      contentPadding = PaddingValues(0.dp),
      onClick = onClick,
    ) {
      Text(text = setTypeText, color = setTypeColor)
    }
  }
}

@Composable
fun SetContent(
  setTypeText: String,
  setTypeColor: Color,
  resultsText: String,
  percentageText: String?,
  repetitionsText: String?,
  rirText: String?,
  onPercentageTextChanged: (String) -> Unit,
  onRepetitionsTextChanged: (String) -> Unit,
  onRIRTextChanged: (String) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    val textfieldModifier =
      Modifier
        .padding(4.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .align(Alignment.CenterVertically)
        .wrapContentSize(Alignment.Center)
        .weight(1f, fill = false)

    val resultsModifier =
      Modifier
        .padding(4.dp)
        .align(Alignment.CenterVertically)
        .width(96.dp)
        .wrapContentSize(Alignment.Center)
        .weight(1f, fill = true)

    SetTypeButton(setTypeText, setTypeColor) {}

    RoundedBox(textfieldModifier.width(84.dp)) {
      Textfield(text = percentageText ?: "", onValueChange = onPercentageTextChanged)
    }

    RoundedBox(textfieldModifier.width(60.dp)) {
      Textfield(text = repetitionsText ?: "", onValueChange = onRepetitionsTextChanged)
    }

    RoundedBox(textfieldModifier.width(60.dp)) {
      Textfield(text = rirText ?: "", onValueChange = onRIRTextChanged)
    }


    Box(modifier = resultsModifier, contentAlignment = Alignment.Center) {
      Text(fontSize = 4.em, text = resultsText)
    }
  }
}

@Composable
fun SetsHeaderRow() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Start,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier = Modifier
        .padding(PaddingValues(horizontal = 4.dp))
        .width(42.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text("Set")
    }

    Box(
      modifier = Modifier
        .width(84.dp)
        .padding(PaddingValues(horizontal = 4.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Text("Target Percentage", textAlign = TextAlign.Center)
    }

    Box(
      modifier = Modifier
        .padding(PaddingValues(horizontal = 4.dp))
        .width(60.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text("Target Reps", textAlign = TextAlign.Center)
    }

    Box(
      modifier = Modifier
        .padding(PaddingValues(horizontal = 4.dp))
        .width(60.dp),
      contentAlignment = Alignment.Center,
    ) {
      Text("Target RIR", textAlign = TextAlign.Center)
    }

    // Blank Space
    Box(
      modifier = Modifier
        .padding(PaddingValues(horizontal = 4.dp))
        .width(60.dp),
      contentAlignment = Alignment.Center,
    ) { Text("Results", textAlign = TextAlign.Center) }
  }
}

@Composable
fun DialogInput(text: String, onValueChange: (String) -> Unit, onImeAction: () -> Unit = {}) {
  Box(
    modifier =
      Modifier
        //                        .padding(4.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .fillMaxWidth()
        .wrapContentSize(Alignment.Center),
    contentAlignment = Alignment.Center,
  ) {
    BasicTextField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(PaddingValues(4.dp)),
      textStyle =
        TextStyle(
          fontSize = 5.em,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
      keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onDone = { onImeAction() }),
      value = text,
      onValueChange = onValueChange,
    )
  }
}

@Composable
fun AddSetText() {
  androidx.compose.material.Text("Add Set", color = MaterialTheme.colorScheme.primary)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBoxListItem(removeSet: () -> Unit, SetLayout: @Composable () -> Unit) {
  val threshold = 0.5f
  val dismissState =
    rememberSwipeToDismissBoxState(
      positionalThreshold = { distance -> distance * threshold },
      confirmValueChange = { swipeToDismissBoxValue ->
        if (swipeToDismissBoxValue == SwipeToDismissBoxValue.EndToStart) {
          removeSet()
          true
        } else {
          false
        }
      },
    )

  SwipeToDismissBox(
    state = dismissState,
    enableDismissFromEndToStart = true,
    enableDismissFromStartToEnd = false,
    backgroundContent = {
      val color by
      animateColorAsState(
        when (dismissState.dismissDirection) {
          SwipeToDismissBoxValue.Settled -> MaterialTheme.colorScheme.primary
          SwipeToDismissBoxValue.EndToStart -> Color.Red
          SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary
        }
      )

      val alignment = Alignment.CenterEnd

      val icon = Icons.Filled.Delete
      val scale by
      animateFloatAsState(
        if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.8f else 1.2f
      )

      Box(
        Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 12.dp),
        contentAlignment = alignment,
      ) {
        Icon(
          icon,
          tint = White,
          contentDescription = "Localized description",
          modifier = Modifier.scale(scale),
        )
      }
    },
  ) {
    Card(
//      colors = , // MaterialTheme.colorScheme.background,
      shape = RoundedCornerShape(CornerSize(0f)),
    ) {
      SetLayout()
    }
  }
}

private const val NULL_SELECTION_TEXT = " "

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownField(
  modifier: Modifier = Modifier,
  label: String,
  options: List<T>,
  selectedOption: T?,
  onOptionSelected: (T?) -> Unit,
  optionToString: (T) -> String = { it.toString() }, // How to display the option
  enabled: Boolean = true,
  allowNullSelection: Boolean = true
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = { if (enabled) expanded = it },
    modifier = modifier
  ) {
    val type = MenuAnchorType.PrimaryEditable
    OutlinedTextField(
      modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
      value = selectedOption?.let { optionToString(it) }
        ?: (if (allowNullSelection) NULL_SELECTION_TEXT else ""),
      onValueChange = {}, // Not directly editable
      readOnly = true,
      label = { Text(label) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
    )
    if (options.isNotEmpty()) {
      ExposedDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
      ) {
        if (allowNullSelection) {
          DropdownMenuItem(
            text = { Text(NULL_SELECTION_TEXT) },
            onClick = {
              onOptionSelected(null)
              expanded = false
            }
          )
        }
        options.forEach { selectionOption ->
          DropdownMenuItem(
            text = { Text(optionToString(selectionOption)) },
            onClick = {
              onOptionSelected(selectionOption)
              expanded = false
            }
          )
        }
      }
    }
  }
}

@Composable
fun ModernizedLoadRIRHeaderRow(
  modernizedLoadRIRData: List<ModernizedLoadRIR>,
  initialLoadRIR: ModernizedLoadRIR?,
  onLoadRIRChange: (ModernizedLoadRIR?) -> Unit
) {
  if (modernizedLoadRIRData.isEmpty()) {
    Text("No chart data available to configure RIR.")
    return
  }

  // Find a valid initial entry or default to the first in the dataset
  val validInitialEntry = modernizedLoadRIRData.find { it == initialLoadRIR }
    ?: modernizedLoadRIRData.firstOrNull()
    ?: run {
      // This should ideally not happen if modernizedLoadRIRData is not empty
      // Provide a non-null fallback if absolutely necessary, though it's better to ensure modernizedLoadRIRData has items
      // For safety, using a placeholder or logging an error
      println("Error: modernizedLoadRIRData is not empty, but couldn't find a valid initial entry or first item.")
      ModernizedLoadRIR(RepCapacity.Low, 0.0, 0.0, 0, 0.0) // A dummy fallback
    }

  var currentRepCapacity by remember { mutableStateOf(initialLoadRIR?.repCapacity) }
  var currentRpe by remember { mutableStateOf(initialLoadRIR?.rpe) }
  var currentRir by remember { mutableStateOf(initialLoadRIR?.rir) }
  var currentRepsPerformed by remember { mutableStateOf(initialLoadRIR?.repsPerformed) }
  var currentRelativeLoadPercent by remember { mutableStateOf(initialLoadRIR?.relativeLoadPercent) }

// Derived options based on current selections
  val repCapacityOptions =
    remember(modernizedLoadRIRData) { getRepCapacityOptions(modernizedLoadRIRData) }

  val rpeOptions = remember(
    modernizedLoadRIRData,
    currentRepCapacity,
    currentRir,
    currentRepsPerformed,
    currentRelativeLoadPercent
  ) {
    getRpeOptions(
      modernizedLoadRIRData, currentRepCapacity,
      currentRir, currentRepsPerformed, currentRelativeLoadPercent
    )
  }

  val rirOptions = remember(
    modernizedLoadRIRData,
    currentRepCapacity,
    currentRpe,
    currentRepsPerformed,
    currentRelativeLoadPercent
  ) {
    getRirOptions(
      modernizedLoadRIRData, currentRepCapacity, currentRpe,
      currentRepsPerformed,
      currentRelativeLoadPercent
    )
  }

  val repsPerformedOptions =
    remember(
      modernizedLoadRIRData,
      currentRepCapacity,
      currentRpe,
      currentRir,
      currentRepsPerformed,
      currentRelativeLoadPercent
    ) {
      getRepsPerformedOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRelativeLoadPercent
      )
    }

  val relativeLoadPercentOptions = remember(
    modernizedLoadRIRData,
    currentRepCapacity,
    currentRpe,
    currentRir,
    currentRepsPerformed
  ) {
    getRelativeLoadPercentOptions(
      modernizedLoadRIRData,
      currentRepCapacity,
      currentRpe,
      currentRir,
      currentRepsPerformed
    )
  }

  // Function to update state and notify parent
  // This central effect updates the state and calls the callback when primary selections change.
  LaunchedEffect(
    currentRepCapacity,
    currentRpe,
    currentRir,
    currentRepsPerformed,
    currentRelativeLoadPercent
  ) {
    val rc = currentRepCapacity
    val rpeVal = currentRpe
    val rirVal = currentRir
    val repsVal = currentRepsPerformed
    val loadVal = currentRelativeLoadPercent

    Log.i(
      "ModernizedLoadRIRHeaderRow.LaunchedEffect",
      "LaunchedEffect: rpeVal: $rpeVal, rirVal: $rirVal, repsVal: $repsVal, loadVal: $loadVal"
    )
    if (rc != null && rpeVal != null && rirVal != null && repsVal != null && loadVal != null) {
      val completeEntry = modernizedLoadRIRData.find {
        it.repCapacity == rc && it.rpe == rpeVal && it.rir == rirVal &&
                it.repsPerformed == repsVal && it.relativeLoadPercent == loadVal
      }
      onLoadRIRChange(completeEntry)
    } else {
      onLoadRIRChange(null)
    }
  }

  val onRepCapacitySelected: (RepCapacity?) -> Unit = { newRepCap ->
    currentRepCapacity = newRepCap
    if (newRepCap == null || currentRpe !in getRpeOptions(
        modernizedLoadRIRData, newRepCap,
        currentRir, currentRepsPerformed, currentRelativeLoadPercent
      )
    ) {
      currentRpe = null
    }
    if (currentRepCapacity == null || currentRpe == null || currentRir !in getRirOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRepsPerformed,
        currentRelativeLoadPercent
      )
    ) {
      currentRir = null
    }
    if (currentRepCapacity == null || currentRpe == null || currentRir == null || currentRepsPerformed !in getRepsPerformedOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRelativeLoadPercent
      )
    ) {
      currentRepsPerformed = null
    }
    if (currentRepCapacity == null || currentRpe == null || currentRir == null || currentRepsPerformed == null || currentRelativeLoadPercent !in getRelativeLoadPercentOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRepsPerformed
      )
    ) {
      currentRelativeLoadPercent = null
    }
  }

  val onRpeSelected: (Double?) -> Unit = { newRpe ->
    currentRpe = newRpe
    if (currentRpe == null || currentRir !in getRirOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRepsPerformed,
        currentRelativeLoadPercent
      )
    ) {
      currentRir = null
    }
    if (currentRpe == null || currentRir == null || currentRepsPerformed !in getRepsPerformedOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRelativeLoadPercent
      )
    ) {
      currentRepsPerformed = null
    }
    if (currentRpe == null || currentRir == null || currentRepsPerformed == null || currentRelativeLoadPercent !in getRelativeLoadPercentOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRepsPerformed
      )
    ) {
      currentRelativeLoadPercent = null
    }
  }

  val onRirSelected: (Double?) -> Unit = { newRir ->
    currentRir = newRir
    if (currentRir == null || currentRpe !in getRpeOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRir,
        currentRepsPerformed,
        currentRelativeLoadPercent
      )
    ) {
      currentRir = null
    }
    if (currentRir == null || currentRepsPerformed !in getRepsPerformedOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRelativeLoadPercent
      )
    ) {
      currentRepsPerformed = null
    }
    if (currentRir == null || currentRepsPerformed == null || currentRelativeLoadPercent !in getRelativeLoadPercentOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRepsPerformed
      )
    ) {
      currentRelativeLoadPercent = null
    }
  }

  val onRepsPerformedSelected: (Int?) -> Unit = { newReps ->
    currentRepsPerformed = newReps
    if (currentRepsPerformed == null || currentRpe !in getRpeOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRir,
        currentRepsPerformed,
        currentRelativeLoadPercent
      )
    ) {
      currentRpe = null
    }
    if (currentRepsPerformed == null || currentRir !in getRirOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRepsPerformed,
        currentRelativeLoadPercent
      )
    ) {
      currentRir = null
    }
    if (currentRepsPerformed == null || currentRelativeLoadPercent !in getRelativeLoadPercentOptions(
        modernizedLoadRIRData,
        currentRepCapacity,
        currentRpe,
        currentRir,
        currentRepsPerformed
      )
    ) {
      currentRelativeLoadPercent = null
    }
  }

  val onRelativeLoadSelected: (Double?) -> Unit = { newLoad ->
    currentRelativeLoadPercent = newLoad
  }

  Column(modifier = Modifier.fillMaxWidth()) {
    Text("Modernized Load RIR Editor", style = MaterialTheme.typography.bodyMedium)
    Row(
      modifier = Modifier
        .fillMaxWidth(),
//    horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      val modifier = Modifier.width(95.dp)
      // RIR Dropdown
      DropdownField(
        modifier = modifier,
        label = "RIR",
        options = rirOptions,
        selectedOption = currentRir,
        onOptionSelected = onRirSelected,
        optionToString = { "%.1f".format(Locale.US, it) }, // Format to one decimal place
        enabled = true
      )

      // RPE Dropdown
      DropdownField(
        modifier = modifier,
        label = "RPE",
        options = rpeOptions,
        selectedOption = currentRpe,
        onOptionSelected = onRpeSelected,
        optionToString = { "%.1f".format(Locale.US, it) },
        enabled = true
      )

      // Reps Performed Dropdown
      DropdownField(
        modifier = modifier,
        label = "Reps",
        options = repsPerformedOptions,
        selectedOption = currentRepsPerformed,
        onOptionSelected = onRepsPerformedSelected,
        // Default toString for Int is fine
        enabled = true
      )

      // Relative Load Percent Dropdown
      DropdownField(
        modifier = modifier,
        label = "% e1RM",
        options = relativeLoadPercentOptions,
        selectedOption = currentRelativeLoadPercent,
        onOptionSelected = onRelativeLoadSelected,
        optionToString = { "%.0f".format(Locale.US, it) },
        enabled = true
      )
    }


    Text("Current State:", style = MaterialTheme.typography.titleMedium)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text("RIR: ${currentRir?.let { "%.1f".format(Locale.US, it) } ?: "NS"}",
        style = MaterialTheme.typography.bodySmall)
      Text("RPE: ${currentRpe?.let { "%.1f".format(Locale.US, it) } ?: "NS"}",
        style = MaterialTheme.typography.bodySmall)
      Text("Reps: ${currentRepsPerformed ?: "NS"}", style = MaterialTheme.typography.bodySmall)
      Text("e1RM%: ${currentRelativeLoadPercent?.let { "%.1f%%".format(Locale.US, it) } ?: "NS"}",
        style = MaterialTheme.typography.bodySmall)
    }
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        "Rep Capacity: ${currentRepCapacity ?: "NS"}",
        style = MaterialTheme.typography.bodySmall
      )
    }
  }
}

// 5. Helper functions to get filtered options
fun getRepCapacityOptions(data: List<ModernizedLoadRIR>): List<RepCapacity> {
  return data.map { it.repCapacity }.distinct().sortedBy { it.ordinal }
}

fun getRpeOptions(
  data: List<ModernizedLoadRIR>, repCapacity: RepCapacity?,
  rir: Double?,
  repsPerformed: Int?,
  relativeLoadPercent: Double?
): List<Double> {
  Log.i(
    "getRpeOptions",
    "repCapacity: $repCapacity, rir: $rir, repsPerformed: $repsPerformed, relativeLoadPercent: $relativeLoadPercent"
  )
  val list = data.asSequence()
    .filter { item ->
      item.repCapacity == repCapacity &&
              (rir == null || item.rir == rir) &&
              (repsPerformed == null || item.repsPerformed == repsPerformed) &&
              (relativeLoadPercent == null || item.relativeLoadPercent == relativeLoadPercent)
    }
    .map { it.rpe }
    .distinct()
    .sortedDescending()
    .toList() // RPE often shown high to low

  Log.i("getRpeOptions", "RPE Dropdown Items: $list")

  return list
}

fun getRirOptions(
  data: List<ModernizedLoadRIR>,
  repCapacity: RepCapacity?,
  rpe: Double?,
  repsPerformed: Int?,
  relativeLoadPercent: Double?,
): List<Double> {
  Log.i(
    "getRirOptions",
    "repCapacity: $repCapacity, rpe: $rpe, repsPerformed: $repsPerformed, relativeLoadPercent: $relativeLoadPercent"
  )
  val list = data.asSequence()
    .filter { item ->
      item.repCapacity == repCapacity &&
              (rpe == null || item.rpe == rpe) &&
              (repsPerformed == null || item.repsPerformed == repsPerformed) &&
              (relativeLoadPercent == null || item.relativeLoadPercent == relativeLoadPercent)
    }
    .map { it.rir }
    .distinct()
    .sorted()
    .toList()

  Log.i("getRirOptions", "RIR Dropdown Items: $list")

  return list
}

fun getRepsPerformedOptions(
  data: List<ModernizedLoadRIR>,
  repCapacity: RepCapacity?,
  rpe: Double?,
  rir: Double?,
  relativeLoadPercent: Double?
): List<Int> {
  Log.i(
    "getRepsPerformedOptions",
    "repCapacity: $repCapacity, rpe: $rpe, rir: $rir, relativeLoadPercent: $relativeLoadPercent"
  )
  val list = data.asSequence()
    .filter { item ->
      item.repCapacity == repCapacity &&
              (rir == null || item.rir == rir) &&
              (rpe == null || item.rpe == rpe) &&
              (relativeLoadPercent == null || item.relativeLoadPercent == relativeLoadPercent)
    }
    .map { it.repsPerformed }
    .distinct()
    .sorted()
    .toList()

  Log.i("getRepsPerformedOptions", "Reps Performed Dropdown Items: $list")

  return list
}

/**
 * Retrieves distinct relative load percentages from a list of [ModernizedLoadRIR]
 * based on optional filtering criteria.
 *
 * @param data The list of ModernizedLoadRIR objects to filter.
 * @param repCapacity The RepCapacity to filter by (if not null).
 * @param rpe The RPE to filter by (if not null).
 * @param rir The RIR to filter by (if not null).
 * @param repsPerformed The number of reps performed to filter by (if not null).
 * @return A list of distinct relative load percentages, sorted in descending order.
 */
fun getRelativeLoadPercentOptions(
  data: List<ModernizedLoadRIR>,
  repCapacity: RepCapacity?,
  rpe: Double?,
  rir: Double?,
  repsPerformed: Int?
): List<Double> {
//  if (repCapacity == null || rpe == null || rir == null || repsPerformed == null) return emptyList()
  Log.i(
    "getRelativeLoadPercentOptions",
    "repCapacity: $repCapacity, rpe: $rpe, rir: $rir, repsPerformed: $repsPerformed"
  )
  val list = data.asSequence()
    .filter { item ->
      item.repCapacity == repCapacity &&
              (rir == null || item.rir == rir) &&
              (rpe == null || item.rpe == rpe) &&
              (repsPerformed == null || item.repsPerformed == repsPerformed)
    }
    .map { it.relativeLoadPercent }
    .distinct()
    .sortedDescending()
    .toList()

  Log.i("getRelativeLoadPercentOptions", "Relative Load Percent Dropdown Items: $list")

  return list
//  return when {
//    rir != null && rpe != null && repsPerformed != null -> {
//      data.filter { it.repCapacity == repCapacity && it.rir == rir && it.rpe == rpe && it.repsPerformed == repsPerformed }
//        .map { it.relativeLoadPercent }.distinct().sortedDescending()
//    }
//
//    rir != null && repsPerformed != null -> {
//      data.filter { it.repCapacity == repCapacity && it.rir == rir && it.repsPerformed == repsPerformed }
//        .map { it.relativeLoadPercent }.distinct().sortedDescending()
//    }
//
//    rpe != null && repsPerformed != null -> {
//      data.filter { it.repCapacity == repCapacity && it.rpe == rpe && it.repsPerformed == repsPerformed }
//        .map { it.relativeLoadPercent }.distinct().sortedDescending()
//    }
//
//    rir != null -> {
//      data.filter { it.repCapacity == repCapacity && it.rir == rir }
//        .map { it.relativeLoadPercent }.distinct().sortedDescending()
//    }
//
//    rpe != null -> {
//      data.filter { it.repCapacity == repCapacity && it.rpe == rpe }
//        .map { it.relativeLoadPercent }.distinct().sortedDescending()
//    }
//
//    repsPerformed != null -> {
//      data.filter { it.repCapacity == repCapacity && it.repsPerformed == repsPerformed }
//        .map { it.relativeLoadPercent }.distinct().sortedDescending()
//    }
//
//    else -> {
//      data.filter { it.repCapacity == repCapacity }
//        .map { it.relativeLoadPercent }.distinct().sortedDescending()
//    }
//  }
}