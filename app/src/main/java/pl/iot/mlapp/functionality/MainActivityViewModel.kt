package pl.iot.mlapp.functionality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import pl.iot.mlapp.mqtt.receivers.MqttCameraReceiver
import pl.iot.mlapp.mqtt.model.MqttErrorType
import pl.iot.mlapp.mqtt.receivers.MqttMlReceiver
import pl.iot.mlapp.mqtt.MqttStatusHandler
import pl.iot.mlapp.mqtt.model.MqttMlResponseModel

class MainActivityViewModel(
    private val cameraReceiver: MqttCameraReceiver,
    private val mlReceiver: MqttMlReceiver,
    private val mqttHandler: MqttStatusHandler,
) : ViewModel() {

    init {
        mqttObserveForErrors()
        observeForMlMessages()
    }

    private val _errorLiveData = MutableLiveData<MqttErrorType>()
    val errorLiveData: LiveData<MqttErrorType> = _errorLiveData

    private val _mlMessage = MutableLiveData<MqttMlResponseModel>()
    val mlMessage: LiveData<MqttMlResponseModel> = _mlMessage

    private fun observeForMlMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            mlReceiver.messageFlow.collect { _mlMessage.postValue(it) }
        }
    }

    private fun mqttObserveForErrors() {
        viewModelScope.launch(Dispatchers.IO) {
            listOf(cameraReceiver.connectionErrorFlow, mlReceiver.connectionErrorFlow)
                .merge()
                .collect { _errorLiveData.postValue(it) }
        }
    }
}