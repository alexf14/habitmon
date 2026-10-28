package com.habitmon.wear.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habitmon.shared.data.db.entity.Companion
import com.habitmon.shared.data.db.entity.PokemonSpecies
import com.habitmon.shared.data.repository.CompanionRepository
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class CompanionDisplayData(
    val companion: Companion,
    val species: PokemonSpecies
) {
    val displayName: String
        get() = companion.nickname ?: species.name
}

sealed interface HomeState {
    data object Loading : HomeState
    data object NoCompanion : HomeState
    data class Ready(val data: CompanionDisplayData) : HomeState
}

class HomeViewModel(
    companionRepository: CompanionRepository
) : ViewModel() {

    val state = companionRepository.getActiveCompanionWithSpecies()
        .map { cws ->
            if (cws == null) HomeState.NoCompanion
            else HomeState.Ready(CompanionDisplayData(cws.companion, cws.species))
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeState.Loading)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeViewModel(HabitMonApplication.repositories.companion)
            }
        }
    }
}
