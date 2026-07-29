package com.example

import android.app.Application
import com.example.database.AppDatabase
import com.example.repository.AttendanceRepository
import com.example.repository.SettingsRepository
import com.example.repository.StudentRepository
import com.example.repository.SubjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GnduApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val studentRepository by lazy {
        StudentRepository(database.studentDao(), database.attendanceDao())
    }
    val attendanceRepository by lazy {
        AttendanceRepository(database.studentDao(), database.attendanceDao())
    }
    val subjectRepository by lazy {
        SubjectRepository(database.subjectDao())
    }
    val settingsRepository by lazy { SettingsRepository(this) }

    override fun onCreate() {
        super.onCreate()
        // Pre-warm and initialize SQLite database in background thread on app start
        applicationScope.launch(Dispatchers.IO) {
            database.studentDao().getStudentCount()
            database.subjectDao().getSubjectCount()
        }
    }
}

