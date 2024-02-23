package com.relatablecode.mp3composeapplication.playback_screen.bottom_section

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.relatablecode.mp3composeapplication.theme.LocalAppTheme

@Composable
fun MP3PlayerSelectionIcon(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    selectedImage: Int,
    unSelectedImage: Int,
    contentDescription: String,
) {
    val iconId = if (isSelected) {
        selectedImage
    } else {
        unSelectedImage
    }

    val tint = if (isSelected) {
        LocalAppTheme.current.selectedImageColor
    } else {
        LocalAppTheme.current.unSelectedImageColor
    }

    Icon(
        painter = painterResource(id = iconId),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.then(
            Modifier.size(24.dp)
        )
    )

}
