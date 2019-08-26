package io.github.movementspeed.nhglib.input.models.impls.system

import io.github.movementspeed.nhglib.input.enums.InputType
import io.github.movementspeed.nhglib.input.models.base.NhgInput

class NhgKeyboardButtonInput(name: String) : NhgInput(name) {
    var keyCode: Int = 0

    init {
        type = InputType.KEYBOARD_BUTTON
    }
}
