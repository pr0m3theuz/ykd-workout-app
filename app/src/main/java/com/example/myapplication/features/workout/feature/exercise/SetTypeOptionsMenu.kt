package com.example.myapplication.features

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.example.myapplication.common.domain.model.SetType
import com.example.myapplication.common.util.createAnnotatedLinkString

// TODO Takes an Array List of Set Types
// TODO Figure out colors for either text or box background
// @Preview
@OptIn(ExperimentalTextApi::class)
@Composable
fun SetTypeOptionsMenu(
  expandMenu: () -> Boolean,
  onDismiss: () -> Unit,
  updateSetType: (setTypeID: Int) -> Unit,
  changeIconText: (iconText: String) -> Unit,
) {

  @Composable
  fun createSetTypes(): ArrayList<SetType> {

    val menu = ArrayList<SetType>()

    SetType.textLinkStyles =
      TextLinkStyles(
        style =
          SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic,
            textDecoration = TextDecoration.Underline,
          )
      )

    val normalSet = SetType.normalSet
    normalSet.lightTextColor = MaterialTheme.colorScheme.onSurface
    normalSet.darkTextColor = MaterialTheme.colorScheme.onSurface
    menu.add(normalSet)

    val warmUpSet = SetType.warmUpSet
    menu.add(warmUpSet)

    val dropSet = SetType.dropSet
    menu.add(dropSet)

    val myoRepSet = SetType.myoRepSet
    menu.add(myoRepSet)

    val myoRepMatchSet = SetType.myoRepMatchSet
    menu.add(myoRepMatchSet)

    val gaintSet = SetType.giantSet
    menu.add(gaintSet)

    val amrapSet = SetType.amrapSet
    amrapSet.lightTextColor = MaterialTheme.colorScheme.error
    amrapSet.darkTextColor = MaterialTheme.colorScheme.error
    menu.add(amrapSet)

    val failureSet = SetType.failureSet
    failureSet.lightTextColor = MaterialTheme.colorScheme.error
    failureSet.darkTextColor = MaterialTheme.colorScheme.error
    menu.add(failureSet)

    return menu
  }

  val menuItems = createSetTypes()

  DropdownMenu(expanded = expandMenu(), onDismissRequest = { onDismiss() }) {
    /*     DropdownMenuItem(
    text = { Text("") },
    leadingIcon = {
        Icon(Icons.Outlined.ArrowBack, contentDescription = "Localized description")
    },
    onClick = { */
    /* onDismiss(); onClickArrowBack() */
    /* }
    )*/

    menuItems.forEachIndexed { _, setType ->
      var openSetTypeDescriptionDialog by remember { mutableStateOf(false) }
      if (openSetTypeDescriptionDialog)
        SetTypeDescriptionDialog(
          setType = setType,
          onDismissRequest = { openSetTypeDescriptionDialog = false },
        )

      DropdownMenuItem(
        text = { Text(setType.name) },
        leadingIcon = {
          Text(
            text = setType.abbreviation ?: setType.name[0].toString(),
            color =
            if (isSystemInDarkTheme()) {
              setType.darkTextColor ?: MaterialTheme.colorScheme.primary
            } else {
              setType.lightTextColor ?: MaterialTheme.colorScheme.primary
            },
            style = TextStyle(fontWeight = FontWeight.Bold),
          )
        },
        trailingIcon = {
          IconButton(onClick = { openSetTypeDescriptionDialog = true }) {
            Icon(
              Icons.Outlined.QuestionMark,
              contentDescription = "Description of ${setType.name}",
              tint = MaterialTheme.colorScheme.onSurface,
            )
          }
        },
        onClick = {
          onDismiss()
          updateSetType(setType.setTypeID)
          changeIconText(setType.abbreviation ?: setType.name[0].toString())
        },
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTypeDescriptionDialog(setType: SetType, onDismissRequest: () -> Unit) {

  val textLinkStyles =
    TextLinkStyles(
      style =
      SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontStyle = FontStyle.Italic,
        textDecoration = TextDecoration.Underline,
      )
    )

  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    confirmButton = { /*TODO*/ },
    title = { Text(text = setType.name) },
    text = {
      Text(
        text = createAnnotatedLinkString(setType.description, textLinkStyles),
        style = MaterialTheme.typography.bodySmall
      )
    },
  )
}
