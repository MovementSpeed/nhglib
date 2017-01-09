package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public enum InputType {
    ACTION,
    VALUE;

    public static InputType fromString(String value) {
        InputType res = null;

        switch (value.toLowerCase()) {
            case "action":
                res = ACTION;
                break;

            case "value":
                res = VALUE;
                break;
        }

        return res;
    }
}
