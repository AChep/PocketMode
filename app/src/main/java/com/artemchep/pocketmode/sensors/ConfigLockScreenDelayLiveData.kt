package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigLockScreenDelayLiveData : ConfigLiveData<Long>(Cfg.KEY_LOCK_SCREEN_DELAY) {

    override fun updateValue() {
        val delay = Cfg.lockScreenDelay
        postValue(delay)
    }

}
