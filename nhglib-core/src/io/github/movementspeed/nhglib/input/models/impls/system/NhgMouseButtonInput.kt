package io.github.movementspeed.nhglib.input.models.impls.system;

import io.github.movementspeed.nhglib.input.enums.InputType;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;

public class NhgMouseButtonInput extends NhgInput {
    private int buttonCode;

    public NhgMouseButtonInput(String name) {
        super(name);
        setType(InputType.MOUSE_BUTTON);
    }

    public int getButtonCode() {
        return buttonCode;
    }

    public void setButtonCode(int buttonCode) {
        this.buttonCode = buttonCode;
    }
}
