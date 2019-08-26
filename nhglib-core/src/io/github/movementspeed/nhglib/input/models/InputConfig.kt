package io.github.movementspeed.nhglib.input.models

import io.github.movementspeed.nhglib.input.enums.InputMode
import io.github.movementspeed.nhglib.input.enums.StickType

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
class InputConfig {
    var keycode: Int = 0
    var controllerId: Int = 0
    var minValue: Float = 0.toFloat()
    var maxValue: Float = 0.toFloat()
    var stickDeadZoneRadius: Float = 0.toFloat()
    var sensitivity: Float = 0.toFloat()
    var inputMode: InputMode? = null
    var stickType: StickType? = null

    init {
        inputMode = InputMode.REPEAT
        stickType = StickType.VIRTUAL
        minValue = java.lang.Float.MIN_VALUE
        maxValue = java.lang.Float.MAX_VALUE
        stickDeadZoneRadius = 0.01f
        sensitivity = 1.0f
    }
}
