package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public enum InputSource {
    INPUT_XY,
    BUTTON,
    CUSTOM;

    public static InputSource fromString(String value) {
        InputSource res = null;

        switch (value) {
            case "inputXY":
                res = INPUT_XY;
                break;

            case "button":
                res = BUTTON;
                break;

            case "custom":
                res = CUSTOM;
                break;
        }

        return res;
    }
}
