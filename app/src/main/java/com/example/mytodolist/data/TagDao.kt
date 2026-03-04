package com.example.mytodolist.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TagDao {

    @Insert
    suspend fun insertTag(tag: Tag): Long

    @Query("SELECT * FROM tags WHERE tag = :tagName LIMIT 1")
    suspend fun getTagByName(tagName: String): Tag?
}