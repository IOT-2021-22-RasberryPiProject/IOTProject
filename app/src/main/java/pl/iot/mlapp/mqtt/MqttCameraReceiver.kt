package pl.iot.mlapp.mqtt

import android.content.Context
import android.util.Log
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttCameraReceiver(
    context: Context,
    private val config: MqttConfig
) {
    private val client = MqttAndroidClient(
        context,
        config.getTcpCameraBroker(),
        config.clientId,
        MemoryPersistence(),
        MqttAndroidClient.Ack.AUTO_ACK
    )

    private val _messageFlow = MutableSharedFlow<MqttCameraResponseModel?>()
    private val _connectionError = MutableSharedFlow<MqttErrorType>()

    val messageFlow: Flow<MqttCameraResponseModel?> = _messageFlow
    val connectionErrorFlow: Flow<MqttErrorType> = _connectionError

    private val TAG = "MqttCamera"

    init {
        val connOptions = MqttConnectOptions()
        connOptions.isAutomaticReconnect = true
        connOptions.isCleanSession = true

        client.connect(connOptions, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                subscribe()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                runBlocking {
                    _connectionError.emit(
                        CameraError.OnConnect(exception?.stackTraceToString().toString())
                    )
                }
                Log.d(TAG, "connection not established")
                exception?.printStackTrace()
            }
        })
    }

    private fun subscribe() {
        client.subscribe(config.cameraTopic, 0)

        client.setCallback(object : MqttCallback {
            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                runBlocking(Dispatchers.IO) {
                    Log.d(TAG, "received $topic")
                    _messageFlow.emit(MqttCameraResponseModel.fromJson(message.payload.decodeToString()))
                }
            }

            override fun connectionLost(cause: Throwable) {
                Log.d(TAG, "connection lost: ${cause.message.toString()}")
                runBlocking { _connectionError.emit(CameraError.LostConnection(cause.message.toString())) }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {} //Nie dotyczy - nie wysyłamy nic
        })
    }

    companion object {
        sealed class CameraError : MqttErrorType() {
            class OnConnect(override val message: String) : CameraError()
            class LostConnection(override val message: String) : CameraError()
        }
    }
}