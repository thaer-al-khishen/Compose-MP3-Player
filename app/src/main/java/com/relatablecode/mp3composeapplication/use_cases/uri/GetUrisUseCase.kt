package com.relatablecode.mp3composeapplication.use_cases.uri

import com.relatablecode.mp3composeapplication.repository.UriRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetUrisUseCase @Inject constructor(
    private val uriRepository: UriRepository
) {

    operator fun invoke(): Flow<Set<String>> {
        return uriRepository.urisFlow
    }

}
