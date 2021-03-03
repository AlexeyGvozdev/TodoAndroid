package com.sinx.todo.ui.add.task

import com.sinx.todo.base.Tea
import com.sinx.todo.base.TeaVM
import com.sinx.todo.core.*
import com.sinx.todo.repository.AddTaskRepository
import com.sinx.todo.ui.table.TaskItem

typealias AddTaskTea = Tea<AddTaskModel, AddTaskViewState, AddTaskAction, AddTaskMsg>
typealias AddTaskTeaVM = TeaVM<AddTaskModel, AddTaskViewState, AddTaskAction, AddTaskMsg>

fun provideAddTaskTea(
    addTaskRepository: AddTaskRepository
) : AddTaskTea {
    val init: Init<AddTaskModel, AddTaskMsg, AddTaskAction> = {
        AddTaskModel() to none()
    }

    val update: Update<AddTaskModel, AddTaskMsg, AddTaskAction> = { model, msg ->
        when (msg) {
            AddTaskMsg.OnClosePressed -> model to none()
            is AddTaskMsg.OnCreatePressed -> model to { dispatch ->
                addTaskRepository.addTask(msg.taskName)
                dispatch(Command.ActionCommand(AddTaskAction.CloseDialog))
            }
        }
    }

    val view: ViewState<AddTaskModel, AddTaskViewState> = { model ->
        AddTaskViewState()
    }

    return AddTaskTea(init, update, view)
}

class AddTaskModel()

class AddTaskViewState()

sealed class AddTaskMsg() {
    class OnCreatePressed(val taskName: String) : AddTaskMsg()
    object OnClosePressed : AddTaskMsg()
}

sealed class AddTaskAction() {
    object CloseDialog : AddTaskAction()
}