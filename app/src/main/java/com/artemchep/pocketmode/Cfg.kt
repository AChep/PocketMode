package com.artemchep.pocketmode

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.properties.ReadWriteProperty
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KProperty

interface Config {
    interface OnConfigChangedListener<T> {
        fun onConfigChanged(keys: Set<T>)
    }
}

/**
 * @author Artem Chepurnoy
 */
object Cfg {
    private const val PREFS_NAME = "config"

    const val KEY_ENABLED = "pocket::enabled"
    private const val DEFAULT_ENABLED = false
    const val KEY_VIBRATE_ON_BEFORE_LOCK_SCREEN = "pocket::vibrate_on_before_lock_screen"
    const val DEFAULT_VIBRATE_ON_BEFORE_LOCK_SCREEN = true
    const val KEY_VIBRATE_DURATION_BEFORE_LOCK_SCREEN = "pocket::vibrate_duratino_before_lock_screen"
    const val DEFAULT_VIBRATE_DURATION_BEFORE_LOCK_SCREEN = 50L // ms
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

    private lateinit var prefs: SharedPreferences
    private val observers = CopyOnWriteArraySet<Config.OnConfigChangedListener<String>>()

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun edit(context: Context, block: () -> Unit) {
        if (!::prefs.isInitialized) {
            init(context)
        }

        block()
    }

    fun observe(observer: Config.OnConfigChangedListener<String>) {
        observers += observer
    }

    fun removeObserver(observer: Config.OnConfigChangedListener<String>) {
        observers -= observer
    }

    private fun notifyChanged(key: String) {
        if (observers.isEmpty()) {
            return
        }

        val keys = setOf(key)
        observers.forEach { it.onConfigChanged(keys) }
    }

    private fun requirePrefs(): SharedPreferences =
        checkNotNull(if (::prefs.isInitialized) prefs else null) {
            "Cfg.init(context) must be called before using preferences."
        }

    private fun booleanConfigDelegate(
        key: String,
        defaultValue: Boolean
    ): ReadWriteProperty<Any?, Boolean> = object : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean =
            requirePrefs().getBoolean(key, defaultValue)

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            val prefs = requirePrefs()
            if (prefs.getBoolean(key, defaultValue) == value) {
                return
            }

            prefs.edit { putBoolean(key, value) }
            notifyChanged(key)
        }
    }

    private fun longConfigDelegate(
        key: String,
        defaultValue: Long
    ): ReadWriteProperty<Any?, Long> = object : ReadWriteProperty<Any?, Long> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Long =
            requirePrefs().getLong(key, defaultValue)

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
            val prefs = requirePrefs()
            if (prefs.getLong(key, defaultValue) == value) {
                return
            }

            prefs.edit { putLong(key, value) }
            notifyChanged(key)
        }
    }

    /**
     * `true` if the service should be running,
     * `false` otherwise.
     */
    var isEnabled by booleanConfigDelegate(KEY_ENABLED, DEFAULT_ENABLED)

    /**
     * `true` if the service should be running,
     * `false` otherwise.
     */
    var vibrateOnBeforeLockScreen by booleanConfigDelegate(
        KEY_VIBRATE_ON_BEFORE_LOCK_SCREEN,
        DEFAULT_VIBRATE_ON_BEFORE_LOCK_SCREEN
    )

    var vibrateDurationBeforeLockScreen by longConfigDelegate(
        KEY_VIBRATE_DURATION_BEFORE_LOCK_SCREEN,
        DEFAULT_VIBRATE_DURATION_BEFORE_LOCK_SCREEN
    )

    /**
     * `true` if the overlay should be shown before locking the screen,
     * `false` otherwise.
     */
    var overlayOnBeforeLockScreen by booleanConfigDelegate(
        KEY_OVERLAY_ON_BEFORE_LOCK_SCREEN,
        DEFAULT_OVERLAY_ON_BEFORE_LOCK_SCREEN
    )
    var proximityWakeLock by booleanConfigDelegate(
        KEY_PROXIMITY_WAKE_LOCK,
        DEFAULT_PROXIMITY_WAKE_LOCK
    )
    var analytics by booleanConfigDelegate(
        KEY_ANALYTICS,
        DEFAULT_ANALYTICS
    )

    /**
     * The delay before screen should turn off after proximity
     * sensor reported "near" state.
     */
    var lockScreenDelay by longConfigDelegate(KEY_LOCK_SCREEN_DELAY, DEFAULT_LOCK_SCREEN_DELAY)
}

val Cfg.keyguardUnlockedDelay: Long get() = keyguardUnlockedDelay(lockScreenDelay)

fun keyguardUnlockedDelay(lockScreenDelay: Long) =
    (lockScreenDelay * 3L)
        .coerceAtLeast(3000L)
        .coerceAtMost(max(8000L, 1000L + lockScreenDelay))
