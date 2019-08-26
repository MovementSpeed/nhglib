package io.github.movementspeed.nhglib.input.configuration.impls

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration
import io.github.movementspeed.nhglib.input.enums.MouseSourceType

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
class MouseInputConfiguration : InputConfiguration() {
    var horizontalSensitivity: Float = 0.toFloat()
    var verticalSensitivity: Float = 0.toFloat()

    var mouseSourceType: MouseSourceType? = null
}
