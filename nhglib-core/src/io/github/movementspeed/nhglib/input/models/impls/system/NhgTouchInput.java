package io.github.movementspeed.nhglib.input.models.impls.system;

import io.github.movementspeed.nhglib.input.models.InputType;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;

public class NhgTouchInput extends NhgInput {
    private int pointerNumber;

    public NhgTouchInput(String name) {
        super(name);
        setType(InputType.TOUCH);
    }

    public int getPointerNumber() {
        return pointerNumber;
    }

    public void setPointerNumber(int pointerNumber) {
        this.pointerNumber = pointerNumber;
    }
}
