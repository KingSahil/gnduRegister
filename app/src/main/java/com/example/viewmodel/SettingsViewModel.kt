package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.SubjectClass
import com.example.repository.SettingsRepository
import com.example.repository.SubjectRepository
import com.example.repository.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _selectedSemester = MutableStateFlow("Sem 3")
    val selectedSemester: StateFlow<String> = _selectedSemester.asStateFlow()

    val semesters = listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8")

    val themeMode: StateFlow<ThemeMode> = settingsRepository.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ThemeMode.SYSTEM)

    val dynamicColor: StateFlow<Boolean> = settingsRepository.dynamicColor
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val subjects: StateFlow<List<SubjectClass>> = combine(
        subjectRepository.getAllSubjects(),
        _selectedSemester
    ) { allSubjects, sem ->
        val cleanSem = sem.replace("Sem ", "").trim()
        allSubjects.filter {
            val s = it.semester.replace("Sem ", "").trim()
            s == cleanSem
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedSemester(semester: String) {
        _selectedSemester.value = semester
    }

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

    fun addSubject(name: String, semester: String = _selectedSemester.value) {
        viewModelScope.launch {
            subjectRepository.addSubject(name, semester)
        }
    }

    fun updateSubject(id: Long, name: String, semester: String = _selectedSemester.value) {
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
