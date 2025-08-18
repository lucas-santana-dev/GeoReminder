package br.com.plussapps.georeminder.domain.usecase


import br.com.plussapps.georeminder.domain.model.Reminder
import br.com.plussapps.georeminder.domain.repository.ReminderRepository

class CreateReminderUseCase(private val repository: ReminderRepository) {
    suspend operator fun invoke(reminder: Reminder): Result<Long> {
        // Aqui você pode adicionar validações antes de salvar, ex: título obrigatório, localização válida etc.
        if (reminder.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Título do lembrete não pode ser vazio"))
        }
        // Salva e retorna o id gerado (ou erro)
        return repository.createReminder(reminder)
    }
}