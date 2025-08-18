package br.com.plussapps.georeminder.di

import androidx.room.Room
import br.com.plussapps.georeminder.data.ReminderDatabase
import br.com.plussapps.georeminder.data.ReminderRepositoryImpl
import br.com.plussapps.georeminder.domain.repository.ReminderRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {
    // Database singleton
    single {
        Room.databaseBuilder(
            androidApplication(),
            ReminderDatabase::class.java,
            "reminder_db"
        )
            //.addMigrations(ReminderDatabase.MIGRATION_1_2)
            .fallbackToDestructiveMigration(false)
            .build()
    }

    // DAO singleton
    single { get<ReminderDatabase>().reminderDao() }

    // Repository singleton
    single<ReminderRepository> { ReminderRepositoryImpl(get()) }

    // Aqui você pode adicionar seus UseCases depois
}