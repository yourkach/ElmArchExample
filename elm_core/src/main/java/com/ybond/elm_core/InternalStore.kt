package com.ybond.elm_core

import kotlinx.coroutines.flow.Flow

interface InternalStore<TState, TUiEvent, TCommand, TInternalEvent, TEffect> :
    Store<TState, TUiEvent, TEffect> {
    fun dispose()
}

interface Store<TState, TUiEvent, TEffect> {
    val stateFlow: Flow<TState>
    val effectsFlow: Flow<TEffect>
    fun obtainEvent(uiEvent: TUiEvent)
}