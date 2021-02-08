package com.sinx.todo.ui.table

import com.sinx.todo.base.BaseViewModel

class TableViewModel : BaseViewModel<TableModel, TableViewState, TableAction, TableMsg>() {

    private var model: TableModel = TableModel(
        tasks = emptyList()
    )

    init {
        dispatch(TableMsg.ConnectToSocket)
    }

    override fun dispatch(msg: TableMsg) {
        model = when (msg) {
            TableMsg.ConnectToSocket -> {
                model.copy(
                    tasks = (0..3).mapIndexed { index, i ->
                        TaskItem(
                            id = index,
                            text = "text$index ".repeat(index + 1),
                            checked = i % 2 == 0
                        )
                    }.sortedBy { it.checked }
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
        }
        viewState = TableViewState(model.tasks)
    }
}

sealed class TableMsg {
    data class CheckedTask(val id: Int, val checked: Boolean) : TableMsg()

    object ConnectToSocket : TableMsg()
}

data class TableModel(
    val tasks: List<TaskItem>
)

data class TableViewState(
    val tasks: List<TaskItem>
)

sealed class TableAction {

}