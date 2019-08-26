package io.github.movementspeed.nhglib.input.enums;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public enum InputType {
    TOUCH,
    KEYBOARD_BUTTON,
    CONTROLLER_BUTTON,
    VIRTUAL_BUTTON,
    VIRTUAL_STICK,
    CONTROLLER_STICK,
    MOUSE_BUTTON;

    public static InputType fromString(String value) {
        InputType res = null;

        switch (value) {
            case "touch":
                res = TOUCH;
                break;

            case "keyboardButton":
                res = KEYBOARD_BUTTON;
                break;

            case "controllerButton":
                res = CONTROLLER_BUTTON;
                break;

            case "virtualButton":
                res = VIRTUAL_BUTTON;
                break;

            case "controllerStick":
                res = CONTROLLER_STICK;
                break;

            case "mouseButton":
                res = MOUSE_BUTTON;
                break;
        }

        return res;
    }
}
