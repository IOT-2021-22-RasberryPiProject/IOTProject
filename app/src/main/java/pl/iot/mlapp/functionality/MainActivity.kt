package pl.iot.mlapp.functionality

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.GlobalContext.startKoin
import pl.iot.mlapp.R
import pl.iot.mlapp.databinding.ActivityMainBinding
import pl.iot.mlapp.di.appModule
import pl.iot.mlapp.functionality.camera.CameraFragment
import pl.iot.mlapp.functionality.notifications.NotificationsFragment
import pl.iot.mlapp.mqtt.MqttCameraReceiver
import pl.iot.mlapp.mqtt.MqttMlReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupKoin()
        setupViews()

        observeForErrors()
    }

    private fun observeForErrors() {
        viewModel.errorLiveData.observe(this) { error ->
            when (error) {
                is MqttCameraReceiver.Companion.CameraError.OnConnect -> showErrorSnackbar(
                    getString(R.string.error_onconnection_camera)
                )
                is MqttCameraReceiver.Companion.CameraError.LostConnection -> showErrorSnackbar(
                    getString(R.string.error_lostconnection_camera)
                )
                is MqttMlReceiver.Companion.MlError.OnConnect -> showErrorSnackbar(
                    getString(R.string.error_onconnection_ml)
                )
                is MqttMlReceiver.Companion.MlError.LostConnection -> showErrorSnackbar(
                    getString(R.string.error_lostconnection_error_ml)
                )
            }
        }
    }

    private fun showErrorSnackbar(message: String) {
        val snackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        snackbar.setAction(getString(R.string.ok)) { snackbar.dismiss() }
        snackbar.show()
    }

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