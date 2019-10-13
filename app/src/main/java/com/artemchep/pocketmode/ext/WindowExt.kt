package com.artemchep.pocketmode.ext

import android.view.View
import android.view.Window
import com.artemchep.pocketmode.models.events.StatusNavBarColor

fun Window.setStatusNavBarColor(statusBarColor: StatusNavBarColor) {
    val visibility = when (statusBarColor) {
        StatusNavBarColor.DARK -> decorView.systemUiVisibility and
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv() and
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        StatusNavBarColor.LIGHT -> decorView.systemUiVisibility or
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }

    decorView.systemUiVisibility = visibility
}
