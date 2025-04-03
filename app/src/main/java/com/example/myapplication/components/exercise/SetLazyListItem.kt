package com.example.myapplication.components.exercise

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.example.myapplication.components.ConfirmCompletedSetDeletionDialog
import com.example.myapplication.data.model.Set
import com.example.myapplication.data.model.WorkoutComponent
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetLazyListItem(
  exerciseSets: WorkoutComponent,
  index: Int,
  set: Set,
  removeSet: () -> Unit,
  setContent: @Composable (/*exerciseSets: ExerciseExerciseSets, setIndex: Int, set: Set*/) -> Unit,
) {
  //    Log.i("SetColumn()", "$set")

  val coroutineScope = rememberCoroutineScope()
  val setCompletedState by remember { mutableStateOf(set.setCompleted) }
  val openDialog = remember { mutableStateOf(true) }

  val dismissState = rememberDismissState(confirmStateChange = { true })

  fun deleteCompletedSet(delete: Boolean) {
    if (delete) {
      removeSet()
    } else {
      coroutineScope.launch {
        dismissState.animateTo(targetValue = DismissValue.Default, anim = TweenSpec())
      }
    }
  }

  fun openConfirmCompletedSetDeletionDialog(open: Boolean) {
    openDialog.value = open
  }

  if (!setCompletedState and dismissState.isDismissed(DismissDirection.EndToStart)) {
    removeSet()
  } else if (setCompletedState and dismissState.isDismissed(DismissDirection.EndToStart)) {
    ConfirmCompletedSetDeletionDialog(
      { open -> openConfirmCompletedSetDeletionDialog(open) },
      { delete -> deleteCompletedSet(delete) }
    )
  }

  // TODO Replace Fractional Threshold with Swipeable
  SwipeToDismiss(
    state = dismissState,
    directions = setOf(DismissDirection.EndToStart),
    dismissThresholds = { dismissDirection ->
      FractionalThreshold(if (dismissDirection == DismissDirection.EndToStart) 0.5f else 1f)
    },
    background = {
      val color by
      animateColorAsState(
        when (dismissState.targetValue) {
          DismissValue.Default -> MaterialTheme.colorScheme.primary
          DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error
          DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
        },
        label = "Background Color"
      )
      val alignment = Alignment.CenterEnd
      val icon = Icons.Filled.Delete
      val scale by
      animateFloatAsState(
        // TODO Animatiton Spec: Spring?
        if (dismissState.targetValue == DismissValue.Default) 0.8f else 1.2f,
        label = "Delete Icon Scale",
      )

      Box(
        Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 12.dp),
        contentAlignment = alignment
      ) {
        Icon(
          icon,
          tint = MaterialTheme.colorScheme.onError,
          contentDescription = "Localized description",
          modifier = Modifier.scale(scale)
        )
      }
    }
  ) {
    Card(
      backgroundColor =
      if (setCompletedState) {
        when (isSystemInDarkTheme()) {
          true -> MaterialTheme.colorScheme.tertiaryContainer
          false -> MaterialTheme.colorScheme.inversePrimary
        }
      } else {
        MaterialTheme.colorScheme.background
      },
      shape = RoundedCornerShape(CornerSize(0f))
    ) {
      setContent(/*exerciseSets, index, set*/)
//      SetContent(exerciseSets = exerciseSets, index, set)
    }
  }
}
