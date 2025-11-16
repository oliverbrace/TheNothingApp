package com.oliverbrace.thenothingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.oliverbrace.thenothingapp.ad.InterstitialAdManager
import com.oliverbrace.thenothingapp.ui.NothingScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NothingViewModel
    private lateinit var adManager: InterstitialAdManager

    private var canTap by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = NothingViewModel()
        adManager = InterstitialAdManager(this)

        setContent {
            val message by viewModel.message.collectAsState()

            NothingScreen(
                text = message,
                canTap = canTap,
                onTap = { viewModel.onTap() }
            )

            LaunchedEffect(Unit) {
                launch {
                    viewModel.showAd.collect {
                        canTap = false
                        adManager.showAd()
                    }
                }

                launch {
                    adManager.adClosedEvent.collect {
                        // Re-enable tap
                        canTap = true
                        // Tell viewModel to show the next message
                        viewModel.showPostAdMessage()
                    }
                }
            }
        }
    }
}