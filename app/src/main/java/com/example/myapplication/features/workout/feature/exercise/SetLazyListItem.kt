package com.example.myapplication.features.workout.feature.exercise

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
import androidx.compose.material.icons.filled.Add
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
import com.example.myapplication.common.domain.model.Set
import com.example.myapplication.common.domain.model.WorkoutComponent
import com.example.myapplication.features.ConfirmCompletedSetDeletionDialog
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SetLazyListItem(
  exerciseSets: WorkoutComponent,
  index: Int,
  set: Set,
  addSetAtIndex: () -> Unit = {},
  removeSet: () -> Unit,
  setContent: @Composable (/*exerciseSets: ExerciseExerciseSets, setIndex: Int, set: Set*/) -> Unit,
) {
  //    Log.i("SetColumn()", "$set")

  val coroutineScope = rememberCoroutineScope()
  val setCompletedState by remember { mutableStateOf(set.setCompleted) }
  val openDialog = remember { mutableStateOf(true) }

  val dismissState =
    rememberDismissState(
      confirmStateChange = {
        when (it) {
          DismissValue.DismissedToStart -> {
            //        openDialog.value = true
            true
          }

          DismissValue.DismissedToEnd -> {
            addSetAtIndex()
            false
          }

          DismissValue.Default -> false
        }
      }
    )

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
      { delete -> deleteCompletedSet(delete) },
    )
  } else if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
    addSetAtIndex()
  }

  // TODO Replace Fractional Threshold with Swipeable
  SwipeToDismiss(
    state = dismissState,
    directions = setOf(DismissDirection.EndToStart, DismissDirection.StartToEnd),
    dismissThresholds = { dismissDirection ->
      FractionalThreshold(
        when (dismissDirection) {
          DismissDirection.EndToStart -> 0.66f
          else -> 0.5f
        }
      )
    },
    background = {
      val color by
      animateColorAsState(
        when (dismissState.targetValue) {
          DismissValue.Default -> MaterialTheme.colorScheme.primary
          DismissValue.DismissedToStart -> MaterialTheme.colorScheme.error
          DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.primary
        },
        label = "Background Color",
      )
      val iconTint by
      animateColorAsState(
        when (dismissState.targetValue) {
          DismissValue.Default -> MaterialTheme.colorScheme.onPrimary
          DismissValue.DismissedToStart -> MaterialTheme.colorScheme.onError
          DismissValue.DismissedToEnd -> MaterialTheme.colorScheme.onPrimary
        },
        label = "Icon Color",
      )

      val alignment =
        when (dismissState.dismissDirection) {
          DismissDirection.EndToStart -> Alignment.CenterEnd
          DismissDirection.StartToEnd -> Alignment.CenterStart
          else -> Alignment.CenterEnd
        }

      val icon =
        when (dismissState.dismissDirection) {
          DismissDirection.StartToEnd -> Icons.Default.Add
          DismissDirection.EndToStart -> Icons.Default.Delete
          else -> Icons.Default.Delete
        }
      val scale by
      animateFloatAsState(
        // TODO Animatiton Spec: Spring?
        if (dismissState.targetValue == DismissValue.Default) 1f else 1.33f,
        label = "Delete Icon Scale",
      )

      val contentDescription =
        when (dismissState.dismissDirection) {
          DismissDirection.StartToEnd -> "Add Set"
          DismissDirection.EndToStart -> "Delete Set"
          else -> "Delete Set"
        }

      Box(
        Modifier
          .fillMaxSize()
          .background(color)
          .padding(horizontal = 12.dp),
        contentAlignment = alignment,
      ) {
        Icon(
          icon,
          tint = iconTint,
          contentDescription = contentDescription,
          modifier = Modifier.scale(scale),
        )
      }
    },
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
      shape = RoundedCornerShape(CornerSize(0f)),
    ) {
      setContent(/*exerciseSets, index, set*/)
      //      SetContent(exerciseSets = exerciseSets, index, set)
    }
  }
}
