package com.example.myapplication

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

const val NOTIFICATION_CHANNEL_ID = "rest_timer"

@HiltAndroidApp
class NameHereApplication : Application() {
//    private val applicationScope = CoroutineScope(SupervisorJob())

//    @Inject
//    lateinit var database: AppRoomDatabase
//
//    @Inject
//    lateinit var repository: Repository

//    private val database by lazy { AppRoomDatabase.getDatabase(this, applicationScope) }
//    val repository by lazy { Repository(database.exerciseDao()) }

    override fun onCreate() {
        super.onCreate()

        // TODO: Step 1.7 call create channel
        createChannel()
    }

    private fun createChannel(/*channelId: String, channelName: String*/) {
        // TODO: Step 1.6 START create a channel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            // TODO: Step 2.4 change importance
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)

            // TODO: Step 2.6 disable badges for this channel

            // TODO Insert notificationChannel customizations here
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.description = "Rest Timer"

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)

            // TODO: Step 1.6 END create a channel
        }
    }
}
