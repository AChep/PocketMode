package com.artemchep.pocketmode.analytics

import com.artemchep.pocketmode.models.Proximity

/**
 * @author Artem Chepurnoy
 */
interface Analytics {

    fun logPocketModeTriggered()

    fun logPocketModeLocked()

    fun logTestProximitySensorChange(proximity: Proximity)

}
