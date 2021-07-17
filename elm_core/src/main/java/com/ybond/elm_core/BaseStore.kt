package com.ybond.elm_core

import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlin.coroutines.CoroutineContext

abstract class BaseStore<TState, TUiEvent, TCommand, TInternalEvent, TEffect>(
    private val actor: Actor<TCommand, TInternalEvent>,
    private val reducer: Reducer<TState, TUiEvent, TCommand, TInternalEvent, TEffect>,
    initialState: TState,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    private val logError: ((context: CoroutineContext, throwable: Throwable) -> Unit)? = null
) : InternalStore<TState, TUiEvent, TCommand, TInternalEvent, TEffect> {

    constructor(
        actor: Actor<TCommand, TInternalEvent>,
        reducer: Reducer<TState, TUiEvent, TCommand, TInternalEvent, TEffect>,
        createInitialState: () -> TState,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onError: ((context: CoroutineContext, throwable: Throwable) -> Unit)? = null
    ) : this(
        actor = actor,
        reducer = reducer,
        initialState = createInitialState(),
        dispatcher = dispatcher,
        logError = onError
    )

    private val coroutineScope: CoroutineScope = CoroutineScope(
        context = dispatcher + SupervisorJob() + CoroutineExceptionHandler(::onCoroutineError)
    )

    private val runningCommandJobsMap = mutableMapOf<String, Job>()


    private val mutableEffectsFlow: MutableSharedFlow<TEffect> = MutableSharedFlow()
    private val mutableStateFlow: MutableStateFlow<TState> = MutableStateFlow(initialState)

    private val reducerResultsFlow = MutableSharedFlow<ReducerResult<TState, TEffect, TCommand>>()

    final override val effectsFlow: Flow<TEffect>
        get() = mutableEffectsFlow
    final override val stateFlow: Flow<TState>
        get() = mutableStateFlow

    private val currentState: TState
        get() = mutableStateFlow.value

    init {
        coroutineScope.launch {
            reducerResultsFlow.collect { processReducerResult(it) }
        }
    }

    final override fun obtainEvent(uiEvent: TUiEvent) {
        uiEvent.reduceWith(reducer::reduceByUiEvent)
    }

    private fun obtainInternalEvent(internalEvent: TInternalEvent) {
        internalEvent.reduceWith(reducer::reduceByInternalEvent)
    }

    @Synchronized
    private fun <T> T.reduceWith(reduceFun: (TState, T) -> ReducerResult<TState, TEffect, TCommand>) {
        val reducerResult = reduceFun(currentState, this)
        mutableStateFlow.value = reducerResult.state
        coroutineScope.launch { reducerResultsFlow.emit(reducerResult) }
    }

    private fun processReducerResult(result: ReducerResult<TState, TEffect, TCommand>) {
        with(result) {
            if (effects.isNotEmpty()) coroutineScope.launch {
                effects.forEach { effect -> mutableEffectsFlow.emit(effect) }
            }
            if (commands.isNotEmpty()) coroutineScope.launch {
                commands.forEach { command ->
                    val job = launch { actor.processCommand(command).let(::obtainInternalEvent) }
                    (command as? ReplaceLaunchCommand)?.replaceRunningCommand(job)
                }
            }
        }
    }

    private fun ReplaceLaunchCommand.replaceRunningCommand(job: Job) {
        synchronized(runningCommandJobsMap) {
            runningCommandJobsMap[commandId]?.cancel()
            runningCommandJobsMap[commandId] = job
        }
    }

    final override fun dispose() {
        onDispose()
        coroutineScope.cancel()
    }

    protected open fun onDispose() {}

    /** Maps throwable received by [Actor.processCommand] execution to [TInternalEvent]
     *
     * @return - [TInternalEvent] if need to handle error
     * - null if need to ignore error
     * */
    open fun Throwable.toInternalEvent(): TInternalEvent? = null

    private fun onCoroutineError(context: CoroutineContext, throwable: Throwable) {
        if (logError?.invoke(context, throwable) != Unit) {
            Log.e(this::class.java.simpleName, "An error occurred", throwable)
        }
        val internalEvent = throwable.toInternalEvent() ?: return
        obtainInternalEvent(internalEvent)
    }

}
