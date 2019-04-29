package com.artemchep.pocketmode.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.LiveData
import com.artemchep.pocketmode.ext.isScreenOn
import com.artemchep.pocketmode.models.Screen

/**
 * @author Artem Chepurnoy
 */
class ScreenLiveData(
    private val context: Context
) : LiveData<Screen>() {
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateScreenState()
        }
    }

    override fun onActive() {
        super.onActive()

        // Register an observer.
        val intentFilter = IntentFilter()
            .apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
            }
        context.registerReceiver(broadcastReceiver, intentFilter)

        // Immediately fire a current state.
        updateScreenState()
    }

    override fun onInactive() {
        context.unregisterReceiver(broadcastReceiver)
        super.onInactive()
    }

    private fun updateScreenState() {
        val screen = when (context.isScreenOn()) {
            true -> Screen.On
            false -> Screen.Off
        }
        postValue(screen)
    }
}