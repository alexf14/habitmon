package com.habitmon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitmon.shared.data.db.entity.Profile

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY created_at LIMIT 1")
    suspend fun findFirst(): Profile?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(profile: Profile)
}
