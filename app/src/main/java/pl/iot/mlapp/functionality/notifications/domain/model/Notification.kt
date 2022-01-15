package pl.iot.mlapp.functionality.notifications.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val statusCode: Int,
    val message: String
)
