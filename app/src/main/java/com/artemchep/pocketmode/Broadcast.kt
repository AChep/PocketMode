package com.artemchep.pocketmode

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager

const val INTENT_ACCESSIBILITY_CHANGED = "accessibility_changed"

const val INTENT_RUNTIME_PERMISSIONS_CHANGED = "runtime_permissions_changed"

fun Context.sendLocalBroadcast(action: String) = sendLocalBroadcast(Intent(action))

fun Context.sendLocalBroadcast(intent: Intent) {
    val lbm = LocalBroadcastManager.getInstance(this)
    lbm.sendBroadcast(intent)
}
