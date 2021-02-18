package com.sinx.todo.ui.table

import androidx.lifecycle.viewModelScope
import com.github.nkzawa.socketio.client.Socket
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.base.BaseViewModel
import com.sinx.todo.base.Either
import com.sinx.todo.core.Update
import com.sinx.todo.core.ViewState
import com.sinx.todo.repository.TableEvent
import com.sinx.todo.repository.TableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@ExperimentalCoroutinesApi
@OptIn(InternalSerializationApi::class)
class TableViewModel : BaseViewModel<TableModel, TableViewState, TableAction, TableMsg>() {

    private var model: TableModel = TableModel(
        tasks = emptyList(),
        connection = false
    )

    private val view: ViewState<TableModel, TableViewState> = { model ->
        TableViewState(model.tasks, model.connection)
    }

    private val update: Update<TableModel, TableMsg> = { model, msg ->
        when (msg) {
            TableMsg.ConnectToSocket -> {
                model.copy(
                    tasks = tasks
                )
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
                }
            }
            is TableMsg.AddTask -> {
                model.copy(
                    tasks = model.tasks + msg.task
                )
            }
            TableMsg.AddTaskPressed -> {
                viewAction = TableAction.ToAddTask
                model
            }
            is TableMsg.Connection -> model.copy(connection = msg.connection)
        }
    }

    private val tasks = (0..3).mapIndexed { index, i ->
        TaskItem(
            id = index,
            text = "text$index ".repeat(index + 1),
            checked = i % 2 == 0
        )
    }.sortedBy { it.checked }.toMutableList()
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
        model = update(model, msg)
        viewState = view(model)
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