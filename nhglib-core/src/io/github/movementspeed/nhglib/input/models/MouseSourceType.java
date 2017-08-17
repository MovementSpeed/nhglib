package io.github.movementspeed.nhglib.input.models;

import com.badlogic.gdx.Input;

/**
 * Created by Fausto Napoli on 01/03/2017.
 */
public enum MouseSourceType {
    MOUSE_XY(-1),
    MOUSE_LEFT(Input.Buttons.LEFT),
    MOUSE_RIGHT(Input.Buttons.RIGHT),
    MOUSE_MIDDLE(Input.Buttons.MIDDLE);

    public int button;

    MouseSourceType(int button) {
        this.button = button;
    }

    public static MouseSourceType fromString(String value) {
        MouseSourceType res = null;

        switch (value) {
            case "mouseXY":
                res = MOUSE_XY;
                break;

            case "mouseLeft":
                res = MOUSE_LEFT;
                break;

            case "mouseRight":
                res = MOUSE_RIGHT;
                break;

            case "mouseMiddle":
                res = MOUSE_MIDDLE;
                break;
        }

        return res;
    }

    public static MouseSourceType fromButtonCode(int button) {
        MouseSourceType res = null;

        for (MouseSourceType mouseSourceType : MouseSourceType.values()) {
            if (mouseSourceType.button == button) {
                res = mouseSourceType;
                break;
            }
        }

        return res;
    }
}
