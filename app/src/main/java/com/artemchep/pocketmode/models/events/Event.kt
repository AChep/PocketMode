package com.artemchep.pocketmode.models.events

import com.artemchep.pocketmode.models.`fun`.Either

/**
 * @author Artem Chepurnoy
 */
open class Event<out T>(
    val value: T
) {
    private var isConsumed = false

    /**
     * Returns the value of event and
     * consumes it.
     */
    fun consume(): Either<Unit, out T> =
        if (isConsumed) {
            Either.Left(Unit)
        } else {
            isConsumed = true
            Either.Right(value)
        }
}
