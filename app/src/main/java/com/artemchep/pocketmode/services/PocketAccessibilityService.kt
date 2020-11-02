package com.artemchep.pocketmode.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import androidx.core.content.getSystemService
import com.artemchep.pocketmode.INTENT_ACCESSIBILITY_CHANGED
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.ext.heart
import com.artemchep.pocketmode.sendLocalBroadcast

/**
 * @author Artem Chepurnoy
 */
class PocketAccessibilityService : AccessibilityService() {
    companion object {
        private const val EVENT_TYPE = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
        private const val EVENT_TEXT_RES = R.string.accessibility_event_screen_locked

        fun createLockScreenEvent(context: Context, clazz: Class<*>) =
            AccessibilityEvent.obtain(EVENT_TYPE)
                .apply {
                    packageName = context.applicationContext.packageName
                    className = clazz.name
                    isEnabled = true
                    text.add(context.getString(EVENT_TEXT_RES))
                }!!

        fun sendLockScreenEvent(context: Context, clazz: Class<*>) {
            val manager = context.getSystemService<AccessibilityManager>()!!
            if (manager.isEnabled) {
                val event = createLockScreenEvent(context, clazz)
                manager.sendAccessibilityEvent(event)
            } else {
                context.heart.analytics.log(
                    "Tried to send the lock event from [${clazz.simpleName}], " +
                            "but the accessibility manager is disabled."
                )
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val isLockScreenEvent = event
            ?.takeIf { it.eventType == EVENT_TYPE }
            ?.text
            ?.firstOrNull() == getString(EVENT_TEXT_RES)
        if (isLockScreenEvent) {
            lockScreen()
        }
    }

    /** Sends the "lock screen" command */
    private fun lockScreen() {
        heart.analytics.logPocketModeLocked()
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
    }

    override fun onCreate() {
        super.onCreate()
        sendLocalBroadcast(INTENT_ACCESSIBILITY_CHANGED)
    }

    override fun onDestroy() {
        super.onDestroy()
        sendLocalBroadcast(INTENT_ACCESSIBILITY_CHANGED)
    }

    override fun onInterrupt() {
    }
}
