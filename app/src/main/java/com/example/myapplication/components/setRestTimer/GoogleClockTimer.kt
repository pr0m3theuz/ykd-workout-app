package com.example.myapplication.components.setRestTimer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
// @Preview
@Composable
fun setGoogleClockTimer(
    openDialog: () -> Unit,
    setTimer: () -> Unit,
    time: String,
    add: (input: String) -> Unit,
    remove: () -> Unit,
    removeAll: () -> Unit,
    visible: Boolean
) {
//    var time by remember { mutableStateOf("") }
//    Log.i("setGoogleClockTimer() time", time)
//    Log.i("setGoogleClockTimer() timeString", time)

    val buttonModifier = Modifier
        .size(80.dp)
//        .wrapContentSize(align = Alignment.Center)
        .padding(0.dp)

    val iconSizeModifier = Modifier.size(24.dp)

    val buttonTextSize = 36.sp
    val buttonTextWeight = FontWeight(375)

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            onClick = { add("1") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "1",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
        Button(
            onClick = { add("2") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "2",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
        Button(
            onClick = { add("3") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "3",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            onClick = { add("4") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "4",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
        Button(
            onClick = { add("5") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "5",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
        Button(
            onClick = { add("6") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "6",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            onClick = { add("7") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "7",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
        Button(
            onClick = { add("8") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "8",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
        Button(
            onClick = { add("9") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "9",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
    }
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Button(
            onClick = {
                add("00")
            },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "00",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight,
                overflow = TextOverflow.Visible
            )
        }
        Button(
            onClick = { add("0") },
            modifier = buttonModifier,
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                "0",
                fontSize = buttonTextSize,
                fontWeight = buttonTextWeight
            )
        }
        Button(
            onClick = { remove() },
            modifier =
            Modifier.combinedClickable(
                onLongClickLabel = "Clear All",
//                            TODO May not work
                onLongClick = { removeAll() },
                onClickLabel = "Backspace",
                onClick = { remove() }
            ).then(buttonModifier),
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp)
        ) {
            Icon(
                Icons.Outlined.Backspace,
                contentDescription = "Localized description",
                modifier = iconSizeModifier
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Button(
            onClick = { /* TODO */ },
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
                        openDialog()
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
    }
}
