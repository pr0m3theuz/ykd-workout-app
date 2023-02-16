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

    val intent = MainActivity.newIntent(applicationContext)
    val pendingIntent = PendingIntent.getActivity(
        applicationContext,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val resources = applicationContext.resources

    val builder = NotificationCompat
        .Builder(
            applicationContext,
            applicationContext.getString(R.string.notification_channel_name)
        )
        .setTicker(resources.getString(R.string.new_timer_title))
        .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
        .setContentTitle(resources.getString(R.string.new_timer_title))
        .setContentText(resources.getString(R.string.rest_timer_text))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

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

/*

private fun notifyUser() {
    val intent = MainActivity.newIntent(context)
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )
    val resources = context.resources

    val notification = NotificationCompat
        .Builder(context, NOTIFICATION_CHANNEL_ID)
        .setTicker(resources.getString(R.string.new_timer_title))
        .setSmallIcon()
        .setContentTitle(resources.getString(R.string.new_timer_title))
        .setContentText(resources.getString(R.string.rest_timer_text))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    if (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    NotificationManagerCompat.from(context).notify(0, notification)
}
*/
