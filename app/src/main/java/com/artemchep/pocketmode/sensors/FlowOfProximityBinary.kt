package com.artemchep.pocketmode.sensors

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.artemchep.pocketmode.models.Proximity
import com.artemchep.pocketmode.models.sensors.ProximitySensorSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author Artem Chepurnoy
 */
@Suppress("FunctionName")
fun flowOfBinaryProximity(
    context: Context,
    proximitySensor: Flow<ProximitySensorSnapshot> = flowOfProximity(context),
): Flow<Proximity> = proximitySensor
    .map {
        it.proximity
    }

@Deprecated(
    message = "Flow analogue is 'flowOfBinaryProximity'",
    replaceWith = ReplaceWith("flowOfBinaryProximity(context, proximitySensor)"),
)
@Suppress("FunctionName")
fun ProximityBinaryLiveData(
    context: Context,
    proximitySensor: LiveData<ProximitySensorSnapshot>,
): LiveData<Proximity> = proximitySensor.map { it.proximity }

fun proximityBinaryTransformationFactory(
    distance: Float,
    distanceMax: Float,
) = if (distance >= distanceMax) {
    Proximity.Far
} else {
    Proximity.Near
}
