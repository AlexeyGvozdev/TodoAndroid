package com.sinx.todo.core

typealias Effect<K, T> = (K) -> T

typealias Update<Model, Msg> = (Model, Msg) -> Model

typealias ViewState<Model, ViewState> = Effect<Model, ViewState>
