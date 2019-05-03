package com.artemchep.pocketmode.sensors

import androidx.lifecycle.LiveData
import com.artemchep.config.Config
import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
abstract class ConfigBaseLiveData<T>(private val key: String) : LiveData<T>() {

    private val configObserver = object : Config.OnConfigChangedListener<String> {
        override fun onConfigChanged(keys: Set<String>) {
            if (key in keys) {
                updateValue()
            }
        }
    }

    override fun onActive() {
        super.onActive()
        Cfg.observe(configObserver)
        updateValue()
    }

    override fun onInactive() {
        Cfg.removeObserver(configObserver)
        super.onInactive()
    }

    protected abstract fun updateValue()

}
