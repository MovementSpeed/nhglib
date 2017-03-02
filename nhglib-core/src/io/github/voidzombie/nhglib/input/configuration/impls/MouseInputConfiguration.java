package io.github.voidzombie.nhglib.input.configuration.impls;

import io.github.voidzombie.nhglib.input.configuration.base.InputConfiguration;
import io.github.voidzombie.nhglib.input.models.MouseSourceType;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class MouseInputConfiguration extends InputConfiguration {
    private Float horizontalSensitivity;
    private Float verticalSensitivity;

    private MouseSourceType mouseSourceType;

    public void setHorizontalSensitivity(Float horizontalSensitivity) {
        this.horizontalSensitivity = horizontalSensitivity;
    }

    public void setVerticalSensitivity(Float verticalSensitivity) {
        this.verticalSensitivity = verticalSensitivity;
    }

    public void setMouseSourceType(MouseSourceType mouseSourceType) {
        this.mouseSourceType = mouseSourceType;
    }

    public Float getHorizontalSensitivity() {
        return horizontalSensitivity;
    }

    public Float getVerticalSensitivity() {
        return verticalSensitivity;
    }

    public MouseSourceType getMouseSourceType() {
        return mouseSourceType;
    }
}
