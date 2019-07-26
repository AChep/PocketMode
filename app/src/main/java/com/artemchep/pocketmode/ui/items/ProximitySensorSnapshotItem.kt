package com.artemchep.pocketmode.ui.items

import android.view.View
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.sensors.ProximitySensorSnapshot
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_sensor.*

/**
 * @author Artem Chepurnoy
 */
class ProximitySensorSnapshotItem(
    val snapshot: ProximitySensorSnapshot
) : AbstractItem<ProximitySensorSnapshotItem, ProximitySensorSnapshotItem.ViewHolder>() {

    override fun getType(): Int = 0

    override fun getLayoutRes(): Int = R.layout.item_sensor

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(v)

    class ViewHolder(
        view: View
    ) : FastAdapter.ViewHolder<ProximitySensorSnapshotItem>(view),
        LayoutContainer {

        override val containerView: View?
            get() = itemView

        override fun bindView(item: ProximitySensorSnapshotItem, payloads: MutableList<Any>) {
            titleTextView.text = item.snapshot.name
            summaryTextView.text = item.snapshot.id.toString()
            proximityCmText.text = itemView.context.getString(R.string.cm, item.snapshot.distance)

            val iconRes = when (item.snapshot.proximity) {
                Proximity.Far -> R.drawable.ic_eye
                Proximity.Near -> R.drawable.ic_eye_off
            }
            proximityIcon.setImageResource(iconRes)
        }

        override fun unbindView(item: ProximitySensorSnapshotItem) {
        }

    }

}