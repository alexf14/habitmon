package com.habitmon.wear.ui.screen.selection

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habitmon.shared.data.repository.CompanionRepository
import com.habitmon.shared.data.repository.SpeciesRepository
import com.habitmon.shared.domain.HabitMonError
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SelectionViewModel(
    speciesRepository: SpeciesRepository,
    private val companionRepository: CompanionRepository
) : ViewModel() {

    val starters = speciesRepository.getSelectableStarters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun chooseStarter(pokedexNumber: Int) {
        viewModelScope.launch {
            try {
                companionRepository.chooseStarter(pokedexNumber)
            } catch (e: HabitMonError) {
                // Un doble toque o un compañero ya existente: la pantalla de intro/home lo resuelve.
                Log.w("HabitMon", "chooseStarter: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repos = HabitMonApplication.repositories
                SelectionViewModel(repos.species, repos.companion)
            }
        }
    }
}
