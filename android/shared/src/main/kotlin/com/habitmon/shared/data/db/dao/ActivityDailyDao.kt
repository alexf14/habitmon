package com.habitmon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitmon.shared.data.db.entity.ActivityDaily
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDailyDao {
    @Query("SELECT * FROM activity_daily WHERE profile_id = :profileId AND date = :date")
    fun getByProfileAndDate(profileId: String, date: String): Flow<ActivityDaily?>

    @Query("SELECT * FROM activity_daily WHERE profile_id = :profileId AND date = :date")
    suspend fun find(profileId: String, date: String): ActivityDaily?

    @Query("SELECT * FROM activity_daily WHERE profile_id = :profileId ORDER BY date DESC")
    fun getByProfile(profileId: String): Flow<List<ActivityDaily>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(activity: ActivityDaily)

    @Query("DELETE FROM activity_daily WHERE profile_id = :profileId")
    suspend fun deleteByProfile(profileId: String)
}
