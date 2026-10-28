package com.habitmon.wear.ui.screen.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habitmon.shared.data.repository.CompanionRepository
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class IntroViewModel(
    private val companionRepository: CompanionRepository
) : ViewModel() {

    val companion = companionRepository.getActiveCompanionWithSpecies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    suspend fun completeIntro() {
        val current = companion.value ?: return
        companionRepository.completeIntro(current.companion.id)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                IntroViewModel(HabitMonApplication.repositories.companion)
            }
        }
    }
}
