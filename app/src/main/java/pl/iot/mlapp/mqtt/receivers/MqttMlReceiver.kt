package pl.iot.mlapp.mqtt.receivers

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import pl.iot.mlapp.functionality.config.IConfigRepository
import pl.iot.mlapp.functionality.config.MqttConfig
import pl.iot.mlapp.mqtt.model.MqttErrorType

class MqttMlReceiver(
    private val context: Context,
    private val configRepository: IConfigRepository
) {
    private val _messageFlow = MutableSharedFlow<String>()
    private val _connectionError = MutableSharedFlow<MqttErrorType>()

    val messageFlow: Flow<String> = _messageFlow
    val connectionErrorFlow: Flow<MqttErrorType> = _connectionError

    private var config: MqttConfig = configRepository.getConfig()
    private var client: MqttAndroidClient = initClient()

    init {
        connect()
    }

    fun reconnect() {
        client.unsubscribe(config.mlTopic)
        config = configRepository.getConfig()
        client.unregisterResources()
        client = initClient()
        connect()
    }

    private fun initClient() = MqttAndroidClient(
        context,
        config.getTcpBroker(),
        config.clientId,
        MemoryPersistence(),
        MqttAndroidClient.Ack.AUTO_ACK
    )

    private fun connect() {
        val connOptions = MqttConnectOptions()

        client.connect(connOptions, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                subscribe(client)
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                runBlocking {
                    _connectionError.emit(
                        MqttErrorType.MlError.OnConnect(exception?.stackTraceToString().toString())
                    )
                }
                exception?.printStackTrace()
            }
        })
    }

    private fun subscribe(client: MqttAndroidClient) {
        client.subscribe(config.mlTopic, 0)

        client.setCallback(object : MqttCallback {
            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                runBlocking(Dispatchers.IO) {
                    _messageFlow.emit(message.payload.decodeToString())
                }
            }

            override fun connectionLost(cause: Throwable) {
                runBlocking { _connectionError.emit(MqttErrorType.MlError.LostConnection(cause.message.toString())) }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken) = Unit //Nie dotyczy - nie wysyłamy nic
        })
    }
}