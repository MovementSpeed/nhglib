package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputConfig {
    private Integer keycode;
    private Integer controllerId;
    private Float minValue;
    private Float maxValue;
    private Float stickDeadZoneRadius;
    private Float sensitivity;
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

    public void setKeycode(Integer keycode) {
        this.keycode = keycode;
    }

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }

    public void setStickDeadZoneRadius(Float stickDeadZoneRadius) {
        this.stickDeadZoneRadius = stickDeadZoneRadius;
    }

    public void setSensitivity(Float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public void setStickType(StickType stickType) {
        this.stickType = stickType;
    }

    public Integer getKeycode() {
        return keycode;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public Float getMinValue() {
        return minValue;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public Float getStickDeadZoneRadius() {
        return stickDeadZoneRadius;
    }

    public Float getSensitivity() {
        return sensitivity;
    }

    public InputMode getInputMode() {
        return inputMode;
    }

    public StickType getStickType() {
        return stickType;
    }
}
