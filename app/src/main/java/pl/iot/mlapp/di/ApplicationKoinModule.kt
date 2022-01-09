package pl.iot.mlapp.di

import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pl.iot.mlapp.functionality.MainActivityViewModel
import pl.iot.mlapp.functionality.camera.CameraFragmentViewModel
import pl.iot.mlapp.mqtt.MqttConfig
import pl.iot.mlapp.mqtt.MqttCameraReceiver
import pl.iot.mlapp.mqtt.MqttMlReceiver

val appModule = module {
    single {
        MqttConfig(
            mlBrokerIp = "192.168.2.229:1883",
            mlTopic = "ml",
            cameraBrokerIp = "192.168.2.229:1883",
            cameraTopic = "camera",
            clientId = "androidClient"
        )
    }
    single { MqttCameraReceiver(androidContext(), get()) }
    single { MqttMlReceiver(androidContext(), get()) }

    viewModel { MainActivityViewModel(get(), get()) }
    viewModel { CameraFragmentViewModel(get(), get()) }
}