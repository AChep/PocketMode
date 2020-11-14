package com.artemchep.pocketmode.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import androidx.lifecycle.LiveData
import com.artemchep.pocketmode.ext.isScreenOn
import com.artemchep.pocketmode.models.Screen

/**
 * @author Artem Chepurnoy
 */
class ScreenLiveData(
    private val context: Context
) : LiveData<Screen?>() {
    companion object {
        private const val SCREEN_CHECK_INTERVAL = 100L
    }

    private val handler = Handler()

    private val updateScreenStateRunnable = Runnable {
        updateScreenState()
    }

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
        handler.removeCallbacksAndMessages(null)
        super.onInactive()
        value = null
    }

    private fun updateScreenState() {
        val screen = isScreenOn()
        postValue(screen)

        // While the screen is on, send the update every
        // few seconds. This is needed because of the
        // Always On mode which may not send the 'screen is off'
        // broadcast.
        if (screen is Screen.On && hasActiveObservers()) {
            handler.postDelayed(updateScreenStateRunnable, SCREEN_CHECK_INTERVAL)
        }
    }

    /**
     * It does not store the screen state, it
     * retrieves it every time.
     */
    private fun isScreenOn(): Screen =
        when (context.isScreenOn()) {
            true -> Screen.On
            false -> Screen.Off
        }
}