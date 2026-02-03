package com.example.myapplication.features.userProfileScreen.like.boostcamp

import android.annotation.SuppressLint
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.myapplication.features.userProfileScreen.like.strengthLog.toRGBAString
import com.google.gson.Gson
import kotlinx.datetime.toKotlinTimeZone
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.time.ExperimentalTime

// TODO Add hover text
data class ChartData(val labels: List<String>, val values: List<Number>)

enum class ChartType(val type: String) {
  VOLUME("volume"),
  FREQUENCY("frequency"),
}

@ExperimentalTime
fun generateRandomExerciseVolumeData(): ChartData {
  val labels = mutableListOf<String>()
  val values = mutableListOf<Number>()

  val timezone = java.util.TimeZone.getDefault().toZoneId().toKotlinTimeZone()
  val endDate = java.time.LocalDate.now()
  // Clock.System.now().toLocalDateTime(timezone).date // Get today's date
  val startDate = endDate.minusDays(90) // Get the date 6 days ago for a week's data

  var currentDate = startDate
  val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  // Adjustable probability for a day to have exercise data (e.g., 0.7 for 70% chance)
  val probabilityOfExercise = 0.45 // Let's say 65% chance of having an exercise entry

  while (!currentDate.isAfter(endDate)) {
    // Decide if this day should have exercise data based on probability
    if (Random.nextDouble() < probabilityOfExercise) {
      labels.add(currentDate.format(formatter))

      // Generate a random volume in kilograms (e.g., between 5000 kg and 20000 kg)
      val volume = Random.nextInt(500, 5001)
      values.add(volume)
    }
    // If the condition is false, this day is simply skipped (no data added)

    currentDate = currentDate.plusDays(1)
  }

  return ChartData(labels, values)
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewChart(
  chartData: ChartData,
  chartType: ChartType,
  yMaxAllowed: Number,
  staticPlot: Boolean = false,
  //  period: Period,
) {
  // TODO Add locale to the chart
  val gson = Gson()
  val chartDataJson = gson.toJson(chartData)
  val xMinAllowed = chartData.labels.first()
  val xMaxAllowed = chartData.labels.last()
  val yMinAllowed = 0

  val color = MaterialTheme.colorScheme.primary.toRGBAString()
  val fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.20f).toRGBAString()

  val paperBGColor = MaterialTheme.colorScheme.surface.toRGBAString()
  val axesColor = MaterialTheme.colorScheme.onSurface.toRGBAString()
  val gridColor = MaterialTheme.colorScheme.outlineVariant.toRGBAString()

  Log.i("WebViewChart", "$chartDataJson")

  val context = LocalContext.current

  val webView = remember {
    WebView(context).apply {
      layoutParams =
        ViewGroup.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.MATCH_PARENT,
        )

      settings.javaScriptEnabled = true
      webViewClient =
        object : WebViewClient() {
          override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            val baseFunctionName =
              when (chartType) {
                ChartType.FREQUENCY -> "displayFrequencyChart"
                ChartType.VOLUME -> "displayVolumeChart"
              }
            val script =
              "$baseFunctionName($chartDataJson, " +
                      "'$xMinAllowed', '$xMaxAllowed', $yMinAllowed, $yMaxAllowed, $staticPlot, " +
                      "'$color', '$fillColor', '$paperBGColor', " +
                      "'$paperBGColor', '$axesColor', '$gridColor')"

            Log.i("WebViewChart", "Initial WebView with new data. Script: $script")

            view?.evaluateJavascript(script, null)
          }
        }
      settings.apply {
        javaScriptEnabled = true
        cacheMode = WebSettings.LOAD_NO_CACHE
      }
      webChromeClient =
        object : WebChromeClient() {
          override fun onConsoleMessage(message: ConsoleMessage): Boolean {
            Log.d(
              "WebViewChart",
              "${message.message()} -- From line " +
                      "${message.lineNumber()} of ${message.sourceId()}",
            )
            return true
          }
        }
      loadUrl("file:///android_asset/web/charts-plotly.html")
    }
  }

  AndroidView(
    factory = { context ->
      FrameLayout(context).apply {
        if (webView.parent != null) {
          (webView.parent as ViewGroup).removeView(webView)
        }
        addView(webView)
      }
    },
    update = {
      Log.i("WebViewChart", "Updating WebView with new data. ChartData: $chartDataJson")

      val baseFunctionName =
        when (chartType) {
          ChartType.FREQUENCY -> "displayFrequencyChart"
          ChartType.VOLUME -> "displayVolumeChart"
        }
      val script =
        "$baseFunctionName($chartDataJson, '$xMinAllowed', '$xMaxAllowed', " +
                "$yMinAllowed, $yMaxAllowed, $staticPlot, '$color', '$fillColor', " +
                "'$paperBGColor', '$paperBGColor', '$axesColor', '$gridColor')"

      Log.i("WebViewChart", "Updating WebView with new data. Script: $script")

      webView.evaluateJavascript(script, null)
      webView.reload()
    },
  )
  //    }

  //    Period.WEEKLY -> {
  //      AndroidView(
  //        factory = { context ->
  //          WebView(context).apply {
  //            layoutParams =
  //              ViewGroup.LayoutParams(
  //                ViewGroup.LayoutParams.MATCH_PARENT,
  //                ViewGroup.LayoutParams.MATCH_PARENT,
  //              )
  //
  //            settings.javaScriptEnabled = true
  //            webViewClient =
  //              object : WebViewClient() {
  //                override fun onPageFinished(view: WebView?, url: String?) {
  //                  super.onPageFinished(view, url)
  //                  val script =
  //                    when (chartType) {
  //                      ChartType.FREQUENCY ->
  //                        "displayFrequencyChart($chartDataJson, '$xMinAllowed', '$xMaxAllowed',
  // $yMinAllowed, $yMaxAllowed, $staticPlot, '$color', '$fillColor', '$paperBGColor',
  // '$paperBGColor', '$axesColor', '$gridColor')"
  //
  //                      ChartType.VOLUME ->
  //                        "displayVolumeChart($chartDataJson, '$xMinAllowed', '$xMaxAllowed',
  // $yMinAllowed, $yMaxAllowed, $staticPlot, '$color', '$fillColor', '$paperBGColor',
  // '$paperBGColor', '$axesColor', '$gridColor')"
  //                    }
  //                  view?.evaluateJavascript(script, null)
  //                }
  //              }
  //            settings.apply {
  //              javaScriptEnabled = true
  //              cacheMode = WebSettings.LOAD_NO_CACHE
  //            }
  //            webChromeClient =
  //              object : WebChromeClient() {
  //                override fun onConsoleMessage(message: ConsoleMessage): Boolean {
  //                  Log.d(
  //                    "WebViewChart",
  //                    "${message.message()} -- From line " +
  //                      "${message.lineNumber()} of ${message.sourceId()}",
  //                  )
  //                  return true
  //                }
  //              }
  //            loadUrl("file:///android_asset/web/charts-plotly.html")
  //            webView = this
  //          }
  //        }
  //        //    update = {it.loadUrl("file:///android_asset/web/charts-plotly.html")}
  //      )
  //    }
  //
  //    Period.MONTHLY -> {
  //      AndroidView(
  //        factory = { context ->
  //          WebView(context).apply {
  //            layoutParams =
  //              ViewGroup.LayoutParams(
  //                ViewGroup.LayoutParams.MATCH_PARENT,
  //                ViewGroup.LayoutParams.MATCH_PARENT,
  //              )
  //
  //            settings.javaScriptEnabled = true
  //            webViewClient =
  //              object : WebViewClient() {
  //                override fun onPageFinished(view: WebView?, url: String?) {
  //                  super.onPageFinished(view, url)
  //                  val script =
  //                    when (chartType) {
  //                      ChartType.FREQUENCY ->
  //                        "displayFrequencyChart($chartDataJson, '$xMinAllowed', '$xMaxAllowed',
  // $yMinAllowed, $yMaxAllowed, $staticPlot, '$color', '$fillColor', '$paperBGColor',
  // '$paperBGColor', '$axesColor', '$gridColor')"
  //
  //                      ChartType.VOLUME ->
  //                        "displayVolumeChart($chartDataJson, '$xMinAllowed', '$xMaxAllowed',
  // $yMinAllowed, $yMaxAllowed, $staticPlot, '$color', '$fillColor', '$paperBGColor',
  // '$paperBGColor', '$axesColor', '$gridColor')"
  //                    }
  //                  view?.evaluateJavascript(script, null)
  //                }
  //              }
  //            settings.apply {
  //              javaScriptEnabled = true
  //              cacheMode = WebSettings.LOAD_NO_CACHE
  //            }
  //            webChromeClient =
  //              object : WebChromeClient() {
  //                override fun onConsoleMessage(message: ConsoleMessage): Boolean {
  //                  Log.d(
  //                    "WebViewChart",
  //                    "${message.message()} -- From line " +
  //                      "${message.lineNumber()} of ${message.sourceId()}",
  //                  )
  //                  return true
  //                }
  //              }
  //            loadUrl("file:///android_asset/web/charts-plotly.html")
  //            webView = this
  //          }
  //        }
  //        //    update = {it.loadUrl("file:///android_asset/web/charts-plotly.html")}
  //      )
  //    }
  //  }

  /*  AndroidView(
      factory = { context ->
        WebView(context).apply {
          layoutParams =
            ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT,
            )

          settings.javaScriptEnabled = true
          webViewClient =
            object : WebViewClient() {
              override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val script =
                  when (chartType) {
                    ChartType.FREQUENCY ->
                      "displayFrequencyChart($chartDataJson, $xMinAllowed, $xMaxAllowed, $yMinAllowed, $yMaxAllowed, $staticPlot, '$color', '$fillColor', '$paperBGColor', '$paperBGColor', '$axesColor', '$gridColor')"
                    ChartType.VOLUME ->
                      "displayVolumeChart($chartDataJson, $xMinAllowed, $xMaxAllowed, $yMinAllowed, $yMaxAllowed, $staticPlot, '$color', '$fillColor', '$paperBGColor', '$paperBGColor', '$axesColor', '$gridColor')"
                  }
                view?.evaluateJavascript(script, null)

              }
            }
          settings.apply {
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
          }
          webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage): Boolean {
              Log.d("WebViewChart", "${message.message()} -- From line " +
                      "${message.lineNumber()} of ${message.sourceId()}")
              return true
            }
          }
          loadUrl("file:///android_asset/web/charts-plotly.html")
          webView = this
        }
      }
  //    update = {it.loadUrl("file:///android_asset/web/charts-plotly.html")}
    )*/
}
