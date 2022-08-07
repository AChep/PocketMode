package com.artemchep.pocketmode.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.Observer
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.artemchep.config.Config
import com.artemchep.pocketmode.Cfg
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.ext.heart
import com.artemchep.pocketmode.ext.vibrateOneShot
import com.artemchep.pocketmode.models.`fun`.Either
import com.artemchep.pocketmode.models.events.*
import com.artemchep.pocketmode.ui.widgets.OverlayWidget
import com.artemchep.pocketmode.viewmodels.PocketViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.Duration
import kotlin.coroutines.CoroutineContext


/**
 * @author Artem Chepurnoy
 */
class PocketService : Service(), CoroutineScope {
    companion object {
        private const val NOTIFICATION_CHANNEL = "pocket_notification_channel"
        private const val NOTIFICATION_ID = 112

        var running = false
    }

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val windowManager by lazy { getSystemService<WindowManager>() }

    private val windowOverlayWidget by lazy {
        // Create a view overlay that is going to be shown
        // when the proximity sensor is covered.
        OverlayWidget(this)
    }

    private var overlayExitJob: Job? = null

    private val overlayObserver = Observer<Boolean> { event ->
        if (event) {
            overlayExitJob?.cancel()

            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_FULLSCREEN,
                PixelFormat.TRANSLUCENT
            )

            try {
                windowManager?.addView(windowOverlayWidget, lp)
            } catch (e: Exception) {
            }
        } else {
            // Play the exit animation and remove view.
            overlayExitJob?.cancel()
            overlayExitJob = windowOverlayWidget
                .afterExitAnimation {
                    // Try to remove the view from the window manager, don't
                    // crash if failed to.
                    try {
                        windowManager?.removeView(windowOverlayWidget)
                    } catch (_: Exception) {
                    }
                }
        }
    }

    private val pocketViewModel by lazy { PocketViewModel(applicationContext) }

    private val pocketObserver = Observer<Event<LockScreenEvent>> { event ->
        val value = event.consume()
        if (value is Either.Right) {
            when (value.b) {
                is OnLockScreen -> lockScreen()
                is BeforeLockScreen -> beforeLockScreen()
                is Idle -> {
                    // Do nothing
                }
            }
        }
    }

    private val configObserver = object : Config.OnConfigChangedListener<String> {
        override fun onConfigChanged(keys: Set<String>) {
            if (Cfg.KEY_ENABLED in keys) {
                stopSelfIfPocketServiceIsDisabled()
            }
        }
    }

    private fun lockScreen() {
        heart.analytics.logPocketModeTriggered()
        PocketAccessibilityService.sendLockScreenEvent(this, javaClass)
    }

    private fun beforeLockScreen() {
        // Vibrate slightly when the proximity sensor
        // detects 'near'.
        if (Cfg.vibrateOnBeforeLockScreen) {
            vibrateOneShot(Cfg.vibrateDurationBeforeLockScreen.coerceAtLeast(0L))
        }
    }

    override fun onCreate() {
        job = Job()
        running = true
        super.onCreate()
        Cfg.observe(configObserver)
        stopSelfIfPocketServiceIsDisabled()

        launch {
            pocketViewModel.lockScreenEventFlow.collect { event ->
                when (event) {
                    is OnLockScreen -> lockScreen()
                    is BeforeLockScreen -> beforeLockScreen()
                    is Idle -> {
                        // Do nothing.
                    }
                }
            }
        }
        launch {
            pocketViewModel.overlayFlow.collect {
                overlayObserver.onChanged(it)
            }
        }
    }

    private fun stopSelfIfPocketServiceIsDisabled() {
        val shouldBeShutdown = !Cfg.isEnabled
        if (shouldBeShutdown) {
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    private fun createNotification(): Notification {
        // Create notification channel
        val channelName = getString(R.string.notification_pocket_channel)
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
        val nm = getSystemService<NotificationManager>()!!
        nm.createNotificationChannel(channel)

        val description = getString(R.string.notification_pocket_description)
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_outline_lock)
            .setContentTitle(getString(R.string.notification_pocket_title))
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .build()
    }

    override fun onDestroy() {
        Cfg.removeObserver(configObserver)
        super.onDestroy()
        job.cancel()
        overlayExitJob?.cancel()
        running = false
    }

    override fun onBind(intent: Intent?): IBinder? = null
}