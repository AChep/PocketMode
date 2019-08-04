package com.artemchep.pocketmode.sensors

import android.content.Context
import android.os.PowerManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData

/**
 * @author Artem Chepurnoy
 */
class WakeLockLiveData(
    private val context: Context,
    private val flagsGetter: () -> Int
) : LiveData<Nothing>() {
    companion object {
        private const val WAKE_LOCK_TAG = "com.artemchep.pocketmode:PocketMode"

        private const val WAKE_LOCK_TIMEOUT = 60L * 1000L // 1m
    }

    private val powerManager by lazy { context.getSystemService<PowerManager>() }

    private var wakeLock: PowerManager.WakeLock? = null

    override fun onActive() {
        super.onActive()

        wakeLock?.release()
        wakeLock =
            powerManager?.newWakeLock(flagsGetter(), WAKE_LOCK_TAG)
                ?.apply {
                    acquire(WAKE_LOCK_TIMEOUT)
                }
    }

    override fun onInactive() {
        wakeLock?.release()
        wakeLock = null
        super.onInactive()
    }
}