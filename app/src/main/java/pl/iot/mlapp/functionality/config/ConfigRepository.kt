package pl.iot.mlapp.functionality.config

import android.content.Context
import pl.iot.mlapp.functionality.sharedpreferences.SharedPreferencesHelper

class ConfigRepository(context: Context) : IConfigRepository {
    private val sharedPreferencesHelper = SharedPreferencesHelper(context)

    override fun getConfig(): MqttConfig = with(sharedPreferencesHelper) {
        return MqttConfig(
            brokerIp = getStringValue(BROKER_IP_KEY, BROKER_IP_DEFAULT_VALUE),
            cameraTopic = getStringValue(CAMERA_TOPIC_KEY, CAMERA_TOPIC_DEFAULT_VALUE),
            mlTopic = getStringValue(ML_TOPIC_KEY, ML_TOPIC_DEFAULT_VALUE)
        )
    }

    override fun saveConfig(config: MqttConfig) = with(sharedPreferencesHelper) {
        saveStringValue(BROKER_IP_KEY, config.brokerIp)
        saveStringValue(CAMERA_TOPIC_KEY, config.cameraTopic)
        saveStringValue(ML_TOPIC_KEY, config.mlTopic)
    }

    companion object {
        private const val BROKER_IP_KEY = "BROKER_IP"
        private const val CAMERA_TOPIC_KEY = "CAMERA_TOPIC_KEY"
        private const val ML_TOPIC_KEY = "ML_TOPIC_KEY"

        private const val BROKER_IP_DEFAULT_VALUE = "192.168.2.138"
        private const val CAMERA_TOPIC_DEFAULT_VALUE = "monitoring/frame"
        private const val ML_TOPIC_DEFAULT_VALUE = "ml"
    }
}