package com.artemchep.pocketmode.ui.activities.base

import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.artemchep.pocketmode.ext.setStatusBarColor
import com.artemchep.pocketmode.models.events.StatusBarColor

/**
 * @author Artem Chepurnoy
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply the color of
        // the status bar.
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> window.setStatusBarColor(StatusBarColor.LIGHT)
            Configuration.UI_MODE_NIGHT_YES -> window.setStatusBarColor(StatusBarColor.DARK)
        }
    }

}