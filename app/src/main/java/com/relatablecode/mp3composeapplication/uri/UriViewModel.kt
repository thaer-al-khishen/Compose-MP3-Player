package com.relatablecode.mp3composeapplication.uri

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.relatablecode.mp3composeapplication.event.UriEvent
import com.relatablecode.mp3composeapplication.use_cases.uri.UriUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UriViewModel @Inject constructor(private val uriUseCases: UriUseCases): ViewModel() {

    private val _uris = MutableStateFlow<Set<Uri>>(emptySet())
    val uris: StateFlow<Set<Uri>> = _uris.asStateFlow()

    init {
        collectUris()
    }

    private fun collectUris() {
        viewModelScope.launch {
            uriUseCases.getUrisUseCase().collect { uriStrings ->
                _uris.value = uriStrings.map { Uri.parse(it) }.toSet()
            }
        }
    }

    fun onEvent(uriEvent: UriEvent) {
        when(uriEvent) {
            is UriEvent.SaveUriEvent -> {
                saveUri(uriEvent.uri)
            }
            is UriEvent.SaveUrisEvent -> {
                saveUris(uriEvent.uris)
            }
        }
    }

    private fun saveUri(uri: Uri) {
        viewModelScope.launch {
            uriUseCases.saveUriUseCase(uri)
        }
    }

    //Created to save multiple uris when the user wants to import several mp3 files at once
    private fun saveUris(uris: List<Uri>) {
        viewModelScope.launch {
            uriUseCases.saveUrisUseCase(uris)
        }
    }

    private fun deleteUri(uri: Uri) {
        viewModelScope.launch {
            uriUseCases.deleteUriUseCase(uri)
        }
    }

}
