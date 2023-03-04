package com.example.myapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import com.example.myapplication.data.AppRoomDatabase
import com.example.myapplication.data.Repository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

const val NOTIFICATION_CHANNEL_ID = "rest_timer"

@HiltAndroidApp
class NameHereApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())

    @Inject
    lateinit var database: AppRoomDatabase

    //    val database by lazy { AppRoomDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { Repository(database.Dao()) }

    override fun onCreate() {
        super.onCreate()

        // TODO: Step 1.7 call create channel
        createChannel()
    }

    private fun createChannel(/*channelId: String, channelName: String*/) {
        // TODO: Step 1.6 START create a channel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)

            // TODO Insert notificationChannel customizations here
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.description = "Rest Timer"

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)

            // TODO: Step 1.6 END create a channel}
    }
}
