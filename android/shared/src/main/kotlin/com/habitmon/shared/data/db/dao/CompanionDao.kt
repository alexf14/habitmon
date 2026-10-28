package com.habitmon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.habitmon.shared.data.db.entity.Companion
import com.habitmon.shared.data.db.entity.CompanionWithSpecies
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanionDao {
    @Query("SELECT * FROM companions WHERE profile_id = :profileId LIMIT 1")
    fun getByProfile(profileId: String): Flow<Companion?>

    @Transaction
    @Query("SELECT * FROM companions WHERE profile_id = :profileId LIMIT 1")
    fun getWithSpeciesByProfile(profileId: String): Flow<CompanionWithSpecies?>

    @Query("SELECT * FROM companions WHERE profile_id = :profileId LIMIT 1")
    suspend fun find(profileId: String): Companion?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(companion: Companion)

    @Update
    suspend fun update(companion: Companion)

    @Query("UPDATE companions SET intro_completed = 1, updated_at = :now WHERE id = :companionId")
    suspend fun markIntroCompleted(companionId: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM companions WHERE profile_id = :profileId")
    suspend fun deleteByProfile(profileId: String)
}
