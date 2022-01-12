package pl.iot.mlapp.mqtt.receivers

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import pl.iot.mlapp.functionality.config.IConfigRepository
import pl.iot.mlapp.functionality.config.MqttConfig
import pl.iot.mlapp.mqtt.MqttMessageReceiverCallback
import pl.iot.mlapp.mqtt.MqttReceiverActionListener
import pl.iot.mlapp.mqtt.model.MqttErrorType

abstract class MqttReceiver<MessageDataModel>(
    private val context: Context,
    private val configRepository: IConfigRepository
) {
    protected val _messageFlow = MutableSharedFlow<MessageDataModel?>()
    protected val _connectionError = MutableSharedFlow<MqttErrorType>()

    val messageFlow: Flow<MessageDataModel?> = _messageFlow
    val connectionErrorFlow: Flow<MqttErrorType> = _connectionError

    protected var config: MqttConfig = configRepository.getConfig()
    private var client: MqttAndroidClient = initClient()

    init {
        connect()
    }

    abstract suspend fun onMessageArrived(topic: String, message: String)
    abstract suspend fun onConnectionLost(throwable: Throwable)
    abstract suspend fun onConnectionFailure(throwable: Throwable)
    abstract fun getTopic(): String

    fun reconnect() {
        client.unsubscribe(getTopic())
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
        val connOptions = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
        }

        client.connect(
            connOptions,
            MqttReceiverActionListener(
                onConnectionSuccess = { subscribeToTopic() },
                onConnectionFailure = { onConnectionFailure(it) }
            )
        )
    }

    private fun subscribeToTopic() = with(client) {
        subscribe(getTopic(), 0)
        setCallback(
            MqttMessageReceiverCallback(
                onMessageArrived = { topic, message -> onMessageArrived(topic, message) },
                onConnectionLost = { onConnectionLost(it) }
            )
        )
    }
}