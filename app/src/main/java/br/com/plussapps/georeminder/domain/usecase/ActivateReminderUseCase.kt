package br.com.plussapps.georeminder.domain.usecase

import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository


class ActivateReminderUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder, active: Boolean): Result<Unit> {
        val updatedReminder = reminder.copy(isActive = active)
        return repository.updateReminder(updatedReminder)
    }
}