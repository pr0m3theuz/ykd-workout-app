package com.example.myapplication.components.exerciseDetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloseFullscreen
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

  var expand by remember { mutableStateOf(false) }

  Scaffold() { paddingValues ->
    Column(modifier = Modifier
      .padding(paddingValues)
      .padding(16.dp)
      .fillMaxSize()) {
      Box(/*modifier = Modifier.fillMaxSize()*/) {

        IconButton(modifier = Modifier.align(Alignment.TopEnd), onClick = { expand = !expand }) {
          Icon(
            imageVector = if (!expand) Icons.Default.OpenInFull else Icons.Default.CloseFullscreen,
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
          )
        }
        VicoChart(modifier = if (expand) Modifier.fillMaxSize() else Modifier.fillMaxHeight(0.5f))
      }
    }
  }
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
          rememberLine(
            remember {
              LineCartesianLayer.LineFill.single(
                fill(primaryColor)
              )
            })
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
