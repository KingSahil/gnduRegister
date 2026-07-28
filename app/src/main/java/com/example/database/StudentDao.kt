package com.example.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY CAST(rollNumber AS INTEGER) ASC, rollNumber ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students ORDER BY CAST(rollNumber AS INTEGER) ASC, rollNumber ASC")
    suspend fun getAllStudentsList(): List<Student>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getStudentById(id: Long): Student?

    @Query("SELECT * FROM students WHERE rollNumber = :rollNumber LIMIT 1")
    suspend fun getStudentByRollNumber(rollNumber: String): Student?

    @Query("""
        SELECT * FROM students 
        WHERE (:semester = '' OR semester = :semester)
        AND (:section = '' OR section = :section)
        AND (:group = '' OR `group` = :group)
        AND (:query = '' OR rollNumber LIKE '%' || :query || '%' OR LOWER(name) LIKE '%' || LOWER(:query) || '%')
        ORDER BY CAST(rollNumber AS INTEGER) ASC, rollNumber ASC
    """)
    fun getFilteredStudents(
        semester: String,
        section: String,
        group: String,
        query: String
    ): Flow<List<Student>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteStudentById(id: Long)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun getStudentCount(): Int

    @Query("SELECT DISTINCT semester FROM students ORDER BY semester ASC")
    fun getSemesters(): Flow<List<String>>

    @Query("SELECT DISTINCT section FROM students ORDER BY section ASC")
    fun getSections(): Flow<List<String>>

    @Query("SELECT DISTINCT `group` FROM students ORDER BY `group` ASC")
    fun getGroups(): Flow<List<String>>
}
