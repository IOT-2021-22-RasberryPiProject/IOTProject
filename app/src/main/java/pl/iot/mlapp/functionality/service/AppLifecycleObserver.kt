package pl.iot.mlapp.functionality.service

import android.content.Context
import android.content.Intent
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class AppLifecycleObserver(
    private val context: Context
): DefaultLifecycleObserver {

    // app goes to the foreground
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        stopForegroundService()
    }

    // App goes to the background
    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        startForegroundService()
    }

    private fun startForegroundService() {
        context.startForegroundService(getIntent(MqttNotificationService.ACTION_START_FOREGROUND_SERVICE))
    }

    private fun stopForegroundService() {
        context.startService(getIntent(MqttNotificationService.ACTION_STOP_FOREGROUND_SERVICE))
    }

    private fun getIntent(intentAction: String): Intent {
        return Intent(context, MqttNotificationService::class.java).apply {
            action = intentAction
        }
    }
}