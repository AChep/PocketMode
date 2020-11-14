package com.artemchep.pocketmode.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData

/**
 * @author Artem Chepurnoy
 */
class ProximityLiveData(
    private val context: Context
) : LiveData<Float?>() {
    private val sensorManager by lazy { context.getSystemService<SensorManager>() }

    private val sensorProximity by lazy { sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY) }

    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            val distance = event.values[0]
            postValue(distance)
        }
    }

    override fun onActive() {
        super.onActive()

        // Register the proximity sensor if it is
        // available
        if (sensorManager != null && sensorProximity != null) {
            val delay = SensorManager.SENSOR_DELAY_NORMAL
            sensorManager!!.registerListener(sensorListener, sensorProximity, delay)
        }
    }

    override fun onInactive() {
        sensorManager?.unregisterListener(sensorListener)
        super.onInactive()
        value = null
    }
}