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
    const val KEY_VIBRATE_ON_BEFORE_LOCK_SCREEN = "pocket::vibrate_on_before_lock_screen"
    const val DEFAULT_VIBRATE_ON_BEFORE_LOCK_SCREEN = true
    const val KEY_OVERLAY_ON_BEFORE_LOCK_SCREEN = "pocket::overlay_on_before_lock_screen"
    const val DEFAULT_OVERLAY_ON_BEFORE_LOCK_SCREEN = false
    const val KEY_PROXIMITY_WAKE_LOCK = "pocket::proximity_wake_lock"
    const val DEFAULT_PROXIMITY_WAKE_LOCK = true
    const val KEY_ANALYTICS = "pocket::analytics"
    const val DEFAULT_ANALYTICS = true

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
     * `true` if the service should be running,
     * `false` otherwise.
     */
    var vibrateOnBeforeLockScreen by configDelegate(
        KEY_VIBRATE_ON_BEFORE_LOCK_SCREEN,
        DEFAULT_VIBRATE_ON_BEFORE_LOCK_SCREEN
    )

    /**
     * `true` if the overlay should be shown before locking the screen,
     * `false` otherwise.
     */
    var overlayOnBeforeLockScreen by configDelegate(
        KEY_OVERLAY_ON_BEFORE_LOCK_SCREEN,
        DEFAULT_OVERLAY_ON_BEFORE_LOCK_SCREEN
    )
    var proximityWakeLock by configDelegate(
        KEY_PROXIMITY_WAKE_LOCK,
        DEFAULT_PROXIMITY_WAKE_LOCK
    )
    var analytics by configDelegate(
        KEY_ANALYTICS,
        DEFAULT_ANALYTICS
    )

    /**
     * The delay before screen should turn off after proximity
     * sensor reported "near" state.
     */
    var lockScreenDelay by configDelegate(KEY_LOCK_SCREEN_DELAY, DEFAULT_LOCK_SCREEN_DELAY)
}

val Cfg.keyguardUnlockedDelay: Long
    get() = lockScreenDelay.let { min(it * 3L, max(it, 5000L)) }
