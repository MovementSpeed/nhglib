package io.github.movementspeed.nhglib.input.enums

/**
 * Created by Fausto Napoli on 01/03/2017.
 */
enum class PointerSourceType {
    POINTER_XY,
    POINTER_DELTA_XY;

    companion object {

        fun fromString(value: String): PointerSourceType? {
            var res: PointerSourceType? = null

            when (value) {
                "pointerXY" -> res = POINTER_XY
                "pointerDeltaXY" -> res = POINTER_DELTA_XY
            }

            return res
        }
    }
}
