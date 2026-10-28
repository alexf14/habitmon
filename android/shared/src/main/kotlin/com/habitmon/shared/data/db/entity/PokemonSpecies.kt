package com.habitmon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_species")
data class PokemonSpecies(
    @PrimaryKey
    @ColumnInfo(name = "pokedex_number")
    val pokedexNumber: Int,

    val name: String,

    @ColumnInfo(name = "primary_type")
    val primaryType: String,

    @ColumnInfo(name = "secondary_type")
    val secondaryType: String? = null,

    val genus: String = "",

    @ColumnInfo(name = "height_dm")
    val heightDm: Int = 0,

    @ColumnInfo(name = "weight_hg")
    val weightHg: Int = 0,

    val description: String = "",

    @ColumnInfo(name = "sprite_path")
    val spritePath: String,

    @ColumnInfo(name = "thumbnail_path")
    val thumbnailPath: String = "",

    @ColumnInfo(name = "silhouette_path")
    val silhouettePath: String? = null,

    val selectable: Boolean = false,
) {
    val numberDisplay: String
        get() = "%03d".format(pokedexNumber)

    val typesDisplay: String
        get() = listOfNotNull(primaryType, secondaryType).joinToString(" / ")

    val heightM: Double
        get() = heightDm / 10.0

    val weightKg: Double
        get() = weightHg / 10.0
}
