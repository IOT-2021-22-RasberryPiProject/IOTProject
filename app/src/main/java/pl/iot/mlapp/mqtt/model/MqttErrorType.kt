package pl.iot.mlapp.mqtt.model

sealed class MqttErrorType {
    abstract val message: String

    sealed class MlError : MqttErrorType() {
        class OnConnect(override val message: String) : MlError()
        class LostConnection(override val message: String) : MlError()
    }

    sealed class CameraError : MqttErrorType() {
        class OnConnect(override val message: String) : CameraError()
        class LostConnection(override val message: String) : CameraError()
    }
}
