package com.artemchep.pocketmode.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.pocketmode.models.Keyguard
import com.artemchep.pocketmode.models.PhoneCall
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.Screen
import com.artemchep.pocketmode.models.`fun`.Either
import com.artemchep.pocketmode.models.events.Event
import com.artemchep.pocketmode.models.issues.NoReadPhoneStatePermissionGranted
import com.artemchep.pocketmode.sensors.*

/**
 * @author Artem Chepurnoy
 */
class PocketViewModel(context: Context) {
    private val proximityLiveData: LiveData<Float> = ProximityLiveData(context)

    private val proximityBinaryLiveData: LiveData<Proximity> =
        ProximityBinaryLiveData(context, proximityLiveData)

    private val screenLiveData: LiveData<Screen> = ScreenLiveData(context)

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

    /**
     * Send the lock events when it thinks that your phone is in
     * the pocket.
     */
    val lockScreenLiveData: LiveData<Event<com.artemchep.pocketmode.models.events.LockScreenEvent>> =
        LockScreenEvent(
            proximityLiveData = proximityBinaryLiveData,
            screenLiveData = screenLiveData,
            phoneCallLiveData = phoneCallSoloLiveData,
            keyguardLiveData = keyguardLiveData
        )
}