package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ConfigOverlayBeforeIsCheckedLiveData() = ConfigPropertyLiveData(
    Cfg.KEY_OVERLAY_ON_BEFORE_LOCK_SCREEN,
    Cfg::overlayOnBeforeLockScreen
)
