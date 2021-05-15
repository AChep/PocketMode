package com.artemchep.pocketmode.checkout

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import com.artemchep.pocketmode.checkout.intentstarters.ActivityIntentStarter
import com.artemchep.pocketmode.checkout.intentstarters.FragmentIntentStarter
import org.solovyev.android.checkout.Billing
import org.solovyev.android.checkout.IntentStarter
import org.solovyev.android.checkout.UiCheckout

/**
 * @author Artem Chepurnoy
 */
class FlexCheckout(tag: Any, billing: Billing) : UiCheckout(tag, billing), IntentStarter {

    var intentStarter: IntentStarter? = null

    /**
     * You should only call [UiCheckout] specific methods from
     * the `with` methods.
     */
    @UiThread
    fun withActivity(activity: Activity, block: UiCheckout.() -> Unit) {
        withIntentStarter(
            ActivityIntentStarter(activity),
            block
        )
    }

    /**
     * You should only call [UiCheckout] specific methods from
     * the `with` methods.
     */
    @UiThread
    fun withFragment(fragment: Fragment, block: UiCheckout.() -> Unit) {
        withIntentStarter(
            FragmentIntentStarter(fragment),
            block
        )
    }

    @UiThread
    fun withIntentStarter(starter: IntentStarter, block: UiCheckout.() -> Unit) {
        if (intentStarter != null) {
            error("You can not nest the `with` calls.")
        }

        intentStarter = starter
        block.invoke(this)
        intentStarter = null
    }

    //
    // UiCheckout
    //

    override fun makeIntentStarter(): IntentStarter = this

    override fun startForResult(intentSender: IntentSender, requestCode: Int, intent: Intent) =
        intentStarter!!.startForResult(intentSender, requestCode, intent)

}