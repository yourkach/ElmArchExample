package com.ybond.elm_core

interface Actor<TCommand, TInternalEvent> {

    suspend fun processCommand(command: TCommand): TInternalEvent

}

