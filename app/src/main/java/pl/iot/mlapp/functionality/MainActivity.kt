package pl.iot.mlapp.functionality

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.scope.scope
import org.koin.core.context.GlobalContext.startKoin
import pl.iot.mlapp.R
import pl.iot.mlapp.databinding.ActivityMainBinding
import pl.iot.mlapp.di.appModule
import pl.iot.mlapp.functionality.camera.CameraFragment
import pl.iot.mlapp.functionality.notifications.NotificationsFragment
import pl.iot.mlapp.mqtt.MqttConfig
import pl.iot.mlapp.mqtt.MqttManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupKoin()
        setupViews()

        mqttTest()
    }

    private fun mqttTest() {
        val mqtt = MqttManager(applicationContext, mqttTestGetConfig())

        lifecycleScope.launch(Dispatchers.IO) {
            mqtt.mlMessageFlow.collect {
                Log.d("mlMessageFlow", it)
            }
        }
    }

    private fun mqttTestGetConfig() = MqttConfig(
        brokerIp = "192.168.2.229:1883",
        mlTopic = "ml",
        cameraTopic = "camera",
        clientId = "androidClient"
    )

    private fun setupKoin() {
        startKoin {
            //androidLogger()
            androidContext(this@MainActivity)
            modules(appModule)
        }
    }

    private fun setupViews() {
        setSupportActionBar(binding.toolbar)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.pageCamera -> {
                    supportFragmentManager.commit {
                        replace<CameraFragment>(R.id.fragmentContainer)
                    }
                    true
                }
                R.id.pageNotifications -> {
                    supportFragmentManager.commit {
                        replace<NotificationsFragment>(R.id.fragmentContainer)
                    }
                    true
                }
                else -> false
            }
        }
        supportFragmentManager.commit {
            replace<CameraFragment>(R.id.fragmentContainer)
        }
    }
}