package com.artemchep.pocketmode.ui.items

import android.view.View
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.databinding.ItemSensorBinding
import com.artemchep.pocketmode.ext.getStringOrEmpty
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.sensors.ProximitySensorSnapshot
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.items.AbstractItem

/**
 * @author Artem Chepurnoy
 */
class ProximitySensorSnapshotItem(
    val snapshot: ProximitySensorSnapshot
) : AbstractItem<ProximitySensorSnapshotItem, ProximitySensorSnapshotItem.ViewHolder>() {

    override fun getType(): Int = 0

    override fun getLayoutRes(): Int = R.layout.item_sensor

    override fun getViewHolder(v: View): ViewHolder = ViewHolder(ItemSensorBinding.bind(v))

    class ViewHolder(
        private val viewBinding: ItemSensorBinding
    ) : FastAdapter.ViewHolder<ProximitySensorSnapshotItem>(viewBinding.root) {
        override fun bindView(item: ProximitySensorSnapshotItem, payloads: MutableList<Any>) {
            viewBinding.titleTextView.text = item.snapshot.name
            viewBinding.summaryTextView.text = item.snapshot.id.toString()
            viewBinding.proximityCmText.text =
                itemView.context.getStringOrEmpty(R.string.cm, item.snapshot.distance)

            val iconRes = when (item.snapshot.proximity) {
                Proximity.Far -> R.drawable.ic_eye
                Proximity.Near -> R.drawable.ic_eye_off
            }
            viewBinding.proximityIcon.setImageResource(iconRes)
        }

        override fun unbindView(item: ProximitySensorSnapshotItem) {
        }
    }

}