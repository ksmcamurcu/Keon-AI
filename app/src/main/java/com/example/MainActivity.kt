package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.KeonMainScreen
import com.example.ui.KeonViewModel
import com.example.ui.theme.KeonTheme
import com.example.widget.KeonChatWidgetProvider


import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.DisposableEffect
import com.example.ui.components.AuthScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkIntentForExtras(intent)

        setContent {
            val viewModel: KeonViewModel = viewModel()
            // KeonTheme configuration was here, let's keep it
            val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()
            val selectedNeonAccent by viewModel.selectedNeonAccent.collectAsStateWithLifecycle()

            KeonTheme(
                activeTheme = selectedTheme,
                activeNeonAccent = selectedNeonAccent
            ) {
                AppContent(viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkIntentForExtras(intent)
    }

    private fun checkIntentForExtras(intent: Intent?) {
        if (intent == null) return
        val action = intent.action
        // ... (Keep existing implementation of checkIntentForExtras)
    }
}

@Composable
fun AppContent(viewModel: KeonViewModel) {
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }
    var isSkipped by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(listener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(listener)
        }
    }

    if (currentUser == null && !isSkipped) {
        AuthScreen(
            onAuthSuccess = { currentUser = FirebaseAuth.getInstance().currentUser },
            onAuthError = { /* Handle error */ },
            onSkip = { isSkipped = true }
        )
    } else {
        KeonMainScreen(viewModel = viewModel)
    }
}

