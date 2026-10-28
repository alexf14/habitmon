package com.habitmon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "pokedex_entries",
    foreignKeys = [
        ForeignKey(
            entity = PokemonSpecies::class,
            parentColumns = ["pokedex_number"],
            childColumns = ["species_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["profile_id", "species_id"], unique = true),
        Index("species_id"),
    ],
)
data class PokedexEntry(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "species_id")
    val speciesId: Int,

    val status: PokedexStatus = PokedexStatus.UNKNOWN,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)
