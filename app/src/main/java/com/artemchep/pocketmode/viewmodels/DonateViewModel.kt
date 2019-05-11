package com.artemchep.pocketmode.viewmodels

import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.pocketmode.checkout.FlexCheckout
import com.artemchep.pocketmode.checkout.intentstarters.ActivityIntentStarter
import com.artemchep.pocketmode.sensors.CheckoutLiveData
import com.artemchep.pocketmode.sensors.ProductLiveData
import org.solovyev.android.checkout.*


/**
 * @author Artem Chepurnoy
 */
class DonateViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val TAG = "DonateViewModel"
    }

    private val requestListener = object : RequestListener<Purchase> {
        override fun onSuccess(result: Purchase) {
            onComplete(wasOwned = false)
        }

        override fun onError(response: Int, e: Exception) {
            when (response) {
                ResponseCodes.ITEM_ALREADY_OWNED -> onComplete(wasOwned = true)
            }
        }

        private fun onComplete(wasOwned: Boolean) {
            if (wasOwned) {
                // Nothing has changed, so we don't need
                // to reload the inventory.
                return
            }

            // Update the inventory.
            if (!productLiveData.hasActiveObservers()) {
                productLiveData.loadInventory()
            }
        }
    }

    val checkoutLiveData: LiveData<FlexCheckout> = MediatorLiveData<FlexCheckout>()
        .apply {
            val source = CheckoutLiveData(application)
            addSource(source) { checkout ->
                // Add the singleton purchase flow
                // listener.
                checkout.createPurchaseFlow(requestListener)
            }
        }

    val productLiveData = ProductLiveData(checkoutLiveData)

    /**
     * Handle fragment/activity on result
     * calls.
     */
    fun result(requestCode: Int, resultCode: Int, data: Intent?) {
        val checkout = checkoutLiveData.value
        checkout?.onActivityResult(requestCode, resultCode, data)
    }

    fun purchase(intentStarter: ActivityIntentStarter, sku: Sku) {
        if (!checkoutLiveData.hasActiveObservers()) {
            val msg = "You should be subscribed to a checkout live data to perform this operation."
            Log.w(TAG, msg)
            return
        }

        val checkout = checkoutLiveData.value!!
        checkout.intentStarter = intentStarter
        checkout.whenReady(object : Checkout.EmptyListener() {
            override fun onReady(requests: BillingRequests) {
                requests.purchase(sku, null, checkout.purchaseFlow)
            }
        })
    }

}