package com.artemchep.pocketmode.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.isGone
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.artemchep.pocketmode.INTENT_ACCESSIBILITY_CHANGED
import com.artemchep.pocketmode.INTENT_RUNTIME_PERMISSIONS_CHANGED
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.sendLocalBroadcast
import com.artemchep.pocketmode.ui.activities.base.BaseActivity
import com.artemchep.pocketmode.util.ObserverConsumer
import com.artemchep.pocketmode.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_access.*
import kotlinx.android.synthetic.main.layout_main.*


/**
 * @author Artem Chepurnoy
 */
class MainActivity : BaseActivity(), View.OnClickListener {
    companion object {
        private const val DD = 100

        private const val RC_RUNTIME_PERMISSIONS = 100
        private const val RC_ACCESSIBILITY = 200
    }

    private val mainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private var isMasterSwitchBroadcasting = false

    private var isVibrateOnBeforeLockScreenSwitchBroadcasting = false

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

        lockScreenDelayMin.text = getString(R.string.ms, lockScreenDelaySeekBar.min * DD)
        lockScreenDelayMax.text = getString(R.string.ms, lockScreenDelaySeekBar.max * DD)

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

        toolbar.setOnClickListener(this)
        accessibilityServiceBtn.setOnClickListener(this)
        callStateBtn.setOnClickListener(this)
        codeBtn.setOnClickListener(this)
        donateBtn.setOnClickListener(this)
        bugReportBtn.setOnClickListener(this)
        translateBtn.setOnClickListener(this)
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
        // Permissions
        isAccessibilityGranted.observe(this@MainActivity, Observer {
            accessibilityServiceBtn.isGone = it
        })
        isReadPhoneCallGranted.observe(this@MainActivity, Observer {
            callStateBtn.isGone = it
        })
        isAllGranted.observe(this@MainActivity, Observer {
            toolbar.isEnabled = it
            masterSwitch.isEnabled = it
            accessContainer.isGone = it
        })
        // Events
        openAccessibilityLiveData.observe(this@MainActivity, ObserverConsumer {
            openAccessibilitySettings()
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
    }

    private fun openAccessibilitySettings() {
        val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
        startActivityForResult(intent, RC_ACCESSIBILITY)
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
        lockScreenDelayCur.text = getString(R.string.settings_lock_screen_delay_cur, delay)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.toolbar -> masterSwitch.isChecked = !masterSwitch.isChecked
            R.id.lockScreenDelayResetBtn -> mainViewModel.setLockScreenDelay()
            // Help
            R.id.donateBtn -> mainViewModel.openDonateToMe()
            R.id.codeBtn -> mainViewModel.openRepo()
            R.id.bugReportBtn -> mainViewModel.openBugReport()
            R.id.translateBtn -> mainViewModel.openTranslationService()
            // Permissions
            R.id.accessibilityServiceBtn -> mainViewModel.grantAccessibilityService()
            R.id.callStateBtn -> mainViewModel.grantCallState()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_ACCESSIBILITY -> sendLocalBroadcast(INTENT_ACCESSIBILITY_CHANGED)
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
