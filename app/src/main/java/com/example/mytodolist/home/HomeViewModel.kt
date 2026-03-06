package com.example.mytodolist.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val taskDao: TaskDao = database.taskDao()
    private val tagDao: TagDao = database.tagDao()
    private val myToDoDao: MyToDoListDao = database.myToDoListDao()
    val allTasks: Flow<List<TaskWithTags>> = taskDao.getTasksWithTags()

    fun addTask(title: String, description: String, tags: List<String>, deadline: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val taskId =
                taskDao.insertTask(Task(title = title, description = description, updatedAt = System.currentTimeMillis(), date = deadline))
            tags.forEach { tagName ->
                var tag = tagDao.getTagByName(tagName)
                if (tag == null) {
                    val tagId = tagDao.insertTag(Tag(tag = tagName))
                    tag = Tag(tagId = tagId, tag = tagName)
                }
                myToDoDao.insertCrossRef(MyToDoList(taskId = taskId, tagId = tag.tagId))
            }
        }
    }

    val alltags: Flow<List<String>> = tagDao.getAllTag()
    fun deleteTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.deleteTask(task)
        }
    }

    fun updateCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            taskDao.updateTaskCompletion(taskId, isCompleted)
        }
    }
}