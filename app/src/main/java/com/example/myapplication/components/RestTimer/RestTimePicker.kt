package com.example.myapplication.components.RestTimer

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

/*

@Preview
@Composable
fun RestTimePicker() {

/*    val activity = LocalContext.current

    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }

    val picker = TimePickerDialog(
        activity,
        { _, mHour: Int, mMinute: Int ->
            mTime.value = "$mHour:$mMinute"
        },
        mHour,
        mMinute,
        true
    )

    picker.show()*/


//    val timePicker =
//        MaterialTimePicker.Builder()
//            .setTimeFormat(TimeFormat.CLOCK_12H)
//            .setHour(12)
//            .setMinute(10)
//            .setTitleText("Set Rest time")
//            .build()
//
//    timePicker.addOnPositiveButtonClickListener {
//        val hour = timePicker.hour
//        val minute = timePicker.minute
//        // Do something with time
//    }
//
//    timePicker.show(activity.supportFragmentManager, "tag")
}

*/

@Preview
@Composable
fun GoogleClockTimer() {
    var timeToDisplay by remember { mutableStateOf(TextFieldValue("126510")) }

    val buttonModifier = Modifier
        .size(50.dp)
//        .wrapContentSize(align = Alignment.Center)
        .padding(0.dp)

    Column {
        Row(modifier = Modifier.height(40.dp)) {
            Text(
                text = timeToDisplay.text.substring(0, 2) /*String.format(
                    "%s", *//* %sm %ss *//*
                    timeToDisplay.text.substring(0, 2)*//*,
                    timeToDisplay.text.substring(2, 4),
                    timeToDisplay.text.substring(4, 6)*//*
                )*/,
                modifier = Modifier.padding(0.dp).align(alignment = Alignment.Top),
                fontSize = 6.em
            )
            Text(
                text = String.format(
                    "h"
                ),
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    .padding(top = 5.dp, end = 8.dp),
                fontSize = 3.em,
                fontWeight = FontWeight.Light
            )
            Text(
                text = timeToDisplay.text.substring(2, 4),
                modifier = Modifier.padding(0.dp).align(alignment = Alignment.Top),
                fontSize = 6.em
            )
            Text(
                text = String.format(
                    "m"
                ),
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    .padding(top = 5.dp, end = 8.dp),
                fontSize = 3.em,
                fontWeight = FontWeight.Light
            )
            Text(
                text = timeToDisplay.text.substring(4, 6),
                modifier = Modifier.padding(0.dp).align(alignment = Alignment.Top),
                fontSize = 6.em
            )
            Text(
                text = String.format(
                    "s"
                ),
                modifier = Modifier.align(alignment = Alignment.CenterVertically)
                    .padding(top = 5.dp),
                fontSize = 3.em,
                fontWeight = FontWeight.Light
            )
        }

        Row(
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("1")
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("2")
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("3")
            }
        }
        Row {
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("4")
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("5")
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("6")
            }
        }
        Row {
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("7")
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("8")
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("9")
            }
        }
        Row {
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "00",
//                    fontSize = 3.em,
                    overflow = TextOverflow.Visible
                )
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("0")
            }
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Outlined.Backspace,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }
        Row {
            Button(
                onClick = { /* TODO */ },
                modifier = buttonModifier,
                shape = ShapeDefaults.ExtraLarge,
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.IconSize)
                )
            }
        }
    }
}
