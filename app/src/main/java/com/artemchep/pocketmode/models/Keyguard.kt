package com.artemchep.pocketmode.models

/**
 * @author Artem Chepurnoy
 */
sealed class Keyguard {
    object Locked : Keyguard()
    object Unlocked : Keyguard()
}
