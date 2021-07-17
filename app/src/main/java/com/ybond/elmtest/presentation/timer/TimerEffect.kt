package com.ybond.elmtest.presentation.timer

sealed class TimerEffect {
    sealed class Message : TimerEffect() {
        object TimerStart : Message()
        object TimerReset : Message()
        data class InfoMessage(val infoText: String) : Message()
    }
}

