package com.ybond.elmtest.presentation.timer

data class TimerScreenState(
    val timeState: TimeState,
    val toggleButtonState: ToggleTimerButtonState,
    val isStopButtonVisible: Boolean
)

sealed class TimeState {
    object Initial : TimeState()
    data class Running(val startTimestampMillis: Long) : TimeState()
    data class Paused(val timeElapsedMillis: Long) : TimeState()
}

enum class ToggleTimerButtonState {
    Start, Pause
}

