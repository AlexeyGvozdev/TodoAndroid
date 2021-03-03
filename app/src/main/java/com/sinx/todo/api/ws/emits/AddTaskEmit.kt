package com.sinx.todo.api.ws.emits

import com.sinx.todo.api.ws.SocketClient

class AddTaskEmit(taskName: String) : SocketClient.Emit("addTask", taskName)