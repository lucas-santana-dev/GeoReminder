package br.com.plussapps.georeminder.domain.usecase

import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository

class GetReminderByIdUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(id: Long): Reminder? {
        return repository.getReminderById(id)
    }
}