package com.habitmon.shared.data.repository

import androidx.room.withTransaction
import com.habitmon.shared.data.db.HabitMonDatabase
import com.habitmon.shared.data.db.entity.CompanionWithSpecies
import com.habitmon.shared.data.db.entity.PokedexStatus
import com.habitmon.shared.domain.HabitMonError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import com.habitmon.shared.data.db.entity.Companion as CompanionEntity

enum class SetCompanionResult { CHANGED, ALREADY_ACTIVE }

/** Reglas del compañero. Equivale a companions/services.py del prototipo Django. */
@OptIn(ExperimentalCoroutinesApi::class)
class CompanionRepository(
    private val db: HabitMonDatabase,
    private val profile: LocalProfile,
    private val pokedex: PokedexRepository,
) {
    private val companionDao = db.companionDao()
    private val speciesDao = db.pokemonSpeciesDao()
    private val entryDao = db.pokedexEntryDao()

    fun getActiveCompanion(): Flow<CompanionEntity?> =
        flow { emit(profile.id()) }.flatMapLatest { companionDao.getByProfile(it) }

    fun getActiveCompanionWithSpecies(): Flow<CompanionWithSpecies?> =
        flow { emit(profile.id()) }.flatMapLatest { companionDao.getWithSpeciesByProfile(it) }

    suspend fun chooseStarter(speciesId: Int) {
        val profileId = profile.id()
        db.withTransaction {
            if (companionDao.find(profileId) != null) throw HabitMonError.CompanionAlreadyExists()
            val species = speciesDao.find(speciesId)
            if (species == null || !species.selectable) throw HabitMonError.InvalidStarter(speciesId)
            companionDao.insert(CompanionEntity(profileId = profileId, speciesId = speciesId))
            pokedex.unlock(speciesId)
        }
    }

    suspend fun completeIntro(companionId: String) {
        companionDao.markIntroCompleted(companionId)
    }

    /** Cambia la especie del compañero activo, conservando nivel y bienestar. */
    suspend fun setActiveCompanion(speciesId: Int): SetCompanionResult {
        val profileId = profile.id()
        return db.withTransaction {
            speciesDao.find(speciesId) ?: throw HabitMonError.PokemonNotFound(speciesId)
            val companion = companionDao.find(profileId) ?: throw HabitMonError.NoCompanion()
            if (companion.speciesId == speciesId) return@withTransaction SetCompanionResult.ALREADY_ACTIVE
            if (entryDao.find(profileId, speciesId)?.status != PokedexStatus.UNLOCKED) {
                throw HabitMonError.PokemonNotUnlocked(speciesId)
            }
            companionDao.update(companion.copy(speciesId = speciesId, updatedAt = System.currentTimeMillis()))
            SetCompanionResult.CHANGED
        }
    }
}
