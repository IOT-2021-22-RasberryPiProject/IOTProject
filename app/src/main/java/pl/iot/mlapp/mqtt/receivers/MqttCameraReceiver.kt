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
import pl.iot.mlapp.mqtt.model.MqttCameraResponseModel
import pl.iot.mlapp.mqtt.model.MqttErrorType

class MqttCameraReceiver(
    private val context: Context,
    private val configRepository: IConfigRepository
) {
    private val _messageFlow = MutableSharedFlow<MqttCameraResponseModel?>()
    private val _connectionError = MutableSharedFlow<MqttErrorType>()

    val messageFlow: Flow<MqttCameraResponseModel?> = _messageFlow
    val connectionErrorFlow: Flow<MqttErrorType> = _connectionError

    private var config: MqttConfig = configRepository.getConfig()
    private var client: MqttAndroidClient = initClient()

    private fun initClient() = MqttAndroidClient(
        context,
        config.getTcpBroker(),
        config.clientId,
        MemoryPersistence(),
        MqttAndroidClient.Ack.AUTO_ACK
    )

    init {
        connect()
    }

    fun reconnect() {
        config = configRepository.getConfig()
        client.unregisterResources()
        client = initClient()
        connect()
    }

    private fun connect() {
        val connOptions = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
        }

        client.connect(connOptions, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) = subscribeToTopic()

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                runBlocking(Dispatchers.IO) {
                    _connectionError.emit(
                        MqttErrorType.CameraError.OnConnect(
                            exception?.stackTraceToString().toString()
                        )
                    )
                }
                exception?.printStackTrace()
            }
        })
    }

    private fun subscribeToTopic() {
        client.subscribe(config.cameraTopic, 0)

        client.setCallback(object : MqttCallback {
            @Throws(Exception::class)
            override fun messageArrived(topic: String, message: MqttMessage) {
                runBlocking(Dispatchers.IO) {
                    _messageFlow.emit(MqttCameraResponseModel.fromJson(message.payload.decodeToString()))
                }
            }

            override fun connectionLost(cause: Throwable) {
                runBlocking { _connectionError.emit(MqttErrorType.CameraError.LostConnection(cause.message.toString())) }
            }

            //Nie dotyczy - nie wysyłamy nic
            override fun deliveryComplete(token: IMqttDeliveryToken) = Unit
        })
    }
}