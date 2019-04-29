package com.artemchep.pocketmode.ext

import android.app.KeyguardManager
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.PowerManager
import android.view.Display
import android.view.accessibility.AccessibilityManager
import androidx.core.content.getSystemService

/**
 * Return whether the keyguard is currently locked.
 */
fun Context.isKeyguardLocked(): Boolean {
    val km = getSystemService<KeyguardManager>()
    return km?.isKeyguardLocked ?: false
}

/**
 * Returns `true` if the screen is turned on,
 * `false` otherwise.
 */
fun Context.isScreenOn(): Boolean {
    run {
        val dm = getSystemService<DisplayManager>()
        val displays = dm?.getDisplays(null)?.takeIf { it.isNotEmpty() } ?: return@run

        var display: Display? = null
        for (d in displays) {
            val virtual = d.flags.and(Display.FLAG_PRESENTATION) != 0
            if (d.isValid && !virtual) {
                display = d

                val type: Int
                try {
                    val method = Display::class.java.getDeclaredMethod("getType")
                    method.isAccessible = true
                    type = method.invoke(d) as Int
                } catch (e: Exception) {
                    continue
                }

                if (type == 1 /* built-in display */) {
                    break
                }
            }
        }

        if (display == null) {
            return false
        }

        return display.state == Display.STATE_ON
    }

    val pm = getSystemService<PowerManager>()
    return pm?.isInteractive ?: false
}

fun Context.isAccessibilityServiceEnabled(): Boolean =
    getSystemService<AccessibilityManager>()?.isEnabled ?: false

