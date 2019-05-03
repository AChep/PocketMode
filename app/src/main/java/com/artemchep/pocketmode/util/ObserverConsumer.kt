package com.artemchep.pocketmode.util

import androidx.lifecycle.Observer
import com.artemchep.pocketmode.models.`fun`.Either
import com.artemchep.pocketmode.models.events.Event

/**
 * @author Artem Chepurnoy
 */
class ObserverConsumer<T>(
    private val callback: (T) -> Unit
) : Observer<Event<T>> {
    override fun onChanged(t: Event<T>) {
        val v = t.consume()
        if (v is Either.Right) {
            // Invoke the callback
            callback.invoke(v.b)
        }
    }
}