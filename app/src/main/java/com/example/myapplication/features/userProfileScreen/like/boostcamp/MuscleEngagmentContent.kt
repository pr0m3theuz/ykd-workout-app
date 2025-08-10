package com.example.myapplication.features.userProfileScreen.like.boostcamp

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeHistory
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import com.example.myapplication.common.domain.model.Muscle
import com.example.myapplication.common.domain.model.MuscleFractionalSets
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.common.util.doubleFormatted
import java.util.Locale

// Data class to hold information for each muscle group
data class MuscleData(
  val name: String,
  val sets: Float,
  val percentageChange: Int,
  val color: Color,
)

// Sample data for the list
val strongOrange = Color(0xFFF57C00)
val mediumOrange = Color(0xFFFFA726)
val lightOrange = Color(0xFFFFB74D)

val muscleDataList =
  listOf(
    MuscleData("Front Delts", 13.0f, 44, strongOrange),
    MuscleData("Triceps", 12.8f, 39, strongOrange),
    MuscleData("Upper Back", 12.0f, 114, strongOrange),
    MuscleData("Middle Delts", 9.6f, 7, mediumOrange),
    MuscleData("Biceps", 7.6f, 15, lightOrange),
    MuscleData("Chest", 7.0f, 75, lightOrange),
    // Add more items here to see the list scroll
    MuscleData("Lats", 6.8f, 22, lightOrange),
    MuscleData("Quads", 6.5f, -10, mediumOrange),
  )

// Preview for Android Studio
@Preview(showBackground = true)
@Composable
fun MuscleEngagementScreenPreview() {
  MyApplicationTheme { MuscleEngagementScreen(muscleDataList = muscleDataList) }
}

@Preview(showBackground = true)
@Composable
fun MuscleEngagementScreenNoDataPreview() {
  MyApplicationTheme { MuscleEngagementScreen(muscleDataList = emptyList()) }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun MuscleEngagementScreenPreviewDarkMode() {
  MyApplicationTheme { MuscleEngagementScreen(muscleDataList = muscleDataList) }
}

@Composable
fun MuscleEngagementScreen(
  muscleEngagementData: List<Pair<Muscle, List<MuscleFractionalSets>>> = emptyList(),
  muscleDataList: List<MuscleData> = emptyList(),
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.surface),
    verticalArrangement = Arrangement.spacedBy(0.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    // --- Static Header Content ---
    if (muscleDataList.isNotEmpty()) {
      // --- List Header ---
      MuscleListHeader()
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

      // --- Muscle Data List ---

      muscleDataList.forEach { muscleData ->
        MuscleListItem(data = muscleData)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
      }
    } else if (muscleEngagementData.isNotEmpty()) {
      // --- List Header ---
      MuscleListHeader()
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

      val maxFractionalSets =
        muscleEngagementData.flatMap { it.second }.groupBy { it.muscle }.map { (muscle, data) ->
          muscle to data.sumOf { it.fractionalSets }
        }.maxOf { it.second }


      muscleEngagementData.forEach { muscleData ->

        Log.d("MuscleEngagementScreen", "maxFractionalSets: $maxFractionalSets")

        MuscleFractionalSetsItem(data = muscleData, maxFractionalSets = maxFractionalSets)
        HorizontalDivider(
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          modifier = Modifier.padding(vertical = 2.dp)
        )
      }
    } else {
      Text("No data available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun MuscleListHeader() {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = "Muscle",
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.weight(1f),
    )
    Text(
      // Use width to align with data columns below
      text = "Sets",
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.width(90.dp),
    )
    Text(
      // Use width to align with data columns below
      text = "% Change",
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.width(90.dp),
    )
  }
}

@Composable
fun MuscleFractionalSetsItem(
  data: Pair<Muscle, List<MuscleFractionalSets>>,
  maxFractionalSets: Double
) {
  val muscle = data.first
  val muscleFractionalSetsList = data.second

  val totalFractionalSets = muscleFractionalSetsList.sumOf { it.fractionalSets }

  val legendAlpha = totalFractionalSets.div(maxFractionalSets).toFloat()

  Log.d("MuscleFractionalSetsItem", "legendAlpha: $legendAlpha")

  var expanded by remember { mutableStateOf(false) }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      // Dot and Muscle Name (flexible width)
      Row(modifier = Modifier.weight(0.5f), verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(10.dp)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = legendAlpha), CircleShape)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
          text = muscle.name,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
      }

      Text(
        //        text = String.format(Locale.getDefault(), "%.1f sets", totalFractionalSets),
        text = doubleFormatted(totalFractionalSets),
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.End,
        modifier = Modifier.width(90.dp),
      )

      IconButton(onClick = { expanded = !expanded }) {
        Icon(
          imageVector = Icons.Default.ExpandMore,
          contentDescription = if (expanded) "Collapse" else "Expand",
          modifier = Modifier.rotate(if (expanded) 180f else 0f),
        )
      }
    }
    AnimatedVisibility(visible = expanded) {
      Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 26.dp)) {
        muscleFractionalSetsList
          .sortedByDescending { it.fractionalSets }
          .forEach { muscleFractionalSet ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 0.dp),
              verticalAlignment = Alignment.CenterVertically,
            ) {
              Text(
                text = muscleFractionalSet.exerciseName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
              )
              Text(
                text = muscleFractionalSet.muscleExercise.roleFunction.replaceFirstChar { it.uppercaseChar() },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(0.25f),
              )
              Text(
                //                  text =
                //                    String.format(
                //                      Locale.getDefault(),
                //                      "%.1f sets",
                //                      muscleFractionalSet.fractionalSets,
                //                    ),
                text = doubleFormatted(muscleFractionalSet.fractionalSets),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(0.25f),
              )
            }
          }
      }
    }
  }
}

@Composable
fun MuscleListItem(data: MuscleData) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // Dot and Muscle Name (flexible width)
    Row(modifier = Modifier.weight(0.5f), verticalAlignment = Alignment.CenterVertically) {
      Box(modifier = Modifier
        .size(10.dp)
        .background(data.color, CircleShape))
      Spacer(modifier = Modifier.width(16.dp))
      Text(
        text = data.name,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
      )
    }

    // Sets count (fixed width for alignment)
    Text(
      text = String.format(Locale.getDefault(), "%.1f sets", data.sets),
      color = MaterialTheme.colorScheme.onSurface,
      style = MaterialTheme.typography.bodyLarge,
      textAlign = TextAlign.End,
      modifier = Modifier.width(90.dp),
    )

    // Percentage change (fixed width for alignment)
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.End,
      modifier = Modifier.width(90.dp),
    ) {
      val greenColor =
        ColorUtils.blendARGB(
          Color.Green.toArgb(),
          MaterialTheme.colorScheme.onSurface.toArgb(),
          0.75f,
        )
      val redColor = MaterialTheme.colorScheme.error

      val color = if (data.percentageChange >= 0) Color(greenColor) else redColor
      val contentDescription = if (data.percentageChange >= 0) "Increase" else "Decrease"
      val sign = if (data.percentageChange >= 0) "+" else ""

      Text(
        text = sign.plus("${data.percentageChange}%"),
        style = MaterialTheme.typography.bodyLarge,
        color = color,
        modifier = Modifier.padding(end = 2.dp),
      )
      Icon(
        imageVector = Icons.Default.ChangeHistory, // A filled triangle icon
        contentDescription = contentDescription,
        tint = color,
        modifier = Modifier
          .size(16.dp)
          .rotate(if (data.percentageChange >= 0) 0f else 180f),
      )
    }
  }
}
