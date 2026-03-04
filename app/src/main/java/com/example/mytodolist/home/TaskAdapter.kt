package com.example.mytodolist.home

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mytodolist.R
import com.example.mytodolist.data.TaskWithTags
import com.example.mytodolist.databinding.TaskItemBinding
import com.google.android.material.chip.Chip

class TaskAdapter(
    private var taskList: List<TaskWithTags>,
    private val onCheckedChange: (Long, Boolean) -> Unit,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(val binding: TaskItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = TaskItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TaskViewHolder(binding)
    }

    override fun getItemCount(): Int = taskList.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val taskWithTags = taskList[position]
        val context = holder.binding.root.context


        holder.binding.taskTitle.text = taskWithTags.task.title
        holder.binding.taskDescription.text = taskWithTags.task.description


        holder.binding.taskCheckbox.setOnCheckedChangeListener(null)
        holder.binding.taskCheckbox.isChecked = taskWithTags.task.isCompleted

        if (taskWithTags.task.isCompleted) {
            holder.binding.taskTitle.paintFlags =
                holder.binding.taskTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.taskDescription.paintFlags =
                holder.binding.taskDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.binding.taskContainer.isEnabled = false
            holder.binding.taskContainer.alpha = 0.5f
        } else {
            holder.binding.taskTitle.paintFlags =
                holder.binding.taskTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.taskDescription.paintFlags =
                holder.binding.taskDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.binding.taskContainer.isEnabled = true
            holder.binding.taskContainer.alpha = 1.0f
        }


        holder.binding.taskCheckbox.setOnCheckedChangeListener { _, isChecked ->
            onCheckedChange(taskWithTags.task.taskId, isChecked)
        }

        holder.binding.taskChipGroup.removeAllViews()
        taskWithTags.tags.forEach { tag ->
            val chip = Chip(context).apply {
                text = tag.tag
                isClickable = false
                isCheckable = false
                setChipBackgroundColorResource(R.color.darkBlue)
                setTextColor(ContextCompat.getColor(context, R.color.primaryGreen))
            }
            holder.binding.taskChipGroup.addView(chip)
            holder.itemView.setOnClickListener {
                onItemClick(taskWithTags.task.taskId)
            }
        }

    }

    fun submitList(newList: List<TaskWithTags>) {
        taskList = newList
        notifyDataSetChanged()
    }
}