package com.example.myapplication.features.userProfileScreen.like.boostcamp

import android.content.res.Configuration
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import com.example.myapplication.R
import com.example.myapplication.common.domain.model.Muscle
import com.example.myapplication.common.domain.model.MuscleFractionalSets
import com.example.myapplication.common.ui.component.ComposableRiveAnimationView
import com.example.myapplication.common.ui.theme.IBMColorBlindPalette
import com.example.myapplication.common.ui.theme.MyApplicationTheme
import com.example.myapplication.common.util.doubleFormatted
import java.util.Locale
import kotlin.math.floor
import kotlin.math.min

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

data class RiveMuscleData(
  val name: String,
  val correspondingMuscles: List<String>,
  var color: Int = 0,
)

val riveMuscleListSource =
  listOf(
    RiveMuscleData(name = "biceps", correspondingMuscles = listOf("BicepsBrachii", "Brachialis")),
    RiveMuscleData(name = "triceps", correspondingMuscles = listOf("TricepsBrachii")),
    RiveMuscleData(name = "infraspinatus", correspondingMuscles = listOf("Infraspinatus")),
    RiveMuscleData(name = "latissimusDorsi", correspondingMuscles = listOf("LatissimusDorsi")),
    RiveMuscleData(name = "rhomboidMajor", correspondingMuscles = listOf("Rhomboids")),
    RiveMuscleData(
      name = "trapezius",
      correspondingMuscles = listOf("TrapeziusLower", "TrapeziusMiddle", "TrapeziusUpper"),
    ),
    RiveMuscleData(
      name = "calves",
      correspondingMuscles = listOf("Gastrocnemius", "Soleus", "Propliteus", "TibialisAnterior"),
    ),
    RiveMuscleData(
      name = "chest",
      correspondingMuscles = listOf("PectoralisClavicular", "PectoralisMinor", "PectoralisSternal"),
    ),
    RiveMuscleData(name = "serratus", correspondingMuscles = listOf("SerratusAnterior")),
    RiveMuscleData(
      name = "forearms",
      correspondingMuscles =
        listOf("Brachioradialis", "Pronators", "Supinators", "WristExtensors", "WristFlexors"),
    ),
    RiveMuscleData(
      name = "gluteMax",
      correspondingMuscles = listOf("GluteusMaximus", "GluteusMinimus"),
    ),
    RiveMuscleData(name = "gluteMedius", correspondingMuscles = listOf("GluteusMedius")),
    RiveMuscleData(name = "sartorius", correspondingMuscles = listOf("Sartorius")),
    RiveMuscleData(
      name = "adductors",
      correspondingMuscles =
        listOf(
          "Gracilis",
          "HipAdductor",
          "HipExternalRotator",
          "HipFlexors",
          "Iliopsoas",
          "Pectineus",
          "Popliteus",
          "TensorFasciaeLatae",
          "Adductors",
        ),
    ),
    RiveMuscleData(
      name = "neck",
      correspondingMuscles = listOf("Splenius", "Sternocleidomastoid", "LevatorScapulae"),
    ),
    RiveMuscleData(
      name = "deltoids",
      correspondingMuscles = listOf("DeltoidAnterior", "DeltoidLateral", "DeltoidPosterior"),
    ),
    RiveMuscleData(name = "teresMajor", correspondingMuscles = listOf("TeresMajor")),
    RiveMuscleData(name = "teresMinor", correspondingMuscles = listOf("TeresMinor")),
    RiveMuscleData(name = "hamstring", correspondingMuscles = listOf("Hamstrings")),
    RiveMuscleData(name = "quadriceps", correspondingMuscles = listOf("Quadriceps")),
    RiveMuscleData(name = "obliques", correspondingMuscles = listOf("Obliques")),
    RiveMuscleData(
      name = "rectusAbdominis",
      correspondingMuscles = listOf("QuadratusLumborum", "RectusAbdominis", "TransverseAbdominis"),
    ),
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
  val riveMuscleList = rememberSaveable { riveMuscleListSource }

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

      val muscleFractionalSetsList =
        muscleEngagementData
          .flatMap { it.second }
          .groupBy { it.muscle }
          .map { (muscle, data) -> muscle to data.sumOf { it.fractionalSets } }

      val maxFractionalSets = muscleFractionalSetsList.maxOf { it.second }

      if (muscleFractionalSetsList.isNotEmpty()) {
        riveMuscleList.forEach { muscleData ->
          val muscleFractionalSetsList =
            muscleFractionalSetsList.filter { (muscle, _) ->
              muscleData.correspondingMuscles.contains(muscle.id)
            }

          val maxFractionalSetsForGroup =
            if (muscleFractionalSetsList.isNotEmpty()) muscleFractionalSetsList.maxOf { it.second }
            else 0.0

          val fraction = maxFractionalSetsForGroup.div(maxFractionalSets).toFloat()

          muscleData.color =
            if (maxFractionalSetsForGroup == 0.0) {
              MaterialTheme.colorScheme.outlineVariant.toArgb()
            } else {
              //              if (fraction == 1.0f) {
              //                IBMColorBlindPalette.colors[4].toArgb()
              //              } else {
              val numberOfColors = IBMColorBlindPalette.colors.size - 1
              val startColor = floor(fraction * numberOfColors).toInt().coerceIn(0, numberOfColors)
              lerp(
                IBMColorBlindPalette.colors[min(0, startColor)],
                IBMColorBlindPalette.colors[min(startColor + 1, numberOfColors)],
                fraction,
              )
                .toArgb()
              //              }
              //              MaterialTheme.colorScheme.primary.copy(alpha = fraction).toArgb()
            }
        }
      }

      MuscleEngagementRiveAnimation(riveMuscleDataList = riveMuscleList)

      MuscleListHeader()
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
      muscleEngagementData.forEach { muscleData ->
//        Log.d("MuscleEngagementScreen", "maxFractionalSets: $maxFractionalSets")

        MuscleFractionalSetsItem(data = muscleData, maxFractionalSets = maxFractionalSets)
        HorizontalDivider(
          color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          modifier = Modifier.padding(vertical = 2.dp),
        )
      }
    } else {
      Text("No data available.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
  }
}

@Composable
fun MuscleEngagementRiveAnimation(riveMuscleDataList: List<RiveMuscleData> = emptyList()) {
  val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
  val onBackground = MaterialTheme.colorScheme.onBackground
  val outline = MaterialTheme.colorScheme.outline
  val outlineVariant = MaterialTheme.colorScheme.outlineVariant
  var animation: RiveAnimationView? = null

  Box(modifier = Modifier
    .fillMaxWidth()
    .padding(8.dp)) {
    ComposableRiveAnimationView(
      animation = R.raw.muscular_system,
      stateMachineName = "stateMachine",
      fit = Fit.FIT_WIDTH,
    ) { view ->
      animation = view
      val riveViewModel = animation.controller.file?.getViewModelByName("viewModel")!!
      val riveInstance = riveViewModel.createInstanceFromIndex(0)
      animation.controller.stateMachines.first().viewModelInstance = riveInstance

      val latsissimusDorsi = riveInstance.getColorProperty("latsissimusDorsi")
      latsissimusDorsi.value = riveMuscleDataList.first { it.name == "latissimusDorsi" }.color

      val hamstring = riveInstance.getColorProperty("hamstring")
      hamstring.value = riveMuscleDataList.first { it.name == "hamstring" }.color

      val gluteMedius = riveInstance.getColorProperty("gluteMedius")
      gluteMedius.value = riveMuscleDataList.first { it.name == "gluteMedius" }.color

      val gluteMax = riveInstance.getColorProperty("gluteMax")
      gluteMax.value = riveMuscleDataList.first { it.name == "gluteMax" }.color

      val rhomboidMajor = riveInstance.getColorProperty("rhomboidMajor")
      rhomboidMajor.value = riveMuscleDataList.first { it.name == "rhomboidMajor" }.color

      val teresMinor = riveInstance.getColorProperty("teresMinor")
      teresMinor.value = riveMuscleDataList.first { it.name == "teresMinor" }.color

      val teresMajor = riveInstance.getColorProperty("teresMajor")
      teresMajor.value = riveMuscleDataList.first { it.name == "teresMajor" }.color

      val infraspinatus = riveInstance.getColorProperty("infraspinatus")
      infraspinatus.value = riveMuscleDataList.first { it.name == "infraspinatus" }.color

      val quadriceps = riveInstance.getColorProperty("quadriceps")
      quadriceps.value = riveMuscleDataList.first { it.name == "quadriceps" }.color

      val adductors = riveInstance.getColorProperty("adductors")
      adductors.value = riveMuscleDataList.first { it.name == "adductors" }.color

      val calves = riveInstance.getColorProperty("calves")
      calves.value = riveMuscleDataList.first { it.name == "calves" }.color

      val rectusAbdominis = riveInstance.getColorProperty("rectusAbdominis")
      rectusAbdominis.value = riveMuscleDataList.first { it.name == "rectusAbdominis" }.color

      val deltoids = riveInstance.getColorProperty("deltoids")
      deltoids.value = riveMuscleDataList.first { it.name == "deltoids" }.color

      val sartorius = riveInstance.getColorProperty("sartorius")
      sartorius.value = riveMuscleDataList.first { it.name == "sartorius" }.color

      val trapezius = riveInstance.getColorProperty("trapezius")
      trapezius.value = riveMuscleDataList.first { it.name == "trapezius" }.color

      val serratus = riveInstance.getColorProperty("serratus")
      serratus.value = riveMuscleDataList.first { it.name == "serratus" }.color

      val neck = riveInstance.getColorProperty("neck")
      neck.value = riveMuscleDataList.first { it.name == "neck" }.color

      val chest = riveInstance.getColorProperty("chest")
      chest.value = riveMuscleDataList.first { it.name == "chest" }.color

      val triceps = riveInstance.getColorProperty("triceps")
      triceps.value = riveMuscleDataList.first { it.name == "triceps" }.color

      val biceps = riveInstance.getColorProperty("biceps")
      biceps.value = riveMuscleDataList.first { it.name == "biceps" }.color

      val obliques = riveInstance.getColorProperty("obliques")
      obliques.value = riveMuscleDataList.first { it.name == "obliques" }.color

      val forearms = riveInstance.getColorProperty("forearms")
      forearms.value = riveMuscleDataList.first { it.name == "forearms" }.color

      val stroke = riveInstance.getColorProperty("stroke")
      stroke.value = onBackground.toArgb()

      val misc = riveInstance.getColorProperty("misc")
      misc.value = outline.toArgb()

      val strokeWidth = riveInstance.getNumberProperty("strokeWidth")
      strokeWidth.value = 15f

      view.play()
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
    /*Text(
      // Use width to align with data columns below
      text = "% Change",
      textAlign = TextAlign.Center,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.width(90.dp),
    )*/
  }
}

@Composable
fun MuscleFractionalSetsItem(
  data: Pair<Muscle, List<MuscleFractionalSets>>,
  maxFractionalSets: Double,
) {
  val muscle = data.first
  val muscleFractionalSetsList = data.second

  val totalFractionalSets = muscleFractionalSetsList.sumOf { it.fractionalSets }

  val fraction = totalFractionalSets.div(maxFractionalSets).toFloat()

  val numberOfColors = IBMColorBlindPalette.colors.size - 1
  val startColor = floor(fraction * numberOfColors).toInt().coerceIn(0, numberOfColors)
  val legendColor =
    lerp(
      IBMColorBlindPalette.colors[min(0, startColor)],
      IBMColorBlindPalette.colors[min(startColor + 1, numberOfColors)],
      fraction,
    )

  var expanded by remember { mutableStateOf(false) }

  Column(modifier = Modifier.fillMaxWidth()) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
      // Dot and Muscle Name (flexible width)
      Row(modifier = Modifier.weight(0.5f), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier
          .size(10.dp)
          .background(legendColor, CircleShape))
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
                text =
                  muscleFractionalSet.muscleExercise.roleFunction.replaceFirstChar {
                    it.uppercaseChar()
                  },
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
