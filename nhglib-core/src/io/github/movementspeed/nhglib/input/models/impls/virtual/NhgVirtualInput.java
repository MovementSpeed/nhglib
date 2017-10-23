package io.github.movementspeed.nhglib.input.models.impls.virtual;

import io.github.movementspeed.nhglib.input.models.base.NhgInput;

public class NhgVirtualInput extends NhgInput {
    private String actorName;

    public NhgVirtualInput(String name) {
        super(name);
    }

    public String getActorName() {
        return actorName;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }
}
