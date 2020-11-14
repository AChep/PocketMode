package com.artemchep.pocketmode.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.artemchep.pocketmode.models.Proximity

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun ProximityBinaryLiveData(
    context: Context,
    proximitySensor: LiveData<Float?>,
): LiveData<Proximity?> {
    val proximityBinaryTransformation = proximityBinaryTransformationFactory(context)
    return Transformations.map(proximitySensor, proximityBinaryTransformation)
}

private fun proximityBinaryTransformationFactory(
    context: Context,
): (distance: Float?) -> Proximity? {
    val sensorProximityMaxRange = context.getSystemService<SensorManager>()
        ?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        ?.maximumRange ?: 1.0f
    return transform@{ distance ->
        distance?.let { proximityBinaryTransformationFactory(it, sensorProximityMaxRange) }
    }
}

fun proximityBinaryTransformationFactory(
    distance: Float,
    distanceMax: Float,
) = if (distance >= distanceMax && distance >= 1.0f) {
    Proximity.Far
} else {
    Proximity.Near
}
