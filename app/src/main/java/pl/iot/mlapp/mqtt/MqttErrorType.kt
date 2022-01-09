package pl.iot.mlapp.mqtt

sealed class MqttErrorType {
    abstract val message: String
}
