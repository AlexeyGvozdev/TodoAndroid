package com.sinx.todo.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sinx.todo.base.TeaVM
import com.sinx.todo.base.Tea

class ViewModelProvideFactory<Model: Any, View: Any, Action: Any, Msg: Any>(private val feature: Tea<Model, View, Action, Msg>) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeaVM::class.java)) {
            return TeaVM(feature) as T
        }
        throw IllegalStateException("")
    }
}