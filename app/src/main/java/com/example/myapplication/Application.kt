package com.example.myapplication

import android.app.Application
import com.example.myapplication.common.dependencyInjection.initKoin
import org.koin.android.ext.koin.androidContext

const val NOTIFICATION_CHANNEL_ID = "rest_timer"

class NameHereApplication : Application()/*, KoinComponent */ {

  override fun onCreate() {
    super.onCreate()

    initKoin {
      // Log Koin into Android logger
//      androidLogger()
      // Reference Android context
      androidContext(this@NameHereApplication)
      // Load modules
    }
  }
}
