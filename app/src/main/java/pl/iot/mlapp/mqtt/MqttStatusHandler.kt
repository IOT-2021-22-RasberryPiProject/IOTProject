package pl.iot.mlapp.mqtt

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

class MqttStatusHandler(
    private val cameraReceiver: MqttCameraReceiver,
    private val mlReceiver: MqttMlReceiver,
) {
    init {
        observeFlows()
    }

    private fun observeFlows() {
        CoroutineScope(Job()).launch(Dispatchers.IO) {
            launch { observeCameraForError() }
            launch { observeMlForError() }
            launch { observeCameraForMessage() }
            launch { observeMlForMessage() }
        }
    }

    private suspend fun observeCameraForError() {
        cameraReceiver.connectionErrorFlow
            .collect {
                printLog(it)
                cameraReceiver.reconnect()
                delay(RETRY_TIME)
            }
    }

    private suspend fun observeMlForError() {
        mlReceiver.connectionErrorFlow
            .collect {
                printLog(it)
                mlReceiver.reconnect()
                delay(RETRY_TIME)
            }
    }

    private suspend fun observeCameraForMessage() {
        cameraReceiver.messageFlow
            .collect { Log.d(CAMERA_TAG, "received") }
    }

    private suspend fun observeMlForMessage() {
        mlReceiver.messageFlow
            .collect { Log.d(ML_TAG, "received") }
    }

    private fun printLog(error: MqttErrorType) =
        when (error) {
            is MqttErrorType.MlError.OnConnect -> Log.d(ML_TAG, "connection not established")
            is MqttErrorType.CameraError.OnConnect -> Log.d(
                CAMERA_TAG,
                "connection not established"
            )
            is MqttErrorType.MlError.LostConnection -> Log.d(ML_TAG, "connection lost")
            is MqttErrorType.CameraError.LostConnection -> Log.d(CAMERA_TAG, "connection lost")
        }

    companion object {
        private const val RETRY_TIME = 3000L //millis
        private const val CAMERA_TAG = "MqttCamera"
        private const val ML_TAG = "MqttMl"
    }
}