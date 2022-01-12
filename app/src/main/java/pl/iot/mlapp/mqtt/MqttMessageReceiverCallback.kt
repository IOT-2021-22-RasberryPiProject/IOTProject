package pl.iot.mlapp.mqtt

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttMessageReceiverCallback(
    private val onMessageArrived: suspend (String, String) -> Unit,
    private val onConnectionLost: suspend (Throwable) -> Unit,
    private val messageArrivedDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val connectionLostDispatcher: CoroutineDispatcher = Dispatchers.IO
) : MqttCallback {
    override fun messageArrived(topic: String, message: MqttMessage) = runBlocking(messageArrivedDispatcher) {
        onMessageArrived(topic, message.payload.decodeToString())
    }

    override fun connectionLost(cause: Throwable) = runBlocking(connectionLostDispatcher) {
        onConnectionLost(cause)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken) = Unit
}