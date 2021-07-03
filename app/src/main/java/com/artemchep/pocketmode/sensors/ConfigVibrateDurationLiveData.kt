package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ConfigVibrateDurationLiveData() = ConfigPropertyLiveData(
    Cfg.KEY_VIBRATE_DURATION_BEFORE_LOCK_SCREEN,
    Cfg::vibrateDurationBeforeLockScreen
)
