package br.com.plussapps.georeminder.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radius: Float?, // raio da geofence
    val triggerTime: Long?, // timestamp para lembrete por tempo
    val isCompleted: Boolean = false
)