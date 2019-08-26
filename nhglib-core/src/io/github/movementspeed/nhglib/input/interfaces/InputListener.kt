package io.github.movementspeed.nhglib.input.interfaces

import io.github.movementspeed.nhglib.input.models.base.NhgInput

/**
 * Created by Fausto Napoli on 09/01/2017.
 */
interface InputListener {
    fun onInput(input: NhgInput)
}
