package br.com.plussapps.georeminder.data

import android.os.Build
import androidx.annotation.RequiresApi
import br.com.plussapps.georeminder.data.mapper.toDomain
import br.com.plussapps.georeminder.data.mapper.toEntity
import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository

class ReminderRepositoryImpl(
    private val reminderDao: ReminderDao
) : ReminderRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createReminder(reminder: Reminder): Result<Long> = runCatching {
        reminderDao.insert(reminder.toEntity())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReminders(): List<Reminder> {
        return reminderDao.getAllOnce().map { it.toDomain() }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getReminderById(id: Long): Reminder? {
        return reminderDao.getById(id)?.toDomain()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun updateReminder(reminder: Reminder): Result<Unit> = runCatching {
        reminderDao.update(reminder.toEntity())
        Unit
    }

    override suspend fun deleteReminder(id: Long): Result<Unit> = runCatching {
        val entity = reminderDao.getById(id) ?: error("Reminder not found for id=$id")
        reminderDao.delete(entity)
        Unit
    }
}