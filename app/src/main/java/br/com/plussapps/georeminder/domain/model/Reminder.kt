package br.com.plussapps.georeminder.domain.model

import java.time.LocalDateTime

data class Reminder(
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val location: Location? = null, // usa domain.model.Location
    val dateTime: LocalDateTime? = null, // usado em GetRemindersForDateUseCase
    val isActive: Boolean = true,        // usado em GetActiveRemindersUseCase
    val isCompleted: Boolean = false
)