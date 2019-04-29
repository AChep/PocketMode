package com.artemchep.pocketmode.models

/**
 * @author Artem Chepurnoy
 */
sealed class Proximity {
    object Far : Proximity()
    object Near : Proximity()
}
