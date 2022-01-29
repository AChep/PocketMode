package com.artemchep.pocketmode.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.artemchep.pocketmode.Cfg
import com.artemchep.pocketmode.Heart

/**
 * @author Artem Chepurnoy
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_LOCKED_BOOT_COMPLETED &&
            intent.action != Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            return
        }

        // We can start foreground services at this point, see
        // https://developer.android.com/guide/components/foreground-services#background-start-restriction-exemptions
        if (Cfg.isEnabled) {
            val heart = context.applicationContext as Heart
            val pocketServiceIntent = heart.pocketServiceIntent
            context.startForegroundService(pocketServiceIntent)
        }
    }

}