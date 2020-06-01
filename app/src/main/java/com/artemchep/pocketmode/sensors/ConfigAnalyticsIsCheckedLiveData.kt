package com.artemchep.pocketmode.sensors

import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
class ConfigAnalyticsIsCheckedLiveData
    : ConfigBaseLiveData<Boolean>(
    Cfg.KEY_ANALYTICS,
    Cfg::analytics
)
