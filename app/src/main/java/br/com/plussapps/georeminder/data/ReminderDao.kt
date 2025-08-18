package br.com.plussapps.georeminder.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: ReminderEntity): Long

    @Update
    suspend fun update(reminder: ReminderEntity)

    @Delete
    suspend fun delete(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): ReminderEntity?

    // Snapshot (combina com a interface do domínio)
    @Query("SELECT * FROM reminders")
    suspend fun getAllOnce(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE isActive = 1")
    suspend fun getActiveOnce(): List<ReminderEntity>

    // Observáveis (úteis para telas reativas, se precisar)
    @Query("SELECT * FROM reminders")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE isActive = 1")
    fun observeActive(): Flow<List<ReminderEntity>>
}