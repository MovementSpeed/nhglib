package io.github.movementspeed.nhglib.input.models.impls.system;

import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.input.enums.InputType;
import io.github.movementspeed.nhglib.input.enums.TouchInputType;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;

public class NhgTouchInput extends NhgInput {
    private int pointerNumber;
    private Array<TouchInputType> touchInputTypes;

    public NhgTouchInput(String name) {
        super(name);
        setType(InputType.TOUCH);
    }

    public boolean hasTouchInputType(TouchInputType touchInputType) {
        boolean res = false;

        for (TouchInputType type : touchInputTypes) {
            if (type == touchInputType) {
                res = true;
            }
        }

        return res;
    }

    public int getPointerNumber() {
        return pointerNumber;
    }

    public void setPointerNumber(int pointerNumber) {
        this.pointerNumber = pointerNumber;
    }

    public void setTouchInputTypes(Array<TouchInputType> touchInputTypes) {
        this.touchInputTypes = touchInputTypes;
    }
}
