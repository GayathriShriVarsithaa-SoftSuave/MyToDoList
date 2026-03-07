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

    @Transaction
    @Query("""
SELECT tasks.* FROM tasks
JOIN todolist_table ON tasks.taskId = todolist_table.taskId
JOIN tags ON tags.tagId = todolist_table.tagId
WHERE
(
    tasks.title LIKE '%' || :query || '%'
    OR tasks.description LIKE '%' || :query || '%'
)
AND
(
    :tagCount = 0 OR tags.tag IN (:selectedTags)
)
GROUP BY tasks.taskId
HAVING :tagCount = 0 OR COUNT(DISTINCT tags.tag) = :tagCount
""")
    fun searchTasks(
        query: String,
        selectedTags: List<String>,
        tagCount: Int
    ): Flow<List<TaskWithTags>>
    @Query("SELECT * FROM tags")
    suspend fun getAllTags(): List<Tag>
}