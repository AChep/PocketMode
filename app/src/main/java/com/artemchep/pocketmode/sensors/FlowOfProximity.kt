package com.artemchep.pocketmode.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.artemchep.pocketmode.util.observerFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap

fun flowOfProximity(
    context: Context,
): Flow<Float> = observerFlow<Float> { callback ->
    val sensorManager = context.getSystemService<SensorManager>()
        ?: return@observerFlow {}

    val sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            // Unused.
        }

        override fun onSensorChanged(event: SensorEvent) {
            val distance = event.values[0]
            callback(distance)
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
): LiveData<Float> = flowOfProximity(context).asLiveData()
