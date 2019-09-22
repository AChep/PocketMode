package com.artemchep.pocketmode.ui.activities

import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.ui.activities.base.BaseActivity
import com.artemchep.pocketmode.ui.items.ProximitySensorSnapshotItem
import com.artemchep.pocketmode.viewmodels.LabViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.activity_lab.*

/**
 * @author Artem Chepurnoy
 */
class LabActivity : BaseActivity(), View.OnClickListener {

    private val labViewModel by lazy {
        ViewModelProviders.of(this).get(LabViewModel::class.java)
    }

    private val itemAdapter by lazy { ItemAdapter<ProximitySensorSnapshotItem>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            statusBar.layoutParams.height = insets.systemWindowInsetTop
            scrollView.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            toolbarContent.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )
            scrollViewContent.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )

            insets.consumeSystemWindowInsets()
        }

        backBtn.setOnClickListener(this)

        sensorsRecyclerView.layoutManager = LinearLayoutManager(this)
        sensorsRecyclerView.adapter =
            FastAdapter.with<ProximitySensorSnapshotItem, ItemAdapter<*>>(itemAdapter)

        labViewModel.setup()
    }

    private fun LabViewModel.setup() {
        proximityLabLiveData.observe(this@LabActivity, Observer {
            val items = it.map(::ProximitySensorSnapshotItem)
            itemAdapter.setNewList(items)
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backBtn -> supportFinishAfterTransition()
        }
    }

}
