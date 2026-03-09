package com.example.mytodolist.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MyToDoListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: MyToDoList)


    @Query("DELETE FROM todolist_table WHERE taskId = :taskId")
    suspend fun deleteTagsForTask(taskId: Long)
}