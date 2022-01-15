package pl.iot.mlapp.functionality.notifications.domain.repository

import pl.iot.mlapp.functionality.notifications.domain.db.dao.NotificationsDao
import pl.iot.mlapp.functionality.notifications.domain.mapper.NotificationMapper
import pl.iot.mlapp.functionality.notifications.domain.model.Notification
import pl.iot.mlapp.functionality.notifications.util.NotificationsDataChangedNotifier
import pl.iot.mlapp.mqtt.model.MqttMlResponseModel

class NotificationsRepository(
    private val notificationsDao: NotificationsDao,
    private val notificationMapper: NotificationMapper,
    private val notificationsDataChangedNotifier: NotificationsDataChangedNotifier
) {

    suspend fun getNotifications(): List<Notification> = notificationsDao.getNotifications()

    suspend fun insertNotification(mqttMlResponseModel: MqttMlResponseModel) {
        val notification = notificationMapper.map(mqttMlResponseModel)
        notificationsDao.insertNotification(notification)
        notificationsDataChangedNotifier.notifyDataChanged()
    }
}