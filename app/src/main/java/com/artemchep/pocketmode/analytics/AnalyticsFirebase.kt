package com.artemchep.pocketmode.analytics

import android.os.Build
import androidx.core.os.bundleOf
import com.artemchep.pocketmode.models.Proximity
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * @author Artem Chepurnoy
 */
class AnalyticsFirebase(
    private val analytics: FirebaseAnalytics
) : Analytics {

    private val deviceInfo by lazy { "device" to Build.MODEL }

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