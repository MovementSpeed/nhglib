package io.github.voidzombie.nhglib.input.configuration.impls;

import io.github.voidzombie.nhglib.input.configuration.base.InputConfiguration;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class PointerInputConfiguration extends InputConfiguration {
    private Float horizontalSensitivity;
    private Float verticalSensitivity;

    public void setHorizontalSensitivity(Float horizontalSensitivity) {
        this.horizontalSensitivity = horizontalSensitivity;
    }

    public void setVerticalSensitivity(Float verticalSensitivity) {
        this.verticalSensitivity = verticalSensitivity;
    }

    public Float getHorizontalSensitivity() {
        return horizontalSensitivity;
    }

    public Float getVerticalSensitivity() {
        return verticalSensitivity;
    }
}
