package io.github.voidzombie.nhglib.input.models;

/**
 * Created by Fausto Napoli on 01/03/2017.
 */
public enum PointerSourceType {
    POINTER_XY,
    POINTER_DELTA_XY;

    public static PointerSourceType fromString(String value) {
        PointerSourceType res = null;

        switch (value) {
            case "pointerXY":
                res = POINTER_XY;
                break;

            case "pointerDeltaXY":
                res = POINTER_DELTA_XY;
                break;
        }

        return res;
    }
}
