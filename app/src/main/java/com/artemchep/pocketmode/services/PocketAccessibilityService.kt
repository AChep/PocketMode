package com.artemchep.pocketmode.services

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.view.accessibility.AccessibilityEvent
import com.artemchep.pocketmode.R

/**
 * @author Artem Chepurnoy
 */
class PocketAccessibilityService : AccessibilityService() {
    companion object {
        private const val EVENT_TYPE = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
        private const val EVENT_TEXT_RES = R.string.accessibility_event_screen_locked

        fun createLockScreenEvent(context: Context) =
            AccessibilityEvent.obtain(EVENT_TYPE)
                .apply {
                    packageName = context.applicationContext.packageName
                    className = PocketService::class.java.name
                    isEnabled = true
                    text.add(context.getString(EVENT_TEXT_RES))
                }!!
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
        performGlobalAction(GLOBAL_ACTION_LOCK_SCREEN)
    }

    override fun onInterrupt() {
    }
}
