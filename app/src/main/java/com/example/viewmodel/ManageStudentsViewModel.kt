package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.Student
import com.example.repository.StudentRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageStudentsViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Student>> = _searchQuery
        .flatMapLatest { query ->
            studentRepository.getFilteredStudents(
                semester = "",
                section = "",
                group = "",
                query = query
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    suspend fun getStudentById(id: Long): Student? {
        return studentRepository.getStudentById(id)
    }

    fun addStudent(
        rollNumber: String,
        name: String,
        semester: String,
        section: String,
        group: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val semFormatted = semester.replace("Sem ", "").trim()
            val secFormatted = section.replace("Section ", "").trim()
            val grpFormatted = if (group.startsWith("Group ")) group else "Group $group"

            val student = Student(
                rollNumber = rollNumber.trim(),
                name = name.trim().uppercase(),
                semester = semFormatted,
                section = secFormatted,
                group = grpFormatted
            )

            val result = studentRepository.addStudent(student)
            result.onSuccess {
                _successMessage.value = "Student added successfully"
                onSuccess()
            }.onFailure { ex ->
                _errorMessage.value = ex.message ?: "Failed to add student"
            }
        }
    }

    fun updateStudent(
        id: Long,
        rollNumber: String,
        name: String,
        semester: String,
        section: String,
        group: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val semFormatted = semester.replace("Sem ", "").trim()
            val secFormatted = section.replace("Section ", "").trim()
            val grpFormatted = if (group.startsWith("Group ")) group else "Group $group"

            val student = Student(
                id = id,
                rollNumber = rollNumber.trim(),
                name = name.trim().uppercase(),
                semester = semFormatted,
                section = secFormatted,
                group = grpFormatted
            )

            val result = studentRepository.updateStudent(student)
            result.onSuccess {
                _successMessage.value = "Student updated successfully"
                onSuccess()
            }.onFailure { ex ->
                _errorMessage.value = ex.message ?: "Failed to update student"
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            studentRepository.deleteStudent(student)
            _successMessage.value = "Student deleted successfully"
        }
    }

    class Factory(private val repository: StudentRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManageStudentsViewModel(repository) as T
        }
    }
}
