package pl.iot.mlapp.functionality.notifications.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.iot.mlapp.databinding.FragmentNotificationsBinding
import pl.iot.mlapp.functionality.notifications.presentation.adapter.NotificationsAdapter
import pl.iot.mlapp.functionality.notifications.presentation.extension.observe
import pl.iot.mlapp.functionality.notifications.presentation.model.NotificationUi

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val binding get() = _binding!!
    private val viewModel: NotificationsViewModel by viewModel()
    private val adapter = NotificationsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        bindObservers()
        viewModel.fetchData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.startCollectingNotifierData()
    }

    override fun onPause() {
        viewModel.stopCollectingNotifierData()
        super.onPause()
    }

    private fun setupView() = with(binding) {
        notificationsList.adapter = adapter
    }

    private fun bindObservers() {
        observe(viewModel.notifications, ::handleNotificationsData)
    }

    private fun handleNotificationsData(notifications: List<NotificationUi>) {
        adapter.submitList(notifications)
    }
}