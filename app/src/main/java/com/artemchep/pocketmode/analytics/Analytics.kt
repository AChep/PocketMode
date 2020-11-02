package com.artemchep.pocketmode.analytics

import com.artemchep.pocketmode.models.Proximity

/**
 * @author Artem Chepurnoy
 */
interface Analytics {

    fun log(message: String)

    fun logPocketModeTriggered()

    fun logPocketModeLocked()

    fun logTestProximitySensorChange(proximity: Proximity)

}
