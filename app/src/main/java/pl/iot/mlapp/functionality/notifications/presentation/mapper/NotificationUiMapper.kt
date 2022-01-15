package pl.iot.mlapp.functionality.notifications.presentation.mapper

import pl.iot.mlapp.R
import pl.iot.mlapp.functionality.notifications.domain.model.Notification
import pl.iot.mlapp.functionality.notifications.presentation.model.NotificationUi

class NotificationUiMapper {

    fun map(notifications: List<Notification>): List<NotificationUi> {
        return notifications.map { notification ->
            NotificationUi(
                message = notification.message,
                notificationColor = notification.statusCode.toNotificationColor()
            )
        }
    }

    private fun Int.toNotificationColor(): Int {
        return when (this) {
            STATUS_CODE_NORMAL -> R.color.dark_red
            STATUS_CODE_WARNING -> R.color.dark_green
            else -> R.color.dark_red
        }
    }

    private companion object {
        const val STATUS_CODE_NORMAL = 0
        const val STATUS_CODE_WARNING = 1
    }
}


