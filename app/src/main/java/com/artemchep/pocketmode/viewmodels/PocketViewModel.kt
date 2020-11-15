package com.artemchep.pocketmode.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import com.artemchep.pocketmode.models.Keyguard
import com.artemchep.pocketmode.models.PhoneCall
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.Screen
import com.artemchep.pocketmode.models.`fun`.Either
import com.artemchep.pocketmode.models.events.Idle
import com.artemchep.pocketmode.models.events.LockScreenEvent
import com.artemchep.pocketmode.sensors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * @author Artem Chepurnoy
 */
class PocketViewModel(context: Context) {
    private val proximityBinaryFlow: Flow<Proximity> = flowOfBinaryProximity(context)

    private val screenFlow: Flow<Screen> = flowOfScreen(context)

    private val keyguardFlow: Flow<Keyguard> = flowOfKeyguard(context)

    private val phoneCallLiveData: LiveData<PhoneCall> = PhoneCallLiveData(context)
        .map { state ->
            when (state) {
                is Either.Left -> PhoneCall.Idle
                is Either.Right -> state.b
            }
        }

    private val overlayBeforeLockingSwitchIsCheckedLiveData = ConfigOverlayBeforeIsCheckedLiveData()

    /**
     * Send the lock events when it thinks that your phone is in
     * the pocket.
     */
    val lockScreenEventFlow: Flow<LockScreenEvent> =
        flowOfLockScreen(
            proximityFlow = proximityBinaryFlow,
            screenFlow = screenFlow,
            keyguardFlow = keyguardFlow,
            phoneCallLiveData = phoneCallLiveData,
        )

    val overlayFlow: Flow<Boolean> = overlayBeforeLockingSwitchIsCheckedLiveData
        .asFlow()
        .flatMapLatest { isEnabled ->
            if (isEnabled) {
                lockScreenEventFlow
                    .map {
                        it != Idle
                    }
            } else flowOf(false)
        }
}