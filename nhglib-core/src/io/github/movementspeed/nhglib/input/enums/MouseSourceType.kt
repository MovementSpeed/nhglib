package io.github.movementspeed.nhglib.input.enums

import com.badlogic.gdx.Input

/**
 * Created by Fausto Napoli on 01/03/2017.
 */
enum class MouseSourceType(var button: Int) {
    MOUSE_XY(-1),
    MOUSE_LEFT(Input.Buttons.LEFT),
    MOUSE_RIGHT(Input.Buttons.RIGHT),
    MOUSE_MIDDLE(Input.Buttons.MIDDLE);

    companion object {
        fun fromString(value: String): MouseSourceType? {
            var res: MouseSourceType? = null

            when (value) {
                "mouseXY" -> res = MOUSE_XY
                "mouseLeft" -> res = MOUSE_LEFT
                "mouseRight" -> res = MOUSE_RIGHT
                "mouseMiddle" -> res = MOUSE_MIDDLE
            }

            return res
        }

        fun fromButtonCode(button: Int): MouseSourceType? {
            var res: MouseSourceType? = null

            for (mouseSourceType in values()) {
                if (mouseSourceType.button == button) {
                    res = mouseSourceType
                    break
                }
            }

            return res
        }
    }
}
