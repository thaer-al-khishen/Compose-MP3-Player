package com.relatablecode.mp3composeapplication

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.relatablecode.mp3composeapplication.mp3_player_device.MP3PlayerDevice

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureStatusBarFullScreenTransparency()

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                MP3PlayerDevice()
            }
        }
    }

    private fun configureStatusBarFullScreenTransparency() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            with(window) {
                // Make the status bar transparent
                statusBarColor = Color.Transparent.toArgb()

                // Request decor fitting system windows to ensure layout stability
                setDecorFitsSystemWindows(false)

                // Set system bar appearance
                val controller = decorView.windowInsetsController
                controller?.apply {
                    // Use LIGHT_STATUS_BAR to ensure dark icons if you have a light background
                    setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }
            }
        } else {
            @Suppress("DEPRECATION")
            with(window) {
                // Make the status bar transparent and content appear under the status bar
                decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                statusBarColor = Color.Transparent.toArgb()

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Ensure status bar icons are visible on light backgrounds
                    decorView.systemUiVisibility =
                        decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                }
            }
        }

        // Handle insets for all versions, adjusting layout to avoid overlap with the status bar
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Here, you can adjust the padding of your views based on the insets
            // Example: view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
//            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }
}
