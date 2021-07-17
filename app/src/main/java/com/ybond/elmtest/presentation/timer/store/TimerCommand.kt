package com.ybond.elmtest.presentation.timer.store

sealed class TimerCommand {
    object ToggleTimer : TimerCommand()
    object ResetTimer : TimerCommand()
}