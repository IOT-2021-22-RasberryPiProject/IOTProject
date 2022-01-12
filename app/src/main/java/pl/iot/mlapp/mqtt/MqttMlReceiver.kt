package pl.iot.mlapp.mqtt

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import pl.iot.mlapp.functionality.config.MqttConfig

class MqttMlReceiver(
    context: Context,
    private val config: MqttConfig
) {
    sealed class MlError : MqttErrorType() {
        class OnConnect(override val message: String) : MlError()
        class LostConnection(override val message: String) : MlError()
    }

    private val client = MqttAndroidClient(
        context,
        config.getTcpBroker(),
        config.clientId,
        MemoryPersistence(),
        MqttAndroidClient.Ack.AUTO_ACK
    )

    private val _messageFlow = MutableSharedFlow<String>()
    private val _connectionError = MutableSharedFlow<MqttErrorType>()

    val messageFlow: Flow<String> = _messageFlow
    val connectionErrorFlow: Flow<MqttErrorType> = _connectionError

    init {
        connect()
    }

    fun reconnect() {
        disconnect()
        connect()
    }

    private fun disconnect() = client.disconnect()

    private fun connect() {
        val connOptions = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
        }

        client.connect(connOptions, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                subscribe()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                runBlocking {
                    _connectionError.emit(
                        MlError.OnConnect(exception?.stackTraceToString().toString())
                    )
                }
                Log.d(TAG, "connection not established")
                exception?.printStackTrace()
            }
        })
    }

    private fun subscribe() {
        client.subscribe(config.mlTopic, 0)

        client.setCallback(object : MqttCallback {
            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                runBlocking(Dispatchers.IO) {
                    Log.d(TAG, "received $topic")
                    _messageFlow.emit(message.payload.decodeToString())
                }
            }

            override fun connectionLost(cause: Throwable) {
                Log.d(TAG, "connection lost: ${cause.message.toString()}")
                runBlocking { _connectionError.emit(MlError.LostConnection(cause.message.toString())) }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {} //Nie dotyczy - nie wysyłamy nic
        })
    }

    companion object {
        private const val TAG = "MqttML"
    }
}