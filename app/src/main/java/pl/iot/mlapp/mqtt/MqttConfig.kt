package pl.iot.mlapp.mqtt

data class MqttConfig(
    val brokerIp: String, // IP:PORT
    val mlTopic: String,
    val cameraTopic: String,
    val clientId: String
) {
    fun getTcpBroker() = "tcp://$brokerIp"
}
