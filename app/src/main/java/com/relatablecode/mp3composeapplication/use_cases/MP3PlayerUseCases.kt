package com.relatablecode.mp3composeapplication.use_cases

import com.relatablecode.mp3composeapplication.use_cases.controls.FastForwardButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.MenuButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.MiddleButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.PlayPauseButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.controls.RewindButtonClickedUseCase
import com.relatablecode.mp3composeapplication.use_cases.general.NavigateToMusicListUseCase
import com.relatablecode.mp3composeapplication.use_cases.general.UpdateMp3ItemsUseCase
import com.relatablecode.mp3composeapplication.use_cases.uri.DeleteUriUseCase
import com.relatablecode.mp3composeapplication.use_cases.uri.GetUrisUseCase
import com.relatablecode.mp3composeapplication.use_cases.uri.SaveUriUseCase
import com.relatablecode.mp3composeapplication.use_cases.uri.SaveUrisUseCase
import javax.inject.Inject

data class MP3PlayerUseCases @Inject constructor(
    val getUrisUseCase: GetUrisUseCase,
    val saveUriUseCase: SaveUriUseCase,
    val saveUrisUseCase: SaveUrisUseCase,
    val deleteUriUseCase: DeleteUriUseCase,
    val menuButtonClickedUseCase: MenuButtonClickedUseCase,
    val rewindButtonClickedUseCase: RewindButtonClickedUseCase,
    val fastForwardButtonClickedUseCase: FastForwardButtonClickedUseCase,
    val playPauseButtonClickedUseCase: PlayPauseButtonClickedUseCase,
    val middleButtonClickedUseCase: MiddleButtonClickedUseCase,
    val updateMp3ItemsUseCase: UpdateMp3ItemsUseCase,
    val navigateToMusicListUseCase: NavigateToMusicListUseCase
)
