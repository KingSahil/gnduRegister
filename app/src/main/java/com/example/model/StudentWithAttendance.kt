package com.example.model

import androidx.compose.runtime.Immutable

@Immutable
data class StudentWithAttendance(
    val student: Student,
    val isPresent: Boolean,
    val attendanceRecordId: Long? = null
)
