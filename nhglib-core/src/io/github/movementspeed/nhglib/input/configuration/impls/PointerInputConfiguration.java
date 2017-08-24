package io.github.movementspeed.nhglib.input.configuration.impls;

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration;
import io.github.movementspeed.nhglib.input.models.PointerSourceType;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class PointerInputConfiguration extends InputConfiguration {
    private int id;

    private float horizontalSensitivity;
    private float verticalSensitivity;

    private PointerSourceType pointerSourceType;

    public void setId(int id) {
        this.id = id;
    }

    public void setHorizontalSensitivity(float horizontalSensitivity) {
        this.horizontalSensitivity = horizontalSensitivity;
    }

    public void setVerticalSensitivity(float verticalSensitivity) {
        this.verticalSensitivity = verticalSensitivity;
    }

    public void setPointerSourceType(PointerSourceType pointerSourceType) {
        this.pointerSourceType = pointerSourceType;
    }

    public int getId() {
        return id;
    }

    public float getHorizontalSensitivity() {
        return horizontalSensitivity;
    }

    public float getVerticalSensitivity() {
        return verticalSensitivity;
    }

    public PointerSourceType getPointerSourceType() {
        return pointerSourceType;
    }
}
