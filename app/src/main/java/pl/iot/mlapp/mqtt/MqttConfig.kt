package pl.iot.mlapp.mqtt

data class MqttConfig(
    val mlBrokerIp: String, // IP:PORT
    val mlTopic: String,

    val cameraBrokerIp: String, //IP:PORT
    val cameraTopic: String,

    val clientId: String
) {
    fun getTcpMlBroker() = "tcp://$mlBrokerIp"
    fun getTcpCameraBroker() = "tcp://$cameraBrokerIp"
}
