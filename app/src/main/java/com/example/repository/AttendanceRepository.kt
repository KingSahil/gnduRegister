package com.example.repository

import com.example.database.AttendanceDao
import com.example.database.StudentDao
import com.example.model.AttendanceRecord
import com.example.model.StudentWithAttendance
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class AttendanceRepository(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao
) {
    fun getStudentsWithAttendance(
        date: String,
        semester: String,
        section: String,
        group: String,
        query: String
    ): Flow<List<StudentWithAttendance>> {
        val studentsFlow = studentDao.getFilteredStudents(semester, section, group, query)
        val attendanceFlow = attendanceDao.getAttendanceForDate(date)

        return combine(studentsFlow, attendanceFlow) { students, records ->
            val attendanceMap = records.associateBy { it.studentId }
            students.map { student ->
                val record = attendanceMap[student.id]
                StudentWithAttendance(
                    student = student,
                    isPresent = record?.present ?: false,
                    attendanceRecordId = record?.id
                )
            }
        }
    }

    suspend fun toggleAttendance(studentId: Long, date: String, currentIsPresent: Boolean) {
        val newIsPresent = !currentIsPresent
        val existing = attendanceDao.getAttendanceForStudentAndDate(studentId, date)
        if (existing != null) {
            attendanceDao.upsertAttendance(
                existing.copy(present = newIsPresent)
            )
        } else {
            attendanceDao.upsertAttendance(
                AttendanceRecord(
                    studentId = studentId,
                    date = date,
                    present = newIsPresent
                )
            )
        }
    }

    fun getAttendanceHistoryDates(): Flow<List<String>> =
        attendanceDao.getAttendanceHistoryDates()

    suspend fun getAttendanceForExport(
        date: String,
        semester: String,
        section: String,
        group: String
    ): List<StudentWithAttendance> {
        val students = studentDao.getAllStudentsList().filter { student ->
            (semester.isEmpty() || student.semester == semester) &&
            (section.isEmpty() || student.section == section) &&
            (group.isEmpty() || student.group == group)
        }
        val records = attendanceDao.getAttendanceListForDate(date)
        val attendanceMap = records.associateBy { it.studentId }

        return students.map { student ->
            val record = attendanceMap[student.id]
            StudentWithAttendance(
                student = student,
                isPresent = record?.present ?: false,
                attendanceRecordId = record?.id
            )
        }
    }
}
