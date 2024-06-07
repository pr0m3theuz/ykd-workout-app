package com.example.myapplication.components

import android.util.Log
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import com.example.myapplication.data.model.SetType

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
    val textLinkStyles =
      TextLinkStyles(
        style =
          SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontStyle = FontStyle.Italic,
            textDecoration = TextDecoration.Underline,
          )
      )

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
            val urlLink = "https://www.youtube.com/watch?v=HDq-68SlPgQ"
            val url = "How to Warm Up for Muscle Growth Training | Hypertrophy Made Simple #3"
            //            val title = "How to warm up\n\n"
            val str = // Option 1
              //              "1. You CAN choose to do easy cardio for 5-10 minutes, or\n\n" +
              "2. Begin with a 20RM+ weight for the first exercise and do it for around 10 reps\n\n" +
                "3. Do at least 1 weight halfway between your 2ORM+ and your planned working weight for a set of ~5 reps"
            // Indented text with weight ranges

            val list =
              // Indented text with weight ranges
              "\t\t\t• The heavier the lift, the more sets of 8,6,4, etc. you do\n" +
                "\t\t\t• 0-50lbs: 1 intermediate set\n" +
                "\t\t\t• 50-200: 2 intermediate sets\n" +
                "\t\t\t• 200-400: 3 intermediate sets\n" +
                "\t\t\t• As needed for bigger weights\n"

            val str3 =
              // Option 4
              "4. Do the working weight or 10% higher for 2-3 reps (potentiation)\n\n" +

                // Option 5 - Emphasize "NEW"
                "5. For each NEW exercise after, if same muscle, do just:"

            val list2 =
              "\t\ta) The 5 rep with weight halfway between 20RM+ and working weight\n" +
                "\t\tb) The working weight itself for 2-3 reps\n"

            Log.i("Warm Up Set", "Warm Up Set Description: $str")

            val startIndex = (str + list + str3 + list2 + url).indexOf(url)
            val endIndex = /*title.length +*/ startIndex + url.length

            Log.i("Warm Up Set", "Warm Up Set Start Index: $startIndex")
            Log.i("Warm Up Set", "Warm Up Set End Index: $endIndex")
            //            append(title)

            append(
              AnnotatedString(
                text = str,
                paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 15.sp)),
              )
            )
            append(
              AnnotatedString(
                text = list,
                paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 32.sp)),
              )
            )
            append(
              AnnotatedString(
                text = str3,
                paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 15.sp)),
              )
            )
            append(
              AnnotatedString(
                text = list2,
                paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 32.sp)),
              )
            )
            append(url)
            addLink(
              url = LinkAnnotation.Url(url = urlLink, styles = textLinkStyles),
              start = startIndex,
              end = endIndex,
            )
          },
        darkTextColor = Color(255, 165, 61), /* Color: Orange */
        lightTextColor = Color(112, 66, 20),
      )
    )

    menu.add(
      SetType(
        3,
        "Drop Set",
        description =
          buildAnnotatedString {
            val urlLink = "https://www.youtube.com/watch?v=51TKExRtxZM"
            val url = "How Get The Most Muscle Growth From Dropsets"
            val str =
              "A technique for continuing an exercise with a lower weight once the target repetitions " +
                "or RIR/RPE has been achieved at a higher weight with minimal rest between sets. " +
                "Reduce the load by 10% to 30%.\n\n" +
                url
            val startIndex = str.indexOf(url)
            val endIndex = startIndex + url.length
            append(str)

            addLink(
              LinkAnnotation.Url(url = urlLink, styles = textLinkStyles),
              startIndex,
              endIndex,
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
            val urlLink = "https://www.youtube.com/watch?v=V71TGRQaLRs"
            val url = "Why Myoreps Are AWESOME"
            val str =
              "Myo reps is a form of “rest pause” training popularized by Borge Fagerli from Norway. " +
                "When performing the method you’re using a lighter weight, do a set close to failure and then take a brief pause between repetitions.\n"

            val list =
              "Example Myo Reps Set\n" +
                "1. Start by doing an activation set: Use a weight that you can get between 12-15 reps with. " +
                "Then do as many reps as you can until you’re 1-2 repetitions away from failure.\n" +
                "2. After the activation set, rest 10-15 seconds\n" +
                "3. After the short rest do another set until you’re 1-2 reps away from failure.\n" +
                "4. Then rest another 10-15 seconds\n" +
                "5. Repeat this process until you no longer can get the same amount of reps on all " +
                "your “minisets”, or after you’ve reached 5 sets in total (4 minisets).\n"

            val startIndex = (str + list + url).indexOf(url)
            val endIndex = startIndex + url.length
            append(str)
            append(
              AnnotatedString(
                text = list,
                paragraphStyle = ParagraphStyle(textIndent = TextIndent(restLine = 15.sp)),
              )
            )
            append(url)
            addLink(LinkAnnotation.Url(url = urlLink, textLinkStyles), startIndex, endIndex)
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
            val urlLink = "https://www.youtube.com/watch?v=KIJ77QTsvlo"
            val url = "Need More Growth? Myo Rep Match Is The Solution!"
            val str =
              "Do the first set as a normal set to the target RIR/RPE. Then in the succeeding sets, " +
                "do myo reps until you reach the same amount of reps as the first set.\n\n" +
                url
            val startIndex = str.indexOf(url)
            val endIndex = startIndex + url.length
            append(str)

            addLink(LinkAnnotation.Url(url = urlLink, textLinkStyles), startIndex, endIndex)
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
            val urlLink = "https://www.youtube.com/watch?v=HmzytOT0ZhM"
            val url = "Giant Sets: Amazing Training Technique You Aren't Using"
            val str =
              "Do as many sets as it takes to get to the total number of prescribed repetitions.\n\n" +
                url
            val startIndex = str.indexOf(url)
            val endIndex = startIndex + url.length

            append(str)

            addLink(LinkAnnotation.Url(url = urlLink, textLinkStyles), startIndex, endIndex)
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
              "A set taken to concentric failure; defined as the inability to complete a repetition in a " +
                "full range of motion at a specific load without the assistance of a training " +
                "partner or compromising a prescribed form or technique."
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
  AlertDialog(
    onDismissRequest = { onDismissRequest() },
    confirmButton = { /*TODO*/},
    title = { Text(text = setType.name) },
    text = { Text(text = setType.description) },
  )
}
