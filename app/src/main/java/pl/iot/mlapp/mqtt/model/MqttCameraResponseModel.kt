package pl.iot.mlapp.mqtt.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.beust.klaxon.Klaxon

data class MqttCameraResponseModel(
    val device: String,
    val time: Double,
    val frame: String,
) {
    fun getBitmap(): Bitmap {
        val bytes = Base64.decode(frame.substring(0, frame.length - 4), Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    // RPI sends "\\n" at the end what needs to be removed
    private fun prepareFrameString() = frame.substring(0, frame.length - 4)

    companion object {
        fun fromJson(json: String) = Klaxon().parse<MqttCameraResponseModel>(json)
    }
}