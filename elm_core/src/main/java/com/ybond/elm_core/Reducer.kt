package com.ybond.elm_core

interface Reducer<TState, TUiEvent,TCommand, TInternalEvent,  TEffect> {

    fun reduceByUiEvent(
        state: TState,
        event: TUiEvent
    ): ReducerResult<TState, TEffect, TCommand>

    fun reduceByInternalEvent(
        state: TState,
        event: TInternalEvent
    ): ReducerResult<TState, TEffect, TCommand>

}

data class ReducerResult<TState, TEffect, TCommand>(
    val state: TState,
    val effects: List<TEffect>,
    val commands: List<TCommand>
)