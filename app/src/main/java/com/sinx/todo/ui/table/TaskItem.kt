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
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskItem, newItem: TaskItem): Boolean {
            return oldItem.checked == newItem.checked && oldItem == newItem
        }
    }
}


@Serializable
class Disconnect : TableResponse()

@Serializable
class Connection : TableResponse()