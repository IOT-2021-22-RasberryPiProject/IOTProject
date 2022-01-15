package pl.iot.mlapp.functionality.notifications.domain.mapper

import pl.iot.mlapp.functionality.notifications.domain.model.Notification
import pl.iot.mlapp.mqtt.model.MqttMlResponseModel

class NotificationMapper {
    fun map(mqqtMlResponseModel: MqttMlResponseModel): Notification {
        return Notification(
            statusCode = mqqtMlResponseModel.statusCode,
            message = mqqtMlResponseModel.message
        )
    }
}