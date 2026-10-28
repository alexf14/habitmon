package com.habitmon.shared.data.repository

import com.habitmon.shared.data.db.HabitMonDatabase
import com.habitmon.shared.data.db.entity.PokedexEntry
import com.habitmon.shared.data.db.entity.PokedexEntryWithSpecies
import com.habitmon.shared.data.db.entity.PokedexStatus
import com.habitmon.shared.domain.HabitMonError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

/** Pokédex del perfil. Equivale a pokedex/services.py del prototipo Django. */
@OptIn(ExperimentalCoroutinesApi::class)
class PokedexRepository(
    db: HabitMonDatabase,
    private val profile: LocalProfile,
) {
    private val speciesDao = db.pokemonSpeciesDao()
    private val entryDao = db.pokedexEntryDao()

    /** Todo el catálogo; las especies sin fila en la Pokédex aparecen como UNKNOWN. */
    fun getAllEntries(): Flow<List<PokedexEntryWithSpecies>> =
        flow { emit(profile.id()) }.flatMapLatest { profileId ->
            combine(speciesDao.getAll(), entryDao.getByProfile(profileId)) { species, entries ->
                val bySpecies = entries.associateBy { it.speciesId }
                species.map { s ->
                    PokedexEntryWithSpecies(s, bySpecies[s.pokedexNumber] ?: unknown(profileId, s.pokedexNumber))
                }
            }
        }

    fun getEntryForSpecies(speciesId: Int): Flow<PokedexEntryWithSpecies?> =
        flow { emit(profile.id()) }.flatMapLatest { profileId ->
            combine(speciesDao.getByNumber(speciesId), entryDao.getByProfileAndSpecies(profileId, speciesId)) { s, e ->
                s?.let { PokedexEntryWithSpecies(it, e ?: unknown(profileId, speciesId)) }
            }
        }

    fun getUnlockedCount(): Flow<Int> =
        flow { emit(profile.id()) }.flatMapLatest { entryDao.countUnlocked(it) }

    /** Desbloqueo normal del juego: progresivo e idempotente. */
    suspend fun unlock(speciesId: Int) {
        val profileId = profile.id()
        val existing = entryDao.find(profileId, speciesId)
        if (existing?.status == PokedexStatus.UNLOCKED) return
        val now = System.currentTimeMillis()
        entryDao.upsert(
            existing?.copy(status = PokedexStatus.UNLOCKED, updatedAt = now)
                ?: PokedexEntry(profileId = profileId, speciesId = speciesId, status = PokedexStatus.UNLOCKED, updatedAt = now)
        )
    }

    /** Solo depuración: fija cualquier estado, incluidas regresiones. UNKNOWN borra la fila. */
    suspend fun updateStatus(speciesId: Int, status: PokedexStatus) {
        val profileId = profile.id()
        speciesDao.find(speciesId) ?: throw HabitMonError.PokemonNotFound(speciesId)
        if (status == PokedexStatus.UNKNOWN) {
            entryDao.delete(profileId, speciesId)
            return
        }
        val now = System.currentTimeMillis()
        val existing = entryDao.find(profileId, speciesId)
        entryDao.upsert(
            existing?.copy(status = status, updatedAt = now)
                ?: PokedexEntry(profileId = profileId, speciesId = speciesId, status = status, updatedAt = now)
        )
    }

    private fun unknown(profileId: String, speciesId: Int) =
        PokedexEntry(id = "", profileId = profileId, speciesId = speciesId, status = PokedexStatus.UNKNOWN, updatedAt = 0)
}
