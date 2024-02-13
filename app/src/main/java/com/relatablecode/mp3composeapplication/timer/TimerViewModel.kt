package com.relatablecode.mp3composeapplication.timer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {

    private val _timer = MutableStateFlow(0L) // Milliseconds
    val timer = _timer.asStateFlow()

    private val _duration = MutableStateFlow(0L) // Total duration of 60,000 milliseconds (1 minute)
    val duration = _duration.asStateFlow()

    private var timerJob: Job? = null
    private var elapsedTime = 0L // Preserve elapsed time when paused

    fun setTimerDuration(duration: Long) {
        viewModelScope.launch {
            _duration.value = duration
        }
    }

    fun startOrResumeTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - elapsedTime
            while (true) {
                val currentTime = System.currentTimeMillis()
                _timer.value = (currentTime - startTime)
                delay(100) // Update every 100 milliseconds
            }
        }
    }

    fun pauseTimer() {
        elapsedTime = _timer.value // Save elapsed time on pause
        timerJob?.cancel()
    }

    fun stopTimer() {
        _timer.value = 0
        elapsedTime = 0 // Reset elapsed time on stop
        timerJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

}
