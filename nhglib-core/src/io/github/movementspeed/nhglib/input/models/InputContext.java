package io.github.movementspeed.nhglib.input.models;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputContext {
    private boolean enabled;
    private String name;

    public InputContext(String name) {
        this.name = name;
        this.enabled = false;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean is(String name) {
        return name.contentEquals(this.name);
    }
}
