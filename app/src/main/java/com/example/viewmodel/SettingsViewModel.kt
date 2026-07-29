package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.SubjectClass
import com.example.repository.SettingsRepository
import com.example.repository.SubjectRepository
import com.example.repository.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val dynamicColor: StateFlow<Boolean> = settingsRepository.dynamicColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val subjects: StateFlow<List<SubjectClass>> = subjectRepository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsRepository.setThemeMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.setDynamicColor(enabled)
        }
    }

    fun addSubject(name: String, semester: String = "Sem 3") {
        viewModelScope.launch {
            subjectRepository.addSubject(name, semester)
        }
    }

    fun updateSubject(id: Long, name: String, semester: String = "Sem 3") {
        viewModelScope.launch {
            subjectRepository.updateSubject(id, name, semester)
        }
    }

    fun deleteSubject(id: Long) {
        viewModelScope.launch {
            subjectRepository.deleteSubject(id)
        }
    }

    class Factory(
        private val settingsRepository: SettingsRepository,
        private val subjectRepository: SubjectRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SettingsViewModel(settingsRepository, subjectRepository) as T
        }
    }
}
