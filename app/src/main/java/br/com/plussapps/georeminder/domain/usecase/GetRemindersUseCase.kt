package br.com.plussapps.georeminder.domain.usecase

import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository

class GetRemindersUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(): List<Reminder> {
        return repository.getReminders()
    }
}