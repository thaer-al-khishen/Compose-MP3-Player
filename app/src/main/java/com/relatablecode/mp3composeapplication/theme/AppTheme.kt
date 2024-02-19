package com.relatablecode.mp3composeapplication.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppTheme(
    val primaryColor: Color,
    val secondaryColor: Color,
    val themeColorName: String,
    val playbackScreenColor: Color,
    val playbackScreenBorderColor: Color,
    val playbackScreenMiddleImageColor: Color,
    val playbackScreenContentColor: Color,
    val selectedImageColor: Color,
    val unSelectedImageColor: Color,
    val playbackButtonColor: Color,
    val playbackTextColor: Color,
    val playbackOuterCircleColor: Color,
    val playbackOuterCircleBorderColor: Color,
    val playbackInnerCircleColor: Color,
    val playbackInnerCircleBorderColor: Color
) {
    companion object {

        fun default() = AppTheme(
            primaryColor = Color(0xFFdbdbdb),
            secondaryColor = Color(0xFFd7d7d7),
            themeColorName = "Gray",
            playbackScreenColor = Color.White,
            playbackInnerCircleBorderColor = Color.Black,
            playbackScreenMiddleImageColor = Color.Black,
            playbackScreenContentColor = Color.Black,
            selectedImageColor = Color.Black,
            unSelectedImageColor = Color(0xFFd7d7d7),
            playbackButtonColor = Color.Black,
            playbackTextColor = Color.Black,
            playbackOuterCircleColor = Color.White,
            playbackOuterCircleBorderColor = Color.White,
            playbackInnerCircleColor = Color(0xFFd7d7d7),
            playbackScreenBorderColor = Color(0xFFd7d7d7)
        )

        fun blue() = AppTheme(
            primaryColor = Color(0xFF70c7df),
            secondaryColor = Color(0xFF80cde2),
            themeColorName = "Blue",
            playbackScreenColor = Color.Black,
            playbackInnerCircleBorderColor = Color.Black,
            playbackScreenMiddleImageColor = Color.White,
            playbackScreenContentColor = Color.White,
            selectedImageColor = Color.White,
            unSelectedImageColor = Color(0xFF80cde2),
            playbackButtonColor = Color.Black,
            playbackTextColor = Color.Black,
            playbackOuterCircleColor = Color.White,
            playbackOuterCircleBorderColor = Color.White,
            playbackInnerCircleColor = Color(0xFF80cde2),
            playbackScreenBorderColor = Color(0xFF80cde2)
        )

        fun red() = AppTheme(
            primaryColor = Color(0xFFe92d38),
            secondaryColor = Color(0xFFef4d4d),
            themeColorName = "Red",
            playbackScreenColor = Color.Black,
            playbackInnerCircleBorderColor = Color.Black,
            playbackScreenMiddleImageColor = Color.White,
            playbackScreenContentColor = Color.White,
            selectedImageColor = Color.White,
            unSelectedImageColor = Color(0xFFef4d4d),
            playbackButtonColor = Color.Black,
            playbackTextColor = Color.Black,
            playbackOuterCircleColor = Color.White,
            playbackOuterCircleBorderColor = Color.White,
            playbackInnerCircleColor = Color(0xFFef4d4d),
            playbackScreenBorderColor = Color(0xFFef4d4d)
        )

    }
}

val LocalAppTheme = staticCompositionLocalOf { AppTheme.default() }
