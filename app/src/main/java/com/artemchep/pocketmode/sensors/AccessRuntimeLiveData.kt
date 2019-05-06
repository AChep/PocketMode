package com.artemchep.pocketmode.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.artemchep.pocketmode.INTENT_ACCESSIBILITY_CHANGED
import com.artemchep.pocketmode.INTENT_RUNTIME_PERMISSIONS_CHANGED

/**
 * @author Artem Chepurnoy
 */
class AccessRuntimeLiveData(
    private val context: Context,
    private val permissions: List<String>
) : LiveData<List<String>>() {
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateRuntimePermissions()
        }
    }

    override fun onActive() {
        super.onActive()

        // Register an observer.
        val intentFilter = IntentFilter()
            .apply {
                addAction(INTENT_RUNTIME_PERMISSIONS_CHANGED)
            }
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.registerReceiver(broadcastReceiver, intentFilter)

        // Immediately fire a current state.
        updateRuntimePermissions()
    }

    override fun onInactive() {
        val lbm = LocalBroadcastManager.getInstance(context)
        lbm.unregisterReceiver(broadcastReceiver)
        super.onInactive()
    }

    private fun updateRuntimePermissions() {
        val declinedPermissions = permissions
            .filter {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }
        postValue(declinedPermissions)
    }
}