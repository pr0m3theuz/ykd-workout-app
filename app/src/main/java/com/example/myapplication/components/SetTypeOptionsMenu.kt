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
import com.example.myapplication.data.SetTypeModel
import com.example.myapplication.newID
import com.example.myapplication.ui.theme.White

private val questionMarkColor = White

private val setTypes = create()

// TODO Takes an Array List of Set Types
// @Preview
@Composable
fun SetTypeOptionsMenu(
    menuItems: List<SetTypeModel> = setTypes.toList(),
    // selectedSetType: SetTypeModel, TODO Not if this is needed
    expandMenu: () -> Boolean,
    onDismiss: () -> Unit
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

        menuItems.forEachIndexed { index, setTypeModel ->
            DropdownMenuItem(
                text = { Text(setTypeModel.name) },
                leadingIcon = {
                    Text(
                        setTypeModel.name.get(0).toString(),
                        color = setTypeModel.textColor
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
                onClick = { /* TODO */ } // TODO Assign Set Type to Set
            )
        }
    }
}

private fun create(): ArrayList<SetTypeModel> {
    var menu = ArrayList<SetTypeModel>()

    menu.add(
        SetTypeModel(
            newID(),
            "Normal",
            description = "Normal",
            textColor = Color.Black
        )
    )

    menu.add(
        SetTypeModel(
            newID(),
            "Warm Up Set",
            description = /*TODO*/"",
            textColor = Color(255, 178, 0) /* Color: Orange */
        )
    )

    menu.add(
        SetTypeModel(
            newID(),
            "Drop Set",
            description = /*TODO*/"",
            textColor = Color(108, 74, 182) /* Color: Purple */
        )
    )

    menu.add(
        SetTypeModel(
            newID(),
            "Myo Rep Set",
            description = /*TODO*/"",
            textColor = Color.Cyan
        )
    )

    menu.add(
        SetTypeModel(
            newID(),
            "Giant Set",
            description = /*TODO*/"",
            textColor = Color.Blue
        )
    )

    menu.add(
        SetTypeModel(
            newID(),
            "AMRAP Set",
            description = /*TODO*/"",
            textColor = Color.Magenta
        )
    )

    menu.add(
        SetTypeModel(
            newID(),
            "Failure Set",
            description = /*TODO*/"",
            textColor = Color.Red
        )
    )

    return menu
}
