package pl.iot.mlapp.functionality.notifications.domain.db

import androidx.room.Database
import androidx.room.RoomDatabase
import pl.iot.mlapp.functionality.notifications.domain.db.dao.NotificationsDao
import pl.iot.mlapp.functionality.notifications.domain.model.Notification

@Database(entities = [Notification::class], version = 1)
abstract class NotificationsDatabase : RoomDatabase() {
    abstract fun notificationsDao(): NotificationsDao
}