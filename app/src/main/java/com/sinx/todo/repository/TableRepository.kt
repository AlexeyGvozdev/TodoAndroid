package com.sinx.todo.repository

import com.github.nkzawa.socketio.client.Socket
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.base.Either
import com.sinx.todo.ui.table.Connection
import com.sinx.todo.ui.table.Disconnect
import com.sinx.todo.ui.table.TableResponse
import com.sinx.todo.ui.table.TaskItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.InternalSerializationApi
import kotlin.reflect.KClass

@OptIn(InternalSerializationApi::class)
class TableRepository(val socketClient: SocketClient) {

    fun subscribe() : Flow<Either<Throwable, out TableResponse>> {
        val newTaskFlow = socketClient.on(SocketClient.Event("newTask", TaskItem::class))
        val disconnectionFlow =
            socketClient.on(SocketClient.Event(Socket.EVENT_DISCONNECT, Disconnect::class))
        val connectionFlow =
            socketClient.on(SocketClient.Event("connection", Connection::class))
        return flowOf(newTaskFlow, disconnectionFlow, connectionFlow).flattenMerge()
    }

    fun unsubscribe() {}

}

@OptIn(InternalSerializationApi::class)
sealed class TableEvent<T : Any>(name: String, clazz: KClass<T>) : SocketClient.Event<T>(name, clazz) {

    object NewTaskEvent : TableEvent<TaskItem>("newTask", TaskItem::class)

}