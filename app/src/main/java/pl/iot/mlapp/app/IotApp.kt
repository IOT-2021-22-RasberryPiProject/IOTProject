package pl.iot.mlapp.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import pl.iot.mlapp.di.appModule
import pl.iot.mlapp.functionality.service.AppLifecycleObserver

class IotApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(applicationContext)
            modules(appModule)
        }

        val appLifecycleObserver: AppLifecycleObserver by inject()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
    }
}