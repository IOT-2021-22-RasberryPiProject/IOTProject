package pl.iot.mlapp.functionality

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.iot.mlapp.R
import pl.iot.mlapp.databinding.ActivityMainBinding
import pl.iot.mlapp.functionality.camera.CameraFragment
import pl.iot.mlapp.functionality.notifications.NotificationsFragment
import pl.iot.mlapp.functionality.settings.SettingsFragment
import pl.iot.mlapp.mqtt.model.MqttErrorType
import pl.iot.mlapp.mqtt.model.MqttMlResponseModel

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

    fun showSnackbar(
        message: String,
        duration: Int = Snackbar.LENGTH_SHORT,
        textColor: Int? = null,
        backgroundColor: Int? = null
    ) = with(binding) {
        val snackbar = Snackbar.make(bottomNavigation, message, duration)
        snackbar.anchorView = bottomNavigation
        snackbar.setAction(getString(R.string.ok)) { snackbar.dismiss() }
        backgroundColor?.let { snackbar.setBackgroundTint(it) }
        textColor?.let {
            snackbar.setTextColor(it)
            snackbar.setActionTextColor(it)
        }
        snackbar.show()
    }

    private fun showErrorSnackbar(message: String) = showSnackbar(
        message,
        duration = Snackbar.LENGTH_INDEFINITE
    )

    private fun setupObservers() {
        viewModel.mlMessage.observe(this, ::mlMessageObserver)
        viewModel.errorLiveData.observe(this, ::errorObserver)
    }

    private fun mlMessageObserver(mlMessage: MqttMlResponseModel) {
        val backgroundColor = when(mlMessage.statusCode) {
            0 -> Color.RED
            1 -> getColor(R.color.dark_green)
            else -> null
        }

        showSnackbar(
            message = mlMessage.message,
            backgroundColor = backgroundColor,
            textColor = Color.WHITE
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