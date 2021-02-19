package com.sinx.todo.ui.table

import androidx.lifecycle.viewModelScope
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.base.BaseViewModel
import com.sinx.todo.base.Either
import com.sinx.todo.base.Feature
import com.sinx.todo.core.Command
import com.sinx.todo.core.Init
import com.sinx.todo.core.Update
import com.sinx.todo.core.ViewState
import com.sinx.todo.repository.TableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@ExperimentalCoroutinesApi
@OptIn(InternalSerializationApi::class)
class TableViewModel : BaseViewModel<TableModel, TableViewState, TableAction, TableMsg>() {

    private var model: TableModel = TableModel(
        tasks = emptyList(),
        connection = false
    )

    private val feature = provideTableFeature()

    val url = "http://192.168.1.2"
    val port = 8003
    private val socketClient: SocketClient = SocketClient("$url:$port")
    private val repository = TableRepository(socketClient)

    init {
        dispatch(TableMsg.ConnectToSocket)
        socketClient.connect()
        viewModelScope.launch(Dispatchers.IO) {
            repository.subscribe().collect { data ->
                if (data is Either.Right) {
                    when (data.right) {
                        is TaskItem -> dispatch(TableMsg.AddTask(data.right))
                        is Disconnect -> dispatch(TableMsg.Connection(false))
                        is Connection -> dispatch(TableMsg.Connection(true))
                    }
                }
            }
        }
    }


    override fun dispatch(msg: TableMsg) {
        val (newModel, effect) = feature.update(model, msg)
        viewModelScope.launch(Dispatchers.Default) {
            effect { command ->
                when (command) {
                    is Command.ActionCommand -> command()?.let {
                        viewAction = it
                    }
                    is Command.MsgCommand -> command()?.let(::dispatch)
                }
            }
        }
        if (newModel != model) {
            model = newModel
            viewState = feature.viewState(model)
        }
    }

    override fun onCleared() {
        socketClient.disconnect()
        super.onCleared()
    }
}

sealed class TableMsg {
    data class CheckedTask(val id: Int, val checked: Boolean) : TableMsg()

    data class AddTask(val task: TaskItem) : TableMsg()

    object ConnectToSocket : TableMsg()
    object AddTaskPressed : TableMsg()
    data class Connection(val connection: Boolean) : TableMsg()
}

data class TableModel(
    val connection: Boolean,
    val tasks: List<TaskItem>
)

data class TableViewState(
    val tasks: List<TaskItem>,
    val connection: Boolean
)

sealed class TableAction {

    object ToAddTask : TableAction()

}

typealias TableFeature = Feature<TableModel, TableViewState, TableAction, TableMsg>

fun provideTableFeature() : TableFeature {
    val init: Init<TableModel, TableMsg, TableAction> = { tableModel ->
        (tableModel ?: TableModel(false, emptyList())) to {}
    }
    val update: Update<TableModel, TableMsg, TableAction> = { model, msg ->
        when (msg) {
            TableMsg.ConnectToSocket -> {
                model.copy(
                    tasks = (0..3).mapIndexed { index, i ->
                        TaskItem(
                            id = index,
                            text = "text$index ".repeat(index + 1),
                            checked = i % 2 == 0
                        )
                    }.sortedBy { it.checked }.toMutableList()
                ) to {}
            }
            is TableMsg.CheckedTask -> {
                model.tasks.indexOfFirst { it.id == msg.id }.let { index ->
                    if (index >= 0) {
                        model.copy(tasks =
                        model.tasks.toMutableList().apply {
                            this[index] =
                                model.tasks[index].copy(checked = msg.checked)
                            sortBy { it.checked }
                        }
                        )
                    } else {
                        model
                    }
                } to {}
            }
            is TableMsg.AddTask -> {
                model.copy(
                    tasks = model.tasks + msg.task
                ) to {}
            }
            TableMsg.AddTaskPressed -> {
                model to { dispatch ->
                    dispatch(Command.ActionCommand(TableAction.ToAddTask))
                }
            }
            is TableMsg.Connection -> model.copy(connection = msg.connection) to {}
        }
    }
    val view: ViewState<TableModel, TableViewState> = { model ->
        TableViewState(model.tasks, model.connection)
    }

    return TableFeature(init, update, view)
}