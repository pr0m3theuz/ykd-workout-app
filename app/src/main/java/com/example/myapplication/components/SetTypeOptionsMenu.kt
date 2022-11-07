package com.example.myapplication.components

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.myapplication.data.SetTypeModel
import com.example.myapplication.ui.theme.MetallicOrange
import com.example.myapplication.ui.theme.White

private val questionMarkColor = White

private val setTypes = mutableListOf<SetTypeModel>()

// TODO Takes an Array List of Set Types
// @Preview
@Composable
fun SetTypeOptionsMenu(
    menuItems: List<SetTypeModel> = setTypes,
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
        DropdownMenuItem(
            text = { Text("Default/Straight Set") },
            leadingIcon = { Text("Warm-up Set".get(0).toString(), color = MetallicOrange) },
            trailingIcon = {
                // TODO Change
            },
            // TODO Change to User's Defaults
            onClick = { /* TODO */ }
        )
        menuItems.forEachIndexed { index, setTypeModel ->
            DropdownMenuItem(
                text = { Text(setTypeModel.name) },
                leadingIcon = { Text(setTypeModel.name, color = setTypeModel.textColor) },
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
