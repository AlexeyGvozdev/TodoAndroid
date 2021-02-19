package com.sinx.todo.core

import kotlinx.coroutines.CoroutineScope

typealias Effect<K, T> = (K) -> T

typealias Update<Model, Msg, Action> = (Model, Msg) -> Pair<Model, (suspend CoroutineScope.(suspend (Command<Msg, Action>) -> Unit) -> Unit)>

typealias ViewState<Model, ViewState> = Effect<Model, ViewState>

typealias Init<Model, Msg, Action> =  (Model?) -> Pair<Model, (suspend CoroutineScope.(suspend (Command<Msg, Action>) -> Unit) -> Unit)>

sealed class Command<Msg, Action>(val msg: Msg?, val action: Action?) {

    class ActionCommand<Msg, Action>(action: Action) :
        Command<Msg, Action>(null, action) {
        operator fun invoke() = action
    }

    class MsgCommand<Msg, Action>(msg: Msg) : Command<Msg, Action>(msg, null) {
        operator fun invoke() = msg
    }
}