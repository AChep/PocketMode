package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigProximityWakeLockIsCheckedLiveData
    : ConfigBaseLiveData<Boolean>(
    Cfg.KEY_PROXIMITY_WAKE_LOCK,
    Cfg::proximityWakeLock
)
