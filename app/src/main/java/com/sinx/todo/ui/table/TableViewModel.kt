package com.sinx.todo.ui.table

import androidx.lifecycle.viewModelScope
import com.sinx.todo.api.ws.SocketClient
import com.sinx.todo.base.BaseViewModel
import com.sinx.todo.core.Update
import com.sinx.todo.core.ViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi

@ExperimentalCoroutinesApi
@OptIn(InternalSerializationApi::class)
class TableViewModel : BaseViewModel<TableModel, TableViewState, TableAction, TableMsg>() {

    private var model: TableModel = TableModel(
        tasks = emptyList()
    )

    private val view: ViewState<TableModel, TableViewState> = { model ->
        TableViewState(model.tasks)
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

    init {
        dispatch(TableMsg.ConnectToSocket)
        socketClient.connect()
        viewModelScope.launch(Dispatchers.IO) {
            socketClient.on<TaskItem>("newTask")
                .collect { task ->
                    dispatch(TableMsg.AddTask(task))
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
}

data class TableModel(
    val tasks: List<TaskItem>
)

data class TableViewState(
    val tasks: List<TaskItem>
)

sealed class TableAction {

    object ToAddTask : TableAction()

}