package com.oliverbrace.thenothingapp.ad

import android.app.Activity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class InterstitialAdManager(activity: Activity) {

    // Weak reference to avoid leaking the activity
    private val activityRef = WeakReference(activity)

    private var interstitialAd: InterstitialAd? = null
    private var adPendingToShow = false

    // Flow that notifies when an ad has been dismissed
    val adClosedEvent = MutableSharedFlow<Unit>()

    // Optional: Flow if you want to signal "ad ready to show"
    val showAdEvent = MutableSharedFlow<Unit>()

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    init {
        MobileAds.initialize(activity)
        loadAd()
    }

    fun loadAd() {
        val activity = activityRef.get() ?: return
        val request = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            "ca-app-pub-4651392825591048/3036159231", // test ad
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
        val activity = activityRef.get()
        if (ad != null && activity != null) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    coroutineScope.launch { adClosedEvent.emit(Unit) }
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
