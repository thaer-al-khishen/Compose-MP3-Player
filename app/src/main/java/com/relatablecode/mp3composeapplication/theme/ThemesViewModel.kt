package com.relatablecode.mp3composeapplication.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.repository.ThemeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemesViewModel @Inject constructor(private val repository: ThemeRepository) : ViewModel() {

    private val themes = listOf(AppTheme.default(), AppTheme.blue(), AppTheme.red())
    private var currentThemeIndex = 0 // Keep track of the current theme index

    private val _currentTheme = MutableStateFlow(themes[currentThemeIndex])
    val currentTheme: StateFlow<AppTheme> = _currentTheme

    init {
        viewModelScope.launch {
            repository.currentThemeIndex.collect { index ->
                currentThemeIndex = index
                _currentTheme.value = themes[currentThemeIndex]
            }
        }

    }

    fun switchToNextTheme() {
        // Increment the index to switch to the next theme
        currentThemeIndex = (currentThemeIndex + 1) % themes.size
        _currentTheme.value = themes[currentThemeIndex]
        viewModelScope.launch {
            repository.saveCurrentThemeIndex(currentThemeIndex)
        }
    }

    fun switchToPreviousTheme() {
        // Decrement the index to switch to the previous theme, handling circular navigation
        currentThemeIndex = if (currentThemeIndex - 1 < 0) themes.size - 1 else currentThemeIndex - 1
        _currentTheme.value = themes[currentThemeIndex]
    }

}
