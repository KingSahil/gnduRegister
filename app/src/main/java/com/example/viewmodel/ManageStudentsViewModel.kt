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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManageStudentsViewModel(
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val _selectedSemester = MutableStateFlow("All")
    val selectedSemester: StateFlow<String> = _selectedSemester.asStateFlow()

    private val _selectedSection = MutableStateFlow("All")
    val selectedSection: StateFlow<String> = _selectedSection.asStateFlow()

    private val _selectedGroup = MutableStateFlow("All")
    val selectedGroup: StateFlow<String> = _selectedGroup.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    val semesters = listOf("All", "Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8")
    val sections = listOf("All", "Section A", "Section B", "Section C", "Section D")
    val groups = listOf("All", "Group 1", "Group 2", "Group 3")

    @OptIn(ExperimentalCoroutinesApi::class)
    val students: StateFlow<List<Student>> = combine(
        _selectedSemester,
        _selectedSection,
        _selectedGroup,
        _searchQuery
    ) { sem, sec, grp, query ->
        val semFormatted = if (sem == "All") "" else sem.replace("Sem ", "").trim()
        val secFormatted = if (sec == "All") "" else sec.replace("Section ", "").trim()
        val grpFormatted = if (grp == "All") "" else grp.trim()

        FilterParams(semFormatted, secFormatted, grpFormatted, query)
    }.flatMapLatest { filter ->
        studentRepository.getFilteredStudents(
            semester = filter.semester,
            section = filter.section,
            group = filter.group,
            query = filter.query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private data class FilterParams(
        val semester: String,
        val section: String,
        val group: String,
        val query: String
    )

    fun setSelectedSemester(sem: String) {
        _selectedSemester.value = sem
    }

    fun setSelectedSection(sec: String) {
        _selectedSection.value = sec
    }

    fun setSelectedGroup(grp: String) {
        _selectedGroup.value = grp
    }

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
