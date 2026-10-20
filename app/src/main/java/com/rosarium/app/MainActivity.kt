package com.rosarium.app

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.rosarium.app.ui.screens.PrayerScreen
import com.rosarium.app.ui.theme.RosariumTheme
import com.rosarium.app.ui.viewmodel.RosaryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RosaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Force portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Edge-to-edge transparent system bars
        enableEdgeToEdge()

        setContent {
            RosariumTheme {
                PrayerScreen(viewModel = viewModel)
            }
        }
    }
}
