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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownField(
  modifier: Modifier = Modifier,
  label: String,
  options: List<T>,
  selectedOption: T,
  onOptionSelected: (T) -> Unit,
  optionToString: (T) -> String = { it.toString() } // How to display the option
) {
  var expanded by remember { mutableStateOf(false) }

  ExposedDropdownMenuBox(
    expanded = expanded,
    onExpandedChange = {
      expanded = it
      Log.i("DropdownField", "onExpandedChange: $it")
    },
    modifier = modifier
  ) {
    val type = MenuAnchorType.PrimaryEditable
    OutlinedTextField(
      modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
      value = optionToString(selectedOption),
      onValueChange = {}, // Not directly editable
      readOnly = true,
      label = { Text(label) },
      trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
    )
    ExposedDropdownMenu(
      expanded = expanded,
      onDismissRequest = { expanded = false }
    ) {
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

@Composable
fun ModernizedLoadRIRHeaderRow(
  modernizedLoadRIRData: List<ModernizedLoadRIR>,
  initialLoadRIR: ModernizedLoadRIR,
  onLoadRIRChange: (ModernizedLoadRIR) -> Unit
) {
  var currentLoadRIR by remember { mutableStateOf(initialLoadRIR) }

  val rirOptions =
    remember { modernizedLoadRIRData.map { it.rir }.distinct().sortedDescending() } // Example

  val rpeOptions = remember { modernizedLoadRIRData.map { it.rpe }.distinct().sorted() }

  val repsPerformedOptions =
    remember { modernizedLoadRIRData.map { it.repsPerformed }.distinct().sorted() }

  val relativeLoadPercentOptions =
    remember { modernizedLoadRIRData.map { it.relativeLoadPercent }.distinct().sortedDescending() }

  // Function to update state and notify parent
  val updateField = { updatedRIR: ModernizedLoadRIR ->
    currentLoadRIR = updatedRIR
    onLoadRIRChange(updatedRIR)
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
        options = rirOptions.sorted(),
        selectedOption = currentLoadRIR.rir,
        onOptionSelected = { newRir -> updateField(currentLoadRIR.copy(rir = newRir)) },
        optionToString = { "%.1f".format(Locale.US, it) } // Format to one decimal place
      )

      // RPE Dropdown
      DropdownField(
        modifier = modifier,
        label = "RPE",
        options = rpeOptions,
        selectedOption = currentLoadRIR.rpe,
        onOptionSelected = { newRpe -> updateField(currentLoadRIR.copy(rpe = newRpe)) },
        optionToString = { "%.1f".format(Locale.US, it) }
      )

      // Reps Performed Dropdown
      DropdownField(
        modifier = modifier,
        label = "Reps",
        options = repsPerformedOptions,
        selectedOption = currentLoadRIR.repsPerformed,
        onOptionSelected = { newReps -> updateField(currentLoadRIR.copy(repsPerformed = newReps)) }
        // Default toString for Int is fine
      )

      // Relative Load Percent Dropdown
      DropdownField(
        modifier = modifier,
        label = "% e1RM",
        options = relativeLoadPercentOptions,
        selectedOption = currentLoadRIR.relativeLoadPercent,
        onOptionSelected = { newLoad -> updateField(currentLoadRIR.copy(relativeLoadPercent = newLoad)) },
        optionToString = { "%.2f".format(Locale.US, it) }
      )
    }

    Text("Current State:", style = MaterialTheme.typography.titleMedium)
    Text("RepCapacity: ${currentLoadRIR.repCapacity}")
    Text("RIR: ${"%.1f".format(Locale.US, currentLoadRIR.rir)}")
    Text("RPE: ${"%.1f".format(Locale.US, currentLoadRIR.rpe)}")
    Text("Reps Performed: ${currentLoadRIR.repsPerformed}")
    Text("Relative Load: ${"%.1f".format(Locale.US, currentLoadRIR.relativeLoadPercent)}%")
  }

}
