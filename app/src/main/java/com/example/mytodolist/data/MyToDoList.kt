package com.example.mytodolist.data

import androidx.room.Entity
import androidx.room.ForeignKey


@Entity(
    tableName = "todolist_table",
    primaryKeys = ["taskId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = ["tagId"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MyToDoList(
    val taskId: Long,
    val tagId: Long
)