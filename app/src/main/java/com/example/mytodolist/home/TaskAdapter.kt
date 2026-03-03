package com.example.mytodolist.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.data.TaskWithTags
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.checkbox.MaterialCheckBox

class TaskAdapter(
    private var taskList: List<TaskWithTags>,
    private val onCheckedChange: (taskId: Long, isChecked: Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.taskTitle)
        val description: TextView = itemView.findViewById(R.id.taskDescription)
        val chipGroup: ChipGroup = itemView.findViewById(R.id.taskChipGroup)
        val checkbox: MaterialCheckBox = itemView.findViewById(R.id.taskCheckbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskWithTags = taskList[position]


        holder.title.text = taskWithTags.task.title
        holder.description.text = taskWithTags.task.description


        holder.chipGroup.removeAllViews()

        taskWithTags.tags.forEach { tag ->
            val chip = Chip(holder.itemView.context)
            chip.text = tag.tag
            chip.isClickable = false
            chip.isCheckable = false
            chip.setTextColor(holder.itemView.resources.getColor(R.color.white, null))
            chip.chipBackgroundColor = holder.itemView.resources.getColorStateList(R.color.green, null)
            holder.chipGroup.addView(chip)
        }


        holder.checkbox.setOnCheckedChangeListener(null)
        holder.checkbox.isChecked = false
        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(taskWithTags.task.taskId, isChecked)
        }
    }

    override fun getItemCount(): Int = taskList.size

    fun submitList(newList: List<TaskWithTags>) {
        taskList = newList
        notifyDataSetChanged()
    }

    fun getTaskAt(position: Int): TaskWithTags {
        return taskList[position]
    }
}