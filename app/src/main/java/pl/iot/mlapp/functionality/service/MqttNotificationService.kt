package pl.iot.mlapp.functionality.service

import android.app.*
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import pl.iot.mlapp.R
import pl.iot.mlapp.functionality.MainActivity
import pl.iot.mlapp.mqtt.receivers.MqttMlReceiver

class MqttNotificationService : LifecycleService() {
    private val mqttMessageReceiver: MqttMlReceiver by inject()
    private val notificationManager by lazy { getSystemService(NotificationManager::class.java) }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.action?.let { handleIntentAction(it) }
        return START_STICKY
    }

    private fun handleIntentAction(action: String) {
        when (action) {
            ACTION_START_FOREGROUND_SERVICE -> startForegroundService()
            ACTION_STOP_FOREGROUND_SERVICE -> stopForegroundService()
        }
    }

    private fun startForegroundService() {
        startForeground(FOREGROUND_NOTIFICATION_ID, createForegroundNotification())
        createMqqtMessagesObserver()
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    private fun createNotificationChannel(
        channelId: String,
        channelTitle: String,
        notificationImportance: Int = NotificationManager.IMPORTANCE_NONE
    ) {
        val notificationChannel =  NotificationChannel(
            channelId,
            channelTitle,
            notificationImportance
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun createForegroundNotification(): Notification {
        createNotificationChannel(
            channelId = FOREGROUND_NOTIFICATION_CHANNEL_ID,
            channelTitle = getString(R.string.notification_foreground_channel_name),
            notificationImportance = NotificationManager.IMPORTANCE_NONE
        )

        return Notification.Builder(this, FOREGROUND_NOTIFICATION_CHANNEL_ID).build()
    }

    private fun createAlertNotification(message: String): Notification {
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            0 or PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(
            channelId = ALERT_NOTIFICATION_CHANNEL_ID,
            channelTitle = getString(R.string.notification_alert_channel_name),
            notificationImportance = NotificationManager.IMPORTANCE_MAX
        )

        return Notification.Builder(this, ALERT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_camera)
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createMqqtMessagesObserver() {
        lifecycleScope.launch(Dispatchers.IO) {
            mqttMessageReceiver.messageFlow.collect { observeIncomingMessages(it) }
        }
    }

    private fun observeIncomingMessages(message: String) {
        Log.d(TAG, "service has received a message: $message")
        notificationManager.notify(ALERT_NOTIFICATION_ID, createAlertNotification(message))
    }

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

        private const val FOREGROUND_NOTIFICATION_CHANNEL_ID = "FOREGROUND_NOTIFICATION_CHANNEL"
        private const val ALERT_NOTIFICATION_CHANNEL_ID = "ALERT_NOTIFICATION_CHANNEL"

        private const val FOREGROUND_NOTIFICATION_ID = 1
        private const val ALERT_NOTIFICATION_ID = 2

        private const val TAG = "FOREGROUND_SERVICE"
    }
}