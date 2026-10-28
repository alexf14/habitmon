package com.habitmon.shared.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.habitmon.shared.data.db.entity.PokemonSpecies
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonSpeciesDao {
    @Query("SELECT * FROM pokemon_species ORDER BY pokedex_number")
    fun getAll(): Flow<List<PokemonSpecies>>

    @Query("SELECT * FROM pokemon_species WHERE pokedex_number = :pokedexNumber")
    fun getByNumber(pokedexNumber: Int): Flow<PokemonSpecies?>

    @Query("SELECT * FROM pokemon_species WHERE pokedex_number = :pokedexNumber")
    suspend fun find(pokedexNumber: Int): PokemonSpecies?

    @Query("SELECT * FROM pokemon_species WHERE selectable = 1 ORDER BY pokedex_number")
    fun getSelectableStarters(): Flow<List<PokemonSpecies>>

    @Query("SELECT COUNT(*) FROM pokemon_species")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(species: List<PokemonSpecies>)
}
