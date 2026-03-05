package com.example.mytodolist.data


import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val taskDao: TaskDao,
    private val tagDao: TagDao,
    private val myToDoListDao: MyToDoListDao
) {

    val allTasksWithTags: Flow<List<TaskWithTags>> = taskDao.getTasksWithTags()

    suspend fun insertTask(task: Task, tags: List<String>) {
        val taskId = taskDao.insertTask(task)

        tags.forEach { tagName ->
            var tag = tagDao.getTagByName(tagName)

            if (tag == null) {
                val tagId = tagDao.insertTag(Tag(tag = tagName))
                tag = Tag(tagId = tagId, tag = tagName)
            }

            myToDoListDao.insertCrossRef(MyToDoList(taskId = taskId, tagId = tag.tagId))
        }
    }

    suspend fun updateTask(task: Task) {
        taskDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun updateTaskCompletion(taskId: Long, isCompleted: Boolean) {
        taskDao.updateTaskCompletion(taskId, isCompleted)
    }
    fun getAllTags():Flow<List<String>>{
        return tagDao.getAllTag()
    }
}