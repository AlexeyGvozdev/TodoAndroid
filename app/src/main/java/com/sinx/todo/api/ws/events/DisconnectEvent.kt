package com.sinx.todo.api.ws.events

import com.github.nkzawa.socketio.client.Socket
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.ui.table.Disconnect

class DisconnectEvent : SocketClient.Event<Disconnect>(Socket.EVENT_DISCONNECT, Disconnect::class)