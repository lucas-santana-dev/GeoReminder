package br.com.plussapps.georeminder.domain.usecase

import br.com.plussapps.georeminder.domain.repository.ReminderRepository

class DeleteReminderUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return repository.deleteReminder(id)
    }
}