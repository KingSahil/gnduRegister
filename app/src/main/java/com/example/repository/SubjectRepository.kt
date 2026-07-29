package com.example.repository

import com.example.database.SubjectDao
import com.example.model.SubjectClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubjectRepository(
    private val subjectDao: SubjectDao
) {
    fun getAllSubjects(): Flow<List<SubjectClass>> = subjectDao.getAllSubjects().map { list ->
        list.distinctBy { Pair(it.name.lowercase().trim(), it.semester.lowercase().trim()) }
    }

    suspend fun addSubject(name: String, semester: String = "Sem 3") {
        if (name.isNotBlank()) {
            subjectDao.insertSubject(SubjectClass(name = name.trim(), semester = semester))
        }
    }

    suspend fun updateSubject(id: Long, newName: String, semester: String = "Sem 3") {
        if (newName.isNotBlank()) {
            subjectDao.updateSubject(SubjectClass(id = id, name = newName.trim(), semester = semester))
        }
    }

    suspend fun deleteSubject(id: Long) {
        subjectDao.deleteSubjectById(id)
    }
}
