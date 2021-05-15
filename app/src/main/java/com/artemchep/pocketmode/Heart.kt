package com.artemchep.pocketmode

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.artemchep.config.Config
import com.artemchep.pocketmode.analytics.Analytics
import com.artemchep.pocketmode.analytics.AnalyticsHolder
import com.artemchep.pocketmode.analytics.AnalyticsStub
import com.artemchep.pocketmode.analytics.createAnalytics
import com.artemchep.pocketmode.services.PocketService
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraHttpSender
import org.acra.data.StringFormat
import org.acra.sender.HttpSender
import org.solovyev.android.checkout.Billing

/**
 * @author Artem Chepurnoy
 */
@AcraCore(
    reportFormat = StringFormat.JSON,
    alsoReportToAndroidFramework = true,
)
@AcraHttpSender(
    uri = BuildConfig.ACRA_URI,
    basicAuthLogin = BuildConfig.ACRA_USERNAME,
    basicAuthPassword = BuildConfig.ACRA_PASSWORD,
    httpMethod = HttpSender.Method.POST,
)
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

    val billing by lazy {
        Billing(this, object : Billing.DefaultConfiguration() {
            override fun getPublicKey(): String {
                return BuildConfig.MY_LICENSE_KEY
            }
        })
    }

    val analytics: Analytics by lazy {
        if (Cfg.analytics) {
            createAnalytics(this)
        } else AnalyticsStub()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ACRA.init(this)
    }

    override fun onCreate() {
        super.onCreate()
        // don't schedule anything in crash reporter process
        if (ACRA.isACRASenderServiceProcess())
            return

        Cfg.init(this)
        Cfg.observe(cfgObserver)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
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
