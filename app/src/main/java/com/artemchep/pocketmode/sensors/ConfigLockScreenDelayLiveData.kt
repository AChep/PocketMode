package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ConfigLockScreenDelayLiveData() = ConfigPropertyLiveData(
    Cfg.KEY_LOCK_SCREEN_DELAY,
    Cfg::lockScreenDelay
)
