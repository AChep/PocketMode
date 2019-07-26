package com.artemchep.pocketmode.models.sensors

import com.artemchep.pocketmode.models.Proximity

/**
 * @author Artem Chepurnoy
 */
data class ProximitySensorSnapshot(
    val id: Int,
    val name: String,
    val proximity: Proximity,
    val distance: Float
)
