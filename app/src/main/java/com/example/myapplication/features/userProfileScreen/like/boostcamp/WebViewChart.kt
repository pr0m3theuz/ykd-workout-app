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
import com.newrelic.com.google.gson.Gson

data class ChartData(val labels: List<String>, val values: List<Number>)

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewChart(
  chartData: ChartData,
  chartType: ChartType,
  yMaxAllowed: Number,
  staticPlot: Boolean = false,
  period: Period,
) {
  // TODO Add locale to the chart
  val gson = Gson()
  val chartDataJson = gson.toJson(chartData)
  val xMinAllowed = chartData.labels.first()
  val xMaxAllowed = chartData.labels.last()
  val yMinAllowed = 0

  val color = MaterialTheme.colorScheme.primary.toRGBAString()
  val fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.40f).toRGBAString()

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
