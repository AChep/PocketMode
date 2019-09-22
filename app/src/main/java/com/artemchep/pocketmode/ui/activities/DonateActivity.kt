package com.artemchep.pocketmode.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.checkout.intentstarters.ActivityIntentStarter
import com.artemchep.pocketmode.models.Loader
import com.artemchep.pocketmode.ui.activities.base.BaseActivity
import com.artemchep.pocketmode.ui.items.SkuItem
import com.artemchep.pocketmode.viewmodels.DonateViewModel
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import kotlinx.android.synthetic.main.activity_donate.*

/**
 * @author Artem Chepurnoy
 */
class DonateActivity : BaseActivity(), View.OnClickListener {

    private val donateViewModel by lazy {
        ViewModelProviders.of(this).get(DonateViewModel::class.java)
    }

    private val itemAdapter by lazy { ItemAdapter<SkuItem>() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            statusBar.layoutParams.height = insets.systemWindowInsetTop
            recyclerView.updatePadding(
                top = insets.systemWindowInsetTop +
                        resources.getDimensionPixelSize(R.dimen.actionBarSize),
                bottom = insets.systemWindowInsetBottom
            )
            toolbarContent.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )

            insets.consumeSystemWindowInsets()
        }

        backBtn.setOnClickListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = FastAdapter.with<SkuItem, ItemAdapter<*>>(itemAdapter)
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
                    errorView.isVisible = false
                    progressView.isVisible = false
                    recyclerView.isVisible = true

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
                    errorView.isVisible = false
                    progressView.isVisible = true
                    recyclerView.isVisible = false
                }
                is Loader.Error -> {
                    errorView.isVisible = true
                    progressView.isVisible = false
                    recyclerView.isVisible = false
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        donateViewModel.result(requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backBtn -> supportFinishAfterTransition()
        }
    }

}
