package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class NHGInput {
    private Boolean handled;
    private String name;
    private InputSource source;
    private InputType type;
    private InputConfig config;

    public NHGInput(String name) {
        this.name = name;

        handled = false;
        config = new InputConfig();
    }

    public void setHandled(Boolean handled) {
        this.handled = handled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSource(InputSource source) {
        this.source = source;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public void setConfig(InputConfig config) {
        this.config = config;
    }

    public Boolean is(String name) {
        return this.name.contentEquals(name);
    }

    public Boolean isHandled() {
        return handled;
    }

    public String getName() {
        return name;
    }

    public InputSource getSource() {
        return source;
    }

    public InputType getType() {
        return type;
    }

    public InputConfig getConfig() {
        return config;
    }
}
