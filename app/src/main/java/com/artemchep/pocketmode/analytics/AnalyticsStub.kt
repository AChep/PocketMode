package com.artemchep.pocketmode.analytics

import com.artemchep.pocketmode.models.Proximity

/**
 * @author Artem Chepurnoy
 */
class AnalyticsStub(
) : Analytics {
    override fun log(message: String) {
    }

    override fun logPocketModeTriggered() {
    }

    override fun logPocketModeLocked() {
    }

    override fun logTestProximitySensorChange(proximity: Proximity) {
    }
}