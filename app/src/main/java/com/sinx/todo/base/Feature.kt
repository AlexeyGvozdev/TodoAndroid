package com.sinx.todo.base

import com.sinx.todo.core.Init
import com.sinx.todo.core.Update
import com.sinx.todo.core.ViewState

class Feature<Model, View, Action, Msg>(
    val init: Init<Model, Msg, Action>,
    val update: Update<Model, Msg, Action>,
    val viewState: ViewState<Model, View>
)