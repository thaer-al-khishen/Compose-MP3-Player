package com.relatablecode.mp3composeapplication.use_cases.uri

import android.net.Uri
import com.relatablecode.mp3composeapplication.repository.UriRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteUriUseCase @Inject constructor(
    private val uriRepository: UriRepository
) {

    suspend operator fun invoke(uri: Uri) {
        uriRepository.deleteUri(uri)
    }

}
