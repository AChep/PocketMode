package com.artemchep.pocketmode

import com.artemchep.config.common.SharedPrefConfig
import kotlin.math.max
import kotlin.math.min

/**
 * @author Artem Chepurnoy
 */
object Cfg : SharedPrefConfig("config") {
    const val KEY_ENABLED = "pocket::enabled"
    private const val DEFAULT_ENABLED = false
    /**
     * The delay before screen should turn off after proximity
     * sensor reported "near" state.
     */
    const val KEY_LOCK_SCREEN_DELAY = "pocket::lock_screen_delay"
    const val DEFAULT_LOCK_SCREEN_DELAY = 1100L // ms

    /**
     * `true` if the service should be running,
     * `false` otherwise.
     */
    var isEnabled by configDelegate(KEY_ENABLED, DEFAULT_ENABLED)
    /**
     * The delay before screen should turn off after proximity
     * sensor reported "near" state.
     */
    var lockScreenDelay by configDelegate(KEY_LOCK_SCREEN_DELAY, DEFAULT_LOCK_SCREEN_DELAY)
}

val Cfg.keyguardUnlockedDelay: Long
    get() = lockScreenDelay.let { min(it * 3L, max(it, 5000L)) }
