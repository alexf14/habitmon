package com.habitmon.shared.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class SyncMetadata(
    @ColumnInfo(name = "sync_version")
    val version: Int = 1,

    @ColumnInfo(name = "origin_device_id")
    val originDeviceId: String = "",

    @ColumnInfo(name = "sync_status")
    val syncStatus: SyncStatus = SyncStatus.LOCAL,
)
