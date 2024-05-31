package com.example.myapplication.components.setRestTimer

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalAnimationApi::class)
// @Preview
@Composable
fun TimerNumberPad(
  openDialog: () -> Unit,
  setTimer: () -> Unit,
  add: (input: String) -> Unit,
  remove: () -> Unit,
  removeAll: () -> Unit,
  visible: Boolean,
) {
  //    var time by remember { mutableStateOf("") }
  //    Log.i("setGoogleClockTimer() time", time)
  //    Log.i("setGoogleClockTimer() timeString", time)

  val buttonModifier =
    Modifier.requiredSize(80.dp)
      /*.aspectRatio(0.5F)*/
      //        .wrapContentSize(align = Alignment.Center)
      .padding(0.dp)

  val iconSizeModifier = Modifier.size(24.dp)

  val buttonTextSize = 36.sp
  val buttonTextWeight = FontWeight(375)

  val numbers = (1 until 10).toList().chunked(3)

  Row(modifier = Modifier.requiredWidth(248.dp), horizontalArrangement = Arrangement.Center) {
    LazyVerticalGrid(
      columns = GridCells.Fixed(3),
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
      numbers.forEachIndexed { index, items ->
        items(
          items,
          // not required as it is the default
          span = { GridItemSpan(1) },
        ) {
          BackspaceButtonOnTap(
            onClick = { add(it.toString()) },
            modifier = buttonModifier,
            //                        shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp),
          ) {
            Text(it.toString(), fontSize = buttonTextSize, fontWeight = buttonTextWeight)
          }

          /*Button(
              onClick = { add(it.toString()) },
              modifier = buttonModifier,
              shape = RoundedCornerShape(100),
              contentPadding = PaddingValues(0.dp)
          ) {
              Text(
                  it.toString(),
                  fontSize = buttonTextSize,
                  fontWeight = buttonTextWeight
              )
          }*/
        }
      }
    }
  }

  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    BackspaceButtonOnTap(
      onClick = { add("00") },
      modifier = buttonModifier,
      //            shape = RoundedCornerShape(100),
      contentPadding = PaddingValues(0.dp),
    ) {
      Text(
        text = "00",
        fontSize = buttonTextSize,
        fontWeight = buttonTextWeight,
        overflow = TextOverflow.Visible,
      )
    }
    BackspaceButtonOnTap(
      onClick = { add("0") },
      modifier = buttonModifier,
      //            shape = RoundedCornerShape(100),
      contentPadding = PaddingValues(0.dp),
    ) {
      Text("0", fontSize = buttonTextSize, fontWeight = buttonTextWeight)
    }

    BackspaceButtonOnTap(
      onClick = { remove() },
      onLongClick = { removeAll() },
      //            shape = RoundedCornerShape(roundedCorners),
      contentPadding = PaddingValues(0.dp),
      modifier = buttonModifier,
    ) {
      Icon(
        Icons.AutoMirrored.Outlined.ArrowBack,
        contentDescription = "Localized description",
        modifier = iconSizeModifier,
      )
    }

    /* BackspaceButton(
        onClick = {
            remove()
        },
        onLongClick = {
            removeAll()
        },
        shape = RoundedCornerShape(roundedCorners),
        contentPadding = PaddingValues(0.dp),
        modifier = buttonModifier
    ) {
        Icon(
            Icons.Outlined.Backspace,
            contentDescription = "Localized description",
            modifier = iconSizeModifier
        )
    }*/
  }

  /*Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
  Button(
      onClick = { */
  /* TODO */
  /* },
      modifier = buttonModifier,
      shape = RoundedCornerShape(100),
      contentPadding = PaddingValues(0.dp)
  ) {
      Icon(
          Icons.Outlined.Delete,
          contentDescription = "Localized description",
          modifier = iconSizeModifier
      )
  }
  Box(modifier = Modifier.width(80.dp).padding(0.dp)) {
      this@Row.AnimatedVisibility(
          visible = visible,
          enter = scaleIn(animationSpec = tween(500)),
          exit = scaleOut(animationSpec = tween(500))
      ) {
          Button(
              onClick = {
                  setTimer()
                  openDialog(*/
  /**/
  /*)
                  },
                  modifier = buttonModifier,
                  shape = RoundedCornerShape(100),
                  contentPadding = PaddingValues(0.dp)
              ) {
                  Icon(
                      Icons.Filled.PlayArrow,
                      contentDescription = "Localized description",
                      modifier = iconSizeModifier
                  )
              }
          }
      }
      Spacer(modifier = Modifier.width(80.dp))
  }*/
}
