package com.artemchep.pocketmode.ext

import android.view.View
import android.view.Window
import com.artemchep.pocketmode.models.events.StatusBarColor

fun Window.setStatusBarColor(statusBarColor: StatusBarColor) {
    val visibility = when (statusBarColor) {
        StatusBarColor.DARK -> decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        StatusBarColor.LIGHT -> decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    decorView.systemUiVisibility = visibility
}
