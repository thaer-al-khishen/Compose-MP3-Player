package com.relatablecode.mp3composeapplication.use_cases.uri

import android.net.Uri
import com.relatablecode.mp3composeapplication.repository.UriRepository

class SaveUriUseCase(
    private val uriRepository: UriRepository
) {
    suspend operator fun invoke(uri: Uri) {
        uriRepository.saveUri(uri)
    }
}
