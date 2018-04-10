package io.github.movementspeed.nhglib.input.models.impls.system;

import io.github.movementspeed.nhglib.input.enums.InputType;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;

public class NhgKeyboardButtonInput extends NhgInput {
    private int keyCode;

    public NhgKeyboardButtonInput(String name) {
        super(name);
        setType(InputType.KEYBOARD_BUTTON);
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }
}
