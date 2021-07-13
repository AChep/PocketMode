package com.artemchep.pocketmode.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.artemchep.pocketmode.models.sensors.ProximitySensorSnapshot
import com.artemchep.pocketmode.util.observerFlow
import kotlinx.coroutines.flow.Flow

fun flowOfProximity(
    context: Context,
): Flow<ProximitySensorSnapshot> = observerFlow<ProximitySensorSnapshot> { callback ->
    val sensorManager = context.getSystemService<SensorManager>()
        ?: return@observerFlow {}

    val sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Unused.
        }

        override fun onSensorChanged(event: SensorEvent) {
            val snapshot = event.sensor.toProximitySensorSnapshot(event)
            callback(snapshot)
        }
    }

    val delay = SensorManager.SENSOR_DELAY_NORMAL
    sensorManager.registerListener(sensorListener, sensorProximity, delay)

    return@observerFlow {
        sensorManager.unregisterListener(sensorListener)
    }
}

@Deprecated(
    message = "Flow analogue is 'flowOfProximity'",
    replaceWith = ReplaceWith("flowOfProximity(context)"),
)
@Suppress("FunctionName")
fun ProximityLiveData(
    context: Context
): LiveData<ProximitySensorSnapshot> = flowOfProximity(context).asLiveData()
