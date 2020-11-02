package com.artemchep.pocketmode.analytics

import android.app.Application
import android.os.Build
import androidx.core.os.bundleOf
import com.artemchep.pocketmode.models.Proximity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

fun createAnalytics(application: Application): Analytics =
    AnalyticsFirebase(FirebaseAnalytics.getInstance(application))

/**
 * @author Artem Chepurnoy
 */
private class AnalyticsFirebase(
    private val analytics: FirebaseAnalytics
) : Analytics {

    private val deviceInfo by lazy { "device" to Build.MODEL }

    override fun log(message: String) {
        FirebaseCrashlytics.getInstance().log(message)
    }

    override fun logPocketModeTriggered() {
        val bundle = bundleOf(deviceInfo)
        analytics.logEvent(AnalyticsEvent.POCKET_MODE_TRIGGERED.name, bundle)
    }

    override fun logPocketModeLocked() {
        val bundle = bundleOf(deviceInfo)
        analytics.logEvent(AnalyticsEvent.POCKET_MODE_LOCKED.name, bundle)
    }

    override fun logTestProximitySensorChange(proximity: Proximity) {
        val bundle = bundleOf(deviceInfo)
        analytics.logEvent(AnalyticsEvent.PROXIMITY_TEST.name, bundle)
    }

}
