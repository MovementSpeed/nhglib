package io.github.movementspeed.nhglib.input.enums;

public enum TouchInputType {
    TAP,
    DRAG,
    PINCH,
    ZOOM;

    public static TouchInputType fromString(String value) {
        TouchInputType res = TAP;

        switch (value.toLowerCase()) {
            case "tap":
                break;

            case "drag":
                res = DRAG;
                break;

            case "pinch":
                res = PINCH;
                break;

            case "zoom":
                res = ZOOM;
                break;
        }

        return res;
    }
}
