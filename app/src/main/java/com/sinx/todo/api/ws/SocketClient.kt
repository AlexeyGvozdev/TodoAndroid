package com.sinx.todo.api.ws

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.sinx.todo.base.Either
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class SocketClient(url: String) {

    private val socketIO = IO.socket(url)

    fun connect() {
        if (socketIO.connected().not()) {
            socketIO.connect()
            socketIO.emit("connection", "")
        }
    }

    @OptIn(InternalSerializationApi::class)
    fun <T : Any> on(event: Event<T>): Flow<Either<Throwable, T>> = subscribeToEvent(event.nameEvent)
        .map {
            try {
                Either.Right(Json.decodeFromString(event.returnClass.serializer(), it))
            } catch (e: Exception) {
                Either.Left(e)
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeToEvent(nameEvent: String) = callbackFlow {
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

    open class Event<T : Any>(val nameEvent: String, val returnClass: KClass<T>)
}