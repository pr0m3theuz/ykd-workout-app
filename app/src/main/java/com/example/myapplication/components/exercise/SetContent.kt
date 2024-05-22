package com.example.myapplication.components.exercise

import android.os.Build
import android.util.Log
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
import androidx.compose.material3.Button
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.SetTypeOptionsMenu
import com.example.myapplication.components.WorkoutViewModel
import com.example.myapplication.components.setRestTimer.TimerScreen
import com.example.myapplication.data.model.ExerciseExerciseSets
import com.example.myapplication.data.model.MassUnit
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.convertKgToPound
import com.example.myapplication.data.model.convertPoundToKg
import com.example.myapplication.data.model.setTextRIR
import com.example.myapplication.utils.checkDoubleInput
import com.example.myapplication.utils.checkIntInput
import com.example.myapplication.utils.doubleFormatted
import org.koin.androidx.compose.koinViewModel

// TODO Add ability to rearrange sets
// TODO Add placeholder text for target weight, reps, & rpe/rir
// TODO FocusRequester.requestFocus() to tab to next field doesn't work

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SetContent(
  exerciseSets: ExerciseExerciseSets,
  setIndex: Int,
  set: Set,
  getWeightUnit: () -> String,
  populatePreviousSetField: () -> Set?,
  updateSetReps: (reps: String) -> Unit,
  updateSetRIR: (rir: String) -> Unit,
  updateSetWeight: (weight: String) -> Unit,
  isTimerRunning: Boolean,
  updateSetType: (setTypeID: Int) -> Unit,
  setComplete: (Boolean) -> Unit,
  startSetCompletedTimer: () -> Unit,
  //  workoutViewModel: WorkoutViewModel = koinViewModel()
) {
  val checkedState = remember { mutableStateOf(set.setCompleted) }
  val weightUnit = getWeightUnit()

  val previousSet = populatePreviousSetField()

  val previousSetText by remember { mutableStateOf(previousSet?.setTextRIR(weightUnit) ?: "") }

  val showKeyboard = remember { mutableStateOf(true) }
  val focusRequester = remember { FocusRequester() }
  val keyboard = LocalSoftwareKeyboardController.current

  val repsFocusRequester = remember { FocusRequester() }
  val rirFocusRequester = remember { FocusRequester() }

  val textFieldBackgroundColor = MaterialTheme.colorScheme.secondaryContainer
  val textColor = MaterialTheme.colorScheme.onSecondaryContainer

  var exerciseWeightFocused by remember { mutableStateOf(false) }
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
    if (getWeightUnit() == MassUnit.POUNDS.abbreviation) {
      updateWeight(previousSet?.weightPounds.toString())
    } else {
      updateWeight(previousSet?.weightKilograms.toString())
    }
    updateReps(previousSet?.repetitions.toString())
    updateRIR(previousSet?.repetitionsInReserve.toString())
  }

  SetContentFrame(
    setTypeOptionsMenu = {
      SetType(
        updateSetType = { setTypeID -> updateSetType(setTypeID) },
        setIndex = setIndex.inc().toString(),
        setTypeID = set.setTypeID,
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
    weightTextField = {
      ExerciseWeightTextField(
        //        boxModifier = Modifier.align(Alignment.CenterVertically),
        //        modifier = Modifier.focusRequester(focusRequester),
        boxModifier = Modifier.width(96.dp),
        textFieldBackgroundColor = textFieldBackgroundColor,
        exerciseWeightText = exerciseWeightText,
        targetWeight = doubleFormatted(set.targetWeight),
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
        onImeAction = {
          updateSetData()
          //          repsFocusRequester.requestFocus()
        },
      )
    },
    repsTextField = {
      ExerciseRepsTextField(
        boxModifier = Modifier.width(60.dp),
        textFieldBackgroundColor = textFieldBackgroundColor,
        //        modifier = Modifier.focusRequester(repsFocusRequester),
        repetitionsText = repetitionsText,
        targetReps = set.targetRepetitions,
        onRepsChange = { reps ->
          repetitionsText = checkIntInput(reps)
          set.repetitions = repetitionsText.toIntOrNull()
        },
        onImeAction = {
          updateSetData()
          //          rirFocusRequester.requestFocus()
        },
      )
    },
    rirTextField = { boxModifier ->
      RIRTextField(
        //        modifier = Modifier.focusRequester(rirFocusRequester),
        boxModifier = boxModifier.width(48.dp),
        textFieldBackgroundColor = textFieldBackgroundColor,
        repetitionsInReserveText = repetitionsInReserveText,
        targetRIR = doubleFormatted(set.targetRepetitionsInReserve),
        onRIRChange = { rir ->
          repetitionsInReserveText =
            when {
              rir.isBlank() -> ""
              else ->
                checkDoubleInput(rir)?.takeIf { it.toDouble() in 0.0..10.0 }?.toString()
                  ?: repetitionsInReserveText
            }
          set.repetitionsInReserve = repetitionsInReserveText.toDoubleOrNull()
        },
        onImeAction = { updateRIR(repetitionsInReserveText) },
      )
    },
  )
}

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SetContentFrame(
  setTypeOptionsMenu: @Composable () -> Unit,
  previousSet: @Composable () -> Unit,
  setCompleteCheckbox: @Composable (boxModifier: Modifier) -> Unit,
  weightTextField: @Composable (boxModifier: Modifier) -> Unit,
  repsTextField: @Composable (boxModifier: Modifier) -> Unit,
  rirTextField: @Composable (boxModifier: Modifier) -> Unit,
  workoutViewModel: WorkoutViewModel = koinViewModel(),
) {
  val showKeyboard = remember { mutableStateOf(true) }
  val focusRequester = remember { FocusRequester() }
  val keyboard = LocalSoftwareKeyboardController.current

  val repsFocusRequester = remember { FocusRequester() }
  val rirFocusRequester = remember { FocusRequester() }

  val textFieldBackgroundColor = MaterialTheme.colorScheme.secondaryContainer
  val textColor = MaterialTheme.colorScheme.onSecondaryContainer

  var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }

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
    weightTextField(Modifier.align(Alignment.CenterVertically))

    repsTextField(Modifier.align(Alignment.CenterVertically))

    rirTextField(Modifier.align(Alignment.CenterVertically))

    setCompleteCheckbox(Modifier)
  }
}

@Composable
private fun SetType(updateSetType: (setTypeID: Int) -> Unit, setTypeID: Int, setIndex: String) {
  var expandedSetTypeOptionsDropdownMenu by remember { mutableStateOf(false) }

  var iconText by remember {
    mutableStateOf(
      when (setTypeID) {
        1 -> setIndex
        2 -> "W"
        3 -> "D"
        4 -> "M"
        5 -> "MM"
        6 -> "G"
        7 -> "A"
        8 -> "F"
        else -> {
          setIndex
        }
      }
    )
  }

  fun getSetTypeColor(setTypeID: Int) {
    /*TODO*/
  }

  var iconColor: Color = Color.White

  fun changeIconColor() {
    //    iconColor =
  }

  Box(modifier = Modifier.wrapContentSize(Alignment.Center), propagateMinConstraints = true) {
    Button(
      modifier = Modifier.padding(8.dp).size(32.dp),
      shape = RoundedCornerShape(size = 8.dp),
      contentPadding = PaddingValues(0.dp),
      onClick = { expandedSetTypeOptionsDropdownMenu = true },
    ) {
      Text(
        text = iconText,
        color = MaterialTheme.colorScheme.onPrimary,
        softWrap = false,
        maxLines = 1,
        minLines = 1,
      )
    }

    SetTypeOptionsMenu(
      // selectedSetType = exerciseSet.setType, TODO Not if this is needed
      expandMenu = { expandedSetTypeOptionsDropdownMenu },
      onDismiss = { expandedSetTypeOptionsDropdownMenu = false },
      updateSetType = { setTypeID -> updateSetType(setTypeID) },
      changeIconText = { abbreviation ->
        iconText = abbreviation
        Log.i("setTypeAbbreviation", "setType iconText: $iconText")
      },
      changeIconColor = { /*changeIconColor()*/},
    )
  }
}

@Composable
private fun PreviousSet(
  boxModifier: Modifier,
  previousSetText: String,
  copyPreviousSetData: () -> Unit,
) {
  Box(/*modifier = Modifier.width(110.dp)*/ ) {
    TextButton(
      onClick = { copyPreviousSetData() },
      modifier = boxModifier,
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
  //  setUUID: RealmUUID,
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
    )
    if (!targetRIR.isNullOrBlank() && repetitionsInReserveText.isNullOrBlank()) {
      Text(text = targetRIR, color = MaterialTheme.colorScheme.outline)
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
        KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
      keyboardActions = KeyboardActions(onDone = { onImeAction() }),
      value = repetitionsText,
      singleLine = true,
      maxLines = 1,
      onValueChange = { onRepsChange(it.take(4).trim()) },
      cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground),
    )
    if (!targetReps.isNullOrBlank() && repetitionsText.isNullOrBlank()) {
      Text(text = targetReps, color = MaterialTheme.colorScheme.outline)
    }
  }
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
    )
    if (!targetWeight.isNullOrBlank() && exerciseWeightText.isNullOrBlank()) {
      Text(text = targetWeight, color = MaterialTheme.colorScheme.outline)
    }
  }
}

private fun getSetIconText(set: Set, setIndex: Int, setTypeAbbreviation: String): String =
  if (set.setTypeID == 1) {
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
