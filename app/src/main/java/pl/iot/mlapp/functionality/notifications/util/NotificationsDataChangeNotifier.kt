package pl.iot.mlapp.functionality.notifications.util

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

object DataChanged

interface NotificationsDataChangedNotifier {
    suspend fun notifyDataChanged()
}

interface NotificationsDataChangedProvider {
    val notificationsDataChanged: Flow<DataChanged>
}

class NotificationsDataChangedNotifierImpl : NotificationsDataChangedNotifier, NotificationsDataChangedProvider {

    private val _notificationsDataChanged = Channel<DataChanged>(capacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val notificationsDataChanged: Flow<DataChanged> = _notificationsDataChanged.receiveAsFlow()

    override suspend fun notifyDataChanged() = _notificationsDataChanged.send(DataChanged)
}
