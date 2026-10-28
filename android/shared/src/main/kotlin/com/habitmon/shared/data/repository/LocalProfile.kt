package com.habitmon.shared.data.repository

import com.habitmon.shared.data.db.dao.ProfileDao
import com.habitmon.shared.data.db.entity.Profile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Perfil único del dispositivo. Equivale a get_local_profile() del prototipo Django. */
class LocalProfile(private val profileDao: ProfileDao) {
    private val mutex = Mutex()

    suspend fun id(): String = mutex.withLock {
        profileDao.findFirst()?.id ?: Profile().also { profileDao.insert(it) }.id
    }
}
