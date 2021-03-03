package com.sinx.todo.ui.table

import com.sinx.todo.base.Either
import com.sinx.todo.base.Tea
import com.sinx.todo.core.*
import com.sinx.todo.repository.TableRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

typealias TableTea = Tea<TableModel, TableViewState, TableAction, TableMsg>

@OptIn(ExperimentalCoroutinesApi::class)
fun provideTableTea(repository: TableRepository): TableTea {
    val init: Init<TableModel, TableMsg, TableAction> = {
        TableModel(false, emptyList()) to batch (
            { dispatch ->
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
        },
            { dispatch ->
                dispatch(Command.MsgCommand(TableMsg.Connection(repository.connected())))
            }
        )
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
                ) to none()
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
                } to none()
            }
            is TableMsg.AddTask -> {
                model.copy(
                    tasks = model.tasks + msg.task
                ) to none()
            }
            TableMsg.AddTaskPressed -> {
                model to { dispatch ->
                    dispatch(Command.ActionCommand(TableAction.ToAddTask()))
                }
            }
            is TableMsg.Connection -> model.copy(connection = msg.connection) to {}
        }
    }
    val view: ViewState<TableModel, TableViewState> = { model ->
        TableViewState(model.tasks, model.connection)
    }

    return TableTea(init, update, view)
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
    class ToAddTask : TableAction()
}