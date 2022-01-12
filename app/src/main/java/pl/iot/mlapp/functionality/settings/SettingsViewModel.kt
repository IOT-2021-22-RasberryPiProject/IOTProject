package pl.iot.mlapp.functionality.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import pl.iot.mlapp.functionality.config.IConfigRepository
import pl.iot.mlapp.functionality.config.MqttConfig

class SettingsViewModel(
    private val configRepository: IConfigRepository
): ViewModel() {
    private val _config = MutableLiveData<MqttConfig>()
    val config: LiveData<MqttConfig> = _config

    init {
        _config.postValue(configRepository.getConfig())
    }

    fun saveConfig(config: MqttConfig) {
        _config.postValue(config)
        configRepository.saveConfig(config)
    }
}