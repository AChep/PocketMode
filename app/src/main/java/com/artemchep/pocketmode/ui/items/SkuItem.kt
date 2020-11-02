package com.artemchep.pocketmode.ui.items

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.databinding.ItemDonationBinding
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import org.solovyev.android.checkout.Sku

/**
 * @author Artem Chepurnoy
 */
class SkuItem(
    val sku: Sku,
    val isPurchased: Boolean
) : AbstractItem<SkuItem, SkuItem.ViewHolder>() {

    override fun getType(): Int = 0

    override fun getLayoutRes(): Int = R.layout.item_donation

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(ItemDonationBinding.bind(v))

    class ViewHolder(
        private val viewBinding: ItemDonationBinding
    ) : FastAdapter.ViewHolder<SkuItem>(viewBinding.root) {
        override fun bindView(item: SkuItem, payloads: MutableList<Any>) {
            viewBinding.priceTextView.text = item.sku.price
            viewBinding.priceTextView.isGone = item.isPurchased
            viewBinding.purchasedTextView.isVisible = item.isPurchased
            viewBinding.titleTextView.text = item.sku.displayTitle
            viewBinding.summaryTextView.text = item.sku.description
        }

        override fun unbindView(item: SkuItem) {
        }

    }

}