package com.example.mytodolist.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mytodolist.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskDao: TaskDao = database.taskDao()
    private val tagDao: TagDao = database.tagDao()
    private val myToDoDao: MyToDoListDao = database.myToDoListDao()
    val allTasks: Flow<List<TaskWithTags>> = taskDao.getTasksWithTags()
    val alltags: Flow<List<String>> = tagDao.getAllTag()

    fun updateTask(taskId:Long,title:String, description:String, tags:List<String>,date:Long)
    {
        viewModelScope.launch(Dispatchers.IO){
            taskDao.updateTask(Task(taskId=taskId,title = title, description = description, date = date))
        }
    }
}