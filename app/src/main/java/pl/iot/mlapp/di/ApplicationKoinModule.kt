package pl.iot.mlapp.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.binds
import org.koin.dsl.module
import pl.iot.mlapp.functionality.MainActivityViewModel
import pl.iot.mlapp.functionality.camera.CameraFragmentViewModel
import pl.iot.mlapp.functionality.config.ConfigRepository
import pl.iot.mlapp.functionality.config.IConfigRepository
import pl.iot.mlapp.functionality.notifications.domain.db.NotificationsDatabase
import pl.iot.mlapp.functionality.notifications.domain.mapper.NotificationMapper
import pl.iot.mlapp.functionality.notifications.domain.repository.NotificationsRepository
import pl.iot.mlapp.functionality.notifications.presentation.NotificationsViewModel
import pl.iot.mlapp.functionality.notifications.presentation.mapper.NotificationUiMapper
import pl.iot.mlapp.functionality.notifications.util.NotificationsDataChangedNotifier
import pl.iot.mlapp.functionality.notifications.util.NotificationsDataChangedNotifierImpl
import pl.iot.mlapp.functionality.notifications.util.NotificationsDataChangedProvider
import pl.iot.mlapp.functionality.service.AppLifecycleObserver
import pl.iot.mlapp.functionality.settings.SettingsViewModel
import pl.iot.mlapp.mqtt.MqttStatusHandler
import pl.iot.mlapp.mqtt.receivers.MqttCameraReceiver
import pl.iot.mlapp.mqtt.receivers.MqttMlReceiver

val appModule = module {
    single { AppLifecycleObserver(androidContext()) }

    single<IConfigRepository> { ConfigRepository(androidContext()) }

    single {
        Room.databaseBuilder(
            androidApplication(),
            NotificationsDatabase::class.java, "notifications-db"
        )
            .build()
    }

    single { NotificationsDataChangedNotifierImpl() } binds arrayOf(
        NotificationsDataChangedNotifier::class,
        NotificationsDataChangedProvider::class
    )

    viewModel {
        NotificationsViewModel(
            notificationsRepository = get(),
            notificationUiMapper = NotificationUiMapper(),
            dataChangedProvider = get()
        )
    }

    single {
        NotificationsRepository(
            notificationsDao = get<NotificationsDatabase>().notificationsDao(),
            notificationMapper = NotificationMapper(),
            notificationsDataChangedNotifier = get()
        )
    }

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
    single(createdAtStart = true) {
        MqttStatusHandler(
            cameraReceiver = get(),
            mlReceiver = get()
        )
    }

    viewModel {
        MainActivityViewModel(
            cameraReceiver = get(),
            mlReceiver = get(),
            notificationsRepository = get()
        )
    }

    viewModel {
        CameraFragmentViewModel(
            cameraReceiver = get()
        )
    }

    viewModel {
        SettingsViewModel(
            configRepository = get(),
            cameraReceiver = get(),
            mlReceiver = get()
        )
    }
}