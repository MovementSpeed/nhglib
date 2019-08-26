package io.github.movementspeed.nhglib.input.models.base

import io.github.movementspeed.nhglib.input.enums.InputAction
import io.github.movementspeed.nhglib.input.enums.InputMode
import io.github.movementspeed.nhglib.input.enums.InputType
import io.github.movementspeed.nhglib.input.models.InputContext

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
abstract class NhgInput(var name: String?) {
    var isHandled: Boolean = false
    var value: Any? = null
    var type: InputType? = null
        protected set
    var mode: InputMode? = null
    var action: InputAction? = null
    var context: InputContext? = null

    val isValid: Boolean
        get() = context!!.isEnabled

    init {
        this.isHandled = false
    }

    fun `is`(name: String): Boolean {
        return this.name?.contentEquals(name) ?: false
    }
}
