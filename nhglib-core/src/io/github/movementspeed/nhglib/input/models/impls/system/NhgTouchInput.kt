package io.github.movementspeed.nhglib.input.models.impls.system

import com.badlogic.gdx.utils.Array
import io.github.movementspeed.nhglib.input.enums.InputType
import io.github.movementspeed.nhglib.input.enums.TouchInputType
import io.github.movementspeed.nhglib.input.models.base.NhgInput

class NhgTouchInput(name: String) : NhgInput(name) {
    var pointerNumber: Int = 0
    private var touchInputTypes: Array<TouchInputType>? = null

    init {
        type = InputType.TOUCH
    }

    fun hasTouchInputType(touchInputType: TouchInputType): Boolean {
        var res = false

        for (type in touchInputTypes!!) {
            if (type == touchInputType) {
                res = true
            }
        }

        return res
    }

    fun setTouchInputTypes(touchInputTypes: Array<TouchInputType>) {
        this.touchInputTypes = touchInputTypes
    }
}
