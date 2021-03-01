package com.sinx.todo.api.ws.events

import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.ui.table.Connection

class ConnectionEvent : SocketClient.Event<Connection>("connection", Connection::class)