package com.artemchep.pocketmode.checkout.intentstarters

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import org.solovyev.android.checkout.IntentStarter

/**
 * @author Artem Chepurnoy
 */
class ActivityIntentStarter(private val activity: Activity) : IntentStarter {

    override fun startForResult(intentSender: IntentSender, requestCode: Int, intent: Intent) {
        activity.startIntentSenderForResult(intentSender, requestCode, intent, 0, 0, 0)
    }

}