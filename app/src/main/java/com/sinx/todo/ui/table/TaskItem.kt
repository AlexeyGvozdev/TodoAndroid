package com.sinx.todo.ui.table

import androidx.recyclerview.widget.DiffUtil

data class TaskItem(val id: Int, val text: String, val checked: Boolean) {

    companion object : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem == newItem
        }
    }

}