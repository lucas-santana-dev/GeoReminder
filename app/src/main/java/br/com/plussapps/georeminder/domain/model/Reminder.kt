package br.com.plussapps.georeminder.domain.model

import java.time.LocalDateTime

data class Reminder (
    val id: Long = 0L,
    val title: String,
    val description: String? = null,
    val dateTime: LocalDateTime, // ou Long se preferir persistência
    val location: Location,
    val isActive: Boolean = true,
    val isCompleted: Boolean = false,
    val isRecurring: Boolean = false,
    val recurrenceRule: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)