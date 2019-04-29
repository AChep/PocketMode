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
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }

        if (Cfg.isEnabled) {
            val heart = context.applicationContext as Heart
            val pocketServiceIntent = heart.pocketServiceIntent
            context.startForegroundService(pocketServiceIntent)
        }
    }

}