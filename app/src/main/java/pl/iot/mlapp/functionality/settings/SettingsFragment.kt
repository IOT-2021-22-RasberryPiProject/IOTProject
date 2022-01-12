package pl.iot.mlapp.functionality.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.iot.mlapp.R
import pl.iot.mlapp.databinding.FragmentSettingsBinding
import pl.iot.mlapp.functionality.MainActivity
import pl.iot.mlapp.functionality.config.MqttConfig

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnClickListeners()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupOnClickListeners() {
        binding.saveButton.setOnClickListener { onSaveButtonClick() }
    }

    private fun onSaveButtonClick() = with(binding) {
        viewModel.saveConfig(
            MqttConfig(
                brokerIp = brokerIpEditText.text.toString(),
                cameraTopic = cameraTopicEditText.text.toString(),
                mlTopic = mlTopicEditText.text.toString()
            )
        )

        (activity as? MainActivity)?.showSnackbar(getString(R.string.setting_save_success))
    }

    private fun setupObservers() {
        viewModel.config.observe(viewLifecycleOwner, ::configObserver)
    }

    private fun configObserver(config: MqttConfig) = with(binding) {
        brokerIpEditText.setText(config.brokerIp)
        cameraTopicEditText.setText(config.cameraTopic)
        mlTopicEditText.setText(config.mlTopic)
    }
}