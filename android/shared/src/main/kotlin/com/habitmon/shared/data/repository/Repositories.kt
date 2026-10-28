package com.habitmon.shared.data.repository

import androidx.room.withTransaction
import com.habitmon.shared.data.db.HabitMonDatabase

/** Punto único de acceso a los repositorios, compartido por las apps Wear OS y Android. */
class Repositories(private val db: HabitMonDatabase) {
    private val profile = LocalProfile(db.profileDao())

    val species = SpeciesRepository(db.pokemonSpeciesDao())
    val pokedex = PokedexRepository(db, profile)
    val companion = CompanionRepository(db, profile, pokedex)
    val activity = ActivityRepository(db, profile)

    /** Solo depuración: borra compañero, Pokédex y actividad. Conserva el perfil y el catálogo. */
    suspend fun resetGame() {
        val profileId = profile.id()
        db.withTransaction {
            db.companionDao().deleteByProfile(profileId)
            db.pokedexEntryDao().deleteByProfile(profileId)
            db.activityDailyDao().deleteByProfile(profileId)
        }
    }
}
