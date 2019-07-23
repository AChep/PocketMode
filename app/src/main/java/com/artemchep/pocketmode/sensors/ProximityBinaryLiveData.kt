package com.artemchep.pocketmode.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.artemchep.pocketmode.models.Proximity

/**
 * @author Artem Chepurnoy
 */
class ProximityBinaryLiveData(
    private val context: Context,
    proximitySensor: LiveData<Float>
) : MediatorLiveData<Proximity>() {
    private val sensorManager by lazy { context.getSystemService<SensorManager>() }

    private val sensorProximityMaxRange by lazy {
        sensorManager
            ?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            ?.maximumRange ?: 1.0f
    }

    init {
        addSource(proximitySensor) { distance ->
            val proximity =
                if (distance >= sensorProximityMaxRange && distance >= 1.0f) {
                    Proximity.Far
                } else {
                    Proximity.Near
                }
            postValue(proximity)
        }
    }
}