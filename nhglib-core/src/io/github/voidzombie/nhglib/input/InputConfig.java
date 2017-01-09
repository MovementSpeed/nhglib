package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputConfig {
    private Integer keycode;
    private Float minValue;
    private Float maxValue;
    private Float sensitivity;
    private InputMode inputMode;

    public InputConfig() {
        inputMode = InputMode.REPEAT;
        minValue = Float.MIN_VALUE;
        maxValue = Float.MAX_VALUE;
        sensitivity = 1.0f;
    }

    public void setKeycode(Integer keycode) {
        this.keycode = keycode;
    }

    public void setMinValue(Float minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(Float maxValue) {
        this.maxValue = maxValue;
    }

    public void setSensitivity(Float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public Integer getKeycode() {
        return keycode;
    }

    public Float getMinValue() {
        return minValue;
    }

    public Float getMaxValue() {
        return maxValue;
    }

    public Float getSensitivity() {
        return sensitivity;
    }

    public InputMode getInputMode() {
        return inputMode;
    }
}
