package io.github.movementspeed.nhglib.input.models;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public enum InputType {
    POINTER,
    KEY,
    BUTTON,
    STICK,
    MOUSE;

    public static InputType fromString(String value) {
        InputType res = null;

        switch (value) {
            case "pointer":
                res = POINTER;
                break;

            case "key":
                res = KEY;
                break;

            case "button":
                res = BUTTON;
                break;

            case "stick":
                res = STICK;
                break;

            case "mouse":
                res = MOUSE;
                break;
        }

        return res;
    }
}
