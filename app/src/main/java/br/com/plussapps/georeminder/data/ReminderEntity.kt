package br.com.plussapps.georeminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radius: Float?,
    val triggerTime: Long?,   // epoch millis
    val isActive: Boolean = true,
    val isCompleted: Boolean = false
)