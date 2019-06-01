package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigLockScreenDelayLiveData
    : ConfigBaseLiveData<Long>(
    Cfg.KEY_LOCK_SCREEN_DELAY,
    Cfg::lockScreenDelay
)
