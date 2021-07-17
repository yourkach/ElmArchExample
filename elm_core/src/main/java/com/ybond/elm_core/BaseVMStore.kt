package com.ybond.elm_core

import androidx.lifecycle.ViewModel

abstract class BaseVMStore<TState, TUiEvent, TCommand, TInternalEvent, TEffect>(
    private val storeImpl: BaseStore<TState, TUiEvent, TCommand, TInternalEvent, TEffect>,
) : ViewModel(), Store<TState, TUiEvent, TEffect> by storeImpl {

    constructor(
        createStore: () -> BaseStore<TState, TUiEvent, TCommand, TInternalEvent, TEffect>,
    ) : this(storeImpl = createStore())

    open fun onClear() {}

    final override fun onCleared() {
        storeImpl.dispose()
        onClear()
        super.onCleared()
    }

}