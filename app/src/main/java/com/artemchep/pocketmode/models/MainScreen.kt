package com.artemchep.pocketmode.models

data class MainScreen(
    val troubleshooting: Troubleshooting,
    val settings: Settings,
) {
    data class Troubleshooting(
        val onLockScreen: () -> Unit,
        val onLaboratoryScreen: () -> Unit,
        val proximityCm: Float,
        val proximityIsClose: Boolean,
    )

    data class Settings(
        val isVibrateBeforeLockingEnabled: Boolean,
        val onVibrateBeforeLockingChanged: (Boolean) -> Unit,
        val isShowOverlayBeforeLockingEnabled: Boolean,
        val onShowOverlayBeforeLockingChanged: (Boolean) -> Unit,
        val isTurnScreenBlackEnabled: Boolean,
        val onTurnScreenBlackChanged: (Boolean) -> Unit,
        val lockDelayMinMs: Long,
        val lockDelayMaxMs: Long,
        val lockDelayMs: Long,
    )
}