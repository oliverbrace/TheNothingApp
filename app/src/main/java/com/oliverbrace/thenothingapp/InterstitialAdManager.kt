package com.oliverbrace.thenothingapp.ad

import android.app.Activity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.flow.MutableSharedFlow

class InterstitialAdManager(private val activity: Activity) {

    private var interstitialAd: InterstitialAd? = null
    private var adPendingToShow = false
    val adClosedEvent = MutableSharedFlow<Unit>()
    val showAdEvent = MutableSharedFlow<Unit>()

    init {
        MobileAds.initialize(activity)
        loadAd()
    }

    fun loadAd() {
        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            "ca-app-pub-3940256099942544/1033173712", // test ad
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    if (adPendingToShow) {
                        adPendingToShow = false
                        showAd()
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    adPendingToShow = false
                }
            }
        )
    }

    fun showAd() {
        val ad = interstitialAd
        if (ad != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    interstitialAd = null
                    loadAd()
                }
            }
            ad.show(activity)
        } else {
            adPendingToShow = true
        }
    }
}