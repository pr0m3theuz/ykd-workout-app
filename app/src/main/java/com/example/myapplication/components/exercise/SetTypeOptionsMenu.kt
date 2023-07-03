package com.example.myapplication.components

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.myapplication.data.SetType
import com.example.myapplication.ui.theme.White

private val questionMarkColor = White

private val setTypes = create()

// TODO Takes an Array List of Set Types
// @Preview
@Composable
fun SetTypeOptionsMenu(
    menuItems: List<SetType> = setTypes.toList(),
    // selectedSetType: SetTypeModel, TODO Not if this is needed
    expandMenu: () -> Boolean,
    onDismiss: () -> Unit,
    updateSetType: (setType: SetType) -> Unit,
    changeIconText: () -> Unit,
    changeIconColor: () -> Unit,
) {
    DropdownMenu(
        expanded = expandMenu(),
        onDismissRequest = { onDismiss() }
    ) {
        /*     DropdownMenuItem(
                 text = { Text("") },
                 leadingIcon = {
                     Icon(Icons.Outlined.ArrowBack, contentDescription = "Localized description")
                 },
                 onClick = { *//* onDismiss(); onClickArrowBack() *//* }
        )*/

        menuItems.forEachIndexed { _, setType ->
            DropdownMenuItem(
                text = { Text(setType.name) },
                leadingIcon = {
                    Text(
                        setType.name[0].toString(),
                        color = if (setType.name == "Normal") {
                            Color.Black
                        } else {
                            setType.textColor
                        }
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { /* TODO doSomething() */ }) {
                        Icon(
                            Icons.Outlined.QuestionMark,
                            contentDescription = "Localized description"
                        )
                    }
                },
                onClick = {
                    onDismiss()
                    updateSetType(setType)
                    changeIconText()
                    changeIconColor()
                } // TODO Assign Set Type to Set
            )
        }
    }
}

private fun create(): ArrayList<SetType> {
    val menu = ArrayList<SetType>()

    menu.add(
        SetType(
            1,
            "Normal",
            description = "Normal",
            textColor = Color.White
        )
    )

    menu.add(
        SetType(
            2,
            "Warm Up Set",
            description = /*TODO*/"",
            textColor = Color(255, 178, 0) /* Color: Orange */
        )
    )

    menu.add(
        SetType(
            3,
            "Drop Set",
            description = /*TODO*/"",
            textColor = Color.LightGray /* Color: Purple */
        )
    )

    menu.add(
        SetType(
            4,
            "Myo Rep",
            description = /*TODO*/"",
            textColor = Color.Cyan
        )
    )

    menu.add(
        SetType(
            4,
            "Myo Rep Match",
            description = /*TODO*/"",
            textColor = Color.Cyan
        )
    )

    menu.add(
        SetType(
            6,
            "Giant Set",
            description = /*TODO*/"",
            textColor = Color.Blue
        )
    )

    menu.add(
        SetType(
            7,
            "AMRAP",
            description = /*TODO*/"",
            textColor = Color.Magenta
        )
    )

    menu.add(
        SetType(
            8,
            "Failure Set",
            description = /*TODO*/"",
            textColor = Color.Red
        )
    )

    return menu
}
