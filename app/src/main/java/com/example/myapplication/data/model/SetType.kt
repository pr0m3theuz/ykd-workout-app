package com.example.myapplication.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLinkStyles


// TODO To Implement in Realm
// TODO Create screen to edit/create set type
data class SetType(
  val setTypeID: Int,
  val name: String,
  val description: String,
  var uri: String? = null,
  var lightTextColor: Color? = null,
  var darkTextColor: Color? = null,
  var abbreviation: String? = null,
) {
  companion object {
    fun getSetType(setTypeID: Int): SetType {
      return when (setTypeID) {
        1 -> normalSet
        2 -> warmUpSet
        3 -> dropSet
        4 -> myoRepSet
        5 -> myoRepMatchSet
        6 -> giantSet
        7 -> amrapSet
        8 -> failureSet
        else -> normalSet
      }
    }

    var textLinkStyles: TextLinkStyles? = null

      val normalSet =
          SetType(
              1,
              "Normal",
              description =
              "A set performed with the target weight to the target repetitions and the target RIR/RPE.",
              //      lightTextColor = MaterialTheme.colorScheme.onSurface,
              //      darkTextColor = MaterialTheme.colorScheme.onSurface,
          )

      val warmUpSet =
          SetType(
              2,
              "Warm Up Set",
              description =
              "<ol><li>Begin with a 20RM+ weight for the first exercise and do it for around 10 reps.</li><li>Do at least 1 weight halfway between your 2ORM+ and your planned working weight for a set of ~5 reps.</li>The heavier the lift, the more sets of 8, 6, 4, etc. you do.<ol><li>0-50lbs: 1 intermediate set</li><li>50-200: 2 intermediate sets</li><li>200-400: 3 intermediate sets</li><li>As needed for bigger weights</li></ol><li>Do the working weight or 10% higher for 2-3 reps (potentiation)</li><li>For each NEW exercise after, if same muscle, do just:</li><ol><li>The 5 rep with weight halfway between 20RM+ and working weight</li><li>The working weight itself for 2-3 reps</li></ol></ol>`br`" +
                      """<a href="https://www.youtube.com/watch?v=HDq-68SlPgQ">How to Warm Up for Muscle Growth Training | Hypertrophy Made Simple #3</a>"""",
              darkTextColor = Color(255, 165, 61), /* Color: Orange */
              lightTextColor = Color(112, 66, 20),
          )

      val dropSet =
          SetType(
              3,
              "Drop Set",
              description =
              """A technique for continuing an exercise with a lower weight once the target repetitions or RIR/RPE has been achieved at a higher weight with minimal rest between sets.Reduce the load by 10% to 30%.
            
<a href="https://www.youtube.com/watch?v=51TKExRtxZM">How Get The Most Muscle Growth From Dropsets</a>"""
                  .trimIndent(),
              lightTextColor = Color(0, 0, 139),
              darkTextColor = Color(0, 204, 255), /* Color: Purple */
          )

      val myoRepSet =
          SetType(
              4,
              "Myo Rep",
              description =
              """Myo Reps is a form of “rest pause” training popularized by Borge Fagerli from Norway. When performing the method you’re using a lighter weight, do a set close to failure and then take a brief pause between repetitions.  

Example Myo Reps Set:<ol><li>Start by doing an activation set: Use a weight that you can get between 12-15 reps with. Then do as many reps as you can until you’re 1-2 repetitions away from failure.</li>
  <li>After the activation set, rest 10-15 seconds.</li>
  <li>After the short rest do another set until you’re 1-2 reps away from failure.</li>
  <li>Then rest another 10-15 seconds.</li>
  <li>Repeat this process until you no longer can get the same amount of reps on all your “minisets”, or after you’ve reached 5 sets in total (4 minisets).</li></ol>`br`
<a href="https://www.youtube.com/watch?v=V71TGRQaLRs">Why Myoreps Are AWESOME</a> 
"""
                  .trimIndent(),
              lightTextColor = Color(106, 0, 232),
              darkTextColor = Color(204, 204, 255),
          )

      val myoRepMatchSet =
          SetType(
              5,
              "Myo Rep Match",
              description =
              """Do the first set as a normal set to the target RIR/RPE. Then in the succeeding sets, do myo reps until you reach the same amount of reps as the first set.
          
<a href="https://www.youtube.com/watch?v=KIJ77QTsvlo">Need More Growth? Myo Rep Match Is The Solution!</a>"""
                  .trimIndent(),

              /*{
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
              }*/
              lightTextColor = Color(162, 0, 109),
              darkTextColor = Color(244, 187, 255),
              abbreviation = "MM",
          )

      val giantSet =
          SetType(
              6,
              "Giant Set",
              description =
              """Do as many sets as it takes to get to the total number of prescribed repetitions

<a href="https://www.youtube.com/watch?v=HmzytOT0ZhM">Giant Sets: Amazing Training Technique You Aren't Using</a>"""
                  .trimIndent(),
              /*{
                val urlLink = "https://www.youtube.com/watch?v=HmzytOT0ZhM"
                val url = "Giant Sets: Amazing Training Technique You Aren't Using"
                val str =
                  "Do as many sets as it takes to get to the total number of prescribed repetitions.\n\n" +
                    url
                val startIndex = str.indexOf(url)
                val endIndex = startIndex + url.length

                append(str)

                addLink(LinkAnnotation.Url(url = urlLink, textLinkStyles), startIndex, endIndex)
              }*/
              lightTextColor = Color(0, 101, 0),
              darkTextColor = Color(201, 255, 229),
          )

      val amrapSet =
          SetType(
              7,
              "AMRAP",
              description = "Perform as many reps as possible to the target RIR/RPE."
          )

      val failureSet =
          SetType(
              8,
              "Failure Set",
              description =
          "A set taken to concentric failure; defined as the inability to complete a repetition in a " +
                  "full range of motion at a specific load without the assistance of a training " +
                  "partner or compromising a prescribed form or technique.",
          )
  }
}
