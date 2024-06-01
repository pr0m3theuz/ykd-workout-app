package com.example.myapplication.components.workoutDialogs.workingSets

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.components.workoutDialogs.AddSetText
import com.example.myapplication.ui.theme.White
import kotlinx.coroutines.launch

@OptIn(
  ExperimentalMaterialApi::class,
  ExperimentalFoundationApi::class,
  ExperimentalMaterial3Api::class,
)
// @Preview
@Composable
fun WorkingSetLazyColumn(
  workingSetViewModel: WorkingSetViewModel = viewModel(),
  //  _workingSets: MutableList<Set>,
  //  removeSet: (workingSet: Set) -> Unit,
  addset: () -> Unit,
) {
  val listState = rememberLazyListState()
  // Remember a CoroutineScope to be able to launch
  val coroutineScope = rememberCoroutineScope()

  val workingSets by workingSetViewModel.sets.collectAsState()

  LazyColumn(
    state = listState,
    modifier = Modifier.fillMaxWidth().height(235.dp),
    //  verticalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    items(count = workingSets.size, key = { index -> workingSets[index].lazyListItemID }) { index ->
      val workingSet = workingSets[index]

      SwipeToDismissBoxItem(
        removeSet = { workingSetViewModel.remove(workingSet) },
        SetLayout = { WorkingSetLayout(setIndex = index, workingSet = workingSet) },
      )
    }
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    TextButton(
      onClick = {
        addset()
        coroutineScope.launch {
          // Animate scroll to the last item
          listState.animateScrollToItem(index = workingSets.size.dec())
        }
      }
    ) {
      AddSetText()
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBoxItem(removeSet: () -> Unit, SetLayout: @Composable () -> Unit) {
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
        Modifier.fillMaxSize().background(color).padding(horizontal = 12.dp),
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
      backgroundColor = MaterialTheme.colorScheme.background,
      shape = RoundedCornerShape(CornerSize(0f)),
    ) {
      SetLayout()
    }
  }
}
