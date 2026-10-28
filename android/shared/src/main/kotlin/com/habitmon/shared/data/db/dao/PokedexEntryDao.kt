package com.habitmon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitmon.shared.data.db.entity.PokedexEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface PokedexEntryDao {
    @Query("SELECT * FROM pokedex_entries WHERE profile_id = :profileId ORDER BY species_id")
    fun getByProfile(profileId: String): Flow<List<PokedexEntry>>

    @Query("SELECT * FROM pokedex_entries WHERE profile_id = :profileId AND species_id = :speciesId")
    fun getByProfileAndSpecies(profileId: String, speciesId: Int): Flow<PokedexEntry?>

    @Query("SELECT * FROM pokedex_entries WHERE profile_id = :profileId AND species_id = :speciesId")
    suspend fun find(profileId: String, speciesId: Int): PokedexEntry?

    @Query("SELECT COUNT(*) FROM pokedex_entries WHERE profile_id = :profileId AND status = 'UNLOCKED'")
    fun countUnlocked(profileId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entry: PokedexEntry)

    @Query("DELETE FROM pokedex_entries WHERE profile_id = :profileId AND species_id = :speciesId")
    suspend fun delete(profileId: String, speciesId: Int)

    @Query("DELETE FROM pokedex_entries WHERE profile_id = :profileId")
    suspend fun deleteByProfile(profileId: String)
}
