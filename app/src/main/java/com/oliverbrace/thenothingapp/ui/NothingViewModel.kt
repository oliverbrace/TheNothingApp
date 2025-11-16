package com.oliverbrace.thenothingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.oliverbrace.thenothingapp.domain.MessageProvider
import com.oliverbrace.thenothingapp.domain.TapController
import com.oliverbrace.thenothingapp.domain.TapResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NothingViewModel(
    private val messageProvider: MessageProvider = MessageProvider(),
    private val tapController: TapController = TapController()
) : ViewModel() {

    private val _message = MutableStateFlow("Tap for nothing")
    val message: StateFlow<String> = _message

    val showAd = MutableSharedFlow<Unit>()

    fun showPostAdMessage() {
        _message.value = messageProvider.randomMessage()
    }

    fun onTap() {
        when (tapController.registerTap()) {
            is TapResult.ShowMessage -> _message.value = messageProvider.randomMessage()
            is TapResult.ShowAd -> {
                _message.value = "Ad coming up..."
                viewModelScope.launch { showAd.emit(Unit) }
            }
        }
    }
}