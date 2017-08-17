package io.github.movementspeed.nhglib.input.models;

/**
 * Created by Fausto Napoli on 09/01/2017.
 */
public enum InputMode {
    SINGLE,
    REPEAT;

    public static InputMode fromString(String value) {
        InputMode res = null;

        switch (value.toLowerCase()) {
            case "single":
                res = SINGLE;
                break;

            case "repeat":
                res = REPEAT;
                break;
        }

        return res;
    }
}
