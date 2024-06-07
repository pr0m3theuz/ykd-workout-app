package com.example.myapplication.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

fun NotificationManager.sendNotification(
  messageBody: String,
  applicationContext: Context,
  silent: Boolean,
  setAutoCancel: Boolean = true,
) {
  val intent = MainActivity.newIntent(applicationContext)

  val pendingIntent =
    PendingIntent.getActivity(
      applicationContext,
      REQUEST_CODE,
      intent,
      PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

  val resources = applicationContext.resources

  val builder =
    NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id),
      )
      .setTicker(resources.getString(R.string.new_timer_title))
      .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
      //      .setLargeIcon(Icon(Icons.Default))
      .setContentTitle(resources.getString(R.string.new_timer_title))
      .setContentText(messageBody)
      .setContentIntent(pendingIntent)
      .setOnlyAlertOnce(true)
      .setAutoCancel(setAutoCancel)
      .setOngoing(true)
      .setSilent(silent)
      .setLights(Color.RED, 3000, 3000)
      .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
      .setPriority(NotificationCompat.PRIORITY_HIGH)

  notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
  cancelAll()
}
