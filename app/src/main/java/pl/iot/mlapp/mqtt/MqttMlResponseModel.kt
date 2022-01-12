package pl.iot.mlapp.mqtt

import com.beust.klaxon.Klaxon

data class MqttMlResponseModel(
    val statusCode: Int,
    val message: String
) {
    companion object {
        fun fromJson(json: String) = Klaxon().parse<MqttMlResponseModel>(json)
    }
}
