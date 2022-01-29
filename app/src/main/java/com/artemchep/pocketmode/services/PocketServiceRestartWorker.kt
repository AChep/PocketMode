package com.artemchep.pocketmode.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.asFlow
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.artemchep.pocketmode.Cfg
import com.artemchep.pocketmode.R
import com.artemchep.pocketmode.sensors.AccessAccessibilityLiveData
import com.artemchep.pocketmode.ui.activities.MainActivity
import kotlinx.coroutines.flow.first

/**
 * @author Artem Chepurnoy
 */
class PocketServiceRestartWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    companion object {
        private const val NOTIFICATION_CHANNEL = "pocket_restart_channel"
        private const val NOTIFICATION_ID = 212
    }

    override suspend fun doWork(): Result {
        val accessibilityGranted = AccessAccessibilityLiveData(applicationContext)
            .asFlow()
            .first()
        if (Cfg.isEnabled && (!PocketService.running || !accessibilityGranted)) {
            val nm = applicationContext.getSystemService<NotificationManager>()!!
            val n = createNotification(
                accessibilityGranted = accessibilityGranted,
            )
            nm.notify(NOTIFICATION_ID, n)
        }

        return Result.success()
    }

    private fun createNotification(
        accessibilityGranted: Boolean,
    ): Notification {
        // Create notification channel
        val channelName = applicationContext.getString(R.string.notification_restart_channel)
        val channel =
            NotificationChannel(
                NOTIFICATION_CHANNEL,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
        val nm = applicationContext.getSystemService<NotificationManager>()!!
        nm.createNotificationChannel(channel)

        val pi = if (accessibilityGranted) {
            val intent = Intent(applicationContext, PocketService::class.java)
            PendingIntent.getForegroundService(
                applicationContext,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            // We can not restart the service, because the system has taken the accessibility
            // permission of the app.
            val intent = Intent(applicationContext, MainActivity::class.java)
            PendingIntent.getActivity(
                applicationContext,
                NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }

        val description = applicationContext.getString(R.string.notification_restart_description)
        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
            .setAutoCancel(true)
            .setSmallIcon(R.drawable.ic_outline_lock)
            .setContentIntent(pi)
            .setColor(0xFFf4ff81.toInt())
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentTitle(applicationContext.getString(R.string.notification_restart_title))
            .setContentText(description)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .build()
    }
}
