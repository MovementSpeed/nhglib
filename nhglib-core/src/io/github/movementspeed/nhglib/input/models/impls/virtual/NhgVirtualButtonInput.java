package io.github.movementspeed.nhglib.input.models.impls.virtual;

import io.github.movementspeed.nhglib.input.models.base.NhgInput;

public class NhgVirtualButtonInput extends NhgInput {
    private String actorName;

    public NhgVirtualButtonInput(String name) {
        super(name);
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }
}
