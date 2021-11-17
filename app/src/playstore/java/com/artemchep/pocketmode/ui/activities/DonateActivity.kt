package com.artemchep.pocketmode.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.checkout.intentstarters.ActivityIntentStarter
import com.artemchep.pocketmode.databinding.ActivityDonateBinding
import com.artemchep.pocketmode.models.Loader
import com.artemchep.pocketmode.ui.activities.base.BaseActivity
import com.artemchep.pocketmode.ui.items.SkuItem
import com.artemchep.pocketmode.viewmodels.DonateViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter

/**
 * @author Artem Chepurnoy
 */
class DonateActivity : BaseActivity(), View.OnClickListener {

    private val donateViewModel by lazy {
        ViewModelProvider(this).get(DonateViewModel::class.java)
    }

    private val itemAdapter by lazy { ItemAdapter<SkuItem>() }

    private val viewBinding by lazy {
        ActivityDonateBinding.bind(findViewById(R.id.content))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        viewBinding.topAppBar.setNavigationOnClickListener {
            supportFinishAfterTransition()
        }

        viewBinding.recyclerView.layoutManager = LinearLayoutManager(this)
        viewBinding.recyclerView.adapter = FastAdapter.with<SkuItem, ItemAdapter<*>>(itemAdapter)
            .withOnClickListener { _, _, item, _ ->
                val intentStarter = ActivityIntentStarter(this)
                donateViewModel.purchase(intentStarter, item.sku)

                // We handled the click
                true
            }

        donateViewModel.setup()
    }

    private fun DonateViewModel.setup() {
        productLiveData.observe(this@DonateActivity, Observer {
            when (it) {
                is Loader.Ok -> {
                    viewBinding.errorView.isVisible = false
                    viewBinding.progressView.isVisible = false
                    viewBinding.recyclerView.isVisible = true

                    // Bind products to recycler view.
                    val items = it.value
                        .flatMap { product ->
                            product.skus
                                .map { sku ->
                                    SkuItem(
                                        sku = sku,
                                        isPurchased = product.isPurchased(sku)
                                    )
                                }
                        }
                        .sortedBy { sku -> sku.sku.detailedPrice.amount }

                    itemAdapter.setNewList(items)
                }
                is Loader.Loading -> {
                    viewBinding.errorView.isVisible = false
                    viewBinding.progressView.isVisible = true
                    viewBinding.recyclerView.isVisible = false
                }
                is Loader.Error -> {
                    viewBinding.errorView.isVisible = true
                    viewBinding.progressView.isVisible = false
                    viewBinding.recyclerView.isVisible = false
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        donateViewModel.result(requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
    }

}
