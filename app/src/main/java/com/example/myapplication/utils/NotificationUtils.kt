package com.example.myapplication.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity
import com.example.myapplication.R

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

// TODO: Step 1.1 extension function to send messages (GIVEN)
/**
 * Builds and delivers the notification.
 *
 * @param context, activity context.
 */

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    // TODO: Step 1.11 create intent
    // val contentIntent = Intent(applicationContext, MainActivity::class.java)

    // TODO: Step 1.12 create PendingIntent
    /*val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_IMMUTABLE
    )*/

    // TODO: Step 1.11 create intent
    val intent = MainActivity.newIntent(applicationContext)

    // TODO: Step 1.12 create PendingIntent
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
    )

    // TODO: Step 2.0 add style

    // TODO: Step 2.2 add snooze action

    // TODO: Step 1.2 get an instance of NotificationCompat.Builder
    // Build the notification
    val resources = applicationContext.resources

    val builder = NotificationCompat
        .Builder(
            applicationContext,
            // TODO: Step 1.8 use the new 'breakfast' notification channel
            applicationContext.getString(R.string.notification_channel_id),
        )
        .setTicker(resources.getString(R.string.new_timer_title))
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle(resources.getString(R.string.new_timer_title))
        .setContentText(messageBody)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    // TODO: Step 1.3 set title, text and icon to builder

    // TODO: Step 1.13 set content intent

    // TODO: Step 2.1 add style to builder

    // TODO: Step 2.3 add snooze action

    // TODO: Step 2.5 set priority

    // TODO: Step 1.4 call notify to send the notification
    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
}

// TODO: Step 1.14 Cancel all notifications
/**
 * Cancels all notifications.
 *
 */
fun NotificationManager.cancelNotifications() {
    cancelAll()
}
