package com.ybond.elmtest.presentation.timer.store

import com.ybond.elm_core.Actor

class TimerActor : Actor<TimerCommand, TimerInternalEvent> {

    private var currentTimerState: TimerInnerState = TimerInnerState.Initial

    override suspend fun processCommand(command: TimerCommand): TimerInternalEvent {
        return when (command) {
            TimerCommand.ResetTimer -> resetTimer()
            TimerCommand.ToggleTimer -> toggleTimer()
        }
    }

    private fun resetTimer(): TimerInternalEvent {
        return synchronized(lock = this) {
            currentTimerState = TimerInnerState.Initial
            TimerInternalEvent.ResetTimer
        }
    }

    private fun toggleTimer(): TimerInternalEvent {
        return synchronized(lock = this) {
            when (val state = currentTimerState) {
                TimerInnerState.Initial -> {
                    val currentTimeMillis = System.currentTimeMillis()
                    currentTimerState = TimerInnerState.Running(startTimeMillis = currentTimeMillis)
                    TimerInternalEvent.StartTimeUpdate(startTimestampMillis = currentTimeMillis)
                }
                is TimerInnerState.Paused -> {
                    val newStartTime = System.currentTimeMillis() - state.timeElapsedMillis
                    currentTimerState = TimerInnerState.Running(startTimeMillis = newStartTime)
                    TimerInternalEvent.StartTimeUpdate(startTimestampMillis = newStartTime)
                }
                is TimerInnerState.Running -> {
                    val elapsedMillis = System.currentTimeMillis() - state.startTimeMillis
                    currentTimerState = TimerInnerState.Paused(timeElapsedMillis = elapsedMillis)
                    TimerInternalEvent.TimerPauseEvent(timeElapsedMillis = elapsedMillis)
                }
            }
        }
    }

    private sealed class TimerInnerState {
        object Initial : TimerInnerState()
        data class Running(val startTimeMillis: Long) : TimerInnerState()
        data class Paused(val timeElapsedMillis: Long) : TimerInnerState()
    }

}

