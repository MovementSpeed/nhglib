package io.github.voidzombie.nhglib.input.configuration.base;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public abstract class InputConfiguration {
    protected String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
