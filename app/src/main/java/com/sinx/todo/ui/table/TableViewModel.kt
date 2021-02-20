package com.sinx.todo.ui.table

import androidx.lifecycle.viewModelScope
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.base.BaseViewModel
import com.sinx.todo.base.Either
import com.sinx.todo.base.Feature
import com.sinx.todo.core.*
import com.sinx.todo.repository.TableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@ExperimentalCoroutinesApi
@OptIn(InternalSerializationApi::class)
class TableViewModel : BaseViewModel<TableModel, TableViewState, TableAction, TableMsg>() {

    val url = "http://192.168.1.2"
    val port = 8003
    private val socketClient: SocketClient = SocketClient("$url:$port")
    private val repository = TableRepository(socketClient)
    private val feature = provideTableFeature(repository)
    private var model: TableModel

    init {
        dispatch(TableMsg.ConnectToSocket)
        socketClient.connect()
        val (initModel, _) = feature.init(null)
        model = initModel
    }

    override fun dispatch(msg: TableMsg) {
        val (newModel, effect) = feature.update(model, msg)
        viewModelScope.launch(Dispatchers.Default) {
            effect { command ->
                when (command) {
                    is Command.ActionCommand -> viewAction = command()
                    is Command.MsgCommand -> dispatch(command())
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

@OptIn(ExperimentalCoroutinesApi::class)
fun provideTableFeature(repository: TableRepository): TableFeature {
    val init: Init<TableModel, TableMsg, TableAction> = { tableModel ->
        (tableModel ?: TableModel(false, emptyList())) to { dispatch ->
            repository.subscribe().collect { data ->
                if (data is Either.Right) {
                    dispatch(
                        Command.MsgCommand(
                            when (data.right) {
                                is TaskItem -> TableMsg.AddTask(data.right)
                                is Disconnect -> TableMsg.Connection(false)
                                is Connection -> TableMsg.Connection(true)
                            }
                        )
                    )
                }
            }
        }
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
                ) to Nill()
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
                } to Nill()
            }
            is TableMsg.AddTask -> {
                model.copy(
                    tasks = model.tasks + msg.task
                ) to Nill()
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