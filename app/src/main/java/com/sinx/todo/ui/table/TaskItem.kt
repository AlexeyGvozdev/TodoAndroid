package com.sinx.todo.ui.table

import androidx.recyclerview.widget.DiffUtil
import com.sinx.todo.api.ws.SocketClient
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

sealed class TableResponse
@Serializable
data class TaskItem(val id: Int, val text: String, val checked: Boolean) : TableResponse() {

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


@Serializable
class Disconnect : TableResponse()

@Serializable
class Connection : TableResponse()