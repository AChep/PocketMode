package com.artemchep.pocketmode

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import com.artemchep.config.Config
import com.artemchep.pocketmode.analytics.Analytics
import com.artemchep.pocketmode.analytics.AnalyticsFirebase
import com.artemchep.pocketmode.analytics.AnalyticsHolder
import com.artemchep.pocketmode.analytics.AnalyticsStub
import com.artemchep.pocketmode.services.PocketService
import com.google.firebase.analytics.FirebaseAnalytics
import org.solovyev.android.checkout.Billing

/**
 * @author Artem Chepurnoy
 */
class Heart : Application() {
    private val cfgObserver = object : Config.OnConfigChangedListener<String> {
        override fun onConfigChanged(keys: Set<String>) {
            if (Cfg.KEY_ENABLED in keys) {
                // Start or stop the service depending on the
                // config.
                val shouldBeEnabled = Cfg.isEnabled
                if (shouldBeEnabled) {
                    startForegroundService(pocketServiceIntent)
                }
                // Else service should monitor the config and
                // kill itself.
            }
        }
    }

    val pocketServiceIntent: Intent
        get() = Intent(this@Heart, PocketService::class.java)

    val billing = Billing(this, object : Billing.DefaultConfiguration() {
        override fun getPublicKey(): String {
            return BuildConfig.MY_LICENSE_KEY
        }
    })

    val analytics: Analytics by lazy {
        if (Cfg.analytics) {
            AnalyticsFirebase(FirebaseAnalytics.getInstance(this))
        } else AnalyticsStub()
    }

    override fun onCreate() {
        super.onCreate()
        Cfg.init(this)
        Cfg.observe(cfgObserver)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                if (activity is AnalyticsHolder) {
                    activity.analytics = analytics
                }
            }
        })

        // Start the service.
        if (Cfg.isEnabled) {
            startForegroundService(pocketServiceIntent)
        }
    }
}
