package pl.iot.mlapp.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.iot.mlapp.functionality.MainActivityViewModel
import pl.iot.mlapp.functionality.camera.CameraFragmentViewModel
import pl.iot.mlapp.functionality.service.AppLifecycleObserver
import pl.iot.mlapp.functionality.settings.SettingsViewModel
import pl.iot.mlapp.functionality.config.ConfigRepository
import pl.iot.mlapp.functionality.config.IConfigRepository
import pl.iot.mlapp.mqtt.receivers.MqttCameraReceiver
import pl.iot.mlapp.mqtt.receivers.MqttMlReceiver
import pl.iot.mlapp.mqtt.MqttStatusHandler

val appModule = module {
    single { AppLifecycleObserver(androidContext()) }

    single<IConfigRepository> { ConfigRepository(androidContext()) }

    single {
        MqttCameraReceiver(
            context = androidContext(),
            configRepository = get()
        )
    }

    single {
        MqttMlReceiver(
            context = androidContext(),
            configRepository = get()
        )
    }
    single {
        MqttStatusHandler(
            cameraReceiver = get(),
            mlReceiver = get()
        )
    }

    viewModel {
        MainActivityViewModel(
            cameraReceiver = get(),
            mlReceiver = get(),
            mqttHandler = get()
        )
    }

    viewModel {
        CameraFragmentViewModel(
            cameraReceiver = get()
        )
    }

    viewModel {
        SettingsViewModel(
            configRepository = get()
        )
    }
}