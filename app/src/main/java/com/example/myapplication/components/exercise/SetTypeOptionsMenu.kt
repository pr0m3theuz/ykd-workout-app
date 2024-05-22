package com.example.myapplication.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.example.myapplication.data.model.SetType

// TODO Takes an Array List of Set Types
// TODO Figure out colors for either text or box background
// @Preview
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

    menu.add(
      SetType(
        1,
        "Normal",
        description =
          buildAnnotatedString {
            append(
              "A set performed with the target weight to the target repetitions and the target RIR/RPE."
            )
          },
        lightTextColor = MaterialTheme.colorScheme.onSurface,
        darkTextColor = MaterialTheme.colorScheme.onSurface,
      )
    )

    menu.add(
      SetType(
        2,
        "Warm Up Set",
        description =
          buildAnnotatedString {
            append("How to warm up\n\n")

            // Option 1
            append("1. You CAN choose to do easy cardio for 5-10 minutes, or\n\n")

            // Option 2 - Bold "2ORM+"
            append(
              "2. Begin with a 2ORM+ weight for the first exercise and do it for around 10 reps\n" +
                "\n"
            )
            // Option 3 - Italicize "20RM+"
            append(
              "3. Do at least 1 weight halfway between your 2ORM+ and your planned working weight for a set of ~5 reps\n"
            )

            // Indented text with weight ranges
            append("\t\tThe heavier the lift, the more sets of 8,6,4, etc. you do\n")
            append("\t\t0-50lbs: 1 intermediate set\n")
            append("\t\t50-200: 2 intermediate sets\n")
            append("\t\t200-400: 3 intermediate sets\n")
            append("\t\tAs needed for bigger weights\n\n")

            // Option 4
            append("4. Do the working weight or 10% higher for 2-3 reps (potentiation)\n\n")

            // Option 5 - Emphasize "NEW"
            append("5. For each NEW exercise after, if same muscle, do just:\n")
            append("\t\ta) The 5 rep with weight halfway between 20RM+ and working weight\n")
            append("\t\tb) The working weight itself for 2-3 reps\n\n")

            // Add link to Youtube video
            //            addLink(text = "How to Warm Up for Muscle Growth Training | Hypertrophy
            // Made Simple #3", url = "https://www.youtube.com/watch?v=HDq-68SlPgQ")
          },
        darkTextColor = Color(255, 165, 61), /* Color: Orange */
        lightTextColor = Color(127, 71, 3),
      )
    )

    menu.add(
      SetType(
        3,
        "Drop Set",
        description =
          buildAnnotatedString {
            append(
              "A technique for continuing an exercise with a lower weight once the target repetitions or RIR/RPE has been achieved at a higher weight with minimal rest between sets. Reduce the load by 10% to 30%." +
                " \n\nhttps://www.youtube.com/watch?v=51TKExRtxZM"
            )
          },
        lightTextColor = Color(0, 0, 139),
        darkTextColor = Color(0, 204, 255), /* Color: Purple */
      )
    )

    menu.add(
      SetType(
        4,
        "Myo Rep",
        description =
          buildAnnotatedString {
            append(
              "Myo reps is a form of “rest pause” training popularized by Borge Fagerli from Norway. When performing the method you’re using a lighter weight, do a set close to failure and then take a brief pause between repetitions. " +
                "Example Myo-Reps Set\n\n" +
                "1. Start by doing an activation set: Use a weight that you can get between 12-15 reps with. Then do as many reps as you can until you’re 1-2 repetitions away from failure.\n\n" +
                "2. After the activation set, rest 10-15 seconds\n\n" +
                "3. After the short rest do another set until you’re 1-2 reps away from failure.\n\n" +
                "4. Then rest another 10-15 seconds\n\n" +
                "5. Repeat this process until you no longer can get the same amount of reps on all your “minisets”, or after you’ve reached 5 sets in total (4 minisets)."
            )
          },
        lightTextColor = Color(106, 0, 232),
        darkTextColor = Color(204, 204, 255),
      )
    )

    menu.add(
      SetType(
        5,
        "Myo Rep Match",
        description =
          buildAnnotatedString {
            append(
              "Do the first set as a normal set to the target RIR/RPE. Then in the succeeding sets, do myo reps until you reach the same amount of reps as the first set.\n\n" +
                "https://www.youtube.com/watch?v=KIJ77QTsvlo&t=501s"
            )
          },
        lightTextColor = Color(162, 0, 109),
        darkTextColor = Color(244, 187, 255),
        abbreviation = "MM",
      )
    )

    menu.add(
      SetType(
        6,
        "Giant Set",
        description =
          buildAnnotatedString {
            append(
              "Do as many sets as it takes to get to the total number of prescribed repetitions.\n\n" +
                "https://www.youtube.com/watch?v=HmzytOT0ZhM"
            )
          },
        lightTextColor = Color(0, 101, 0),
        darkTextColor = Color(201, 255, 229),
      )
    )

    menu.add(
      SetType(
        7,
        "AMRAP",
        description =
          buildAnnotatedString {
            append("Perform as many reps as possible to the target RIR/RPE.")
          },
        lightTextColor = MaterialTheme.colorScheme.error,
        darkTextColor = MaterialTheme.colorScheme.error,
      )
    )

    menu.add(
      SetType(
        8,
        "Failure Set",
        description =
          buildAnnotatedString {
            append(
              "A set taken to concentric failure; defined as the inability to complete a repetition in a full range of motion at a specific load without the assistance of a training partner or compromising a prescribed form or technique."
            )
          },
        lightTextColor = MaterialTheme.colorScheme.error,
        darkTextColor = MaterialTheme.colorScheme.error,
      )
    )

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
            color = if (isSystemInDarkTheme()) setType.darkTextColor else setType.lightTextColor,
            style = TextStyle(fontWeight = FontWeight.Bold),
          )
        },
        trailingIcon = {
          IconButton(onClick = { openSetTypeDescriptionDialog = true }) {
            Icon(
              Icons.Outlined.QuestionMark,
              contentDescription = "Localized description",
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
  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    confirmButton = { /*TODO*/},
    title = { Text(text = setType.name) },
    text = { Text(text = setType.description) },
  )
}
