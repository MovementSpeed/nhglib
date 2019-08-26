package io.github.movementspeed.nhglib.input.enums

/**
 * Created by Fausto Napoli on 23/01/2017.
 */
enum class StickType {
    LEFT,
    RIGHT,
    VIRTUAL;

    companion object {
        fun fromString(value: String): StickType? {
            var res: StickType? = null

            when (value.toLowerCase()) {
                "left" -> res = LEFT
                "right" -> res = RIGHT
                "virtual" -> res = VIRTUAL
            }

            return res
        }
    }
}
