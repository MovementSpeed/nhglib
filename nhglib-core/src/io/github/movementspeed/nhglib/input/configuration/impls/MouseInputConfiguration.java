package io.github.movementspeed.nhglib.input.configuration.impls;

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration;
import io.github.movementspeed.nhglib.input.enums.MouseSourceType;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class MouseInputConfiguration extends InputConfiguration {
    private float horizontalSensitivity;
    private float verticalSensitivity;

    private MouseSourceType mouseSourceType;

    public void setHorizontalSensitivity(float horizontalSensitivity) {
        this.horizontalSensitivity = horizontalSensitivity;
    }

    public void setVerticalSensitivity(float verticalSensitivity) {
        this.verticalSensitivity = verticalSensitivity;
    }

    public void setMouseSourceType(MouseSourceType mouseSourceType) {
        this.mouseSourceType = mouseSourceType;
    }

    public float getHorizontalSensitivity() {
        return horizontalSensitivity;
    }

    public float getVerticalSensitivity() {
        return verticalSensitivity;
    }

    public MouseSourceType getMouseSourceType() {
        return mouseSourceType;
    }
}
