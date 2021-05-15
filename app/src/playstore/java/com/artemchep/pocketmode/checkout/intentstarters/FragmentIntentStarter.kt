package com.artemchep.pocketmode.checkout.intentstarters

import android.content.Intent
import android.content.IntentSender
import androidx.fragment.app.Fragment
import org.solovyev.android.checkout.IntentStarter

/**
 * @author Artem Chepurnoy
 */
class FragmentIntentStarter(private val fragment: Fragment) : IntentStarter {

    override fun startForResult(intentSender: IntentSender, requestCode: Int, intent: Intent) {
        fragment.startIntentSenderForResult(intentSender, requestCode, intent, 0, 0, 0, null)
    }

}