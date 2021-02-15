package com.sinx.todo.api.ws

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.sinx.todo.base.Either
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

    inline fun <reified T : Any> on(nameEvent: String): Flow<Either<Throwable, T>> = subscribeToEvent(nameEvent)
        .map {
            try {
                Either.Right(Json.decodeFromString(T::class.serializer(), it))
            } catch (e: Exception) {
                Either.Left(e)
            }
        }

    fun subscribeToEvent(nameEvent: String) = callbackFlow {
        socketIO.on(nameEvent) { body ->
            for (data in body) {
                if (nameEvent == Socket.EVENT_DISCONNECT) {
                    offer("{}")
                } else {
                    offer(data.toString())
                }
            }
        }
        awaitClose { cancel() }
    }

    fun disconnect() {
        socketIO.disconnect()
    }
}