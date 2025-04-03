package com.example.myapplication.components.exercise

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.SetTypeOptionsMenu
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.setRestTimer.TimerScreen
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.SetType
import com.example.myapplication.data.model.WorkoutComponent
import com.example.myapplication.data.model.convertKgToPound
import com.example.myapplication.data.model.convertPoundToKg
import com.example.myapplication.data.model.setSummaryRIR
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.checkIntInput
import com.example.myapplication.utils.checkRIRInput
import com.example.myapplication.utils.doubleFormatted
import org.koin.androidx.compose.koinViewModel

// TODO Add ability to rearrange sets
// TODO Add placeholder text for target weight, reps, & rpe/rir
// TODO FocusRequester.requestFocus() to tab to next field doesn't work

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SetContent(
  exerciseSets: WorkoutComponent,
  setIndex: Int,
  set: Set,
  getWeightUnit: () -> String,
  populatePreviousSetField: () -> Set?,
  updateSetReps: (reps: String) -> Unit,
  updateSetRIR: (rir: String) -> Unit,
  updateSetWeight: (weight: String) -> Unit,
  isTimerRunning: Boolean,
  updateSetType: (setType: SetType) -> Unit,
  setComplete: (Boolean) -> Unit,
  startSetCompletedTimer: () -> Unit,
) {
  val checkedState = remember { mutableStateOf(set.setCompleted) }
  val weightUnit = getWeightUnit()

  val previousSet = populatePreviousSetField()

  val previousSetText by remember { mutableStateOf(previousSet?.setSummaryRIR(weightUnit) ?: "") }

  val exerciseWeight = getExerciseWeight(set, weightUnit)
  var exerciseWeightText by remember { mutableStateOf(exerciseWeight) }

  val repetitions = getRepetitions(set)

  var repetitionsText by remember { mutableStateOf(repetitions) }

  val repetitionsInReserve = getRepetitionsInReserve(set)

  var repetitionsInReserveText by remember { mutableStateOf(repetitionsInReserve) }

  val ratePerceivedEffort = getRatePerceivedEffort(set)

  var ratePerceivedEffortText by remember { mutableStateOf(ratePerceivedEffort) }

  fun updateReps(reps: String) {
    updateSetReps(reps)
  }

  fun updateRIR(rir: String) {
    updateSetRIR(rir)
  }

  fun updateWeight(weight: String) {
    updateSetWeight(weight)
    //    weightFocusState = true
    //    weightState.isFocusedDirty = weightFocusState
  }

  fun updateSetData() {
    updateWeight(exerciseWeightText)
    updateReps(repetitionsText)
    updateRIR(repetitionsInReserveText)
  }

  var openTimerScreen by remember { mutableStateOf(false) }

  fun openTimerScreen(open: Boolean) {
    openTimerScreen = open
  }

  if (openTimerScreen && isTimerRunning) {
    TimerScreen(openDialog = { open -> openTimerScreen(open) })
  }

  fun copyPreviousSetData() {
    // TODO Show toast message that all fields are must be empty for this to work
    // Only copy if all fields are empty
    if (
      exerciseWeightText.isEmpty() &&
        repetitionsText.isEmpty() &&
        repetitionsInReserveText.isEmpty()
    ) {
      if (getWeightUnit() == MassUnit.POUNDS.abbreviation) {
        updateWeight(previousSet?.weightPounds.toString())
      } else {
        updateWeight(previousSet?.weightKilograms.toString())
      }
      updateReps(previousSet?.repetitions.toString())
      updateRIR(previousSet?.repetitionsInReserve.toString())
    }
  }

  SetContentFrame(
    setTypeOptionsMenu = {
      SetTypeComposable(
        updateSetType = { setType -> updateSetType(setType) },
        setIndex = setIndex.inc().toString(),
        setType = set.setTypeID,
      )
    },
    previousSet = {
      PreviousSet(
        Modifier.width(110.dp),
        previousSetText,
        copyPreviousSetData = { copyPreviousSetData() },
      )
    },
    setCompleteCheckbox = {
      SetCompleteCheckbox(
        checkedState,
        completeSet = { setComplete(it) },
        startRestTimer = { startSetCompletedTimer() },
        isTimerRunning = isTimerRunning,
      )
    },
    exerciseWeightText = exerciseWeightText,
    targetWeight = doubleFormatted(set.targetWeightPounds),
    onWeightChange = {
      exerciseWeightText =
        when {
          it.isBlank() -> ""
          else -> checkDoubleInput(it) ?: exerciseWeightText
        }

      // TODO Select Weight Unit based on Exercise or User Settings
      // TODO set.weightKilograms = weightState.text.toDoubleOrNull()
      // TODO set.weightPounds = set.convertKgToPound()
      // TODO Do unit conversion
      // TODO Is this redundant?
      if (getWeightUnit() == MassUnit.POUNDS.abbreviation) {
        set.weightPounds = exerciseWeightText.toDoubleOrNull()
        set.weightKilograms = set.convertPoundToKg()
      } else {
        set.weightKilograms = exerciseWeightText.toDoubleOrNull()
        set.weightPounds = set.convertKgToPound()
      }
    },
    repetitionsText = repetitionsText,
    targetReps = set.targetRepetitions,
    onRepsChange = { reps ->
      repetitionsText = checkIntInput(reps)
      set.repetitions = repetitionsText.toIntOrNull()
    },
    repetitionsInReserveText = repetitionsInReserveText,
    targetRIR = doubleFormatted(set.targetRepetitionsInReserve),
    onRIRChange = { rir ->
      repetitionsInReserveText =
        when {
          rir.isBlank() -> ""
          else ->
            checkRIRInput(rir)?.takeIf { it.toDouble() in 0.0..10.0 }?.toString()
              ?: repetitionsInReserveText
        }
      set.repetitionsInReserve = repetitionsInReserveText.toDoubleOrNull()
    },
  )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SetContentFrame(
  setTypeOptionsMenu: @Composable () -> Unit,
  previousSet: @Composable () -> Unit,
  setCompleteCheckbox: @Composable (boxModifier: Modifier) -> Unit,
  workoutViewModel: WorkoutViewModel = koinViewModel(),
  exerciseWeightText: String,
  targetWeight: String?,
  onWeightChange: (newWeight: String) -> Unit,
  repetitionsText: String,
  targetReps: String?,
  onRepsChange: (newReps: String) -> Unit,
  repetitionsInReserveText: String,
  targetRIR: String?,
  onRIRChange: (newRIR: String) -> Unit,
) {

  val focusRequester = remember { FocusRequester() }
  val focusManager = LocalFocusManager.current

  val textFieldBackgroundColor = MaterialTheme.colorScheme.secondaryContainer

  var openTimerScreen by remember { mutableStateOf(false) }

  fun openTimerScreen(open: Boolean) {
    openTimerScreen = open
  }

  if (openTimerScreen && workoutViewModel.isTimerRunning) {
    TimerScreen(openDialog = { open -> openTimerScreen(open) })
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    setTypeOptionsMenu()

    // TODO Get Previous Set Data by Exercise, Workout, Block, Plan
    // TODO If Previous RIR is null, text = "110 x 10" or "110 x 10 @ 1RM%"
    previousSet()

    val boxModifier: Modifier =
      Modifier
        .align(Alignment.CenterVertically)
        .padding(4.dp)
        .clip(RoundedCornerShape(8.dp))
        .background(textFieldBackgroundColor)
        .wrapContentSize(Alignment.Center)
    val textFieldModifier: Modifier = Modifier

    val textStyle =
      TextStyle(
        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground,
      )

    val modifier = textFieldModifier.padding(PaddingValues(8.dp))

    val cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)

    // Weight TextField
    Box(
      modifier = boxModifier.width(96.dp),
      propagateMinConstraints = true,
      contentAlignment = Alignment.Center,
    ) {
      BasicTextField(
        value = exerciseWeightText,
        onValueChange = { onWeightChange(it.take(6).trim()) },
        modifier = modifier,
        textStyle = textStyle,
        singleLine = true,
        maxLines = 1,
        keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusRequester.requestFocus() }),
        cursorBrush = cursorBrush,
      )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!targetWeight.isNullOrBlank() && exerciseWeightText.isBlank()) {
                Text(
                    text = targetWeight,
                    color = MaterialTheme.colorScheme.outline
                )
            }
      }
    }

    // Reps TextField
    Box(
      modifier = boxModifier.width(60.dp),
      propagateMinConstraints = true,
      contentAlignment = Alignment.Center,
    ) {
      BasicTextField(
        modifier = modifier.focusRequester(focusRequester),
        textStyle = textStyle,
        keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
        value = repetitionsText,
        singleLine = true,
        maxLines = 1,
        onValueChange = { onRepsChange(it.take(4).trim()) },
        cursorBrush = cursorBrush,
      )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (!targetReps.isNullOrBlank() && repetitionsText.isBlank()) {
                Text(text = targetReps, color = MaterialTheme.colorScheme.outline)
            }
      }
    }

    val uneditedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy()
    var textColor by remember { mutableStateOf(uneditedTextColor) }

    var changeTextColor by remember { mutableStateOf(false) }

    fun changeTextColor() {
      changeTextColor = true
    }

    if (changeTextColor) {
      textColor = MaterialTheme.colorScheme.onBackground
    }

    // RIR/RPE TextField
    Box(
      modifier = boxModifier.width(48.dp),
      propagateMinConstraints = true,
      contentAlignment = Alignment.Center,
    ) {
      BasicTextField(
        modifier = modifier,
        textStyle =
        TextStyle(
          fontSize = MaterialTheme.typography.bodyLarge.fontSize,
          textAlign = TextAlign.Center,
          color = textColor,
        ),
        singleLine = true,
        maxLines = 1,
        keyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
        value = repetitionsInReserveText,
        onValueChange = { onRIRChange(it.take(4).trim()) },
        cursorBrush = cursorBrush,
          decorationBox = { innerTextField ->
              Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.Center
              ) {
                  if (!targetRIR.isNullOrBlank() && repetitionsInReserveText.isBlank()) {
                      Text(text = targetRIR, color = MaterialTheme.colorScheme.outline)
                  }
              }
              innerTextField()
          },
      )

    }

    setCompleteCheckbox(Modifier)
  }
}

@Composable
private fun SetTypeComposable(
  updateSetType: (setType: SetType) -> Unit,
  setType: SetType,
  setIndex: String
) {
  var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }

  val iconText by remember {
    mutableStateOf(
      when (setType) {
        SetType.normalSet -> setIndex
        SetType.warmUpSet -> "W"
        SetType.dropSet -> "D"
        SetType.myoRepSet -> "M"
        SetType.myoRepMatchSet -> "MM"
        SetType.giantSet -> "G"
        SetType.amrapSet -> "A"
        SetType.failureSet -> "F"
        else -> {
          setIndex
        }
      }
    )
  }

  val buttonBackgroundColor =
    when (setType) {
      SetType.warmUpSet -> MaterialTheme.colorScheme.tertiary
      SetType.amrapSet -> MaterialTheme.colorScheme.error
      SetType.failureSet -> MaterialTheme.colorScheme.error
      else -> MaterialTheme.colorScheme.primary
    }

  val iconColor: Color =
    when (setType) {
      SetType.warmUpSet -> MaterialTheme.colorScheme.onSecondary
      SetType.amrapSet -> MaterialTheme.colorScheme.onError
      SetType.failureSet -> MaterialTheme.colorScheme.onError
      else -> MaterialTheme.colorScheme.onPrimary
    }

  Box(modifier = Modifier.wrapContentSize(Alignment.Center), propagateMinConstraints = true) {
    Button(
      modifier = Modifier
        .padding(8.dp)
        .size(32.dp),
      shape = RoundedCornerShape(size = 8.dp),
      contentPadding = PaddingValues(0.dp),
      onClick = { expandedSetTypeOptionsDropdownMenu = true },
      colors = ButtonDefaults.buttonColors(containerColor = buttonBackgroundColor),
    ) {
      Text(text = iconText, color = iconColor, softWrap = false, maxLines = 1, minLines = 1)
    }

    SetTypeOptionsMenu(
      expandMenu = { expandedSetTypeOptionsDropdownMenu },
      onDismiss = { expandedSetTypeOptionsDropdownMenu = false },
      updateSetType = { setTypeID -> updateSetType(setType) },
      changeIconText = { abbreviation ->
        //        iconText = abbreviation
        //        Log.i("setTypeAbbreviation", "setType iconText: $iconText")
      },
    )
  }
}

@Composable
private fun PreviousSet(
  modifier: Modifier,
  previousSetText: String,
  copyPreviousSetData: () -> Unit,
) {
  Box(/*modifier = Modifier.width(110.dp)*/ ) {
    TextButton(
      onClick = { copyPreviousSetData() },
      modifier = modifier,
      shape = RoundedCornerShape(size = 8.dp),
      contentPadding = PaddingValues(0.dp),
    ) {
      Text(
        text = previousSetText,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodySmall,
      )
    }
  }
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
private fun SetCompleteCheckbox(
  checkedState: MutableState<Boolean>,
  completeSet: (Boolean) -> Unit,
  startRestTimer: () -> Unit,
  isTimerRunning: Boolean,
  //  workoutViewModel: WorkoutViewModel,
  //  exerciseSets: ExerciseExerciseSets,
  //  setUUID: Uuid,
  //  set: Set,
) {
  var openTimerScreen by remember { mutableStateOf(false) }

  fun openTimerScreen(open: Boolean) {
    openTimerScreen = open
  }

  if (openTimerScreen && isTimerRunning) {
    TimerScreen(openDialog = { open -> openTimerScreen(open) })
  }

  Checkbox(
    checked = checkedState.value,
    onCheckedChange = {
      checkedState.value = it
      completeSet(it)
      startRestTimer()
      //        if (!workoutViewModel.isTimerRunning) {
      //          openTimerScreen(it)
      //        }
    },
  )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ExerciseWeightTextField(
  boxModifier: Modifier = Modifier,
  textFieldModifier: Modifier = Modifier,
  textFieldBackgroundColor: Color,
  exerciseWeightText: String,
  targetWeight: String?,
  onWeightChange: (newWeight: String) -> Unit,
  onImeAction: () -> Unit = {},
) {
  Box(
    modifier =
    boxModifier
      .padding(4.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(textFieldBackgroundColor)
      //      .align(Alignment.CenterVertically)
      //      .width(96.dp)
      .wrapContentSize(Alignment.Center),
    propagateMinConstraints = true,
    contentAlignment = Alignment.Center,
  ) {
    BasicTextField(
      value = exerciseWeightText,
      onValueChange = { onWeightChange(it.take(6).trim()) },
      modifier = textFieldModifier.padding(PaddingValues(8.dp)),
      textStyle =
        TextStyle(
          fontSize = MaterialTheme.typography.bodyLarge.fontSize,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground,
        ),
      singleLine = true,
      maxLines = 1,
      keyboardOptions =
      KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = { onImeAction() }),
      cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
    )
    if (!targetWeight.isNullOrBlank() && exerciseWeightText.isBlank()) {
      Text(text = targetWeight, color = MaterialTheme.colorScheme.outline)
    }
  }
}

@Composable
private fun ExerciseRepsTextField(
  boxModifier: Modifier = Modifier,
  textFieldModifier: Modifier = Modifier,
  textFieldBackgroundColor: Color,
  repetitionsText: String,
  targetReps: String?,
  onRepsChange: (newReps: String) -> Unit,
  onImeAction: () -> Unit = {},
) {
  Box(
    modifier =
    boxModifier
      .padding(4.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(textFieldBackgroundColor)
      //        .align(Alignment.CenterVertically)
        //        .width(96.dp)
        .wrapContentSize(Alignment.Center),
    propagateMinConstraints = true,
    contentAlignment = Alignment.Center,
  ) {
    BasicTextField(
      modifier = textFieldModifier.padding(PaddingValues(8.dp)),
      textStyle =
        TextStyle(
          fontSize = MaterialTheme.typography.bodyLarge.fontSize,
          textAlign = TextAlign.Center,
          color = MaterialTheme.colorScheme.onBackground,
        ),
      keyboardOptions =
      KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
      keyboardActions = KeyboardActions(onNext = { onImeAction() }),
      value = repetitionsText,
      singleLine = true,
      maxLines = 1,
      onValueChange = { onRepsChange(it.take(4).trim()) },
      cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
    )
    if (!targetReps.isNullOrBlank() && repetitionsText.isBlank()) {
      Text(text = targetReps, color = MaterialTheme.colorScheme.outline)
    }
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RIRTextField(
  boxModifier: Modifier = Modifier,
  textFieldModifier: Modifier = Modifier,
  textFieldBackgroundColor: Color,
  repetitionsInReserveText: String,
  targetRIR: String?,
  onRIRChange: (newRIR: String) -> Unit,
  onImeAction: () -> Unit = {},
) {
  val uneditedTextColor = MaterialTheme.colorScheme.onSecondaryContainer.copy()
  var textColor by remember { mutableStateOf(uneditedTextColor) }

  var changeTextColor by remember { mutableStateOf(false) }

  fun changeTextColor() {
    changeTextColor = true
  }

  if (changeTextColor) {
    textColor = MaterialTheme.colorScheme.onBackground
  }

  Box(
    modifier =
    boxModifier
      .padding(4.dp)
      .clip(RoundedCornerShape(8.dp))
      .background(textFieldBackgroundColor)
      //      .align(Alignment.CenterVertically)
        //        .width(40.dp)
        .wrapContentSize(Alignment.Center),
    propagateMinConstraints = true,
    contentAlignment = Alignment.Center,
  ) {
    BasicTextField(
      modifier = textFieldModifier.padding(PaddingValues(8.dp)),
      textStyle =
        TextStyle(
          fontSize = MaterialTheme.typography.bodyLarge.fontSize,
          textAlign = TextAlign.Center,
          color = textColor,
        ),
      singleLine = true,
      maxLines = 1,
      keyboardOptions =
      KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = { onImeAction() }),
      value = repetitionsInReserveText,
      onValueChange = { onRIRChange(it.take(4).trim()) },
      cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
    )
    if (!targetRIR.isNullOrBlank() && repetitionsInReserveText.isBlank()) {
      Text(text = targetRIR, color = MaterialTheme.colorScheme.outline)
    }
  }
}

private fun getSetIconText(set: Set, setIndex: Int, setTypeAbbreviation: String): String =
  if (set.setTypeID == com.example.myapplication.data.model.SetType.normalSet) {
    setIndex.inc().toString()
  } else {
    setTypeAbbreviation
  }

private fun getRatePerceivedEffort(set: Set): String = doubleFormatted(set.ratePerceivedEffort)

private fun getRepetitionsInReserve(set: Set): String = doubleFormatted(set.repetitionsInReserve)

private fun getRepetitions(set: Set): String =
  when (set.repetitions) {
    null -> ""
    else -> set.repetitions.toString()
  }

private fun getExerciseWeight(set: Set, weightUnit: String): String =
  doubleFormatted(
    if (weightUnit == MassUnit.POUNDS.abbreviation) set.weightPounds else set.weightKilograms
  )

@Preview
@Composable
private fun SetContentPreview() {
  TestTextField2()
}

@Composable
fun TestTextField2() {
  val state = rememberTextFieldState("")

  BasicTextField(state = state)
  if (state.text.isEmpty()) {
    Text(text = "Enter your name", color = Color.Gray)
  }
}
