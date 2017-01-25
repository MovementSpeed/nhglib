package io.github.voidzombie.nhglib.input.configuration.impls;

import io.github.voidzombie.nhglib.input.configuration.base.InputConfiguration;
import io.github.voidzombie.nhglib.input.models.StickType;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class StickInputConfiguration extends InputConfiguration {
    private Integer controllerId;
    private StickType stickType;

    public void setControllerId(Integer controllerId) {
        this.controllerId = controllerId;
    }

    public void setStickType(StickType stickType) {
        this.stickType = stickType;
    }

    public Integer getControllerId() {
        return controllerId;
    }

    public StickType getStickType() {
        return stickType;
    }
}
