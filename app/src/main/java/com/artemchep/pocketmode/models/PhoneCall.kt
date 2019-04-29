package com.artemchep.pocketmode.models

/**
 * @author Artem Chepurnoy
 */
sealed class PhoneCall {
    object Ongoing : PhoneCall()
    object Idle : PhoneCall()
}
