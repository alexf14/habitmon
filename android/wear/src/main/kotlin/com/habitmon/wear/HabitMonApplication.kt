package com.habitmon.wear

import android.app.Application
import com.habitmon.shared.data.db.HabitMonDatabase
import com.habitmon.shared.data.repository.Repositories

class HabitMonApplication : Application() {

    val repositories: Repositories by lazy {
        Repositories(HabitMonDatabase.getInstance(this))
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: HabitMonApplication
            private set

        val repositories: Repositories get() = instance.repositories
    }
}
