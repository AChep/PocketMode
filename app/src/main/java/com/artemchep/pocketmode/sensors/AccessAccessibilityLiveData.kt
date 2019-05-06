package com.artemchep.pocketmode.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemchep.pocketmode.INTENT_ACCESSIBILITY_CHANGED
import com.artemchep.pocketmode.ext.isAccessibilityServiceEnabled

/**
 * @author Artem Chepurnoy
 */
class AccessAccessibilityLiveData(
    private val context: Context
) : LiveData<Boolean>() {
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateValue()
        }
    }

    override fun onActive() {
        super.onActive()

        // Register an observer.
        val intentFilter = IntentFilter()
            .apply {
                addAction(INTENT_ACCESSIBILITY_CHANGED)
            }
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.registerReceiver(broadcastReceiver, intentFilter)

        // Immediately fire a current state.
        updateValue()
    }

    override fun onInactive() {
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.unregisterReceiver(broadcastReceiver)
        super.onInactive()
    }

    private fun updateValue() {
        val isGranted = context.isAccessibilityServiceEnabled()
        postValue(isGranted)
    }
}