package com.ybond.elmtest.presentation.timer.store

import com.ybond.elm_core.BaseStore
import com.ybond.elmtest.presentation.timer.*

class TimerStore : BaseStore<TimerScreenState, TimerUiEvent, TimerCommand, TimerInternalEvent, TimerEffect>(
    actor = TimerActor(),
    reducer = TimerReducer(),
    initialState = createInitialState()
) {

    companion object {
        private fun createInitialState(): TimerScreenState = TimerScreenState(
            timeState = TimeState.Initial,
            toggleButtonState = ToggleTimerButtonState.Start,
            isStopButtonVisible = false
        )
    }

}

