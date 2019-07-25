package com.artemchep.pocketmode.models.events

/**
 * @author Artem Chepurnoy
 */
sealed class LockScreenEvent

object BeforeLockScreen : LockScreenEvent()
object OnLockScreen : LockScreenEvent()
object Idle : LockScreenEvent()
