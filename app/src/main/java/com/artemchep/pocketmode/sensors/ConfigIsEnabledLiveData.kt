package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigIsEnabledLiveData : ConfigLiveData<Boolean>(Cfg.KEY_ENABLED) {

    override fun updateValue() {
        val isEnabled = Cfg.isEnabled
        postValue(isEnabled)
    }

}
