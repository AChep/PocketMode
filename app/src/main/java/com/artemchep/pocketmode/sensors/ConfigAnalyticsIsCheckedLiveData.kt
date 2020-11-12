package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ConfigAnalyticsIsCheckedLiveData() = ConfigPropertyLiveData(
    Cfg.KEY_ANALYTICS,
    Cfg::analytics
)
