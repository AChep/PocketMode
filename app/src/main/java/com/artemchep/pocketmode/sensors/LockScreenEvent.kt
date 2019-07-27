package com.artemchep.pocketmode.sensors

import android.util.Log
import androidx.annotation.UiThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.artemchep.pocketmode.Cfg
import com.artemchep.pocketmode.keyguardUnlockedDelay
import com.artemchep.pocketmode.models.Keyguard
import com.artemchep.pocketmode.models.PhoneCall
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.Screen
import com.artemchep.pocketmode.models.events.*
import com.artemchep.pocketmode.models.events.LockScreenEvent
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * @author Artem Chepurnoy
 */
class LockScreenEvent(
    private val proximityLiveData: LiveData<Proximity>,
    private val screenLiveData: LiveData<Screen>,
    private val phoneCallLiveData: LiveData<PhoneCall>,
    private val keyguardLiveData: LiveData<Keyguard>
) : MediatorLiveData<Event<LockScreenEvent>>(), CoroutineScope {
    companion object {
        private const val TAG = "LockScreenEvent"

        private const val DELAY_BEFORE_LOCK_SCREEN = 100L // ms.
        private const val DELAY_BEFORE_IDLE = 50L // ms.
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + coroutineContextJob

    private lateinit var coroutineContextJob: Job

    // ---- Main ----

    init {
        value = Event(Idle)
    }

    private val elapsedRealtime: Long
        get() = System.currentTimeMillis()

    private val proximityObserver = Observer<Proximity> { proximity ->
        when (proximity) {
            is Proximity.Near -> launchLockScreenEventJob()
            is Proximity.Far -> cancelLockScreenEventJob()
        }
    }

    private val keyguardObserver = Observer<Keyguard> { keyguard ->
        state = when (keyguard) {
            is Keyguard.Locked -> state.copy(lastKeyguardLockedTime = elapsedRealtime)
            is Keyguard.Unlocked -> state
        }
    }

    private var state = State(
        lastKeyguardLockedTime = 0L,
        isPhoneCallOngoing = false,
        isScreenOn = false,
        isActive = false
    )
        set(value) {
            field = value

            // Log the state.
            Log.d(TAG, "The state is $state")

            updateProximitySensorSubscription(value)
            updateKeyguardSensorSubscription(value)
        }

    private fun updateProximitySensorSubscription(state: State) {
        val isActive = state.run {
            elapsedRealtime - lastKeyguardLockedTime < Cfg.keyguardUnlockedDelay &&
                    !isPhoneCallOngoing &&
                    isScreenOn &&
                    isActive
        }
        if (isActive) {
            proximityLiveData.observeForever(proximityObserver)
        } else {
            proximityLiveData.removeObserver(proximityObserver)
            cancelLockScreenEventJob()
        }
    }

    private fun updateKeyguardSensorSubscription(state: State) {
        val isActive = state.run {
            isScreenOn &&
                    isActive
        }
        if (isActive) {
            keyguardLiveData.observeForever(keyguardObserver)
        } else {
            keyguardLiveData.removeObserver(keyguardObserver)
        }
    }

    /**
     * A job that sends the "Lock screen" event after a small
     * delay.
     */
    private var sendLockScreenEventJob: Job? = null

    init {
        // Observe the screen state changes and check the
        // proximity sensor only if screen is on.
        addSource(screenLiveData) { screen ->
            state = when (screen) {
                is Screen.On -> state.copy(isScreenOn = true)
                is Screen.Off -> state.copy(isScreenOn = false)
            }
        }

        // Observe the changes in a phone state.
        addSource(phoneCallLiveData) { phoneCall ->
            state = when (phoneCall) {
                is PhoneCall.Ongoing -> state.copy(isPhoneCallOngoing = true)
                is PhoneCall.Idle -> state.copy(isPhoneCallOngoing = false)
            }
        }
    }

    override fun onActive() {
        super.onActive()
        coroutineContextJob = Job()
        state = state.copy(isActive = true)
    }

    override fun onInactive() {
        state = state.copy(isActive = false)
        coroutineContextJob.cancel()
        super.onInactive()
    }

    /**
     * @author Artem Chepurnoy
     */
    private data class State(
        val lastKeyguardLockedTime: Long,
        val isPhoneCallOngoing: Boolean,
        val isScreenOn: Boolean,
        val isActive: Boolean
    )

    // ---- Lock screen event ----

    /**
     * Launches the lock screen job,
     * if it is not active.
     */
    private fun launchLockScreenEventJob() {
        if (sendLockScreenEventJob?.isActive == true) {
            return
        }

        sendLockScreenEventJob = launch(Dispatchers.Main) {
            delay(DELAY_BEFORE_LOCK_SCREEN)

            // This is a small hack to answer Android's slow updating of the
            // screen's state. Screen can be off, but no broadcast about it received yet.
            //
            // Otherwise it would be useless.
            if (screenLiveData.value != Screen.On) {
                return@launch
            }

            sendBeforeLockScreenEvent()
            delay(Cfg.lockScreenDelay - DELAY_BEFORE_LOCK_SCREEN)
            sendOnLockScreenEvent()
            delay(DELAY_BEFORE_IDLE)
        }.apply {
            invokeOnCompletion {
                sendIdleEvent()
            }
        }
    }

    /**
     * Cancels the lock screen job.
     */
    private fun cancelLockScreenEventJob() {
        sendLockScreenEventJob?.cancel()
        sendLockScreenEventJob = null
    }

    @UiThread
    private fun sendBeforeLockScreenEvent() {
        val value = Event(BeforeLockScreen)
        setValue(value)
    }

    @UiThread
    private fun sendOnLockScreenEvent() {
        val value = Event(OnLockScreen)
        setValue(value)
    }

    private fun sendIdleEvent() {
        val value = Event(Idle)
        postValue(value)
    }
}