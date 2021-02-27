package com.sinx.todo.api.ws

class WebSocketConnectionBehavior(private val socketClient: SocketClient) {
    fun connect() = socketClient.connect()
    fun disconnect() = socketClient.disconnect()
}