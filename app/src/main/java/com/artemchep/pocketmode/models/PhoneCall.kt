package com.artemchep.pocketmode.models

/**
 * @author Artem Chepurnoy
 */
sealed class PhoneCall {
    /**
     * Device call state: Ringing. A new call arrived and is
     *  ringing or waiting. In the latter case, another call is
     *  already active.
     */
    object Ongoing : PhoneCall()
    /**
     * Device call state: Off-hook. At least one call exists
     * that is dialing, active, or on hold, and no calls are ringing
     * or waiting.
     */
    object Offhook : PhoneCall()
    object Idle : PhoneCall()
}
