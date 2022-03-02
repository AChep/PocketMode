package com.artemchep.pocketmode.viewmodels

import android.Manifest
import android.app.Application
import android.provider.Settings
import androidx.lifecycle.*
import com.artemchep.pocketmode.*
import com.artemchep.pocketmode.ext.context
import com.artemchep.pocketmode.models.MainScreen
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.events.*
import com.artemchep.pocketmode.models.sensors.ProximitySensorSnapshot
import com.artemchep.pocketmode.sensors.*
import com.artemchep.pocketmode.services.PocketAccessibilityService
import com.artemchep.pocketmode.util.combine

/**
 * @author Artem Chepurnoy
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val masterSwitchIsCheckedLiveData = ConfigIsCheckedLiveData()

    val vibrateBeforeLockingSwitchIsCheckedLiveData = ConfigVibrateBeforeIsCheckedLiveData()

    val overlayBeforeLockingSwitchIsCheckedLiveData = ConfigOverlayBeforeIsCheckedLiveData()

    val proximityWakeLockIsCheckedLiveData = ConfigProximityWakeLockIsCheckedLiveData()

    val analyticsIsCheckedLiveData = ConfigAnalyticsIsCheckedLiveData()

    val lockScreenDelayLiveData = ConfigLockScreenDelayLiveData()

    val vibrateDurationLiveData = ConfigVibrateDurationLiveData()

    val proximityLiveData: LiveData<ProximitySensorSnapshot> = ProximityLiveData(context)

    val appInfoLiveData: LiveData<out String> = MutableLiveData<String>()
        .apply {
            val versionName = BuildConfig.VERSION_NAME
            val versionCode = BuildConfig.VERSION_CODE
            val flavor = BuildConfig.FLAVOR
            val name = context.getString(R.string.app_name)
            value = "$name v$versionName+$versionCode ($flavor)"
        }

    val proximityBinaryLiveData: LiveData<Proximity> =
        ProximityBinaryLiveData(context, proximityLiveData)
            .distinctUntilChanged()

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

    val isRequiredGranted = MediatorLiveData<Boolean>()
        .apply {
            val resolver: (Any) -> Unit = {
                val isAccessibilityGranted = isAccessibilityGranted.value ?: false
                val isAllGranted = isAccessibilityGranted
                postValue(isAllGranted)
            }

            addSource(isAccessibilityGranted, resolver)
        }

    val isAllGranted = MediatorLiveData<Boolean>()
        .apply {
            val resolver: (Any) -> Unit = {
                val isAccessibilityGranted = isAccessibilityGranted.value ?: false
                val isReadPhoneCallGranted = isReadPhoneCallGranted.value ?: false
                val isAllGranted = isAccessibilityGranted && isReadPhoneCallGranted
                postValue(isAllGranted)
            }

            addSource(isAccessibilityGranted, resolver)
            addSource(isReadPhoneCallGranted, resolver)
        }

    // ---- Out ----

    private val mainScreenMinLockDelay = context.resources.getInteger(R.integer.minDelay).toLong()

    private val mainScreenMaxLockDelay = context.resources.getInteger(R.integer.maxDelay).toLong()

    private val mainScreenSettings = combine(
        overlayBeforeLockingSwitchIsCheckedLiveData,
        vibrateBeforeLockingSwitchIsCheckedLiveData,
        proximityWakeLockIsCheckedLiveData,
        lockScreenDelayLiveData,
    ) { overlayEnabled, shouldVibrateBeforeLocking, shouldUseProximityWakeLock, lockDelay ->
        MainScreen.Settings(
            isVibrateBeforeLockingEnabled = shouldVibrateBeforeLocking,
            onVibrateBeforeLockingChanged = ::setVibrateOnBeforeLockScreen,
            isShowOverlayBeforeLockingEnabled = overlayEnabled,
            onShowOverlayBeforeLockingChanged = ::setOverlayOnBeforeLockScreen,
            isTurnScreenBlackEnabled = shouldUseProximityWakeLock,
            onTurnScreenBlackChanged = ::setProximityWakeLock,
            lockDelayMinMs = mainScreenMinLockDelay,
            lockDelayMaxMs = mainScreenMaxLockDelay,
            lockDelayMs = lockDelay,
        )
    }

    private val mainScreenTroubleshooting = combine(
        overlayBeforeLockingSwitchIsCheckedLiveData,
        vibrateBeforeLockingSwitchIsCheckedLiveData,
        proximityWakeLockIsCheckedLiveData,
        lockScreenDelayLiveData,
    ) { overlay, vibrateBeforeLocking, shouldUseProximityWakeLock, lockDelay ->
        MainScreen.Troubleshooting(
            onLockScreen = {},
            onLaboratoryScreen = {},
            proximityCm = 1f,
            proximityIsClose = false,
        )
    }

    val mainScreen = combine(
        mainScreenSettings,
        mainScreenTroubleshooting,
    ) { settings, troubleshooting ->
        MainScreen(
            settings = settings,
            troubleshooting = troubleshooting,
        )
    }

    // ---- Events ----

    val openAccessibilityLiveData = MutableLiveData<Event<OpenAccessibilityEvent>>()

    val openOverlaysLiveData = MutableLiveData<Event<OpenOverlaysEvent>>()

    val openRuntimePermissionLiveData = MutableLiveData<Event<OpenRuntimePermissionsEvent>>()

    val openUrlLiveData = MutableLiveData<Event<OpenUrlEvent>>()

    val openDonateToMeLiveData = MutableLiveData<Event<Unit>>()

    val openLabLiveData = MutableLiveData<Event<Unit>>()

    /**
     * Turns the master switch on and
     * off.
     */
    fun setMasterSwitchEnabled(isEnabled: Boolean = !masterSwitchIsCheckedLiveData.value!!) {
        if (isEnabled) {
            if (isRequiredGranted.value == true) {
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

    fun setOverlayOnBeforeLockScreen(overlayOnBeforeLockScreen: Boolean = Cfg.DEFAULT_OVERLAY_ON_BEFORE_LOCK_SCREEN) {
        val canDrawOverlays = Settings.canDrawOverlays(context)
        if (canDrawOverlays || !overlayOnBeforeLockScreen) {
            // Update the preference, we have got the
            // permission.
            Cfg.edit(context) {
                Cfg.overlayOnBeforeLockScreen = overlayOnBeforeLockScreen
            }
        } else if (!canDrawOverlays) {
            val event = Event(OpenOverlaysEvent)
            openOverlaysLiveData.postValue(event)
        }
    }

    fun setProximityWakeLock(proximityWakeLock: Boolean = Cfg.DEFAULT_PROXIMITY_WAKE_LOCK) {
        Cfg.edit(context) {
            Cfg.proximityWakeLock = proximityWakeLock
        }
    }

    fun setAnalytics(analytics: Boolean = Cfg.DEFAULT_ANALYTICS) {
        Cfg.edit(context) {
            Cfg.analytics = analytics
        }
    }

    fun setLockScreenDelay(delay: Long = Cfg.DEFAULT_LOCK_SCREEN_DELAY) {
        Cfg.edit(context) {
            Cfg.lockScreenDelay = delay
        }
    }

    fun setVibrateDurationDelay(duration: Long = Cfg.DEFAULT_VIBRATE_DURATION_BEFORE_LOCK_SCREEN) {
        Cfg.edit(context) {
            Cfg.vibrateDurationBeforeLockScreen = duration
        }
    }

    fun lockScreen() {
        PocketAccessibilityService.sendLockScreenEvent(context, javaClass)
    }

    fun openRepo() = openUrl(LINK_REPOSITORY)

    fun openBugReport() = openUrl(LINK_BUG_REPORT)

    fun openBugReportDontKillMyApp() = openUrl(LINK_BUG_REPORT_DONT_KILL_MY_APP)

    fun openSwSensorResetApp() = openUrl(LINK_SW_SENSOR_RESET_APP)

    fun openTranslationService() = openUrl(LINK_TRANSLATE)

    private fun openUrl(url: String) {
        val event = Event(OpenUrlEvent(url))
        openUrlLiveData.postValue(event)
    }

    fun openDonateToMe() {
        val event = Event(Unit)
        openDonateToMeLiveData.postValue(event)
    }

    fun openLab() {
        val event = Event(Unit)
        openLabLiveData.postValue(event)
    }

    fun openApps() = openUrl(LINK_APPS)

    fun openWarInfo() = kotlin.run {
        val url = context.getString(R.string.war_learn_more_url)
        openUrl(url)
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