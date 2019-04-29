package com.artemchep.pocketmode.models

/**
 * @author Artem Chepurnoy
 */
sealed class Screen {
    object On : Screen()
    object Off : Screen()
}
