package com.habitmon.wear.ui.screen.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.habitmon.shared.data.repository.ActivityRepository
import com.habitmon.wear.HabitMonApplication
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ActivityViewModel(
    activityRepository: ActivityRepository
) : ViewModel() {

    val todayActivity = activityRepository.getTodayActivity()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ActivityViewModel(HabitMonApplication.repositories.activity)
            }
        }
    }
}
