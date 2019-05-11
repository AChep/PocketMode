package com.artemchep.pocketmode.ui.items

import android.view.View
import com.artemchep.pocketmode.R
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_donation.*
import org.solovyev.android.checkout.Sku

/**
 * @author Artem Chepurnoy
 */
class SkuItem(
    val sku: Sku
) : AbstractItem<SkuItem, SkuItem.ViewHolder>() {

    override fun getType(): Int = 0

    override fun getLayoutRes(): Int = R.layout.item_donation

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(view: View) : FastAdapter.ViewHolder<SkuItem>(view), LayoutContainer {

        override val containerView: View?
            get() = itemView

        override fun bindView(item: SkuItem, payloads: MutableList<Any>) {
            priceTextView.text = item.sku.price
            titleTextView.text = item.sku.displayTitle
            summaryTextView.text = item.sku.description
        }

        override fun unbindView(item: SkuItem) {
        }

    }

}