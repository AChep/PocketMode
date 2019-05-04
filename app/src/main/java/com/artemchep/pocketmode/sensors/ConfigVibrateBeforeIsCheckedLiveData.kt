package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigVibrateBeforeIsCheckedLiveData
    : ConfigBaseLiveData<Boolean>(Cfg.KEY_VIBRATE_ON_BEFORE_LOCK_SCREEN) {

    override fun updateValue() {
        val isEnabled = Cfg.vibrateOnBeforeLockScreen
        postValue(isEnabled)
    }

}
