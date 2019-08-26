package io.github.movementspeed.nhglib.input.configuration.impls;

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration;
import io.github.movementspeed.nhglib.input.enums.InputMode;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class KeyInputConfiguration extends InputConfiguration {
    private int keyCode;
    private InputMode inputMode;

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
    }

    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public InputMode getInputMode() {
        return inputMode;
    }
}
