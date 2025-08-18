package br.com.plussapps.georeminder.domain.usecase

import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository


class UpdateReminderUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder): Result<Unit> {
        return repository.updateReminder(reminder)
    }
}