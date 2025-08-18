package br.com.plussapps.georeminder.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ReminderEntity::class],
    version = 1,
    exportSchema = false)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Adiciona coluna isActive com default = 1 (true)
                database.execSQL(
                    "ALTER TABLE reminders ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1"
                )
            }
        }
    }
}