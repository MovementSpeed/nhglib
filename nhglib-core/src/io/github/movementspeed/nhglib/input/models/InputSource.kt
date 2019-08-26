package io.github.movementspeed.nhglib.input.models

/**
 * Created by Fausto Napoli on 22/01/2017.
 */
class InputSource {
    var name: String? = null
    var value: Any? = null

    fun `is`(name: String): Boolean {
        return name.contentEquals(name)
    }
}
