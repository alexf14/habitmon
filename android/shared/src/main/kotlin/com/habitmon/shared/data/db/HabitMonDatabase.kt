package com.habitmon.shared.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.habitmon.shared.data.db.converter.Converters
import com.habitmon.shared.data.db.dao.ActivityDailyDao
import com.habitmon.shared.data.db.dao.CompanionDao
import com.habitmon.shared.data.db.dao.PokedexEntryDao
import com.habitmon.shared.data.db.dao.PokemonSpeciesDao
import com.habitmon.shared.data.db.dao.ProfileDao
import com.habitmon.shared.data.db.entity.ActivityDaily
import com.habitmon.shared.data.db.entity.PokedexEntry
import com.habitmon.shared.data.db.entity.PokemonSpecies
import com.habitmon.shared.data.db.entity.Profile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.habitmon.shared.data.db.entity.Companion as CompanionEntity

@Database(
    entities = [
        Profile::class,
        PokemonSpecies::class,
        CompanionEntity::class,
        PokedexEntry::class,
        ActivityDaily::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class HabitMonDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun pokemonSpeciesDao(): PokemonSpeciesDao
    abstract fun companionDao(): CompanionDao
    abstract fun pokedexEntryDao(): PokedexEntryDao
    abstract fun activityDailyDao(): ActivityDailyDao

    companion object {
        private const val DATABASE_NAME = "habitmon.db"

        @Volatile
        private var instance: HabitMonDatabase? = null

        fun getInstance(context: Context): HabitMonDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context.applicationContext).also { instance = it }
            }

        private fun buildDatabase(appContext: Context): HabitMonDatabase =
            Room.databaseBuilder(appContext, HabitMonDatabase::class.java, DATABASE_NAME)
                .addCallback(CatalogSeeder(appContext))
                .build()
    }

    /** Carga el catálogo de especies si la tabla está vacía (primera ejecución). */
    private class CatalogSeeder(private val appContext: Context) : Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                val dao = getInstance(appContext).pokemonSpeciesDao()
                if (dao.getCount() == 0) {
                    dao.insertAll(KantoCatalog.load(appContext))
                }
            }
        }
    }
}
