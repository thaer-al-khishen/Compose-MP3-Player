package com.relatablecode.mp3composeapplication.use_cases.uri

import javax.inject.Inject

data class UriUseCases @Inject constructor(
    val getUrisUseCase: GetUrisUseCase,
    val saveUriUseCase: SaveUriUseCase,
    val saveUrisUseCase: SaveUrisUseCase,
    val deleteUriUseCase: DeleteUriUseCase
)
