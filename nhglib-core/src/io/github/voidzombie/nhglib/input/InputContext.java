package io.github.voidzombie.nhglib.input;

import com.badlogic.gdx.utils.ArrayMap;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputContext {
    private final ArrayMap<String, NhgInput> inputMap;

    private Boolean active;
    private String name;

    public InputContext(String name) {
        this.name = name;

        active = false;
        inputMap = new ArrayMap<>();
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addInput(NhgInput input) {
        if (input != null) {
            inputMap.put(input.getName(), input);
        }
    }

    public Boolean isActive() {
        return active;
    }

    public String getName() {
        return name;
    }

    public NhgInput getInput(String name) {
        return inputMap.get(name);
    }

    public ArrayMap.Values<NhgInput> getInputs() {
        return inputMap.values();
    }
}
