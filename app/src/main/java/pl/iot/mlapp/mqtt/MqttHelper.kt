package pl.iot.mlapp.mqtt

import org.eclipse.paho.client.mqttv3.IMqttClient
import org.eclipse.paho.client.mqttv3.MqttClient
import java.util.*

class MqttHelper {

    init {
        val publisherId: String = UUID.randomUUID().toString()
        val publisher: IMqttClient = MqttClient("tcp://iot.eclipse.org:1883", publisherId)
    }
}