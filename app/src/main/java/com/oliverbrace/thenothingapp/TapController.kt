package com.oliverbrace.thenothingapp.domain

sealed interface TapResult {
    object ShowAd : TapResult
    object ShowMessage : TapResult
}

class TapController(private val adThreshold: Int = 5) {

    private var count = 0

    fun registerTap(): TapResult {
        count++
        return if (count % adThreshold == 0) TapResult.ShowAd
        else TapResult.ShowMessage
    }
}