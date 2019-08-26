package io.github.movementspeed.nhglib.input.models;

import io.github.movementspeed.nhglib.input.enums.InputMode;
import io.github.movementspeed.nhglib.input.enums.StickType;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputConfig {
    private int keycode;
    private int controllerId;
    private float minValue;
    private float maxValue;
    private float stickDeadZoneRadius;
    private float sensitivity;
    private InputMode inputMode;
    private StickType stickType;

    public InputConfig() {
        inputMode = InputMode.REPEAT;
        stickType = StickType.VIRTUAL;
        minValue = Float.MIN_VALUE;
        maxValue = Float.MAX_VALUE;
        stickDeadZoneRadius = 0.01f;
        sensitivity = 1.0f;
    }

    public void setKeycode(int keycode) {
        this.keycode = keycode;
    }

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setStickDeadZoneRadius(float stickDeadZoneRadius) {
        this.stickDeadZoneRadius = stickDeadZoneRadius;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public void setStickType(StickType stickType) {
        this.stickType = stickType;
    }

    public int getKeycode() {
        return keycode;
    }

    public int getControllerId() {
        return controllerId;
    }

    public float getMinValue() {
        return minValue;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public float getStickDeadZoneRadius() {
        return stickDeadZoneRadius;
    }

    public float getSensitivity() {
        return sensitivity;
    }

    public InputMode getInputMode() {
        return inputMode;
    }

    public StickType getStickType() {
        return stickType;
    }
}
