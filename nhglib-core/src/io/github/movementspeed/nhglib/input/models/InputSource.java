package io.github.movementspeed.nhglib.input.models;

/**
 * Created by Fausto Napoli on 22/01/2017.
 */
public class InputSource {
    private String name;
    private Object value;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Boolean is(String name) {
        return name.contentEquals(name);
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
