package com.sinx.todo.api.ws

import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import com.sinx.todo.api.middleware.WebSocketMiddleware
import com.sinx.todo.base.Either
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class SocketClient(url: String) {
    private val socketIO = IO.socket(url)

    private val listMiddleware: MutableList<WebSocketMiddleware> = mutableListOf()

    fun addMiddleware(webSocketMiddleware: WebSocketMiddleware) {
        listMiddleware.add(webSocketMiddleware)
    }

    fun connect() {
        if (socketIO.connected().not()) {
            socketIO.connect()
            socketIO.emit("connection", "")
        }
    }

    fun connected() = socketIO.connected()

    @OptIn(InternalSerializationApi::class)
    fun <T : Any> on(event: Event<T>): Flow<Either<Throwable, T>> =
        subscribeToEvent(event.nameEvent)
            .map { json ->
                try {
                    listMiddleware.forEach { middleware -> middleware(event.nameEvent, json) }
                    Either.Right(Json.decodeFromString(event.returnClass.serializer(), json))
                } catch (e: Exception) {
                    Either.Left(e)
                }
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeToEvent(nameEvent: String) = callbackFlow {
        val listener: (args: Array<Any>) -> Unit = { body ->
            for (data in body) {
                if (nameEvent == Socket.EVENT_DISCONNECT) {
                    offer("{}")
                } else {
                    offer(data.toString())
                }
            }
        }
        socketIO.on(nameEvent, listener)
        awaitClose {
            socketIO.off(nameEvent, listener)
            cancel()
        }
    }

    fun disconnect() {
        socketIO.disconnect()
    }

    fun emit(emit: Emit) {
        socketIO.emit(emit.nameEvent, emit.data)
    }

    open class Event<T : Any>(val nameEvent: String, val returnClass: KClass<T>)
    open class Emit(val nameEvent: String, val data: Any)
}