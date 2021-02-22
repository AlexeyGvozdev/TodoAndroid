package com.sinx.todo.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore

class TeaViewModelProvider(val store: ViewModelStore, factory: Factory) :
    ViewModelProvider(store, factory) {

}