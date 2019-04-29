package com.artemchep.pocketmode.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.artemchep.pocketmode.Cfg
import com.artemchep.pocketmode.LINK_BUG_REPORT
import com.artemchep.pocketmode.LINK_REPOSITORY
import com.artemchep.pocketmode.LINK_TRANSLATE
import com.artemchep.pocketmode.ext.context
import com.artemchep.pocketmode.ext.isAccessibilityServiceEnabled
import com.artemchep.pocketmode.models.events.Event
import com.artemchep.pocketmode.models.events.OpenAccessibilityEvent
import com.artemchep.pocketmode.models.events.OpenUrlEvent
import com.artemchep.pocketmode.sensors.ConfigIsEnabledLiveData
import com.artemchep.pocketmode.sensors.ConfigLockScreenDelayLiveData

/**
 * @author Artem Chepurnoy
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    val masterSwitchLiveData = ConfigIsEnabledLiveData()

    val lockScreenDelayLiveData = ConfigLockScreenDelayLiveData()

    val openAccessibilityLiveData = MutableLiveData<Event<OpenAccessibilityEvent>>()

    val openUrlLiveData = MutableLiveData<Event<OpenUrlEvent>>()

    /**
     * Turns the master switch on and
     * off.
     */
    fun setMasterSwitchEnabled(isEnabled: Boolean = !masterSwitchLiveData.value!!) {
        if (isEnabled) {
            if (context.isAccessibilityServiceEnabled()) {
                // Enable the pocket service.
                Cfg.edit(context) {
                    Cfg.isEnabled = true
                }
            } else {
                val event = Event(OpenAccessibilityEvent)
                openAccessibilityLiveData.postValue(event)
            }
        } else {
            // Disable the pocket service.
            Cfg.edit(context) {
                Cfg.isEnabled = false
            }
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

}