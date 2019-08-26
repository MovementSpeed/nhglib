package io.github.movementspeed.nhglib.input.enums

/**
 * Created by Fausto Napoli on 09/01/2017.
 */
enum class InputMode {
    ONCE,
    REPEAT;

    companion object {

        fun fromString(value: String): InputMode? {
            var res: InputMode? = null

            when (value.toLowerCase()) {
                "once" -> res = ONCE
                "repeat" -> res = REPEAT
            }

            return res
        }
    }
}
