package com.artemchep.pocketmode.ext

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

fun <T> Continuation<T>.resumeOrNothing(value: T) {
    try {
        resume(value)
    } catch (_: IllegalStateException) {
    }
}
