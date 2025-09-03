package com.example.myapplication.common.ui.component

import android.graphics.Color
import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import app.rive.runtime.kotlin.core.Fit
import app.rive.runtime.kotlin.core.Rive
import com.example.myapplication.R
import com.example.myapplication.common.ui.theme.MyApplicationTheme

@Composable
fun ComposableRiveAnimationView(
  modifier: Modifier = Modifier,
  @RawRes animation: Int,
  stateMachineName: String? = null,
  alignment: app.rive.runtime.kotlin.core.Alignment = app.rive.runtime.kotlin.core.Alignment.CENTER,
  fit: Fit = Fit.CONTAIN,
  onInit: (RiveAnimationView) -> Unit
) {
  AndroidView(
    modifier = modifier.fillMaxWidth(),
    factory = { context ->
      RiveAnimationView(context).also {
        it.setRiveResource(
          resId = animation,
//          autoplay = true,
          stateMachineName = stateMachineName,
          alignment = alignment,
          fit = fit,
//          autoBind = true,
        )

//        val riveViewModel = it.controller.file?.getViewModelByName("viewModel")!!
//        val riveInstance = riveViewModel.createInstanceFromName("Instance")
//        it.controller.stateMachines.first().viewModelInstance = riveInstance
//
//        // TODO Map rive muscles to source muscle data & color
//        val latsissimusDorsi = riveInstance.getColorProperty("latsissimusDorsi")
//        latsissimusDorsi.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val hamstring = riveInstance.getColorProperty("hamstring")
//        hamstring.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val gluteMedius = riveInstance.getColorProperty("gluteMedius")
//        gluteMedius.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val gluteMax = riveInstance.getColorProperty("gluteMax")
//        gluteMax.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val rhomboidMajor = riveInstance.getColorProperty("rhomboidMajor")
//        rhomboidMajor.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val teresMinor = riveInstance.getColorProperty("teresMinor")
//        teresMinor.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val teresMajor = riveInstance.getColorProperty("teresMajor")
//        teresMajor.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val infraspinatus = riveInstance.getColorProperty("infraspinatus")
//        infraspinatus.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val quadriceps = riveInstance.getColorProperty("quadriceps")
//        quadriceps.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val adductors = riveInstance.getColorProperty("adductors")
//        adductors.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val calves = riveInstance.getColorProperty("calves")
//        calves.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val rectusAbdominis = riveInstance.getColorProperty("rectusAbdominis")
//        rectusAbdominis.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val deltoids = riveInstance.getColorProperty("deltoids")
//        deltoids.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val sartorius = riveInstance.getColorProperty("sartorius")
//        sartorius.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val trapezius = riveInstance.getColorProperty("trapezius")
//        trapezius.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val serratus = riveInstance.getColorProperty("serratus")
//        serratus.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val neck = riveInstance.getColorProperty("neck")
//        neck.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val chest = riveInstance.getColorProperty("chest")
//        chest.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val triceps = riveInstance.getColorProperty("triceps")
//        triceps.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val biceps = riveInstance.getColorProperty("biceps")
//        biceps.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val obliques = riveInstance.getColorProperty("obliques")
//        obliques.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val forearms = riveInstance.getColorProperty("forearms")
//        forearms.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val stroke = riveInstance.getColorProperty("stroke")
//        stroke.value = color.copy(alpha = legendAlpha).toArgb()
//
//        val misc = riveInstance.getColorProperty("misc")
//        misc.value = color.copy(alpha = legendAlpha).toArgb()
      }
    },
    update = { view -> onInit(view) }
  )
}

@Preview
@Composable
fun ComposableRiveAnimationViewPreview() {
  val color = MaterialTheme.colorScheme.tertiary
  val background = MaterialTheme.colorScheme.background
  val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
  val transparent = Color.TRANSPARENT
  val legendAlpha = 1f
  var animation: RiveAnimationView? = null

  Rive.init(LocalContext.current)
  MyApplicationTheme {
    Column(modifier = Modifier.fillMaxWidth()) {
      Box(
        modifier = Modifier.fillMaxWidth()
      ) {
        ComposableRiveAnimationView(
          animation = R.raw.muscular_system,
          stateMachineName = "stateMachine",
          fit = Fit.CONTAIN,
        ) { view ->
          animation = view
          val riveViewModel = animation.controller.file?.getViewModelByName("viewModel")!!
          val riveInstance = riveViewModel.createInstanceFromIndex(0)
          animation.controller.stateMachines.first().viewModelInstance = riveInstance

          val latsissimusDorsi = riveInstance.getColorProperty("latsissimusDorsi")
          latsissimusDorsi.value = color.copy(alpha = legendAlpha).toArgb()

          val hamstring = riveInstance.getColorProperty("hamstring")
          hamstring.value = color.copy(alpha = legendAlpha).toArgb()

          val gluteMedius = riveInstance.getColorProperty("gluteMedius")
          gluteMedius.value = color.copy(alpha = legendAlpha).toArgb()

          val gluteMax = riveInstance.getColorProperty("gluteMax")
          gluteMax.value = color.copy(alpha = legendAlpha).toArgb()

          val rhomboidMajor = riveInstance.getColorProperty("rhomboidMajor")
          rhomboidMajor.value = color.copy(alpha = legendAlpha).toArgb()

          val teresMinor = riveInstance.getColorProperty("teresMinor")
          teresMinor.value = color.copy(alpha = legendAlpha).toArgb()

          val teresMajor = riveInstance.getColorProperty("teresMajor")
          teresMajor.value = color.copy(alpha = legendAlpha).toArgb()

          val infraspinatus = riveInstance.getColorProperty("infraspinatus")
          infraspinatus.value = color.copy(alpha = legendAlpha).toArgb()

          val quadriceps = riveInstance.getColorProperty("quadriceps")
          quadriceps.value = color.copy(alpha = legendAlpha).toArgb()

          val adductors = riveInstance.getColorProperty("adductors")
          adductors.value = color.copy(alpha = legendAlpha).toArgb()

          val calves = riveInstance.getColorProperty("calves")
          calves.value = color.copy(alpha = legendAlpha).toArgb()

          val rectusAbdominis = riveInstance.getColorProperty("rectusAbdominis")
          rectusAbdominis.value = color.copy(alpha = legendAlpha).toArgb()

          val deltoids = riveInstance.getColorProperty("deltoids")
          deltoids.value = color.copy(alpha = legendAlpha).toArgb()

          val sartorius = riveInstance.getColorProperty("sartorius")
          sartorius.value = color.copy(alpha = legendAlpha).toArgb()

          val trapezius = riveInstance.getColorProperty("trapezius")
          trapezius.value = color.copy(alpha = legendAlpha).toArgb()

          val serratus = riveInstance.getColorProperty("serratus")
          serratus.value = color.copy(alpha = legendAlpha).toArgb()

          val neck = riveInstance.getColorProperty("neck")
          neck.value = color.copy(alpha = legendAlpha).toArgb()

          val chest = riveInstance.getColorProperty("chest")
          chest.value = color.copy(alpha = legendAlpha).toArgb()

          val triceps = riveInstance.getColorProperty("triceps")
          triceps.value = color.copy(alpha = legendAlpha).toArgb()

          val biceps = riveInstance.getColorProperty("biceps")
          biceps.value = color.copy(alpha = legendAlpha).toArgb()

          val obliques = riveInstance.getColorProperty("obliques")
          obliques.value = color.copy(alpha = legendAlpha).toArgb()

          val forearms = riveInstance.getColorProperty("forearms")
          forearms.value = color.copy(alpha = legendAlpha).toArgb()

          val stroke = riveInstance.getColorProperty("stroke")
          stroke.value = background.copy(alpha = legendAlpha).toArgb()

          val misc = riveInstance.getColorProperty("misc")
          misc.value = surfaceVariant.copy(alpha = legendAlpha).toArgb()

          view.play()
        }
      }
    }
  }
}