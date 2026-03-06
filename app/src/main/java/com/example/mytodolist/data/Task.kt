package com.example.mytodolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long = 0,
    val title: String,
    val description: String,
    val date:Long,
    val isCompleted:Boolean=false,
    val createdAt:Long=System.currentTimeMillis(),
    val updatedAt:Long
)