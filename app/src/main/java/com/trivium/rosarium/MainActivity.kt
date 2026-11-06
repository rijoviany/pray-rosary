package com.trivium.rosarium

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.trivium.rosarium.ui.screens.MainScreen
import com.trivium.rosarium.ui.theme.RosariumTheme
import com.trivium.rosarium.ui.viewmodel.RosaryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: RosaryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Force portrait orientation
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // Edge-to-edge transparent system bars
        enableEdgeToEdge()

        // Handle initial incoming invite deep link
        handleIncomingIntent(intent)

        setContent {
            val settings by viewModel.settingsState.collectAsState()
            RosariumTheme(darkTheme = settings.isDarkMode) {
                MainScreen(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null) {
            // Check for ?code=... in deep links (e.g. https://journs.web.app/invite?code=8f7b2c9a or rosarium://invite?code=8f7b2c9a)
            val code = uri.getQueryParameter("code")
                ?: uri.lastPathSegment?.takeIf { it != "invite" }
            if (!code.isNullOrBlank()) {
                viewModel.handleIncomingInviteCode(code)
                return
            }
        }
        val directCode = intent?.getStringExtra("invite_code")
        if (!directCode.isNullOrBlank()) {
            viewModel.handleIncomingInviteCode(directCode)
        }
    }
}
