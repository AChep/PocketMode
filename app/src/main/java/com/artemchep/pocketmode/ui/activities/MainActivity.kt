package com.artemchep.pocketmode.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.afollestad.materialdialogs.MaterialDialog
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.models.`fun`.Either
import com.artemchep.pocketmode.viewmodels.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @author Artem Chepurnoy
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        private const val DD = 100
    }

    private val mainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private var isMasterSwitchBroadcasting = false

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
            switch.isChecked = mainViewModel.masterSwitchLiveData.value!!

            // Ask the view model to change the state.
            mainViewModel.setMasterSwitchEnabled(isChecked)

            isMasterSwitchBroadcasting = false
        }

        toolbar.setOnClickListener(this)
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
        masterSwitchLiveData.observe(this@MainActivity, Observer {
            isMasterSwitchBroadcasting = true
            masterSwitch.isChecked = it
            isMasterSwitchBroadcasting = false
        })
        openAccessibilityLiveData.observe(this@MainActivity, Observer {
            val v = it.consume()
            if (v is Either.Right) {
                openAccessibilitySettings()
            }
        })
        openUrlLiveData.observe(this@MainActivity, Observer {
            val v = it.consume()
            if (v is Either.Right) {
                openUrl(v.b.url)
            }
        })
    }

    private fun openAccessibilitySettings() {
        MaterialDialog(this).show {
            title(res = R.string.accessibility_why_title)
            message(res = R.string.accessibility_why_description)

            // Open accessibility settings on positive button text.
            positiveButton(res = android.R.string.ok)
            positiveButton {
                val intent = Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivity(intent)
            }
        }
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
            R.id.donateBtn -> {
                val message = getString(R.string.coming_soon)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
            R.id.lockScreenDelayResetBtn -> mainViewModel.setLockScreenDelay()
            R.id.codeBtn -> mainViewModel.openRepo()
            R.id.bugReportBtn -> mainViewModel.openBugReport()
            R.id.translateBtn -> {
                val message = getString(R.string.coming_soon)
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
