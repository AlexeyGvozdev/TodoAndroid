package com.sinx.todo.ui.table

import androidx.recyclerview.widget.DiffUtil
import kotlinx.serialization.Serializable

@Serializable
data class TaskItem(val id: Int, val text: String, val checked: Boolean) {

    companion object : DiffUtil.ItemCallback<TaskItem>() {
        override fun areItemsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            val b = oldItem.id == newItem.id
            return b
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            val b = oldItem.checked == newItem.checked && oldItem == newItem
            return b
        }
    }
}
