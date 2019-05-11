package com.artemchep.pocketmode

import android.app.Application
import android.content.Intent
import com.artemchep.config.Config
import com.artemchep.pocketmode.services.PocketService
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

    override fun onCreate() {
        super.onCreate()
        Cfg.init(this)
        Cfg.observe(cfgObserver)

        // Start the service.
        if (Cfg.isEnabled) {
            startForegroundService(pocketServiceIntent)
        }
    }
}
