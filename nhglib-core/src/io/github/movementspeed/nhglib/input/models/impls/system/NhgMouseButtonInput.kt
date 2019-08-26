package io.github.movementspeed.nhglib.input.models.impls.system

import io.github.movementspeed.nhglib.input.enums.InputType
import io.github.movementspeed.nhglib.input.models.base.NhgInput

class NhgMouseButtonInput(name: String) : NhgInput(name) {
    var buttonCode: Int = 0

    init {
        type = InputType.MOUSE_BUTTON
    }
}
