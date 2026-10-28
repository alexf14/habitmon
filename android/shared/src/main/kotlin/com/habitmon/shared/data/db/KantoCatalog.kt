package com.habitmon.shared.data.db

import android.content.Context
import com.habitmon.shared.data.db.entity.PokemonSpecies
import org.json.JSONArray

/** Catálogo de Kanto. El JSON es una copia de backend/companions/data/kanto_species.json. */
object KantoCatalog {
    private const val ASSET = "kanto_species.json"
    private val STARTERS = setOf(1, 4, 7)

    fun load(context: Context): List<PokemonSpecies> {
        val json = context.assets.open(ASSET).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val rows = JSONArray(json)
        return (0 until rows.length()).map { i ->
            val row = rows.getJSONObject(i)
            val number = row.getInt("number")
            val n = "%03d".format(number)
            PokemonSpecies(
                pokedexNumber = number,
                name = row.getString("name"),
                primaryType = row.getString("primary_type"),
                secondaryType = row.optString("secondary_type").takeUnless { row.isNull("secondary_type") },
                genus = row.optString("genus"),
                heightDm = row.optInt("height_dm"),
                weightHg = row.optInt("weight_hg"),
                description = row.optString("description"),
                spritePath = "sprites/pokemon/animated/$n.gif",
                thumbnailPath = "sprites/pokemon/thumbs/$n.png",
                silhouettePath = "sprites/pokemon/silhouettes/$n.png",
                selectable = number in STARTERS,
            )
        }
    }
}
