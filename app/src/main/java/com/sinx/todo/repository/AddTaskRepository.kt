package com.sinx.todo.repository

import com.github.nkzawa.socketio.client.Socket
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.api.ws.emits.AddTaskEmit
import com.sinx.todo.base.Either
import com.sinx.todo.ui.table.Connection
import com.sinx.todo.ui.table.Disconnect
import com.sinx.todo.ui.table.TableResponse
import com.sinx.todo.ui.table.TaskItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf

class AddTaskRepository(private val socketClient: SocketClient) {

    fun addTask(taskName: String) {
        socketClient.emit(AddTaskEmit(taskName))
    }
}
