package com.sinx.todo.api.ws

import com.github.nkzawa.socketio.client.IO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

@ExperimentalCoroutinesApi
@InternalSerializationApi
class SocketClient(url: String) {

    private val socketIO = IO.socket(url)

    fun connect() {
        if (socketIO.connected().not()) {
            socketIO.connect()
            socketIO.emit("connection", "")
        }
    }

    inline fun <reified T : Any> on(nameEvent: String): Flow<T> = subscribeToEvent(nameEvent)
        .map { Json.decodeFromString(T::class.serializer(), it) }

    @ExperimentalCoroutinesApi
    fun subscribeToEvent(nameEvent: String) = callbackFlow {
        socketIO.on(nameEvent) { body ->
            for (data in body) {
                offer(data.toString())
            }
        }
        awaitClose { cancel() }
    }

    fun disconnect() {
        socketIO.disconnect()
    }
}