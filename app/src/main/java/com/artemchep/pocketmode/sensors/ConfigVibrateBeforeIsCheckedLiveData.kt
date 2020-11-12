package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ConfigVibrateBeforeIsCheckedLiveData() = ConfigPropertyLiveData(
    Cfg.KEY_VIBRATE_ON_BEFORE_LOCK_SCREEN,
    Cfg::vibrateOnBeforeLockScreen
)
