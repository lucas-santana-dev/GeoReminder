package br.com.plussapps.georeminder.data.mapper

import android.os.Build
import androidx.annotation.RequiresApi
import br.com.plussapps.georeminder.data.ReminderEntity
import br.com.plussapps.georeminder.domain.model.Location
import br.com.plussapps.georeminder.domain.model.Reminder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
fun ReminderEntity.toDomain(): Reminder {
    val domainLocation = if (latitude != null && longitude != null) {
        Location(
            latitude = latitude,
            longitude = longitude,
            name = null,
            radius = radius ?: 100f
        )
    } else {
        null
    }

    val dateTime = triggerTime?.let {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
    }

    return Reminder(
        id = id,
        title = title,
        description = description,
        location = domainLocation,
        dateTime = dateTime,
        isActive = isActive,
        isCompleted = isCompleted
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Reminder.toEntity(): ReminderEntity {
    val epochMillis = dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    return ReminderEntity(
        id = id,
        title = title,
        description = description,
        latitude = location?.latitude,
        longitude = location?.longitude,
        radius = location?.radius,
        triggerTime = epochMillis,
        isActive = isActive,
        isCompleted = isCompleted
    )
}