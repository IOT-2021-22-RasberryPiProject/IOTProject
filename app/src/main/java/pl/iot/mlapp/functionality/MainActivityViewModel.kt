package pl.iot.mlapp.functionality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import pl.iot.mlapp.functionality.notifications.domain.repository.NotificationsRepository
import pl.iot.mlapp.mqtt.model.MqttErrorType
import pl.iot.mlapp.mqtt.model.MqttMlResponseModel
import pl.iot.mlapp.mqtt.receivers.MqttCameraReceiver
import pl.iot.mlapp.mqtt.receivers.MqttMlReceiver

class MainActivityViewModel(
    private val cameraReceiver: MqttCameraReceiver,
    private val mlReceiver: MqttMlReceiver,
    private val notificationsRepository: NotificationsRepository
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
            mlReceiver.messageFlow.collect { mqttMessageModel ->
                _mlMessage.postValue(mqttMessageModel)
            }
        }
    }

    private fun mqttObserveForErrors() {
        viewModelScope.launch(Dispatchers.IO) {
            listOf(cameraReceiver.connectionErrorFlow, mlReceiver.connectionErrorFlow)
                .merge()
                .debounce(DEBOUNCE_TIMEOUT_MILLIS)
                .collect { _errorLiveData.postValue(it) }
        }
    }

    companion object {
        private const val DEBOUNCE_TIMEOUT_MILLIS = 1000L
    }
}