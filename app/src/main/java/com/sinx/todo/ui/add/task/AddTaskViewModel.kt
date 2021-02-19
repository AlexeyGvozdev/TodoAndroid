package com.sinx.todo.ui.add.task

import com.sinx.todo.base.BaseViewModel
import com.sinx.todo.core.Update
import com.sinx.todo.core.ViewState

class AddTaskViewModel :
    BaseViewModel<AddTaskModel, AddTaskViewState, AddTaskAction, AddTaskMsg>() {

    private var model: AddTaskModel = AddTaskModel()

    override fun dispatch(msg: AddTaskMsg) {
//        model = update(model, msg)
//        viewState = view(model)
    }

//    private val update: Update<AddTaskModel, AddTaskMsg> = { model, msg ->
//        when (msg) {
//            is AddTaskMsg.OnCreatePressed -> {
//                viewAction = AddTaskAction.CloseDialog
//                model
//            }
//            AddTaskMsg.OnClosePressed -> {
//                viewAction = AddTaskAction.CloseDialog
//                model
//            }
//        }
//    }
//
//    private val view: ViewState<AddTaskModel, AddTaskViewState> = { mdoel ->
//        AddTaskViewState()
//    }
}

class AddTaskModel()

class AddTaskViewState()

sealed class AddTaskMsg() {
    class OnCreatePressed(taskName: String) : AddTaskMsg()
    object OnClosePressed : AddTaskMsg()
}

sealed class AddTaskAction() {
    object CloseDialog : AddTaskAction()
}