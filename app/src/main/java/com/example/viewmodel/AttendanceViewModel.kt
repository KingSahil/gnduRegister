package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.model.StudentWithAttendance
import com.example.repository.AttendanceRepository
import com.example.repository.SubjectRepository
import com.example.util.DateUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val attendanceRepository: AttendanceRepository,
    private val subjectRepository: SubjectRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(DateUtils.getTodayDbDate())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _selectedSubject = MutableStateFlow("digital logic")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _selectedSemester = MutableStateFlow("Sem 3")
    val selectedSemester: StateFlow<String> = _selectedSemester.asStateFlow()

    private val _selectedSection = MutableStateFlow("Section B")
    val selectedSection: StateFlow<String> = _selectedSection.asStateFlow()

    private val _selectedGroup = MutableStateFlow("All")
    val selectedGroup: StateFlow<String> = _selectedGroup.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val semesters = listOf("Sem 1", "Sem 2", "Sem 3", "Sem 4", "Sem 5", "Sem 6", "Sem 7", "Sem 8")
    val sections = listOf("Section A", "Section B", "Section C", "Section D")
    val groups = listOf("All", "Group 1", "Group 2", "Group 3")

    val subjects: StateFlow<List<String>> = subjectRepository.getAllSubjects()
        .map { list ->
            val names = list.map { it.name }
            if (names.isEmpty()) listOf("digital logic", "DSA", "Cpp", "english") else names
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf("digital logic", "DSA", "Cpp", "english"))

    private val _classFilters = combine(_selectedSemester, _selectedSection, _selectedGroup) { sem, sec, grp ->
        Triple(sem, sec, grp)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val studentsWithAttendance: StateFlow<List<StudentWithAttendance>> = combine(
        _selectedDate,
        _selectedSubject,
        _classFilters,
        _searchQuery
    ) { date, subject, filters, query ->
        val (sem, sec, grp) = filters
        val semNumber = sem.replace("Sem ", "").trim()
        val secLetter = sec.replace("Section ", "").trim()
        val grpName = if (grp == "All" || grp == "All Groups") "" else grp.trim()

        FilterState(
            date = date,
            subject = subject,
            semester = semNumber,
            section = secLetter,
            group = grpName,
            query = query
        )
    }.flatMapLatest { state ->
        attendanceRepository.getStudentsWithAttendance(
            date = state.date,
            subject = state.subject,
            semester = state.semester,
            section = state.section,
            group = state.group,
            query = state.query
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private data class FilterState(
        val date: String,
        val subject: String,
        val semester: String,
        val section: String,
        val group: String,
        val query: String
    )

    val presentCount: StateFlow<Int> = studentsWithAttendance.map { list ->
        list.count { it.isPresent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val absentCount: StateFlow<Int> = studentsWithAttendance.map { list ->
        list.count { !it.isPresent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val historyDates: StateFlow<List<String>> = attendanceRepository.getAttendanceHistoryDates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSelectedDate(date: String) {
        _selectedDate.value = date
    }

    fun setSelectedSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun setSelectedSemester(semester: String) {
        _selectedSemester.value = semester
    }

    fun setSelectedSection(section: String) {
        _selectedSection.value = section
    }

    fun setSelectedGroup(group: String) {
        _selectedGroup.value = group
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleStudentAttendance(studentWithAttendance: StudentWithAttendance) {
        viewModelScope.launch {
            attendanceRepository.toggleAttendance(
                studentId = studentWithAttendance.student.id,
                date = _selectedDate.value,
                subject = _selectedSubject.value,
                currentIsPresent = studentWithAttendance.isPresent
            )
        }
    }

    suspend fun getExportList(): List<StudentWithAttendance> {
        val semNumber = _selectedSemester.value.replace("Sem ", "").trim()
        val secLetter = _selectedSection.value.replace("Section ", "").trim()
        val grpName = if (_selectedGroup.value == "All" || _selectedGroup.value == "All Groups") "" else _selectedGroup.value
        val list = attendanceRepository.getAttendanceForExport(
            date = _selectedDate.value,
            subject = _selectedSubject.value,
            semester = semNumber,
            section = secLetter,
            group = grpName
        )
        return list.sortedWith(
            compareByDescending<StudentWithAttendance> { it.isPresent }
                .thenBy { it.student.rollNumber.toIntOrNull() ?: Int.MAX_VALUE }
                .thenBy { it.student.rollNumber }
        )
    }

    class Factory(
        private val attendanceRepository: AttendanceRepository,
        private val subjectRepository: SubjectRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AttendanceViewModel(attendanceRepository, subjectRepository) as T
        }
    }
}
