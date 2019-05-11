package com.artemchep.pocketmode.sensors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.pocketmode.listOfSkus
import com.artemchep.pocketmode.models.Loader
import org.solovyev.android.checkout.Checkout
import org.solovyev.android.checkout.Inventory
import org.solovyev.android.checkout.ProductTypes

/**
 * @author Artem Chepurnoy
 */
class ProductLiveData(
    private val checkoutLiveData: LiveData<out Checkout>
) : MediatorLiveData<Loader<Inventory.Products>>() {

    init {
        addSource(checkoutLiveData) {
            // We assume that the checkout live data
            // immediately returns the checkout object
            // once we've subscribed to it.
            loadInventory(it)
        }
    }

    private val inventoryCallback = Inventory.Callback {
        postValue(Loader.Ok(it))
    }

    fun loadInventory(checkout: Checkout = checkoutLiveData.value!!) {
        postValue(Loader.Loading())

        val request = Inventory.Request.create().apply {
            loadAllPurchases()
            loadSkus(ProductTypes.IN_APP, listOfSkus())
        }
        checkout.loadInventory(request, inventoryCallback)
    }

}