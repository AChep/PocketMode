package com.artemchep.pocketmode.services

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.artemchep.pocketmode.viewmodels.PocketViewModel

/**
 * @author Artem Chepurnoy
 */
class PocketServiceRestartWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val intent = Intent(applicationContext, PocketViewModel::class.java)
        applicationContext.startForegroundService(intent)

        return Result.success()
    }
}
