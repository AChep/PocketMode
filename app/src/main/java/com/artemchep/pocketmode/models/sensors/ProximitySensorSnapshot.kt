package com.artemchep.pocketmode.models.sensors

import com.artemchep.pocketmode.models.Proximity

private val REGEX = "^\\d+$".toRegex()

/**
 * @author Artem Chepurnoy
 */
data class ProximitySensorSnapshot(
    val id: Int,
    val name: String,
    val proximity: Proximity,
    val isRuntime: Boolean,
    val distance: Float
) {
    /**
     * `true` if the sensor might be a software sensor that is
     * prone to issues, `false` otherwise.
     */
    val isSoftware by lazy {
        isRuntime ||
                name.isBlank() ||
                REGEX.matches(name)
    }
}
