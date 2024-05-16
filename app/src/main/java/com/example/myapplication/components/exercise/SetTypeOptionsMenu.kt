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
import com.example.myapplication.data.model.SetType
import com.example.myapplication.ui.theme.White

// TODO Takes an Array List of Set Types
// @Preview
@Composable
fun SetTypeOptionsMenu(
  menuItems: List<SetType> = setTypes.toList(),
  // selectedSetType: SetTypeModel, TODO Not if this is needed
  expandMenu: () -> Boolean,
  onDismiss: () -> Unit,
  updateSetType: (setTypeID: Int) -> Unit,
  changeIconText: () -> Unit,
  changeIconColor: () -> Unit,
) {
  val questionMarkColor = White
  fun createSetTypes(): ArrayList<SetType> {

    val menu = ArrayList<SetType>()

    menu.add(SetType(1, "Normal", description = "Normal", textColor = Color.White))

    menu.add(
      SetType(
        2,
        "Warm Up Set",
        description = /*TODO*/ "",
        textColor = Color(255, 178, 0), /* Color: Orange */
      )
    )

    menu.add(
      SetType(
        3,
        "Drop Set",
        description = /*TODO*/
          "A technique for continuing an exercise with a lower weight once the target repetitions or RIR/RPE has been achieved at a higher weight with minimal rest between sets. Reduce the load by 10% to 30%. https://www.youtube.com/watch?v=51TKExRtxZM",
        textColor = Color.LightGray, /* Color: Purple */
      )
    )

    menu.add(
      SetType(
        4,
        "Myo Rep",
        description = /*TODO*/
          "Myo reps is a form of “rest pause” training popularized by Borge Fagerli from Norway. When performing the method you’re using a lighter weight, do a set close to failure and then take a brief pause between repetitions. " +
            "Example Myo-Reps Set\n" +
            "1. Start by doing an activation set: Use a weight that you can get between 12-15 reps with. Then do as many reps as you can until you’re 1-2 repetitions away from failure.\n" +
            "2. After the activation set, rest 10-15 seconds\n" +
            "3. After the short rest do another set until you’re 1-2 reps away from failure.\n" +
            "4. Then rest another 10-15 seconds\n" +
            "5. Repeat this process until you no longer can get the same amount of reps on all your “minisets”, or after you’ve reached 5 sets in total (4 minisets).",
        textColor = Color.Cyan,
      )
    )

    menu.add(
      SetType(
        4,
        "Myo Rep Match",
        description = /*TODO*/
          "Do the first set as a normal set to the target RIR/RPE. Then in the succeeding sets, do myo reps until you reach the same amount of reps as the first set. https://www.youtube.com/watch?v=KIJ77QTsvlo&t=501s",
        textColor = Color.Cyan,
      )
    )

    menu.add(
      SetType(
        6,
        "Giant Set",
        description = /*TODO*/
          "Do as many sets as it takes to get to the total number of prescribed repetitions. https://www.youtube.com/watch?v=HmzytOT0ZhM",
        textColor = Color.Blue,
      )
    )

    menu.add(
      SetType(
        7,
        "AMRAP",
        description = /*TODO*/ "Perform as many reps as possible to the target RIR/RPE.",
        textColor = Color.Magenta,
      )
    )

    menu.add(
      SetType(
        8,
        "Failure Set",
        description = /*TODO*/
          "A set taken to concentric failure; defined as the inability to complete a repetition in a full range of motion at a specific load without the assistance of a training partner or compromising a prescribed form or technique.",
        textColor = Color.Red,
      )
    )

    return menu
  }

  val setTypes = createSetTypes()

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
      DropdownMenuItem(
        text = { Text(setType.name) },
        leadingIcon = {
          Text(
            setType.name[0].toString(),
            color =
              if (setType.name == "Normal") {
                Color.Black
              } else {
                setType.textColor
              },
          )
        },
        trailingIcon = {
          IconButton(onClick = { /* TODO doSomething() */}) {
            Icon(Icons.Outlined.QuestionMark, contentDescription = "Localized description")
          }
        },
        onClick = {
          onDismiss()
          updateSetType(setType.setTypeID)
          changeIconText()
          changeIconColor()
        }, // TODO Assign Set Type to Set
      )
    }
  }
}
