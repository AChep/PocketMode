package com.artemchep.pocketmode.models.events

/**
 * @author Artem Chepurnoy
 */
data class OpenRuntimePermissionsEvent(
    val permissions: List<String>
)
