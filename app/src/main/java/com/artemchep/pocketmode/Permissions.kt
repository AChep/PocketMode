package com.artemchep.pocketmode

import android.Manifest
import android.os.Build

val READ_PHONE_STATE_PERMISSIONS = listOf(
    Manifest.permission.READ_PHONE_STATE
)

val NOTIFICATION_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    listOf(Manifest.permission.POST_NOTIFICATIONS)
} else {
    emptyList()
}
