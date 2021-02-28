package com.sinx.todo.api.ws

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class WebSocketConnectionBehavior(private val socketClient: SocketClient) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun connect() = socketClient.connect()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun disconnect() = socketClient.disconnect()
}