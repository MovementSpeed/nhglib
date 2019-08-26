package io.github.movementspeed.nhglib.input.configuration.impls

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration
import io.github.movementspeed.nhglib.input.enums.InputMode

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
class KeyInputConfiguration : InputConfiguration() {
    var keyCode: Int = 0
    var inputMode: InputMode? = null
}
