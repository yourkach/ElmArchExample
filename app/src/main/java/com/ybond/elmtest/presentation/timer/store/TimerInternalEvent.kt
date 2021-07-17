package com.ybond.elmtest.presentation.timer.store

sealed class TimerInternalEvent {
    data class StartTimeUpdate(val startTimestampMillis: Long) : TimerInternalEvent()
    data class TimerPauseEvent(val timeElapsedMillis: Long) : TimerInternalEvent()
    object ResetTimer : TimerInternalEvent()
}

