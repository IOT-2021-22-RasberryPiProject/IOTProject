package pl.iot.mlapp.functionality.config

interface IConfigRepository {
    fun getConfig(): MqttConfig
    fun saveConfig(config: MqttConfig)
}