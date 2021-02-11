package com.sinx.todo.base

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
abstract class BaseViewModel<Model, State, Action, Msg> : ViewModel() {

    private val TAG = BaseViewModel::class.java.simpleName
    private val _viewStates: MutableStateFlow<State?> = MutableStateFlow(null)
    fun viewStates(): StateFlow<State?> = _viewStates

    private var _viewState: State? = null
    protected var viewState: State
        get() = _viewState
            ?: throw UninitializedPropertyAccessException("\"viewState\" was queried before being initialized")
        set(value) {
            _viewState = value
            _viewStates.value = value
        }


    private val _viewActions: MutableStateFlow<Action?> = MutableStateFlow(null)
    fun viewAction(): StateFlow<Action?> = _viewActions

    private var _viewAction: Action? = null
    protected var viewAction: Action
        get() = _viewAction
            ?: throw UninitializedPropertyAccessException("\"viewAction\" was queried before being initialized")
        set(value) {
            _viewAction = null
            _viewActions.value = null
            _viewAction = value
            _viewActions.onEach {
                val i = 0
            }
            _viewActions.value = value
        }

    abstract fun dispatch(msg: Msg)
}