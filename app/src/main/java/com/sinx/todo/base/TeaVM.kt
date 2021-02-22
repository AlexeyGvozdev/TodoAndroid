package com.sinx.todo.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sinx.todo.core.Command
import com.sinx.todo.core.ModelWithEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class TeaVM<Model: Any, View : Any, Action : Any, Msg : Any>(private val feature: Tea<Model, View, Action, Msg>) :
    ViewModel() {

    private var model: Model

    init {
        model = middlewareEffect(feature.init())
    }

    private val TAG = TeaVM::class.java.simpleName
    private val _viewStates: MutableStateFlow<View?> = MutableStateFlow(null)
    fun viewStates(): StateFlow<View?> = _viewStates

    private var _viewState: View? = null
    private var viewState: View
        get() = _viewState
            ?: throw UninitializedPropertyAccessException("\"viewState\" was queried before being initialized")
        set(value) {
            _viewState = value
            _viewStates.value = value
        }


    private val _viewActions: MutableStateFlow<Action?> = MutableStateFlow(null)
    fun viewAction(): StateFlow<Action?> = _viewActions

    private var _viewAction: Action? = null
    private var viewAction: Action
        get() = _viewAction
            ?: throw UninitializedPropertyAccessException("\"viewAction\" was queried before being initialized")
        set(value) {
            _viewAction = value
            _viewActions.value = value
        }


    fun dispatch(msg: Msg) {
        val newModel = middlewareEffect(feature.update(model, msg))
        if (newModel != model) {
            model = newModel
            viewState = feature.viewState(model)
        }
    }

    private fun middlewareEffect(modelWithEffect: ModelWithEffect<Model, Msg, Action>) : Model {
        val (model, effect) = modelWithEffect
        viewModelScope.launch(Dispatchers.Default) {
            effect { command ->
                when (command) {
                    is Command.ActionCommand -> viewAction = command()
                    is Command.MsgCommand -> dispatch(command())
                }
            }
        }
        return model
    }
}