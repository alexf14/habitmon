package com.habitmon.shared.data.db.entity

/** Una especie del catálogo con su estado en la Pokédex del perfil (UNKNOWN si no hay fila). */
data class PokedexEntryWithSpecies(
    val species: PokemonSpecies,
    val entry: PokedexEntry,
)
