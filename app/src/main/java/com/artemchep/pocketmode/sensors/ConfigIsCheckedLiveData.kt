package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ConfigIsCheckedLiveData() = ConfigPropertyLiveData(
    Cfg.KEY_ENABLED,
    Cfg::isEnabled
)
