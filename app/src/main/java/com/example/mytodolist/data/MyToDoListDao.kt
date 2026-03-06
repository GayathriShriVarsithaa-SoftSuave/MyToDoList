package com.example.mytodolist.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface MyToDoListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCrossRef(crossRef: MyToDoList)

}