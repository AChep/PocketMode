package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigIsCheckedLiveData
    : ConfigBaseLiveData<Boolean>(
    Cfg.KEY_ENABLED,
    Cfg::isEnabled
)
