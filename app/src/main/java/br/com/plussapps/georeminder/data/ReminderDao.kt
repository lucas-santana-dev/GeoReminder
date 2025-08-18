package br.com.plussapps.georeminder.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    @Update
    suspend fun update(reminder: Reminder)

    @Delete
    suspend fun delete(reminder: Reminder)

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getById(id: Long): Reminder?

    @Query("SELECT * FROM reminders")
    fun getAll(): Flow<List<Reminder>>

    @Query("SELECT * FROM reminders WHERE isCompleted = 0")
    fun getActive(): Flow<List<Reminder>>
}