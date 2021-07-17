package com.ybond.elmtest.presentation.timer

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.ybond.elmtest.R
import com.ybond.elmtest.viewModelFactory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class TimerFragment : Fragment(R.layout.fragment_timer) {

    private val viewModel: TimerViewModel by viewModelFactory { TimerViewModel() }

    private var timerTextView: TextView? = null
    private var stopButton: Button? = null
    private var toggleButton: Button? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        timerTextView = view.findViewById(R.id.tvTimeText)
        stopButton = view.findViewById(R.id.btnStopTimer)
        toggleButton = view.findViewById(R.id.btnToggleTimer)
        setListeners()
        lifecycleScope.launch {
            viewModel.stateFlow.collect { it.render() }
        }
        lifecycleScope.launch {
            viewModel.effectsFlow.collect { /*TODO()*/ }
        }
    }

    private fun setListeners() {
        stopButton?.setOnClickListener { viewModel.obtainEvent(TimerUiEvent.StopButtonClick) }
        toggleButton?.setOnClickListener { viewModel.obtainEvent(TimerUiEvent.ToggleButtonClick) }
    }

    override fun onDestroyView() {
        timerTextView = null
        stopButton = null
        toggleButton = null
        super.onDestroyView()
    }

    private fun TimerScreenState.render() {
        stopButton?.updateVisibility(isVisible = isStopButtonVisible)
        toggleButton?.updateText(
            text = when (toggleButtonState) {
                ToggleTimerButtonState.Start -> getString(R.string.start)
                ToggleTimerButtonState.Pause -> getString(R.string.pause)
            }
        )
        timerTextView?.updateText(
            text = when (timeState) {
                is TimeState.Paused -> {
                    stopUpdateTimeJob()
                    timeState.timeElapsedMillis.formatAsTimeText()
                }
                is TimeState.Running -> {
                    restartUpdateTimeJob(timeState.startTimestampMillis)
                    val elapsedMillis = System.currentTimeMillis() - timeState.startTimestampMillis
                    elapsedMillis.formatAsTimeText()
                }
                TimeState.Initial -> {
                    stopUpdateTimeJob()
                    0L.formatAsTimeText()
                }
            }
        )
    }

    private var updateTimeJob: Job? = null
    private fun restartUpdateTimeJob(timerStartMillis: Long) {
        updateTimeJob?.cancel()
        updateTimeJob = lifecycleScope.launch {
            while (true) {
                val elapsedMillis = System.currentTimeMillis() - timerStartMillis
                timerTextView?.updateText(text = elapsedMillis.formatAsTimeText())
                delay(timeMillis = 10)
            }
        }
    }

    private fun stopUpdateTimeJob() {
        updateTimeJob?.cancel()
    }
}

val twoZeroDigitsFormat = DecimalFormat("00")
val threeDigitsFormat = DecimalFormat("000")
fun Long.formatAsTimeText(): String {
    val millis = this % 1000
    val seconds = this / 1000 % 60
    val minutes = this / 1000 / 60
    return "$minutes:${twoZeroDigitsFormat.format(seconds)}.${threeDigitsFormat.format(millis)}"
}

fun View.updateVisibility(isVisible: Boolean) {
    if (this.isVisible != isVisible) this.isVisible = isVisible
}

fun TextView.updateText(text: CharSequence) {
    if (this.text != text) setText(text)
}
