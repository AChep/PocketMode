package com.artemchep.pocketmode.analytics

/**
 * @author Artem Chepurnoy
 */
interface AnalyticsHolder {
    var analytics: Analytics
}

/**
 * @author Artem Chepurnoy
 */
class AnalyticsHolderImpl : AnalyticsHolder {
    override lateinit var analytics: Analytics
}
