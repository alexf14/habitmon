package com.habitmon.shared.data.db.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CompanionWithSpecies(
    @Embedded val companion: Companion,
    @Relation(parentColumn = "species_id", entityColumn = "pokedex_number")
    val species: PokemonSpecies,
) {
    val displayName: String
        get() = companion.nickname ?: species.name
}
