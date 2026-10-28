package com.habitmon.wear.ui.screen.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habitmon.shared.data.db.entity.PokedexStatus
import com.habitmon.shared.data.repository.Repositories
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DebugViewModel(private val repos: Repositories) : ViewModel() {

    val stats: StateFlow<String> = combine(
        repos.activity.getTodayActivity(),
        repos.companion.getActiveCompanionWithSpecies(),
        repos.pokedex.getUnlockedCount(),
    ) { activity, companion, unlocked ->
        "Pasos hoy: ${activity?.steps ?: 0}\n" +
            "Km hoy: ${"%.2f".format(activity?.kilometers ?: 0.0)}\n" +
            "Compañero: ${companion?.species?.name ?: "ninguno"}\n" +
            "Desbloqueados: $unlocked"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Cargando...")

    private val _lastResult = MutableStateFlow("")
    val lastResult: StateFlow<String> = _lastResult

    fun addSteps(count: Int) = run("+$count pasos") { repos.activity.incrementSteps(count) }

    fun addKilometers(km: Double) = run("+$km km") { repos.activity.incrementKilometers(km) }

    fun unlockPokemon(pokedexNumber: Int) =
        run("Desbloqueado #%03d".format(pokedexNumber)) {
            repos.pokedex.updateStatus(pokedexNumber, PokedexStatus.UNLOCKED)
        }

    fun setCompanion(pokedexNumber: Int) =
        run("Compañero #%03d".format(pokedexNumber)) {
            repos.pokedex.unlock(pokedexNumber)
            repos.companion.setActiveCompanion(pokedexNumber)
        }

    fun resetAll() = run("Reset completo") { repos.resetGame() }

    private fun run(label: String, action: suspend () -> Unit) {
        viewModelScope.launch {
            _lastResult.value = try {
                action()
                "OK: $label"
            } catch (e: Exception) {
                "ERROR: ${e.message}"
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { DebugViewModel(HabitMonApplication.repositories) }
        }
    }
}
