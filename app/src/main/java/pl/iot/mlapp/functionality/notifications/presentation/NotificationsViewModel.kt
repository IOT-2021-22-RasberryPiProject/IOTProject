package pl.iot.mlapp.functionality.notifications.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import pl.iot.mlapp.functionality.notifications.domain.model.Notification
import pl.iot.mlapp.functionality.notifications.domain.repository.NotificationsRepository
import pl.iot.mlapp.functionality.notifications.presentation.extension.map
import pl.iot.mlapp.functionality.notifications.presentation.mapper.NotificationUiMapper
import pl.iot.mlapp.functionality.notifications.presentation.model.NotificationUi
import pl.iot.mlapp.functionality.notifications.util.NotificationsDataChangedProvider

class NotificationsViewModel(
    private val notificationsRepository: NotificationsRepository,
    private val notificationUiMapper: NotificationUiMapper,
    private val dataChangedProvider: NotificationsDataChangedProvider
) : ViewModel() {

    private var notifierScope: CoroutineScope? = null

    private val _notifications = MutableLiveData<List<Notification>>()

    override fun onCleared() {
        notifierScope?.cancel()
        super.onCleared()
    }

    val notifications: LiveData<List<NotificationUi>> = _notifications.map { notifications ->
        notificationUiMapper.map(notifications)
    }

    fun fetchData() = viewModelScope.launch {
        _notifications.value = notificationsRepository.getNotifications()
    }

    fun startCollectingNotifierData() {
        notifierScope = CoroutineScope(
            context = viewModelScope.coroutineContext + Job(parent = viewModelScope.coroutineContext[Job])
        )
        notifierScope?.let {
            dataChangedProvider.notificationsDataChanged
                .onEach { updateData() }
                .launchIn(it)
        }
    }

    fun stopCollectingNotifierData() = notifierScope?.cancel()

    private fun updateData() = viewModelScope.launch {
        _notifications.value = notificationsRepository.getNotifications()
    }
}