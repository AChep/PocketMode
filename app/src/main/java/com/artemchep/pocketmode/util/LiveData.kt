package com.artemchep.pocketmode.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

fun <P1, P2, T : Any> combine(
    a: LiveData<P1>,
    b: LiveData<P2>,
    combine: (P1, P2) -> T,
) = internalCombine(
    a,
    b,
) {
    combine(it[0] as P1, it[1] as P2)
}

fun <P1, P2, P3, T : Any> combine(
    a: LiveData<P1>,
    b: LiveData<P2>,
    c: LiveData<P3>,
    combine: (P1, P2, P3) -> T,
) = internalCombine(
    a,
    b,
    c,
) {
    combine(it[0] as P1, it[1] as P2, it[2] as P3)
}

fun <P1, P2, P3, P4, T : Any> combine(
    a: LiveData<P1>,
    b: LiveData<P2>,
    c: LiveData<P3>,
    d: LiveData<P4>,
    combine: (P1, P2, P3, P4) -> T,
) = internalCombine(
    a,
    b,
    c,
    d,
) {
    combine(it[0] as P1, it[1] as P2, it[2] as P3, it[3] as P4)
}

fun <P1, P2, P3, P4, P5, T : Any> combine(
    a: LiveData<P1>,
    b: LiveData<P2>,
    c: LiveData<P3>,
    d: LiveData<P4>,
    e: LiveData<P5>,
    combine: (P1, P2, P3, P4, P5) -> T,
) = internalCombine(
    a,
    b,
    c,
    d,
    e,
) {
    combine(it[0] as P1, it[1] as P2, it[2] as P3, it[3] as P4, it[4] as P5)
}

fun <P1, P2, P3, P4, P5, P6, T : Any> combine(
    a: LiveData<P1>,
    b: LiveData<P2>,
    c: LiveData<P3>,
    d: LiveData<P4>,
    e: LiveData<P5>,
    f: LiveData<P5>,
    combine: (P1, P2, P3, P4, P5, P6) -> T,
) = internalCombine(
    a,
    b,
    c,
    d,
    e,
    f,
) {
    combine(it[0] as P1, it[1] as P2, it[2] as P3, it[3] as P4, it[4] as P5, it[5] as P6)
}

private val NOTHING = Any()

private fun <T : Any> internalCombine(
    vararg liveData: LiveData<out Any?>,
    combine: (Array<Any?>) -> T,
): LiveData<out T> {
    var counter = liveData.size
    val slots = Array<Any?>(liveData.size) { NOTHING }

    return object : MutableLiveData<T>() {
        val observers = Array<Observer<Any?>>(liveData.size) { index ->
            Observer { value ->
                val valuePrev = slots[index]
                if (valuePrev === NOTHING) {
                    counter--
                }

                slots[index] = value

                if (counter == 0) {
                    val output = combine(slots)
                    setValue(output)
                }
            }
        }

        override fun onActive() {
            super.onActive()
            liveData.forEachIndexed { index, liveData ->
                liveData.observeForever(observers[index])
            }
        }

        override fun onInactive() {
            liveData.forEachIndexed { index, liveData ->
                liveData.removeObserver(observers[index])
            }
            super.onInactive()
        }
    }
}
