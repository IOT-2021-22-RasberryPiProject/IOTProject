package pl.iot.mlapp.mqtt

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.beust.klaxon.Klaxon

data class MqttCameraResponseModel(
    val device: String,
    val time: String,
    val frame: String,
) {
    fun getBitmap(): Bitmap {
        val bytes = Base64.decode(frame, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    companion object {
        fun fromJson(json: String) = Klaxon().parse<MqttCameraResponseModel>(json)
    }
}