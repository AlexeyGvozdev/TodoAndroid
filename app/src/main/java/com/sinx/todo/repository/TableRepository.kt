package com.sinx.todo.repository

import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.api.ws.events.ConnectionEvent
import com.sinx.todo.api.ws.events.DisconnectEvent
import com.sinx.todo.api.ws.events.NewTaskEvent
import com.sinx.todo.base.Either
import com.sinx.todo.ui.table.TableResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
class TableRepository(private val socketClient: SocketClient) {

    fun subscribe(): Flow<Either<Throwable, out TableResponse>> {
        val newTaskFlow = socketClient.on(NewTaskEvent())
        val disconnectionFlow = socketClient.on(DisconnectEvent())
        val connectionFlow = socketClient.on(ConnectionEvent())
        return flowOf(newTaskFlow, disconnectionFlow, connectionFlow).flattenMerge()
    }

    fun connected() = socketClient.connected()
}