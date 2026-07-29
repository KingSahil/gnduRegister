package com.example.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "subjects")
data class SubjectClass(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val semester: String = "Sem 3"
)
