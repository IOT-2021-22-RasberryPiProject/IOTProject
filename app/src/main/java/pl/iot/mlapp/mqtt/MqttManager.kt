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

class MqttManager(
    context: Context,
    private val config: MqttConfig
) {
    private val client = MqttAndroidClient(
        context,
        config.getTcpBroker(),
        config.clientId,
        MemoryPersistence(),
        MqttAndroidClient.Ack.AUTO_ACK
    )

    private val _mlMessageFlow = MutableSharedFlow<String>()
    private val _cameraDataFlow = MutableSharedFlow<ByteArray>()
    private val _connectionError = MutableSharedFlow<ErrorType>()

    val mlMessageFlow: Flow<String> = _mlMessageFlow
    val cameraDataFlow: Flow<ByteArray> = _cameraDataFlow
    val connectionErrorFlow: Flow<ErrorType> = _connectionError

    init {
        val connOptions = MqttConnectOptions()
        connOptions.isAutomaticReconnect = true
        connOptions.isCleanSession = true

        client.connect(connOptions, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                subscribe()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                runBlocking { _connectionError.emit(ErrorType.OnConnect(exception?.stackTraceToString().toString())) }
                exception?.printStackTrace()
            }
        })
    }

    private fun subscribe() {
        client.subscribe(config.mlTopic, 0)
        client.subscribe(config.cameraTopic, 0)

        client.setCallback(object : MqttCallback {
            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                runBlocking(Dispatchers.IO) {
                    Log.d("MQTT received: ", "topic: $topic")
                    if (topic == config.mlTopic)
                        _mlMessageFlow.emit(message.payload.decodeToString())
                    else if (topic == config.cameraTopic)
                        _cameraDataFlow.emit(message.payload)
                }
            }

            override fun connectionLost(cause: Throwable) {
                Log.d("MQTT connectionLost: ", cause.message.toString())
                runBlocking { _connectionError.emit(ErrorType.LostConnection(cause.message.toString())) }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {} //Nie dotyczy - nie wysyłamy nic
        })
    }

    companion object {
        sealed class ErrorType {
            abstract val message: String

            class OnConnect(override val message: String) : ErrorType()
            class LostConnection(override val message: String) : ErrorType()
        }
    }
}