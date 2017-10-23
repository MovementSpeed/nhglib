package io.github.movementspeed.nhglib.input.models.base;

import io.github.movementspeed.nhglib.input.enums.InputAction;
import io.github.movementspeed.nhglib.input.models.InputMode;
import io.github.movementspeed.nhglib.input.models.InputType;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public abstract class NhgInput {
    private boolean handled;
    private Object value;
    private String name;
    private InputType type;
    private InputMode mode;
    private InputAction action;

    public NhgInput(String name) {
        this.name = name;

        handled = false;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMode(InputMode mode) {
        this.mode = mode;
    }

    public void setAction(InputAction action) {
        this.action = action;
    }

    public boolean is(String name) {
        return this.name.contentEquals(name);
    }

    public boolean isHandled() {
        return handled;
    }

    public Object getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public InputType getType() {
        return type;
    }

    public InputMode getMode() {
        return mode;
    }

    public InputAction getAction() {
        return action;
    }

    protected void setType(InputType type) {
        this.type = type;
    }
}
