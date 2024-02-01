package com.relatablecode.mp3composeapplication.black_screen.bottom_section

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

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

    Image(
        painter = painterResource(id = iconId),
        contentDescription = contentDescription,
        modifier = modifier.then(
            Modifier.size(24.dp)
        )
    )

}
