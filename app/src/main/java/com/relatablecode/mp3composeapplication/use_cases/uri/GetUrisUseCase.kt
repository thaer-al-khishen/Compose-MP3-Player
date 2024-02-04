package com.relatablecode.mp3composeapplication.use_cases.uri

import com.relatablecode.mp3composeapplication.repository.UriRepository
import kotlinx.coroutines.flow.Flow

class GetUrisUseCase(
    private val uriRepository: UriRepository
) {

    operator fun invoke(): Flow<Set<String>> {
        return uriRepository.urisFlow
    }

}
