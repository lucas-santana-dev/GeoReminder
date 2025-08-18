package br.com.plussapps.georeminder.domain.repository

import br.com.plussapps.georeminder.domain.model.Reminder


interface ReminderRepository {
    suspend fun createReminder(reminder: Reminder): Result<Long>
    suspend fun getReminders(): List<Reminder>
    suspend fun getReminderById(id: Long): Reminder?
    suspend fun updateReminder(reminder: Reminder): Result<Unit>
    suspend fun deleteReminder(id: Long): Result<Unit>
}