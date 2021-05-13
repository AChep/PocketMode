package com.artemchep.pocketmode.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.artemchep.pocketmode.models.Proximity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun flowOfBinaryProximity(
    context: Context,
    proximitySensor: Flow<Float> = flowOfProximity(context),
): Flow<Proximity> {
    val proximityBinaryTransformation = proximityBinaryTransformationFactory(context)
    return proximitySensor
        .map {
            proximityBinaryTransformation(it)
        }
}

@Deprecated(
    message = "Flow analogue is 'flowOfBinaryProximity'",
    replaceWith = ReplaceWith("flowOfBinaryProximity(context, proximitySensor)"),
)
@Suppress("FunctionName")
fun ProximityBinaryLiveData(
    context: Context,
    proximitySensor: LiveData<Float>,
): LiveData<Proximity> {
    val proximityBinaryTransformation = proximityBinaryTransformationFactory(context)
    return Transformations.map(proximitySensor, proximityBinaryTransformation)
}

private fun proximityBinaryTransformationFactory(
    context: Context,
): (distance: Float) -> Proximity {
    val sensorProximityMaxRange = context.getSystemService<SensorManager>()
        ?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        ?.maximumRange ?: 1.0f
    return transform@{ distance ->
        proximityBinaryTransformationFactory(distance, sensorProximityMaxRange)
    }
}

fun proximityBinaryTransformationFactory(
    distance: Float,
    distanceMax: Float,
) = if (distance >= distanceMax) {
    Proximity.Far
} else {
    Proximity.Near
}
