package pl.iot.mlapp.functionality.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.iot.mlapp.mqtt.MqttCameraReceiver
import pl.iot.mlapp.mqtt.MqttMlReceiver

class CameraFragmentViewModel(
    private val cameraReceiver: MqttCameraReceiver,
    private val mlReceiver: MqttMlReceiver
) : ViewModel() {
    private val _bitmapLiveData = MutableLiveData<Bitmap>()
    val bitmapLiveData: LiveData<Bitmap> = _bitmapLiveData

    init {
        observeCamera()
    }

    private fun observeCamera() {
        viewModelScope.launch(Dispatchers.Default) {
            cameraReceiver.messageFlow
                .collect { _bitmapLiveData.postValue(it?.getBitmap()) }
        }
    }
}