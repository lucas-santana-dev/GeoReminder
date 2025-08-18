package br.com.plussapps.georeminder.domain.usecase

import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository

class CompleteReminderUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder): Result<Unit> {
        val completedReminder = reminder.copy(isCompleted = true)
        return repository.updateReminder(completedReminder)
    }
}