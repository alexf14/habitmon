package com.habitmon.wear.debug

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.habitmon.shared.data.db.entity.PokedexStatus
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Puerta trasera para simular entradas desde el ordenador (solo builds de debug).
 * Ver android/DEBUG.md para la lista de comandos.
 */
class DebugReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val repos = HabitMonApplication.repositories
        val pending = goAsync()

        scope.launch {
            try {
                val result = when (intent.action) {
                    ACTION_ADD_STEPS -> {
                        val count = intent.getIntExtra("count", 1000)
                        repos.activity.incrementSteps(count)
                        "+$count pasos"
                    }
                    ACTION_SET_STEPS -> {
                        val steps = intent.requireInt("steps")
                        repos.activity.updateSteps(steps)
                        "pasos = $steps"
                    }
                    ACTION_SET_KM -> {
                        val km = intent.getFloatExtra("km", -1f)
                        require(km >= 0f) { "Falta el extra --ef km <valor>" }
                        repos.activity.updateKilometers(km.toDouble())
                        "km = $km"
                    }
                    ACTION_SET_COMPANION -> {
                        val id = intent.requireInt("pokemon_id")
                        // Atajo de pruebas: desbloquea antes de cambiar, para no tener que encadenar comandos.
                        repos.pokedex.unlock(id)
                        "compañero #$id: ${repos.companion.setActiveCompanion(id)}"
                    }
                    ACTION_UNLOCK_POKEMON -> {
                        val id = intent.requireInt("pokemon_id")
                        val status = intent.getStringExtra("status")?.let { PokedexStatus.valueOf(it.uppercase()) }
                            ?: PokedexStatus.UNLOCKED
                        repos.pokedex.updateStatus(id, status)
                        "#$id = $status"
                    }
                    ACTION_RESET -> {
                        repos.resetGame()
                        "reset completo"
                    }
                    else -> "acción desconocida: ${intent.action}"
                }
                Log.i(TAG, "OK $result")
                pending.resultData = "OK $result"
            } catch (e: Exception) {
                Log.e(TAG, "ERROR ${intent.action}: ${e.message}", e)
                pending.resultData = "ERROR ${e.message}"
            } finally {
                pending.finish()
            }
        }
    }

    private fun Intent.requireInt(name: String): Int {
        val value = getIntExtra(name, -1)
        require(value >= 0) { "Falta el extra --ei $name <valor>" }
        return value
    }

    companion object {
        private const val TAG = "HabitMonDebug"
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        const val ACTION_ADD_STEPS = "com.habitmon.debug.ADD_STEPS"
        const val ACTION_SET_STEPS = "com.habitmon.debug.SET_STEPS"
        const val ACTION_SET_KM = "com.habitmon.debug.SET_KM"
        const val ACTION_SET_COMPANION = "com.habitmon.debug.SET_COMPANION"
        const val ACTION_UNLOCK_POKEMON = "com.habitmon.debug.UNLOCK_POKEMON"
        const val ACTION_RESET = "com.habitmon.debug.RESET"
    }
}
