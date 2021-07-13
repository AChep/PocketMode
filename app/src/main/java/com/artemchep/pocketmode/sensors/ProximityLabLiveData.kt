package com.artemchep.pocketmode.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import com.artemchep.pocketmode.models.sensors.ProximitySensorSnapshot
import java.util.concurrent.ConcurrentHashMap

/**
 * @author Artem Chepurnoy
 */
class ProximityLabLiveData(
    private val context: Context
) : LiveData<List<ProximitySensorSnapshot>>() {

    private val data: MutableMap<Int, ProximitySensorSnapshot> = ConcurrentHashMap()

    private val sensorManager by lazy { context.getSystemService<SensorManager>() }

    private val sensorsProximity by lazy { sensorManager?.getSensorList(Sensor.TYPE_PROXIMITY) }

    private val sensorsProximityWithListeners: List<Pair<Sensor, SensorEventListener>>? by lazy {
        sensorsProximity
            ?.map { sensor ->
                sensor to object : SensorEventListener {
                    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    }

                    override fun onSensorChanged(event: SensorEvent) {
                        data[sensor.id] = sensor.toProximitySensorSnapshot(event)
                        postValueReadOnly(data)
                    }
                }
            }
    }

    override fun onActive() {
        super.onActive()
        data.clear()
        postValueReadOnly(data)

        // Register the proximity sensor if it is
        // available
        if (sensorManager != null) {
            val delay = SensorManager.SENSOR_DELAY_NORMAL
            // Subscribe to all of the sensors
            // at once.
            sensorsProximityWithListeners?.forEach { (sensor, listener) ->
                sensorManager!!.registerListener(listener, sensor, delay)
            }
        }
    }

    override fun onInactive() {
        sensorsProximityWithListeners?.forEach { (_, listener) ->
            sensorManager?.unregisterListener(listener)
        }
        super.onInactive()
    }

    private fun postValueReadOnly(map: Map<*, ProximitySensorSnapshot>) =
        postValue(map.values.toList())

}

// Converters

fun Sensor.toProximitySensorSnapshot(event: SensorEvent) =
    run {
        val distance = event.values[0]
        val proximity = proximityBinaryTransformationFactory(distance, maximumRange)
        ProximitySensorSnapshot(
            id = id,
            name = name,
            isRuntime = isDynamicSensor,
            proximity = proximity,
            distance = distance,
        )
    }
