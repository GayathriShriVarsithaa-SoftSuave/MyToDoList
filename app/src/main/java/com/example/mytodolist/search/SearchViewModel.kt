package com.example.mytodolist.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mytodolist.data.AppDatabase
import com.example.mytodolist.data.Tag
import com.example.mytodolist.data.TaskWithTags
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    init {
        search()
    }
    private val dao = AppDatabase.getDatabase(application).taskDao()
    private val query = MutableStateFlow("")
    private val selectedTags = MutableStateFlow<List<String>>(emptyList())

    private val _results = MutableStateFlow<List<TaskWithTags>>(emptyList())
    val results: StateFlow<List<TaskWithTags>> = _results

    fun updateQuery(text: String) {
        query.value = text
        search()
    }

    fun updateTags(tags: List<String>) {
        selectedTags.value = tags
        search()
    }

    private fun search() {
        viewModelScope.launch {
            val tasks = dao.searchTasks(
                query.value,
                selectedTags.value,
                selectedTags.value.size
            ).first()

            _results.value = tasks
        }
    }

    suspend fun getAllTags(): List<Tag> {
        return dao.getAllTags()
    }
    fun updateCompletion(taskId: Long, isCompleted: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.updateTaskCompletion(taskId, isCompleted)
        }
    }
}