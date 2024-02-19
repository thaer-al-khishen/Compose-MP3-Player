package com.relatablecode.mp3composeapplication.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemesViewModel : ViewModel() {

    private val themes = listOf(AppTheme.default(), AppTheme.blue(), AppTheme.red())
    private var currentThemeIndex = 0 // Keep track of the current theme index

    private val _currentTheme = MutableStateFlow(themes[currentThemeIndex])
    val currentTheme: StateFlow<AppTheme> = _currentTheme

    fun switchToNextTheme() {
        // Increment the index to switch to the next theme
        currentThemeIndex = (currentThemeIndex + 1) % themes.size
        _currentTheme.value = themes[currentThemeIndex]
    }

    fun switchToPreviousTheme() {
        // Decrement the index to switch to the previous theme, handling circular navigation
        currentThemeIndex = if (currentThemeIndex - 1 < 0) themes.size - 1 else currentThemeIndex - 1
        _currentTheme.value = themes[currentThemeIndex]
    }

}
