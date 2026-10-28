package com.habitmon.wear.ui.screen.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habitmon.shared.data.repository.CompanionRepository
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WelcomeViewModel(
    companionRepository: CompanionRepository
) : ViewModel() {

    val companionState = companionRepository.getActiveCompanion()
        .map { companion ->
            when {
                companion == null -> CompanionCheckState.NoCompanion
                !companion.introCompleted -> CompanionCheckState.NeedsIntro
                else -> CompanionCheckState.Ready
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CompanionCheckState.Loading)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                WelcomeViewModel(HabitMonApplication.repositories.companion)
            }
        }
    }
}
