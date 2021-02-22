package com.sinx.todo.core

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import com.sinx.todo.base.TeaVM
import com.sinx.todo.base.Tea

@MainThread
inline fun <reified T : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any> Fragment.initViewModel(
    feature: Tea<T, T1, T2, T3>
): Lazy<TeaVM<T, T1, T2, T3>> = createViewModelLazy(TeaVM(feature)::class,
    { this.viewModelStore }, { ViewModelProvideFactory(feature) }
)
