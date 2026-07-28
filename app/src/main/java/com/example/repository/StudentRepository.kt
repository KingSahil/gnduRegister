package com.example.repository

import com.example.database.AttendanceDao
import com.example.database.StudentDao
import com.example.model.Student
import kotlinx.coroutines.flow.Flow

class StudentRepository(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao
) {
    fun getAllStudents(): Flow<List<Student>> = studentDao.getAllStudents()

    fun getFilteredStudents(
        semester: String,
        section: String,
        group: String,
        query: String
    ): Flow<List<Student>> = studentDao.getFilteredStudents(
        semester = semester,
        section = section,
        group = group,
        query = query
    )

    suspend fun getStudentById(id: Long): Student? = studentDao.getStudentById(id)

    suspend fun validateStudent(rollNumber: String, name: String, excludeId: Long? = null): String? {
        val trimmedRoll = rollNumber.trim()
        val trimmedName = name.trim()

        if (trimmedRoll.isEmpty()) return "Roll number is required"
        if (trimmedName.isEmpty()) return "Student name cannot be empty"

        val existing = studentDao.getStudentByRollNumber(trimmedRoll)
        if (existing != null && (excludeId == null || existing.id != excludeId)) {
            return "Student with Roll Number $trimmedRoll already exists"
        }

        return null
    }

    suspend fun addStudent(student: Student): Result<Long> {
        val error = validateStudent(student.rollNumber, student.name)
        if (error != null) return Result.failure(IllegalArgumentException(error))

        return try {
            val id = studentDao.insertStudent(student.copy(
                rollNumber = student.rollNumber.trim(),
                name = student.name.trim().uppercase()
            ))
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateStudent(student: Student): Result<Unit> {
        val error = validateStudent(student.rollNumber, student.name, excludeId = student.id)
        if (error != null) return Result.failure(IllegalArgumentException(error))

        return try {
            studentDao.updateStudent(student.copy(
                rollNumber = student.rollNumber.trim(),
                name = student.name.trim().uppercase()
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteStudent(student: Student) {
        // First delete student's attendance records
        attendanceDao.deleteAttendanceByStudentId(student.id)
        studentDao.deleteStudent(student)
    }

    suspend fun deleteStudentById(id: Long) {
        attendanceDao.deleteAttendanceByStudentId(id)
        studentDao.deleteStudentById(id)
    }

    fun getSemesters(): Flow<List<String>> = studentDao.getSemesters()
    fun getSections(): Flow<List<String>> = studentDao.getSections()
    fun getGroups(): Flow<List<String>> = studentDao.getGroups()
}
