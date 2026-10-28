package com.habitmon.wear.ui.screen.pokedex

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habitmon.shared.data.db.entity.PokedexEntryWithSpecies
import com.habitmon.shared.data.repository.CompanionRepository
import com.habitmon.shared.data.repository.PokedexRepository
import com.habitmon.shared.domain.HabitMonError
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PokedexViewModel(
    private val pokedexRepository: PokedexRepository,
    private val companionRepository: CompanionRepository
) : ViewModel() {

    val entries = pokedexRepository.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getSpeciesDetail(pokedexNumber: Int): Flow<PokedexEntryWithSpecies?> =
        pokedexRepository.getEntryForSpecies(pokedexNumber)

    fun setActiveCompanion(pokedexNumber: Int, onDone: () -> Unit) {
        viewModelScope.launch {
            try {
                companionRepository.setActiveCompanion(pokedexNumber)
                onDone()
            } catch (e: HabitMonError) {
                Log.w("HabitMon", "setActiveCompanion: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repos = HabitMonApplication.repositories
                PokedexViewModel(repos.pokedex, repos.companion)
            }
        }
    }
}
