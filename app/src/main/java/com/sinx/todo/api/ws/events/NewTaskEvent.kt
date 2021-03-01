package com.sinx.todo.api.ws.events

import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.ui.table.TaskItem

class NewTaskEvent : SocketClient.Event<TaskItem>("newTask", TaskItem::class)