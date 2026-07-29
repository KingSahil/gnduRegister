package com.example.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Immutable
@Entity(
    tableName = "students",
    indices = [
        Index(value = ["rollNumber"], unique = true),
        Index(value = ["semester", "section", "group"])
    ]
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rollNumber: String,
    val name: String,
    val semester: String,
    val section: String,
    val group: String
)
