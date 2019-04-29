package com.artemchep.pocketmode.models.`fun`

/**
 * @author Artem Chepurnoy
 */
sealed class Either<Left, Right> {
    data class Left<Left, Right>(val a: Left) : Either<Left, Right>()
    data class Right<Left, Right>(val b: Right) : Either<Left, Right>()
}
