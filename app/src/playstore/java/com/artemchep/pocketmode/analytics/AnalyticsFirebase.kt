package com.artemchep.pocketmode.analytics

import android.app.Application
import android.os.Build
import androidx.core.os.bundleOf
import com.artemchep.pocketmode.models.Proximity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

fun createAnalytics(application: Application): Analytics =
    AnalyticsFirebase(application)

/**
 * @author Artem Chepurnoy
 */
private class AnalyticsFirebase(
    private val application: Application
) : Analytics {

    private val deviceInfo by lazy { "device" to Build.MODEL }

    private var analytics: FirebaseAnalytics? = null
        get() = field ?: try {
            FirebaseAnalytics.getInstance(application)
                .also {
                    field = it
                }
        } catch (e: java.lang.Exception) {
            null
        }

    private var crashlytics: FirebaseCrashlytics? = null
        get() = field ?: try {
            FirebaseCrashlytics.getInstance()
                .also {
                    field = it
                }
        } catch (e: java.lang.Exception) {
            null
        }

    override fun log(message: String) {
        crashlytics?.log(message)
    }

    override fun logPocketModeTriggered() {
        val bundle = bundleOf(deviceInfo)
        analytics?.logEvent(AnalyticsEvent.POCKET_MODE_TRIGGERED.name, bundle)
    }

    override fun logPocketModeLocked() {
        val bundle = bundleOf(deviceInfo)
        analytics?.logEvent(AnalyticsEvent.POCKET_MODE_LOCKED.name, bundle)
    }

    override fun logTestProximitySensorChange(proximity: Proximity) {
        val bundle = bundleOf(deviceInfo)
        analytics?.logEvent(AnalyticsEvent.PROXIMITY_TEST.name, bundle)
    }

}
