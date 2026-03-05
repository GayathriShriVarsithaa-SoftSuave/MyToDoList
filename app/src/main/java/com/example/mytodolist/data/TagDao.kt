package com.example.mytodolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Insert
    suspend fun insertTag(tag: Tag): Long

    @Query("SELECT * FROM tags WHERE tag = :tagName LIMIT 1")
    suspend fun getTagByName(tagName: String): Tag?

    @Query("SELECT DISTINCT tag FROM tags")
    fun getAllTag(): Flow<List<String>>

    @Query("DELETE FROM tags WHERE tagid NOT IN(SELECT tagid from todolist_table)")
    fun deletetag()
}