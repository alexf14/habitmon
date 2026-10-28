package com.habitmon.shared.data.repository

import androidx.room.withTransaction
import com.habitmon.shared.data.db.HabitMonDatabase
import com.habitmon.shared.data.db.entity.ActivityDaily
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

/** Actividad diaria. Equivale a activity/services.py del prototipo Django. */
@OptIn(ExperimentalCoroutinesApi::class)
class ActivityRepository(
    private val db: HabitMonDatabase,
    private val profile: LocalProfile,
) {
    private val dao = db.activityDailyDao()

    fun getTodayActivity(): Flow<ActivityDaily?> =
        flow { emit(profile.id()) }.flatMapLatest { dao.getByProfileAndDate(it, today()) }

    suspend fun incrementSteps(count: Int) {
        require(count >= 0) { "count must be non-negative" }
        modifyToday { it.copy(steps = it.steps + count) }
    }

    suspend fun updateSteps(steps: Int) {
        require(steps >= 0) { "steps must be non-negative" }
        modifyToday { it.copy(steps = steps) }
    }

    suspend fun updateKilometers(kilometers: Double) {
        require(kilometers >= 0) { "kilometers must be non-negative" }
        modifyToday { it.copy(kilometers = kilometers) }
    }

    suspend fun incrementKilometers(kilometers: Double) {
        require(kilometers >= 0) { "kilometers must be non-negative" }
        modifyToday { it.copy(kilometers = it.kilometers + kilometers) }
    }

    private suspend fun modifyToday(change: (ActivityDaily) -> ActivityDaily) {
        val profileId = profile.id()
        val date = today()
        db.withTransaction {
            val now = System.currentTimeMillis()
            val current = dao.find(profileId, date)
                ?: ActivityDaily(profileId = profileId, date = date, createdAt = now, updatedAt = now)
            dao.upsert(change(current).copy(updatedAt = now))
        }
    }

    private fun today(): String = LocalDate.now().toString()
}
