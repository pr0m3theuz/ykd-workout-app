package com.example.myapplication.common.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.myapplication.R

class AlarmReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val notificationManager =
      ContextCompat.getSystemService(context, NotificationManager::class.java)
        as NotificationManager

    notificationManager.cancelNotifications()

    notificationManager.sendNotification(
      context.getText(R.string.rest_timer_ended_text).toString(),
      context,
      silent = false,
      setAutoCancel = false,
    )
  }
}
