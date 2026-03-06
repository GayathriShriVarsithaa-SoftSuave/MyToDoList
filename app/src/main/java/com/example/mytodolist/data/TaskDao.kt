package com.example.mytodolist.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE taskId = :taskId")
    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean)

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY updatedAt DESC,createdAt DESC")
    fun getTasksWithTags(): Flow<List<TaskWithTags>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE taskId = :id")
    fun getTaskWithTagsById(id: Long): Flow<TaskWithTags?>

    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTaskById(taskId: Long)
    
//    @Transaction
//    @Query()

}