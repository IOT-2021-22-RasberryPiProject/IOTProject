package pl.iot.mlapp.functionality

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.iot.mlapp.R
import pl.iot.mlapp.databinding.ActivityMainBinding
import pl.iot.mlapp.extensions.showSnackbar
import pl.iot.mlapp.functionality.camera.CameraFragment
import pl.iot.mlapp.functionality.notifications.NotificationsFragment
import pl.iot.mlapp.functionality.settings.SettingsFragment
import pl.iot.mlapp.mqtt.model.MqttErrorType
import pl.iot.mlapp.mqtt.model.MqttMlResponseModel
import pl.iot.mlapp.mqtt.receivers.MqttMlReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        //WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.mlMessage.observe(this, ::mlMessageObserver)
        viewModel.errorLiveData.observe(this, ::errorObserver)
    }

    private fun mlMessageObserver(mlMessage: MqttMlResponseModel) {
        val textColor = when(mlMessage.statusCode) {
            0 -> Color.RED
            1 -> Color.GREEN
            else -> null
        }

        binding.bottomNavigation.showSnackbar(
            message = mlMessage.message,
            textColor = textColor
        )
    }

    private fun errorObserver(errorModel: MqttErrorType) {
        when (errorModel) {
            is MqttErrorType.CameraError.OnConnect -> showErrorSnackbar(
                getString(R.string.error_onconnection_camera)
            )
            is MqttErrorType.CameraError.LostConnection -> showErrorSnackbar(
                getString(R.string.error_lostconnection_camera)
            )
            is MqttErrorType.MlError.OnConnect -> showErrorSnackbar(
                getString(R.string.error_onconnection_ml)
            )
            is MqttErrorType.MlError.LostConnection -> showErrorSnackbar(
                getString(R.string.error_lostconnection_error_ml)
            )
        }
    }


    private fun showErrorSnackbar(message: String) = binding.fragmentContainer.showSnackbar(
        message,
        duration = Snackbar.LENGTH_INDEFINITE
    )

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
                R.id.pageSettings -> {
                    supportFragmentManager.commit {
                        replace<SettingsFragment>(R.id.fragmentContainer)
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