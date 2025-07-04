package com.example.myapplication.features.workout.feature.exercise

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.lerp
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
  addSet: () -> Unit = {},
  removeSet: () -> Unit = {},
  setContent: @Composable () -> Unit,
) {
  //    Log.i("SetColumn()", "$set")

  val coroutineScope = rememberCoroutineScope()
  val setCompletedState by remember { mutableStateOf(set.setCompleted) }
  var openDialog by remember { mutableStateOf(false) }

  val swipeToDismissBoxState =
    rememberSwipeToDismissBoxState(
      confirmValueChange = {
        when (it) {
          SwipeToDismissBoxValue.Settled -> {}
          SwipeToDismissBoxValue.StartToEnd -> {
            addSet()
          }

          SwipeToDismissBoxValue.EndToStart -> {
            when (setCompletedState) {
              true -> {
                openDialog = true
              }

              false -> {
                removeSet()
              }
            }
          }
        }
        it != SwipeToDismissBoxValue.StartToEnd
      }
    )

  swipeToDismissBoxState

  fun deleteCompletedSet(delete: Boolean) {
    if (delete) {
      removeSet()
    } else {
      coroutineScope.launch {
        swipeToDismissBoxState.dismiss(SwipeToDismissBoxValue.Settled)
      }
    }
  }

  if (openDialog) {
    ConfirmCompletedSetDeletionDialog(
      { openDialog = it },
      { delete -> deleteCompletedSet(delete) },
    )
  }

  SwipeToDismissBox(
    state = swipeToDismissBoxState,
    enableDismissFromStartToEnd = false, // TODO
    enableDismissFromEndToStart = true,
    backgroundContent = {
      val scale by
      animateFloatAsState(
        if (swipeToDismissBoxState.dismissDirection != SwipeToDismissBoxValue.Settled) 1.2f else 0.8f,
        label = "Icon Scale",
      )

      when (swipeToDismissBoxState.dismissDirection) {
        SwipeToDismissBoxValue.Settled -> {}
        SwipeToDismissBoxValue.EndToStart -> {
          val initialColor = MaterialTheme.colorScheme.surface
          val targetColor = MaterialTheme.colorScheme.error
          Icon(
            Icons.Filled.Delete,
            tint = MaterialTheme.colorScheme.onError,
            contentDescription = "Delete Set",
            modifier =
              Modifier
                .fillMaxSize()
                .background(lerp(initialColor, targetColor, swipeToDismissBoxState.progress))
                .wrapContentSize(Alignment.CenterEnd)
                .padding(horizontal = 12.dp),
          )
        }

        SwipeToDismissBoxValue.StartToEnd -> {
          val initialColor = MaterialTheme.colorScheme.surface
          val targetColor = MaterialTheme.colorScheme.primary

          Icon(
            Icons.Filled.Add,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = "Add Set",
            modifier =
              Modifier
                .fillMaxSize()
                .drawBehind {
                  drawRect(lerp(initialColor, targetColor, swipeToDismissBoxState.progress))
                }
                .wrapContentSize(Alignment.CenterStart)
                .padding(horizontal = 12.dp),
          )
        }
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
      setContent()
    }
  }
}
