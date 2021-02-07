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
                    tasks = (0..10).mapIndexed { index, i ->
                        TaskItem(
                            id = index,
                            text = "text",
                            checked = i % 2 == 0
                        )
                    }
                )
            }
        }
        viewState = TableViewState(model.tasks)
    }
}

sealed class TableMsg {
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