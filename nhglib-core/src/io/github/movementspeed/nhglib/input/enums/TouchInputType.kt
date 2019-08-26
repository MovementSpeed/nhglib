package io.github.movementspeed.nhglib.input.enums

enum class TouchInputType {
    TAP,
    DRAG,
    PINCH,
    ZOOM;

    companion object {
        fun fromString(value: String): TouchInputType {
            var res = TAP

            when (value.toLowerCase()) {
                "drag" -> res = DRAG
                "pinch" -> res = PINCH
                "zoom" -> res = ZOOM
            }

            return res
        }
    }
}
