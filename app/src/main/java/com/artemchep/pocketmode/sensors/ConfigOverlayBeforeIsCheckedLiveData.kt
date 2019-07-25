package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigOverlayBeforeIsCheckedLiveData
    : ConfigBaseLiveData<Boolean>(
    Cfg.KEY_OVERLAY_ON_BEFORE_LOCK_SCREEN,
    Cfg::overlayOnBeforeLockScreen
)
