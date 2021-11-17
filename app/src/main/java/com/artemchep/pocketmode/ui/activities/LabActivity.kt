package com.artemchep.pocketmode.ui.activities

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.databinding.ActivityLabBinding
import com.artemchep.pocketmode.ui.activities.base.BaseActivity
import com.artemchep.pocketmode.ui.items.ProximitySensorSnapshotItem
import com.artemchep.pocketmode.viewmodels.LabViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * @author Artem Chepurnoy
 */
class LabActivity : BaseActivity(), View.OnClickListener {

    private val labViewModel by lazy {
        ViewModelProvider(this).get(LabViewModel::class.java)
    }

    private val itemAdapter by lazy { ItemAdapter<ProximitySensorSnapshotItem>() }

    private val viewBinding by lazy {
        ActivityLabBinding.bind(findViewById(R.id.content))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lab)

        viewBinding.topAppBar.setNavigationOnClickListener {
            supportFinishAfterTransition()
        }

        viewBinding.sensorsRecyclerView.layoutManager = LinearLayoutManager(this)
        viewBinding.sensorsRecyclerView.adapter =
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
    }

}
