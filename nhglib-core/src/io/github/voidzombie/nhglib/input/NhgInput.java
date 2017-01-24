package io.github.voidzombie.nhglib.input;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class NhgInput {
    private Boolean handled;
    private String name;
    private InputType type;
    private InputConfig config;
    private InputSource inputSource;

    public NhgInput(String name) {
        this.name = name;

        handled = false;
        config = new InputConfig();
        inputSource = new InputSource();
    }

    public void setHandled(Boolean handled) {
        this.handled = handled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public void setConfig(InputConfig config) {
        this.config = config;
    }

    public void setInputSource(InputSource inputSource) {
        this.inputSource = inputSource;
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

    public InputType getType() {
        return type;
    }

    public InputConfig getConfig() {
        return config;
    }

    public InputSource getInputSource() {
        return inputSource;
    }
}
