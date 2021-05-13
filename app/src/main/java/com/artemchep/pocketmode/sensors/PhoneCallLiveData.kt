package com.artemchep.pocketmode.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import com.artemchep.pocketmode.models.PhoneCall
import com.artemchep.pocketmode.models.`fun`.Either
import com.artemchep.pocketmode.models.issues.NoReadPhoneStatePermissionGranted

/**
 * @author Artem Chepurnoy
 */
class PhoneCallLiveData(
    private val context: Context
) : LiveData<Either<NoReadPhoneStatePermissionGranted, PhoneCall>>() {
    private val callStateObserver = object : PhoneStateListener() {
        override fun onCallStateChanged(state: Int, incomingNumber: String) {
            super.onCallStateChanged(state, incomingNumber)
            postPhoneState(state)
        }
    }

    override fun onActive() {
        super.onActive()
        val phoneStatePermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
        if (phoneStatePermission == PackageManager.PERMISSION_GRANTED) {
            withTelephonyManager {
                listen(callStateObserver, PhoneStateListener.LISTEN_CALL_STATE)
            }
        } else {
            postNoReadPhoneStatePermissionGranted()
        }
    }

    private fun postNoReadPhoneStatePermissionGranted() {
        val value = NoReadPhoneStatePermissionGranted()
        postValue(Either.Left(value))
    }

    private fun postPhoneState(phoneState: Int) {
        val value = when (phoneState) {
            TelephonyManager.CALL_STATE_RINGING -> PhoneCall.Ongoing
            TelephonyManager.CALL_STATE_OFFHOOK -> PhoneCall.Offhook
            else -> PhoneCall.Idle
        }
        postValue(Either.Right(value))
    }

    override fun onInactive() {
        withTelephonyManager {
            listen(callStateObserver, PhoneStateListener.LISTEN_NONE)
            // Change the phone call state
            // to idle.
            postPhoneState(TelephonyManager.CALL_STATE_IDLE)
        }
        super.onInactive()
    }

    private inline fun withTelephonyManager(crossinline block: TelephonyManager.() -> Unit) {
        val tm = context.getSystemService<TelephonyManager>()
        try {
            tm?.apply(block)
        } catch (e: SecurityException) {
            postNoReadPhoneStatePermissionGranted()
        }
    }
}
