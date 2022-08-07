package com.artemchep.pocketmode

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.*
import com.artemchep.config.Config
import com.artemchep.pocketmode.analytics.Analytics
import com.artemchep.pocketmode.analytics.AnalyticsHolder
import com.artemchep.pocketmode.analytics.AnalyticsStub
import com.artemchep.pocketmode.analytics.createAnalytics
import com.artemchep.pocketmode.services.PocketService
import com.artemchep.pocketmode.services.PocketServiceRestartWorker
import com.google.android.material.color.DynamicColors
import org.solovyev.android.checkout.Billing
import java.time.Duration

/**
 * @author Artem Chepurnoy
 */
class Heart : Application() {
    companion object {
        private const val WORK_RESTART_ID = "PocketService::restart"
        private const val WORK_RESTART_PERIOD = 2 * 60 * 60 * 1000L // 2h
    }

    private val cfgObserver = object : Config.OnConfigChangedListener<String> {
        override fun onConfigChanged(keys: Set<String>) {
            if (Cfg.KEY_ENABLED in keys) {
                // Start or stop the service depending on the
                // config.
                val shouldBeEnabled = Cfg.isEnabled
                if (shouldBeEnabled) {
                    if (ProcessLifecycleOwner.get().lifecycle.currentState >= Lifecycle.State.STARTED)
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
        DynamicColors.applyToActivitiesIfAvailable(this)
    }

    override fun onCreate() {
        super.onCreate()
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

        // Start a scheduler to restart service in
        // few hours in cause it was killed.
        val request = PeriodicWorkRequestBuilder<PocketServiceRestartWorker>(
            Duration.ofMillis(WORK_RESTART_PERIOD)
        ).build()
        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(WORK_RESTART_ID, ExistingPeriodicWorkPolicy.REPLACE, request)
    }
}
