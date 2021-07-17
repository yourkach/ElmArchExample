package com.ybond.elmtest.presentation.timer.store

import com.ybond.elm_core.Reducer
import com.ybond.elm_core.ReducerResult
import com.ybond.elmtest.presentation.timer.*

class TimerReducer :
    Reducer<TimerScreenState, TimerUiEvent, TimerCommand, TimerInternalEvent, TimerEffect> {

    override fun reduceByUiEvent(
        state: TimerScreenState,
        event: TimerUiEvent
    ): ReducerResult<TimerScreenState, TimerEffect, TimerCommand> {
        return when (event) {
            TimerUiEvent.StopButtonClick -> {
                ReducerResult(
                    state = state,
                    effects = emptyList(),
                    commands = listOf(TimerCommand.ResetTimer)
                )
            }
            TimerUiEvent.ToggleButtonClick -> {
                ReducerResult(
                    state = state,
                    effects = emptyList(),
                    commands = listOf(TimerCommand.ToggleTimer)
                )
            }
        }
    }

    override fun reduceByInternalEvent(
        state: TimerScreenState,
        event: TimerInternalEvent
    ): ReducerResult<TimerScreenState, TimerEffect, TimerCommand> {
        return when (event) {
            TimerInternalEvent.ResetTimer -> {
                ReducerResult(
                    state = state.copy(
                        timeState = TimeState.Initial,
                        toggleButtonState = ToggleTimerButtonState.Start,
                        isStopButtonVisible = false
                    ),
                    effects = listOf(TimerEffect.Message.TimerReset),
                    commands = emptyList()
                )
            }
            is TimerInternalEvent.StartTimeUpdate -> {
                return ReducerResult(
                    state = state.copy(
                        timeState = TimeState.Running(startTimestampMillis = event.startTimestampMillis),
                        toggleButtonState = ToggleTimerButtonState.Pause,
                        isStopButtonVisible = true
                    ),
                    effects = listOf(TimerEffect.Message.TimerStart),
                    commands = emptyList()
                )
            }
            is TimerInternalEvent.TimerPauseEvent -> {
                return ReducerResult(
                    state = state.copy(
                        timeState = TimeState.Paused(timeElapsedMillis = event.timeElapsedMillis),
                        toggleButtonState = ToggleTimerButtonState.Pause,
                        isStopButtonVisible = true
                    ),
                    effects = emptyList(),
                    commands = emptyList()
                )
            }
        }
    }

}
