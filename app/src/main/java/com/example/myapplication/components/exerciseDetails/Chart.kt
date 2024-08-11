package com.example.myapplication.components.exerciseDetails

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.BaseAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.random.Random

@Preview
@Composable
fun ChartDemoScreen() {
  Scaffold() { paddingValues ->
    Column(modifier = Modifier
      .padding(paddingValues)
      .padding(16.dp)
      .fillMaxSize()) {
      //      ChartWrapper()
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartWrapper(navigateToFullscreenChartScreen: () -> Unit) {

  Box(/*modifier = Modifier.fillMaxSize()*/) {
    VicoChart(modifier = Modifier.height(200.dp))
    IconButton(
      modifier = Modifier.align(Alignment.TopEnd),
      onClick = { navigateToFullscreenChartScreen() },
    ) {
      Icon(
        painter = painterResource(R.drawable.expand_content_24px),
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = null,
      )
    }
  }
}

@Composable
fun FullScreenChart(navigateUp: () -> Unit) {
  val context = LocalContext.current
  val landscapeOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
  val portraitOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

  var orientation = landscapeOrientation

  /*  val activity = context.findActivity()

  activity?.requestedOrientation = landscapeOrientation*/

//  TODO Add y axis label & units
//  TODO Add x axis label & units

  Surface(modifier = Modifier.fillMaxSize()) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        IconButton(onClick = { orientation = portraitOrientation; navigateUp() }) {
          Icon(
            Icons.AutoMirrored.Default.ArrowBack,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(text = "Exercise", style = MaterialTheme.typography.titleMedium)
          Text(text = "Details", style = MaterialTheme.typography.titleSmall)
        }
        IconButton(onClick = {
          Toast.makeText(
            context,
            "TODO: Implement DropdownMenu",
            Toast.LENGTH_SHORT
          ).show()
        }) {
          Icon(
            Icons.Default.FilterList,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
          )
        }
        TextButton(onClick = {
          Toast.makeText(
            context,
            "TODO: Implement DropdownMenu",
            Toast.LENGTH_SHORT
          ).show()
        }) {
          Text(text = "Programs")
        }
        IconButton(
          onClick = {
            orientation = portraitOrientation
            navigateUp()
          }
        ) {
          Icon(
            painter = painterResource(R.drawable.collapse_content_24px),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
          )
        }
        IconButton(
          onClick = {
            Toast.makeText(context, "TODO: Implement Share", Toast.LENGTH_SHORT).show()
          }
        ) {
          Icon(
            Icons.Default.Share,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
          )
        }
      }


      Box(modifier = Modifier.fillMaxSize()) {
        VicoChart(modifier = Modifier.fillMaxSize())
        Box(modifier = Modifier.align(Alignment.TopEnd)) {
          Row() {

          }
        }
      }
    }
  }

  DisposableEffect(orientation) {
    val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
    val originalOrientation = activity.requestedOrientation
    activity.requestedOrientation = orientation
    onDispose {
      // restore original orientation when view disappears
      activity.requestedOrientation = portraitOrientation
    }
  }
}

fun Context.findActivity(): Activity? =
  when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
  }

@Composable
fun VicoChart(modifier: Modifier) {
  val x = (1..50).toList()

  val modelProducer = remember { CartesianChartModelProducer() }
  LaunchedEffect(Unit) {
    withContext(Dispatchers.Default) {
      modelProducer.runTransaction {
        /* Learn more:
        https://patrykandpatrick.com/vico/wiki/cartesian-charts/layers/line-layer#data. */
        lineSeries { series(x, x.map { Random.nextFloat() * 15 }) }
      }
    }
  }

  val marker = rememberMarker()
  val sizeConstraint = remember { BaseAxis.SizeConstraint.Auto() }
  val primaryColor = MaterialTheme.colorScheme.primary

  CartesianChartHost(
    chart =
    rememberCartesianChart(
      rememberLineCartesianLayer(
        LineCartesianLayer.LineProvider.series(
          rememberLine(remember { LineCartesianLayer.LineFill.single(fill(primaryColor)) })
        )
      ),
      startAxis = rememberStartAxis(),
      bottomAxis = rememberBottomAxis(guideline = null, tick = rememberAxisTickComponent()),
      marker = marker,
      /*persistentMarkers =
      rememberExtraLambda(marker) { marker at PERSISTENT_MARKER_X },*/
    ),
    modelProducer = modelProducer,
    modifier = modifier,
    zoomState = rememberVicoZoomState(zoomEnabled = true),
  )
}
