package com.artemchep.pocketmode.sensors

import android.content.Context
import androidx.lifecycle.LiveData
import com.artemchep.pocketmode.ext.isKeyguardLocked
import com.artemchep.pocketmode.models.Keyguard
import kotlinx.coroutines.*

/**
 * @author Artem Chepurnoy
 */
class KeyguardLiveData(
    private val context: Context
) : LiveData<Keyguard>() {
    companion object {
        private const val PERIOD = 200L
    }

    private lateinit var observeKeyguardJob: Job

    override fun onActive() {
        super.onActive()
        observeKeyguardJob = GlobalScope.launch(Dispatchers.Main) {
            while (isActive) {
                delay(PERIOD)
                updateKeyguardState()
            }
        }

        // Immediately fire a current state.
        updateKeyguardState()
    }

    override fun onInactive() {
        observeKeyguardJob.cancel()
        super.onInactive()
    }

    private fun updateKeyguardState() {
        val keyguard = when (context.isKeyguardLocked()) {
            true -> Keyguard.Locked
            false -> Keyguard.Unlocked
        }
        postValue(keyguard)
    }
}