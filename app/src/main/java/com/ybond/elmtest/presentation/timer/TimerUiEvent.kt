package com.ybond.elmtest.presentation.timer

import android.widget.ToggleButton

sealed class TimerUiEvent {
    object ToggleButtonClick : TimerUiEvent()
    object StopButtonClick : TimerUiEvent()
}
