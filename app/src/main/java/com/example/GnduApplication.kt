package com.example

import android.app.Application
import com.example.database.AppDatabase
import com.example.repository.AttendanceRepository
import com.example.repository.SettingsRepository
import com.example.repository.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class GnduApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val studentRepository by lazy {
        StudentRepository(database.studentDao(), database.attendanceDao())
    }
    val attendanceRepository by lazy {
        AttendanceRepository(database.studentDao(), database.attendanceDao())
    }
    val settingsRepository by lazy { SettingsRepository(this) }
}
