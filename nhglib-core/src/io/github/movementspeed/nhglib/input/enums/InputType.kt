package io.github.movementspeed.nhglib.input.enums

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
enum class InputType {
    TOUCH,
    KEYBOARD_BUTTON,
    CONTROLLER_BUTTON,
    VIRTUAL_BUTTON,
    VIRTUAL_STICK,
    CONTROLLER_STICK,
    MOUSE_BUTTON;

    companion object {
        fun fromString(value: String): InputType? {
            var res: InputType? = null

            when (value) {
                "touch" -> res = TOUCH
                "keyboardButton" -> res = KEYBOARD_BUTTON
                "controllerButton" -> res = CONTROLLER_BUTTON
                "virtualButton" -> res = VIRTUAL_BUTTON
                "controllerStick" -> res = CONTROLLER_STICK
                "mouseButton" -> res = MOUSE_BUTTON
            }

            return res
        }
    }
}
