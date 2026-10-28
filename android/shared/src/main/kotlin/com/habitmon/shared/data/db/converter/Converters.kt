package com.habitmon.shared.data.db.converter

import androidx.room.TypeConverter
import com.habitmon.shared.data.db.entity.PokedexStatus
import com.habitmon.shared.data.db.entity.SyncStatus

class Converters {
    @TypeConverter
    fun fromPokedexStatus(status: PokedexStatus): String = status.name

    @TypeConverter
    fun toPokedexStatus(value: String): PokedexStatus = PokedexStatus.valueOf(value)

    @TypeConverter
    fun fromSyncStatus(status: SyncStatus): String = status.name

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus = SyncStatus.valueOf(value)
}
