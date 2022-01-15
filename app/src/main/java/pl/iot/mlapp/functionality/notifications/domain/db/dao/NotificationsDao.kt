package pl.iot.mlapp.functionality.notifications.domain.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import pl.iot.mlapp.functionality.notifications.domain.model.Notification

@Dao
interface NotificationsDao {
    @Query("SELECT * FROM notifications")
    suspend fun getNotifications(): List<Notification>

    @Insert
    suspend fun insertNotification(vararg notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)
}