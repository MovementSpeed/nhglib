package io.github.movementspeed.nhglib.input.configuration.impls

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration
import io.github.movementspeed.nhglib.input.enums.PointerSourceType

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
class PointerInputConfiguration : InputConfiguration() {
    var id: Int = 0

    var horizontalSensitivity: Float = 0.toFloat()
    var verticalSensitivity: Float = 0.toFloat()

    var pointerSourceType: PointerSourceType? = null
}
