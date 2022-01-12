package pl.iot.mlapp.functionality.service

import android.app.*
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import org.koin.android.ext.android.inject
import pl.iot.mlapp.R
import pl.iot.mlapp.functionality.MainActivity
import pl.iot.mlapp.mqtt.MqttMlReceiver

class MqttNotificationService : LifecycleService() {
    private val mqttMessageReceiver: MqttMlReceiver by inject()

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
        startForeground(IOT_NOTIFICATION_ID, createNotification())
        createMqqtMessagesObserver()
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    private fun createNotification(): Notification {
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            0 or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationChannel = NotificationChannel(
            IOT_NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)

        return Notification.Builder(this, IOT_NOTIFICATION_CHANNEL_ID)
            .setContentTitle("IOT")
            .setContentText("Aplikacja dziala w tle")
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createMqqtMessagesObserver() {
        lifecycleScope.launch {
            mqttMessageReceiver.messageFlow.collect { messagesObserver(it) }
        }
    }

    private fun messagesObserver(message: String) {
        Log.d(TAG, "service has received a message: $message")
    }

    companion object {
        const val ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"

        private const val IOT_NOTIFICATION_CHANNEL_ID = "IOT_NOTIFICATION_CHANNEL"
        private const val IOT_NOTIFICATION_ID = 1

        private const val TAG = "FOREGROUND_SERVICE"
    }
}