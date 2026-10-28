package com.habitmon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "companions",
    foreignKeys = [
        ForeignKey(
            entity = PokemonSpecies::class,
            parentColumns = ["pokedex_number"],
            childColumns = ["species_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("species_id"), Index("profile_id")],
)
data class Companion(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "profile_id")
    val profileId: String,

    @ColumnInfo(name = "species_id")
    val speciesId: Int,

    val nickname: String? = null,

    val level: Int = 1,
    val experience: Int = 0,
    val happiness: Int = 70,
    val energy: Int = 100,
    val bond: Int = 0,

    @ColumnInfo(name = "intro_completed")
    val introCompleted: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
) {
    val displayName: String
        get() = nickname ?: ""
}
