package com.artemchep.pocketmode.sensors

import android.content.Context
import androidx.lifecycle.LiveData
import com.artemchep.pocketmode.Heart
import com.artemchep.pocketmode.checkout.FlexCheckout

/**
 * Live data that starts a [FlexCheckout] if someone observes it and
 * stops it later.
 *
 * @author Artem Chepurnoy
 */
class CheckoutLiveData(private val context: Context) : LiveData<FlexCheckout>() {

    companion object {
        const val TAG = "CheckoutLiveData"
    }

    private val checkout by lazy {
        val application = context.applicationContext as Heart
        return@lazy FlexCheckout(TAG, application.billing)
    }

    override fun onActive() {
        super.onActive()
        // Post the checkout instance when we went active
        // a first time.
        value ?: setValue(checkout)

        // Start the checkout process.
        checkout.start()
    }

    override fun onInactive() {
        checkout.stop()
        super.onInactive()
    }

}