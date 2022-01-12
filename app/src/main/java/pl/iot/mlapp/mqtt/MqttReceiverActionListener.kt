package pl.iot.mlapp.mqtt

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken

class MqttReceiverActionListener(
    private val onConnectionSuccess: (IMqttToken?) -> Unit,
    private val onConnectionFailure: suspend (Throwable) -> Unit,
    private val onConnectionFailureDispatcher: CoroutineDispatcher = Dispatchers.IO
): IMqttActionListener {
    override fun onSuccess(asyncActionToken: IMqttToken?) {
        onConnectionSuccess(asyncActionToken)
    }

    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable) {
        runBlocking(onConnectionFailureDispatcher) {
            onConnectionFailure(exception)
        }

        exception.printStackTrace()
    }
}