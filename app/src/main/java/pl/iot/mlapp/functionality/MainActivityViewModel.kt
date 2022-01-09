package pl.iot.mlapp.functionality

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import pl.iot.mlapp.mqtt.MqttCameraReceiver
import pl.iot.mlapp.mqtt.MqttErrorType
import pl.iot.mlapp.mqtt.MqttMlReceiver

class MainActivityViewModel(
    private val mqttCamera: MqttCameraReceiver,
    private val mqttMl: MqttMlReceiver
) : ViewModel() {

    init {
        mqttObserveForErrors()
    }

    private val _errorLiveData = MutableLiveData<MqttErrorType>()
    val errorLiveData: LiveData<MqttErrorType> = _errorLiveData

    private fun mqttObserveForErrors() {
        viewModelScope.launch(Dispatchers.IO) {
            listOf(mqttCamera.connectionErrorFlow, mqttMl.connectionErrorFlow)
                .merge()
                .collect { _errorLiveData.postValue(it) }
        }
    }
}