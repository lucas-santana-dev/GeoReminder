package br.com.plussapps.georeminder.domain.usecase

import android.os.Build
import androidx.annotation.RequiresApi
import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository
import java.time.LocalDateTime

class GetRemindersForDateUseCase(private val repository: ReminderRepository) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend operator fun invoke(date: LocalDateTime): List<Reminder> {
        val targetDate = date.toLocalDate()
        return repository.getReminders().filter { it.dateTime?.toLocalDate() == targetDate }
    }
}