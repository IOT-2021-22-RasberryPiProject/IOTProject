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
import pl.iot.mlapp.mqtt.MqttMessageReceiverCallback
import pl.iot.mlapp.mqtt.MqttReceiverActionListener
import pl.iot.mlapp.mqtt.model.MqttCameraResponseModel
import pl.iot.mlapp.mqtt.model.MqttErrorType
import pl.iot.mlapp.mqtt.model.MqttMlResponseModel

class MqttMlReceiver(
    context: Context,
    configRepository: IConfigRepository
): MqttReceiver<MqttMlResponseModel>(context, configRepository) {
    override fun getTopic(): String = config.mlTopic

    override suspend fun onMessageArrived(topic: String, message: String) {
        _messageFlow.emit(MqttMlResponseModel.fromJson(message))
    }

    override suspend fun onConnectionLost(throwable: Throwable) {
        _connectionError.emit(MqttErrorType.MlError.LostConnection(throwable.message.toString()))
    }

    override suspend fun onConnectionFailure(throwable: Throwable) {
        _connectionError.emit(MqttErrorType.MlError.OnConnect(throwable.stackTraceToString()))
    }
}