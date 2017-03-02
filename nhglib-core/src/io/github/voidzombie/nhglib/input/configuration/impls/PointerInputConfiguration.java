package io.github.voidzombie.nhglib.input.configuration.impls;

import io.github.voidzombie.nhglib.input.configuration.base.InputConfiguration;
import io.github.voidzombie.nhglib.input.models.PointerSourceType;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class PointerInputConfiguration extends InputConfiguration {
    private Integer id;

    private Float horizontalSensitivity;
    private Float verticalSensitivity;

    private PointerSourceType pointerSourceType;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setHorizontalSensitivity(Float horizontalSensitivity) {
        this.horizontalSensitivity = horizontalSensitivity;
    }

    public void setVerticalSensitivity(Float verticalSensitivity) {
        this.verticalSensitivity = verticalSensitivity;
    }

    public void setPointerSourceType(PointerSourceType pointerSourceType) {
        this.pointerSourceType = pointerSourceType;
    }

    public Integer getId() {
        return id;
    }

    public Float getHorizontalSensitivity() {
        return horizontalSensitivity;
    }

    public Float getVerticalSensitivity() {
        return verticalSensitivity;
    }

    public PointerSourceType getPointerSourceType() {
        return pointerSourceType;
    }
}
