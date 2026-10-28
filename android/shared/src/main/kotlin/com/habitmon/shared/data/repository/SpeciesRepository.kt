package com.habitmon.shared.data.repository

import com.habitmon.shared.data.db.dao.PokemonSpeciesDao
import com.habitmon.shared.data.db.entity.PokemonSpecies
import kotlinx.coroutines.flow.Flow

class SpeciesRepository(private val speciesDao: PokemonSpeciesDao) {
    fun getSelectableStarters(): Flow<List<PokemonSpecies>> = speciesDao.getSelectableStarters()
}
