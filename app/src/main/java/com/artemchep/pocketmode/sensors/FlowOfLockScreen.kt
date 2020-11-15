package com.artemchep.pocketmode.sensors

import androidx.lifecycle.LiveData
import androidx.lifecycle.asFlow
import com.artemchep.pocketmode.Cfg
import com.artemchep.pocketmode.keyguardUnlockedDelay
import com.artemchep.pocketmode.models.Keyguard
import com.artemchep.pocketmode.models.PhoneCall
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.Screen
import com.artemchep.pocketmode.models.events.BeforeLockScreen
import com.artemchep.pocketmode.models.events.Idle
import com.artemchep.pocketmode.models.events.LockScreenEvent
import com.artemchep.pocketmode.models.events.OnLockScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

/**
 * @author Artem Chepurnoy
 */
fun flowOfLockScreen(
    proximityFlow: Flow<Proximity>,
    screenFlow: Flow<Screen>,
    phoneCallLiveData: LiveData<PhoneCall>,
    keyguardFlow: Flow<Keyguard>
): Flow<LockScreenEvent> {
    val phoneCallFlow = phoneCallLiveData.asFlow().distinctUntilChanged()
    return screenFlow
        .distinctUntilChanged()
        // If the screen is on -> subscribe to the
        // keyguard flow.
        .flatMapLatest { screen ->
            when (screen) {
                is Screen.On -> combine(
                    keyguardFlow.distinctUntilChanged(),
                    phoneCallFlow,
                ) { keyguard, phoneCall ->
                    when {
                        // Disable the pocket mode while the call
                        // is ongoing.
                        phoneCall is PhoneCall.Ongoing -> flowOf(false)
                        // Stay subscribed while the user is on the
                        // keyguard.
                        keyguard is Keyguard.Locked -> flowOf(true)
                        keyguard is Keyguard.Unlocked -> flow {
                            emit(true)
                            delay(Cfg.keyguardUnlockedDelay)
                            emit(false)
                        }
                        else -> throw IllegalStateException()
                    }
                }
                    .flatMapLatest { it }
                    .distinctUntilChanged()
                    .flatMapLatest { isActive ->
                        if (isActive) {
                            proximityFlow
                                .distinctUntilChanged()
                                .flatMapLatest { proximity ->
                                    when (proximity) {
                                        is Proximity.Far -> flowOf(Idle)
                                        // When the sensor gets covered, wait for a bit and if it
                                        // does not get uncovered -> send the lock screen event.
                                        is Proximity.Near -> flow {
                                            emit(BeforeLockScreen)
                                            delay(Cfg.lockScreenDelay)
                                            emit(OnLockScreen)
                                        }
                                    }
                                }
                        } else flowOf(Idle)
                    }
                is Screen.Off -> flowOf(Idle)
            }
        }
}
