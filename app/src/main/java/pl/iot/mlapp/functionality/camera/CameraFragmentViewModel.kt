package pl.iot.mlapp.functionality.camera

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import pl.iot.mlapp.mqtt.MqttCameraReceiver

class CameraFragmentViewModel(
    private val cameraReceiver: MqttCameraReceiver,
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