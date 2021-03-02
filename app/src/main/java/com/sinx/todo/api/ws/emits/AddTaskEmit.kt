package com.sinx.todo.api.ws.emits

import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.ui.table.TaskItem

class AddTaskEmit(task: TaskItem) : SocketClient.Emit("addTask", task)