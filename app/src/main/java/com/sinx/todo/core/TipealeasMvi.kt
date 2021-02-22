package com.sinx.todo.core

import kotlinx.coroutines.CoroutineScope

typealias Next<K, T> = (K) -> T

typealias ModelWithEffect<Model, Msg, Action> = Pair<Model, (suspend CoroutineScope.(suspend (Command<Msg, Action>) -> Unit) -> Unit)>

typealias Update<Model, Msg, Action> = (Model, Msg) -> ModelWithEffect<Model, Msg, Action>

typealias ViewState<Model, ViewState> = Next<Model, ViewState>

typealias Init<Model, Msg, Action> =  () -> ModelWithEffect<Model, Msg, Action>

fun <Msg, Action> none() : (suspend CoroutineScope.(suspend (Command<Msg, Action>) -> Unit) -> Unit) = {}

sealed class Command<Msg, Action>(private val msg: Msg?, private val action: Action?) {

    class ActionCommand<Msg, Action>(private val action: Action) :
        Command<Msg, Action>(null, action) {
        operator fun invoke() = action
    }

    class MsgCommand<Msg, Action>(private val msg: Msg) : Command<Msg, Action>(msg, null) {
        operator fun invoke() = msg
    }
}