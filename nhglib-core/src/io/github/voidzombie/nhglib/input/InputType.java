package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public enum InputType {
    INPUT_XY,
    KEY,
    BUTTON,
    STICK;

    public static InputType fromString(String value) {
        InputType res = null;

        switch (value) {
            case "inputXY":
                res = INPUT_XY;
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
        }

        return res;
    }
}
