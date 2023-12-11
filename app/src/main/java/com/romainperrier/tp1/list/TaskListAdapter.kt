package com.romainperrier.tp1.list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.romainperrier.tp1.R
import com.romainperrier.tp1.databinding.ItemTaskBinding

class TaskListAdapter(val adapterListener: TaskListListener) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {
    inner class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        private val taskTitleTextView: TextView = itemView.findViewById(R.id.task_title)
        private val taskDescriptionTextView: TextView = itemView.findViewById(R.id.task_description)

        fun bind(task: Task) {
            taskTitleTextView.text = task.title
            taskDescriptionTextView.text = task.description
            binding?.taskDelete?.setOnClickListener {
                adapterListener.onClickDelete(task)
            }
            binding?.taskEdit?.setOnClickListener {
                adapterListener.onClickEdit(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }
}
