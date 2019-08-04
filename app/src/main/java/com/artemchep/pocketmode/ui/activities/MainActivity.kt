package com.artemchep.pocketmode.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.SeekBar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.callbacks.onShow
import com.artemchep.pocketmode.*
import com.artemchep.pocketmode.analytics.AnalyticsHolder
import com.artemchep.pocketmode.analytics.AnalyticsHolderImpl
import com.artemchep.pocketmode.ext.getStringOrEmpty
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.ui.activities.base.BaseActivity
import com.artemchep.pocketmode.util.ObserverConsumer
import com.artemchep.pocketmode.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_access.*
import kotlinx.android.synthetic.main.layout_main.*
import kotlinx.android.synthetic.main.layout_troubleshooting.*


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
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private var isMasterSwitchBroadcasting = false

    private var isVibrateOnBeforeLockScreenSwitchBroadcasting = false

    private var isOverlayOnBeforeLockScreenSwitchBroadcasting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lockScreenDelaySeekBar.max = resources.getInteger(R.integer.maxDelay) / DD
        lockScreenDelaySeekBar.min = resources.getInteger(R.integer.minDelay) / DD
        lockScreenDelaySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val delay = progress * DD
                bindLockScreenDelay(delay.toLong())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val delay = lockScreenDelaySeekBar.progress * DD
                mainViewModel.setLockScreenDelay(delay.toLong())
            }
        })

        lockScreenDelayMin.text = getStringOrEmpty(R.string.ms, lockScreenDelaySeekBar.min * DD)
        lockScreenDelayMax.text = getStringOrEmpty(R.string.ms, lockScreenDelaySeekBar.max * DD)

        masterSwitch.setOnCheckedChangeListener { switch, isChecked ->
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

        vibrateOnBeforeLockScreenCheckBox.setOnCheckedChangeListener { switch, isChecked ->
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

        overlayOnBeforeLockScreenCheckBox.setOnCheckedChangeListener { switch, isChecked ->
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

        toolbar.setOnClickListener(this)
        accessibilityServiceBtn.setOnClickListener(this)
        callStateBtn.setOnClickListener(this)
        codeBtn.setOnClickListener(this)
        donateBtn.setOnClickListener(this)
        bugReportBtn.setOnClickListener(this)
        translateBtn.setOnClickListener(this)
        labBtn.setOnClickListener(this)
        lockScreenBtn.setOnClickListener(this)
        lockScreenDelayResetBtn.setOnClickListener(this)

        mainViewModel.setup()
    }

    private fun MainViewModel.setup() {
        lockScreenDelayLiveData.observe(this@MainActivity, Observer {
            lockScreenDelaySeekBar.progress = it.toInt() / DD
            bindLockScreenDelay(it)
        })
        masterSwitchIsCheckedLiveData.observe(this@MainActivity, Observer {
            isMasterSwitchBroadcasting = true
            masterSwitch.isChecked = it
            isMasterSwitchBroadcasting = false
        })
        vibrateBeforeLockingSwitchIsCheckedLiveData.observe(this@MainActivity, Observer {
            isVibrateOnBeforeLockScreenSwitchBroadcasting = true
            vibrateOnBeforeLockScreenCheckBox.isChecked = it
            isVibrateOnBeforeLockScreenSwitchBroadcasting = false
        })
        overlayBeforeLockingSwitchIsCheckedLiveData.observe(this@MainActivity, Observer {
            isOverlayOnBeforeLockScreenSwitchBroadcasting = true
            overlayOnBeforeLockScreenCheckBox.isChecked = it
            isOverlayOnBeforeLockScreenSwitchBroadcasting = false
        })
        proximityBinaryLiveData.observe(this@MainActivity, Observer {
            val iconRes = when (it) {
                Proximity.Far -> R.drawable.ic_eye
                Proximity.Near -> R.drawable.ic_eye_off
            }
            proximityIcon.setImageResource(iconRes)

            // Log the changes in a proximity
            // sensors; maybe this will help to investigate issues with
            // some devices.
            analytics.logTestProximitySensorChange(it)
        })
        proximityLiveData.observe(this@MainActivity, Observer {
            proximityCmText.text = getStringOrEmpty(R.string.cm, it)
        })
        // Permissions
        isAccessibilityGranted.observe(this@MainActivity, Observer {
            accessibilityServiceBtn.isGone = it
        })
        isReadPhoneCallGranted.observe(this@MainActivity, Observer {
            callStateBtn.isGone = it
        })
        isAllGranted.observe(this@MainActivity, Observer {
            accessContainer.isGone = it
        })
        isRequiredGranted.observe(this@MainActivity, Observer {
            toolbar.isEnabled = it
            lockScreenBtn.isEnabled = it
            masterSwitch.isEnabled = it
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
        lockScreenDelayCur.text = getStringOrEmpty(R.string.settings_lock_screen_delay_cur, delay)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.toolbar -> masterSwitch.isChecked = !masterSwitch.isChecked
            R.id.lockScreenBtn -> mainViewModel.lockScreen()
            R.id.lockScreenDelayResetBtn -> mainViewModel.setLockScreenDelay()
            // Help
            R.id.donateBtn -> mainViewModel.openDonateToMe()
            R.id.codeBtn -> mainViewModel.openRepo()
            R.id.bugReportBtn -> mainViewModel.openBugReport()
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
