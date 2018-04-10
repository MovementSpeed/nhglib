package io.github.movementspeed.nhglib.input.configuration.impls;

import io.github.movementspeed.nhglib.input.configuration.base.InputConfiguration;
import io.github.movementspeed.nhglib.input.enums.StickType;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class StickInputConfiguration extends InputConfiguration {
    private int controllerId;
    private StickType stickType;

    public void setControllerId(int controllerId) {
        this.controllerId = controllerId;
    }

    public void setStickType(StickType stickType) {
        this.stickType = stickType;
    }

    public int getControllerId() {
        return controllerId;
    }

    public StickType getStickType() {
        return stickType;
    }
}
