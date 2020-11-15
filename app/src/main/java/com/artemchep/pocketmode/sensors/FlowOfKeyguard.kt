package com.artemchep.pocketmode.sensors

import android.content.Context
import com.artemchep.pocketmode.ext.isKeyguardLocked
import com.artemchep.pocketmode.models.Keyguard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

private const val KEYGUARD_CHECK_INTERVAL = 300L

/**
 * @author Artem Chepurnoy
 */
fun flowOfKeyguard(
    context: Context
): Flow<Keyguard> = flow<Keyguard> {
    while (true) {
        val keyguard = when (context.isKeyguardLocked()) {
            true -> Keyguard.Locked
            false -> Keyguard.Unlocked
        }
        emit(keyguard)
        delay(KEYGUARD_CHECK_INTERVAL)
    }
}
    .flowOn(Dispatchers.Main)
