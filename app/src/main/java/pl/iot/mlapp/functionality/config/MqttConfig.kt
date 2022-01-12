package pl.iot.mlapp.functionality.config

data class MqttConfig(
    val brokerIp: String,
    val mlTopic: String,
    val cameraTopic: String,
    val clientId: String = "androidClient"
) {
    fun getTcpBroker() = "tcp://$brokerIp"
}
