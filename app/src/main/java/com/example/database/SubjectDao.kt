package com.example.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.SubjectClass
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {
    @Query("SELECT * FROM subjects ORDER BY id ASC")
    fun getAllSubjects(): Flow<List<SubjectClass>>

    @Query("SELECT * FROM subjects ORDER BY id ASC")
    suspend fun getSubjectList(): List<SubjectClass>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectClass): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubjects(subjects: List<SubjectClass>)

    @Update
    suspend fun updateSubject(subject: SubjectClass)

    @Query("DELETE FROM subjects WHERE id = :id")
    suspend fun deleteSubjectById(id: Long)

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun getSubjectCount(): Int

    @Query("DELETE FROM subjects WHERE id NOT IN (SELECT MIN(id) FROM subjects GROUP BY LOWER(TRIM(name)), semester)")
    suspend fun deleteDuplicates()
}
