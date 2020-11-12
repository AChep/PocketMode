package com.artemchep.pocketmode.sensors

import androidx.lifecycle.LiveData
import com.artemchep.config.Config
import com.artemchep.pocketmode.Cfg

/**
 * @author Artem Chepurnoy
 */
open class ConfigPropertyLiveData<T>(
    private val key: String,
    private val getter: () -> T
) : LiveData<T>() {
    private val configObserver = object : Config.OnConfigChangedListener<String> {
        override fun onConfigChanged(keys: Set<String>) {
            if (key in keys) {
                updateValue()
            }
        }
    }

    init {
        value = getter()
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

    private fun updateValue() {
        val v = getter()
        postValue(v)
    }

}
