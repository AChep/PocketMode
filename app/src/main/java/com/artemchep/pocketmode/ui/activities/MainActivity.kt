package com.artemchep.pocketmode.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.SeekBar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.callbacks.onShow
import com.artemchep.pocketmode.*
import com.artemchep.pocketmode.analytics.AnalyticsHolder
import com.artemchep.pocketmode.analytics.AnalyticsHolderImpl
import com.artemchep.pocketmode.databinding.ActivityMainBinding
import com.artemchep.pocketmode.ext.getStringOrEmpty
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.ui.activities.base.BaseActivity
import com.artemchep.pocketmode.util.ObserverConsumer
import com.artemchep.pocketmode.viewmodels.MainViewModel
import eightbitlab.com.blurview.RenderScriptBlur

/**
 * @author Artem Chepurnoy
 */
class MainActivity : BaseActivity(),
    AnalyticsHolder by AnalyticsHolderImpl(),
    View.OnClickListener {

    companion object {
        private const val DD = 100

        private const val RC_RUNTIME_PERMISSIONS = 100
        private const val RC_ACCESSIBILITY = 200
        private const val RC_OVERLAYS = 300
    }

    private val mainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private var isMasterSwitchBroadcasting = false

    private var isVibrateOnBeforeLockScreenSwitchBroadcasting = false

    private var isOverlayOnBeforeLockScreenSwitchBroadcasting = false

    private var isProximityWakeLockSwitchBroadcasting = false

    private var isAnalyticsSwitchBroadcasting = false

    private val viewBinding by lazy {
        ActivityMainBinding.bind(findViewById(android.R.id.content))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewBinding.toolbarBlurView
            .setupWith(viewBinding.root)
            .setBlurAlgorithm(RenderScriptBlur(this))
            .setBlurRadius(5f)
            .setBlurAutoUpdate(true)
            .setHasFixedTransformationMatrix(true)

        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { _, insets ->
            viewBinding.statusBar.layoutParams.height = insets.systemWindowInsetTop
            viewBinding.scrollView.updatePadding(
                top = insets.systemWindowInsetTop,
                bottom = insets.systemWindowInsetBottom
            )
            viewBinding.toolbarContent.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )
            viewBinding.scrollViewContent.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )

            insets.consumeSystemWindowInsets()
        }

        viewBinding.mainStub.lockScreenDelaySeekBar.max =
            resources.getInteger(R.integer.maxDelay) / DD
        viewBinding.mainStub.lockScreenDelaySeekBar.min =
            resources.getInteger(R.integer.minDelay) / DD
        viewBinding.mainStub.lockScreenDelaySeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val delay = progress * DD
                bindLockScreenDelay(delay.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val delay = viewBinding.mainStub.lockScreenDelaySeekBar.progress * DD
                mainViewModel.setLockScreenDelay(delay.toLong())
            }
        })

        viewBinding.mainStub.lockScreenDelayMin.text =
            getStringOrEmpty(R.string.ms, viewBinding.mainStub.lockScreenDelaySeekBar.min * DD)
        viewBinding.mainStub.lockScreenDelayMax.text =
            getStringOrEmpty(R.string.ms, viewBinding.mainStub.lockScreenDelaySeekBar.max * DD)

        viewBinding.masterSwitch.setOnCheckedChangeListener { switch, isChecked ->
            if (isMasterSwitchBroadcasting) {
                return@setOnCheckedChangeListener
            }

            isMasterSwitchBroadcasting = true

            // Revert to old state of the switch, because we can not
            // change the state from the view.
            switch.isChecked = mainViewModel.masterSwitchIsCheckedLiveData.value!!

            // Ask the view model to change the state.
            mainViewModel.setMasterSwitchEnabled(isChecked)

            isMasterSwitchBroadcasting = false
        }

        viewBinding.mainStub.vibrateOnBeforeLockScreenCheckBox.setOnCheckedChangeListener { switch, isChecked ->
            if (isVibrateOnBeforeLockScreenSwitchBroadcasting) {
                return@setOnCheckedChangeListener
            }

            isVibrateOnBeforeLockScreenSwitchBroadcasting = true

            // Revert to old state of the switch, because we can not
            // change the state from the view.
            switch.isChecked = mainViewModel.vibrateBeforeLockingSwitchIsCheckedLiveData.value!!

            // Ask the view model to change the state.
            mainViewModel.setVibrateOnBeforeLockScreen(isChecked)

            isVibrateOnBeforeLockScreenSwitchBroadcasting = false
        }

        viewBinding.mainStub.overlayOnBeforeLockScreenCheckBox.setOnCheckedChangeListener { switch, isChecked ->
            if (isOverlayOnBeforeLockScreenSwitchBroadcasting) {
                return@setOnCheckedChangeListener
            }

            isOverlayOnBeforeLockScreenSwitchBroadcasting = true

            // Revert to old state of the switch, because we can not
            // change the state from the view.
            switch.isChecked = mainViewModel.overlayBeforeLockingSwitchIsCheckedLiveData.value!!

            // Ask the view model to change the state.
            mainViewModel.setOverlayOnBeforeLockScreen(isChecked)

            isOverlayOnBeforeLockScreenSwitchBroadcasting = false
        }

        viewBinding.mainStub.proximityWakeLockCheckBox.setOnCheckedChangeListener { switch, isChecked ->
            if (isProximityWakeLockSwitchBroadcasting) {
                return@setOnCheckedChangeListener
            }

            isProximityWakeLockSwitchBroadcasting = true

            // Revert to old state of the switch, because we can not
            // change the state from the view.
            switch.isChecked = mainViewModel.proximityWakeLockIsCheckedLiveData.value!!

            // Ask the view model to change the state.
            mainViewModel.setProximityWakeLock(isChecked)

            isProximityWakeLockSwitchBroadcasting = false
        }

        viewBinding.mainStub.analyticsCheckBox.isVisible = BuildConfig.ANALYTICS
        viewBinding.mainStub.analyticsCheckBox.setOnCheckedChangeListener { switch, isChecked ->
            if (isAnalyticsSwitchBroadcasting) {
                return@setOnCheckedChangeListener
            }

            isAnalyticsSwitchBroadcasting = true

            // Revert to old state of the switch, because we can not
            // change the state from the view.
            switch.isChecked = mainViewModel.analyticsIsCheckedLiveData.value!!

            // Ask the view model to change the state.
            mainViewModel.setAnalytics(isChecked)

            isAnalyticsSwitchBroadcasting = false
        }

        viewBinding.toolbar.setOnClickListener(this)
        viewBinding.accessStub.accessibilityServiceBtn.setOnClickListener(this)
        viewBinding.accessStub.callStateBtn.setOnClickListener(this)
        viewBinding.mainStub.codeBtn.setOnClickListener(this)
        viewBinding.mainStub.donateBtn.setOnClickListener(this)
        viewBinding.mainStub.bugReportBtn.setOnClickListener(this)
        viewBinding.mainStub.translateBtn.setOnClickListener(this)
        viewBinding.troubleshootingStub.labBtn.setOnClickListener(this)
        viewBinding.troubleshootingStub.troubleshootingDontKillMyApp.setOnClickListener(this)
        viewBinding.troubleshootingStub.lockScreenBtn.setOnClickListener(this)
        viewBinding.mainStub.lockScreenDelayResetBtn.setOnClickListener(this)

        viewBinding.mainStub.aboutAuthor.text = getStringOrEmpty(
            R.string.about_author,
            getString(R.string.about_author_artem_chepurnoy)
        )

        mainViewModel.setup()
    }

    private fun MainViewModel.setup() {
        lockScreenDelayLiveData.observe(this@MainActivity, Observer {
            viewBinding.mainStub.lockScreenDelaySeekBar.progress = it.toInt() / DD
            bindLockScreenDelay(it)
        })
        masterSwitchIsCheckedLiveData.observe(this@MainActivity, Observer {
            isMasterSwitchBroadcasting = true
            viewBinding.masterSwitch.isChecked = it
            isMasterSwitchBroadcasting = false
        })
        vibrateBeforeLockingSwitchIsCheckedLiveData.observe(this@MainActivity, Observer {
            isVibrateOnBeforeLockScreenSwitchBroadcasting = true
            viewBinding.mainStub.vibrateOnBeforeLockScreenCheckBox.isChecked = it
            isVibrateOnBeforeLockScreenSwitchBroadcasting = false
        })
        overlayBeforeLockingSwitchIsCheckedLiveData.observe(this@MainActivity, Observer {
            isOverlayOnBeforeLockScreenSwitchBroadcasting = true
            viewBinding.mainStub.overlayOnBeforeLockScreenCheckBox.isChecked = it
            isOverlayOnBeforeLockScreenSwitchBroadcasting = false
        })
        proximityWakeLockIsCheckedLiveData.observe(this@MainActivity, Observer {
            isProximityWakeLockSwitchBroadcasting = true
            viewBinding.mainStub.proximityWakeLockCheckBox.isChecked = it
            isProximityWakeLockSwitchBroadcasting = false
        })
        analyticsIsCheckedLiveData.observe(this@MainActivity, Observer {
            isAnalyticsSwitchBroadcasting = true
            viewBinding.mainStub.analyticsCheckBox.isChecked = it
            isAnalyticsSwitchBroadcasting = false
        })
        proximityBinaryLiveData.observe(this@MainActivity, Observer {
            val iconRes = when (it) {
                Proximity.Far -> R.drawable.ic_eye
                Proximity.Near -> R.drawable.ic_eye_off
            }
            viewBinding.troubleshootingStub.proximityIcon.setImageResource(iconRes)

            // Log the changes in a proximity
            // sensors; maybe this will help to investigate issues with
            // some devices.
            analytics.logTestProximitySensorChange(it)
        })
        proximityLiveData.observe(this@MainActivity, Observer {
            viewBinding.troubleshootingStub.proximityCmText.text = getStringOrEmpty(R.string.cm, it)
        })
        appInfoLiveData.observe(this@MainActivity, Observer {
            viewBinding.aboutAppVersionInfo.text = it
        })
        // Permissions
        isAccessibilityGranted.observe(this@MainActivity, Observer {
            viewBinding.accessStub.accessibilityServiceBtn.isGone = it
        })
        isReadPhoneCallGranted.observe(this@MainActivity, Observer {
            viewBinding.accessStub.callStateBtn.isGone = it
        })
        isAllGranted.observe(this@MainActivity, Observer {
            viewBinding.accessContainer.isGone = it
        })
        isRequiredGranted.observe(this@MainActivity, Observer {
            viewBinding.toolbar.isEnabled = it
            viewBinding.troubleshootingStub.lockScreenBtn.isEnabled = it
            viewBinding.masterSwitch.isEnabled = it
        })
        // Events
        openAccessibilityLiveData.observe(this@MainActivity, ObserverConsumer {
            openAccessibilitySettings()
        })
        openOverlaysLiveData.observe(this@MainActivity, ObserverConsumer {
            openOverlaysSettings()
        })
        openRuntimePermissionLiveData.observe(this@MainActivity, ObserverConsumer {
            openRuntimePermissions(it.permissions)
        })
        openUrlLiveData.observe(this@MainActivity, ObserverConsumer {
            openUrl(it.url)
        })
        openDonateToMeLiveData.observe(this@MainActivity, ObserverConsumer {
            val intent = Intent(this@MainActivity, DonateActivity::class.java)
            startActivity(intent)
        })
        openLabLiveData.observe(this@MainActivity, ObserverConsumer {
            val intent = Intent(this@MainActivity, LabActivity::class.java)
            startActivity(intent)
        })
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivityForResult(intent, RC_ACCESSIBILITY)
    }

    private fun openOverlaysSettings() {
        MaterialDialog(this).show {
            title(R.string.access)
            message(R.string.access_overlays)
            positiveButton(res = android.R.string.ok) {
                val intent = Intent(android.provider.Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    .apply {
                        data = "package:$packageName".toUri()
                    }
                startActivityForResult(intent, RC_ACCESSIBILITY)
            }

            onShow { dialog ->
                val typedValue = TypedValue()
                val styledAttribute =
                    obtainStyledAttributes(
                        typedValue.data,
                        intArrayOf(android.R.attr.textColorPrimary)
                    )

                val textColorPrimary = styledAttribute.getColorStateList(0)
                dialog.getActionButton(WhichButton.POSITIVE).setTextColor(textColorPrimary)

                styledAttribute.recycle()
            }
        }
    }

    private fun openRuntimePermissions(permissions: List<String>) {
        requestPermissions(permissions.toTypedArray(), RC_RUNTIME_PERMISSIONS)
    }

    private fun openUrl(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()

        try {
            customTabsIntent.launchUrl(this, Uri.parse(url))
        } catch (_: Exception) {
        }
    }

    private fun bindLockScreenDelay(delay: Long) {
        viewBinding.mainStub.lockScreenDelayCur.text =
            getStringOrEmpty(R.string.settings_lock_screen_delay_cur, delay)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.toolbar -> viewBinding.masterSwitch.isChecked = !viewBinding.masterSwitch.isChecked
            R.id.lockScreenBtn -> mainViewModel.lockScreen()
            R.id.lockScreenDelayResetBtn -> mainViewModel.setLockScreenDelay()
            // Help
            R.id.donateBtn -> mainViewModel.openDonateToMe()
            R.id.codeBtn -> mainViewModel.openRepo()
            R.id.bugReportBtn -> mainViewModel.openBugReport()
            R.id.troubleshootingDontKillMyApp -> mainViewModel.openBugReportDontKillMyApp()
            R.id.translateBtn -> mainViewModel.openTranslationService()
            R.id.labBtn -> mainViewModel.openLab()
            // Permissions
            R.id.accessibilityServiceBtn -> mainViewModel.grantAccessibilityService()
            R.id.callStateBtn -> mainViewModel.grantCallState()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_ACCESSIBILITY -> sendLocalBroadcast(INTENT_ACCESSIBILITY_CHANGED)
            RC_OVERLAYS -> sendLocalBroadcast(INTENT_OVERLAYS_CHANGED)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        sendLocalBroadcast(INTENT_RUNTIME_PERMISSIONS_CHANGED)
    }

}
