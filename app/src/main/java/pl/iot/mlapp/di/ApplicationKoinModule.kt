package pl.iot.mlapp.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.iot.mlapp.mqtt.MqttConfig
import pl.iot.mlapp.mqtt.MqttManager

val appModule = module {
    single {
        MqttConfig(
            brokerIp = "192.168.2.229:1883",
            mlTopic = "ml",
            cameraTopic = "camera",
            clientId = "androidClient"
        )
    }
    single { MqttManager(androidContext(), get()) }
}