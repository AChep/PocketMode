package com.artemchep.pocketmode.viewmodels

import android.Manifest
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.artemchep.pocketmode.Cfg
import com.artemchep.pocketmode.LINK_BUG_REPORT
import com.artemchep.pocketmode.LINK_REPOSITORY
import com.artemchep.pocketmode.LINK_TRANSLATE
import com.artemchep.pocketmode.ext.context
import com.artemchep.pocketmode.models.events.Event
import com.artemchep.pocketmode.models.events.OpenAccessibilityEvent
import com.artemchep.pocketmode.models.events.OpenRuntimePermissionsEvent
import com.artemchep.pocketmode.models.events.OpenUrlEvent
import com.artemchep.pocketmode.sensors.*

/**
 * @author Artem Chepurnoy
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val masterSwitchIsCheckedLiveData = ConfigIsCheckedLiveData()

    val vibrateBeforeLockingSwitchIsCheckedLiveData = ConfigVibrateBeforeIsCheckedLiveData()

    val lockScreenDelayLiveData = ConfigLockScreenDelayLiveData()

    // ---- Permissions ----

    val isAccessibilityGranted = AccessAccessibilityLiveData(context)

    private val readPhoneCallStatePermissions = listOf(Manifest.permission.READ_PHONE_STATE)

    val isReadPhoneCallGranted = MediatorLiveData<Boolean>()
        .apply {
            val src = AccessRuntimeLiveData(context, readPhoneCallStatePermissions)
            addSource(src) {
                val isGranted = it.isEmpty()
                postValue(isGranted)
            }
        }

    val isAllGranted = MediatorLiveData<Boolean>()
        .apply {
            val resolver: (Any) -> Unit = {
                val isAccessibilityGranted = isAccessibilityGranted.value ?: false
                val isAllGranted = isAccessibilityGranted
                postValue(isAllGranted)
            }

            addSource(isAccessibilityGranted, resolver)
        }

    // ---- Events ----

    val openAccessibilityLiveData = MutableLiveData<Event<OpenAccessibilityEvent>>()

    val openRuntimePermissionLiveData = MutableLiveData<Event<OpenRuntimePermissionsEvent>>()

    val openUrlLiveData = MutableLiveData<Event<OpenUrlEvent>>()

    val openDonateToMeLiveData = MutableLiveData<Event<Unit>>()

    /**
     * Turns the master switch on and
     * off.
     */
    fun setMasterSwitchEnabled(isEnabled: Boolean = !masterSwitchIsCheckedLiveData.value!!) {
        if (isEnabled) {
            if (isAllGranted.value == true) {
                // Enable the pocket service.
                Cfg.edit(context) {
                    Cfg.isEnabled = true
                }
            }
        } else {
            // Disable the pocket service.
            Cfg.edit(context) {
                Cfg.isEnabled = false
            }
        }
    }

    fun setVibrateOnBeforeLockScreen(vibrateOnBeforeLockScreen: Boolean = Cfg.DEFAULT_VIBRATE_ON_BEFORE_LOCK_SCREEN) {
        Cfg.edit(context) {
            Cfg.vibrateOnBeforeLockScreen = vibrateOnBeforeLockScreen
        }
    }

    fun setLockScreenDelay(delay: Long = Cfg.DEFAULT_LOCK_SCREEN_DELAY) {
        Cfg.edit(context) {
            Cfg.lockScreenDelay = delay
        }
    }

    fun openRepo() = openUrl(LINK_REPOSITORY)

    fun openBugReport() = openUrl(LINK_BUG_REPORT)

    fun openTranslationService() = openUrl(LINK_TRANSLATE)

    private fun openUrl(url: String) {
        val event = Event(OpenUrlEvent(url))
        openUrlLiveData.postValue(event)
    }

    fun openDonateToMe() {
        val event = Event(Unit)
        openDonateToMeLiveData.postValue(event)
    }

    // ---- Permissions ----

    fun grantAccessibilityService() {
        val event = Event(OpenAccessibilityEvent)
        openAccessibilityLiveData.postValue(event)
    }

    fun grantCallState() {
        val event = Event(OpenRuntimePermissionsEvent(readPhoneCallStatePermissions))
        openRuntimePermissionLiveData.postValue(event)
    }

}