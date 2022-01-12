package pl.iot.mlapp.di

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.iot.mlapp.functionality.MainActivityViewModel
import pl.iot.mlapp.functionality.camera.CameraFragmentViewModel
import pl.iot.mlapp.functionality.service.AppLifecycleObserver
import pl.iot.mlapp.functionality.settings.SettingsViewModel
import pl.iot.mlapp.functionality.config.ConfigRepository
import pl.iot.mlapp.functionality.config.IConfigRepository
import pl.iot.mlapp.functionality.config.MqttConfig
import pl.iot.mlapp.mqtt.MqttCameraReceiver
import pl.iot.mlapp.mqtt.MqttMlReceiver

val appModule = module {
    single {
        MqttConfig(
            brokerIp = "192.168.2.138",
            mlTopic = "ml",
            cameraTopic = "monitoring/frame",
            clientId = "androidClient"
        )
    }

    single { MqttCameraReceiver(androidContext(), get()) }
    single { MqttMlReceiver(androidContext(), get()) }

    single { AppLifecycleObserver(androidContext()) }

    single<IConfigRepository> { ConfigRepository(androidApplication()) }

    viewModel { MainActivityViewModel(get(), get()) }
    viewModel { CameraFragmentViewModel(get(), get()) }
    viewModel { SettingsViewModel(configRepository = get()) }
}