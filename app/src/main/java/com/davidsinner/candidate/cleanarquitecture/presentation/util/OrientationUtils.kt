package com.davidsinner.candidate.cleanarquitecture.presentation.util


import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Represents the current window orientation.
 */
enum class OrientationScreen {
    Portrait,
    Landscape
}

/**
 * A Composable function that remembers and provides the current window orientation.
 * It recomposes when the configuration changes (e.g., device rotation).
 *
 * @return The current [Orientation] (Portrait or Landscape).
 */
@Composable
fun rememberWindowOrientation(): OrientationScreen {
    val configuration = LocalConfiguration.current
    var currentOrientation by remember { mutableIntStateOf(configuration.orientation) }

    LocalConfiguration.current.also {
        if (it.orientation != currentOrientation) {
            currentOrientation = it.orientation
        }
    }

    return if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
        OrientationScreen.Landscape
    } else {
        OrientationScreen.Portrait
    }
}