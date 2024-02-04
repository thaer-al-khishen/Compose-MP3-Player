package com.relatablecode.mp3composeapplication.use_cases.uri

import android.net.Uri
import com.relatablecode.mp3composeapplication.repository.UriRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaveUrisUseCase @Inject constructor(
    private val uriRepository: UriRepository
) {
    //Created to save multiple uris when the user wants to import several mp3 files at once
    suspend operator fun invoke(uris: List<Uri>) {
        uriRepository.saveUris(uris)
    }

}
