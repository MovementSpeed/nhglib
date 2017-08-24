package io.github.movementspeed.nhglib.input.models;

import io.github.movementspeed.nhglib.input.enums.InputAction;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class NhgInput {
    private boolean handled;
    private String name;
    private InputType type;
    private InputSource inputSource;
    private InputAction inputAction;

    public NhgInput(String name) {
        this.name = name;

        handled = false;
        inputSource = new InputSource();
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
    }

    public void setInputAction(InputAction inputAction) {
        this.inputAction = inputAction;
    }

    public boolean is(String name) {
        return this.name.contentEquals(name);
    }

    public boolean isHandled() {
        return handled;
    }

    public String getName() {
        return name;
    }

    public InputType getType() {
        return type;
    }

    public InputSource getInputSource() {
        return inputSource;
    }

    public InputAction getInputAction() {
        return inputAction;
    }
}
