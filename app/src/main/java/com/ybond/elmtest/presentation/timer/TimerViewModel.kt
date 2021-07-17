package com.ybond.elmtest.presentation.timer

import com.ybond.elm_core.BaseVMStore
import com.ybond.elmtest.presentation.timer.store.TimerCommand
import com.ybond.elmtest.presentation.timer.store.TimerInternalEvent
import com.ybond.elmtest.presentation.timer.store.TimerStore

class TimerViewModel : BaseVMStore<TimerScreenState, TimerUiEvent, TimerCommand, TimerInternalEvent, TimerEffect>(
    storeImpl = TimerStore()
)