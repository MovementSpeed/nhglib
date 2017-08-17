package io.github.movementspeed.nhglib.input.configuration.impls;

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration;
import io.github.movementspeed.nhglib.input.models.InputMode;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class KeyInputConfiguration extends InputConfiguration {
    private Integer keyCode;
    private InputMode inputMode;

    public void setKeyCode(Integer keyCode) {
        this.keyCode = keyCode;
    }

    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public Integer getKeyCode() {
        return keyCode;
    }

    public InputMode getInputMode() {
        return inputMode;
    }
}
