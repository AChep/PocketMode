package com.artemchep.pocketmode.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.artemchep.pocketmode.ext.isScreenOn
import com.artemchep.pocketmode.models.Screen
import com.artemchep.pocketmode.util.observerFlow
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

private const val SCREEN_ON_CHECK_INTERVAL = 600L

fun flowOfScreen(
    context: Context,
): Flow<Screen> = observerFlow<Screen> { callback ->
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val screen = isScreenOn(context)
            callback(screen)
        }
    }
    val intentFilter = IntentFilter()
        .apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
    context.registerReceiver(broadcastReceiver, intentFilter)

    val screen = isScreenOn(context)
    callback(screen)
    return@observerFlow {
        context.unregisterReceiver(broadcastReceiver)
    }
}
    .flatMapLatest { screen ->
        when (screen) {
            is Screen.On -> flow {
                emit(screen)

                // While the screen is on, send the update every
                // few seconds. This is needed because of the
                // Always On mode which may not send the 'screen is off'
                // broadcast.
                while (true) {
                    delay(SCREEN_ON_CHECK_INTERVAL)
                    val newScreen = isScreenOn(context)
                    emit(newScreen)
                }
            }
            else -> flowOf(screen)
        }
    }
    .flowOn(Dispatchers.Main)
    .distinctUntilChanged()

/**
 * It does not store the screen state, it
 * retrieves it every time.
 */
private fun isScreenOn(context: Context): Screen =
    when (context.isScreenOn()) {
        true -> Screen.On
        false -> Screen.Off
    }
