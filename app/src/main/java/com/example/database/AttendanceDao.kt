package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.AttendanceRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE date = :date AND subject = :subject")
    fun getAttendanceForDateAndSubject(date: String, subject: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE date = :date AND subject = :subject")
    suspend fun getAttendanceListForDateAndSubject(date: String, subject: String): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE studentId = :studentId AND date = :date AND subject = :subject LIMIT 1")
    suspend fun getAttendanceForStudentDateAndSubject(studentId: Long, date: String, subject: String): AttendanceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAttendance(record: AttendanceRecord): Long

    @Query("DELETE FROM attendance_records WHERE id = :id")
    suspend fun deleteAttendanceById(id: Long)

    @Query("DELETE FROM attendance_records WHERE studentId = :studentId AND date = :date AND subject = :subject")
    suspend fun deleteAttendanceByStudentDateAndSubject(studentId: Long, date: String, subject: String)

    @Query("DELETE FROM attendance_records WHERE studentId = :studentId")
    suspend fun deleteAttendanceByStudentId(studentId: Long)

    @Query("SELECT DISTINCT date FROM attendance_records ORDER BY date DESC")
    fun getAttendanceHistoryDates(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM attendance_records WHERE date = :date AND subject = :subject AND present = 1")
    fun getPresentCountForDateAndSubject(date: String, subject: String): Flow<Int>
}
