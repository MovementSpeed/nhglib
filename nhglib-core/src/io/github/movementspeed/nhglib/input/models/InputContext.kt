package io.github.movementspeed.nhglib.input.models

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
class InputContext(private val name: String) {
    var isEnabled: Boolean = false

    init {
        this.isEnabled = false
    }

    fun `is`(name: String): Boolean {
        return name.contentEquals(this.name)
    }
}
