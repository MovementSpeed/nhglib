package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 23/01/2017.
 */
public enum StickType {
    LEFT,
    RIGHT,
    VIRTUAL;

    public static StickType fromString(String value) {
        StickType res = null;

        switch (value.toLowerCase()) {
            case "left":
                res = LEFT;
                break;

            case "right":
                res = RIGHT;
                break;

            case "virtual":
                res = VIRTUAL;
                break;
        }

        return res;
    }
}
