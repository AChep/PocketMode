package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ConfigProximityWakeLockIsCheckedLiveData() = ConfigPropertyLiveData(
    Cfg.KEY_PROXIMITY_WAKE_LOCK,
) {
    false
}
