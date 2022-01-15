package pl.iot.mlapp.functionality.notifications.presentation.model

import androidx.annotation.ColorInt
import androidx.annotation.ColorRes

data class NotificationUi(
    val message: String,
    @ColorRes val notificationColor: Int
)


