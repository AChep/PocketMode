package com.artemchep.pocketmode.viewmodels

import android.content.Context
import android.os.PowerManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import com.artemchep.pocketmode.models.Keyguard
import com.artemchep.pocketmode.models.PhoneCall
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.Screen
import com.artemchep.pocketmode.models.`fun`.Either
import com.artemchep.pocketmode.models.events.Idle
import com.artemchep.pocketmode.models.events.LockScreenEvent
import com.artemchep.pocketmode.models.issues.NoReadPhoneStatePermissionGranted
import com.artemchep.pocketmode.sensors.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * @author Artem Chepurnoy
 */
class PocketViewModel(context: Context) {
    private val proximityLiveData: LiveData<Float?> = ProximityLiveData(context)

    private val proximityBinaryLiveData: LiveData<Proximity?> =
        ProximityBinaryLiveData(context, proximityLiveData)
            .distinctUntilChanged()

    /**
     * Holds the wake lock while being
     * observed.
     */
    private val wakeLockLiveData: LiveData<Nothing> = WakeLockLiveData(context) {
        PowerManager.PARTIAL_WAKE_LOCK
    }

    private val screenLiveData: LiveData<Screen?> = ScreenLiveData(context)

    private val phoneCallLiveData: LiveData<Either<NoReadPhoneStatePermissionGranted, PhoneCall>> =
        PhoneCallLiveData(context)

    private val phoneCallSoloLiveData = MediatorLiveData<PhoneCall>()
        .apply {
            addSource(phoneCallLiveData) { state ->
                val value = when (state) {
                    is Either.Left -> PhoneCall.Idle
                    is Either.Right -> state.b
                }
                postValue(value)
            }
        }

    private val keyguardLiveData: LiveData<Keyguard> = KeyguardLiveData(context)
        .distinctUntilChanged()

    private val overlayBeforeLockingSwitchIsCheckedLiveData = ConfigOverlayBeforeIsCheckedLiveData()

    /**
     * Send the lock events when it thinks that your phone is in
     * the pocket.
     */
    val lockScreenEventFlow: Flow<LockScreenEvent> =
        LockScreenEventFlow(
            proximityLiveData = proximityBinaryLiveData,
            screenLiveData = screenLiveData,
            phoneCallLiveData = phoneCallSoloLiveData,
            keyguardLiveData = keyguardLiveData
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