package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.input.handler.InputProxy;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;

public class InputSystem extends BaseSystem implements InputListener {
    private InputProxy inputProxy;
    private Array<InputListener> inputListeners;

    public InputSystem() {
        inputListeners = new Array<>();
        inputProxy = new InputProxy(this);
    }

    @Override
    public void onInput(NhgInput input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.onInput(input);
        }
    }

    @Override
    protected void processSystem() {
        inputProxy.update();
    }

    public void addInputListener(InputListener inputListener) {
        inputListeners.add(inputListener);
    }

    public InputProxy getInputProxy() {
        return inputProxy;
    }
}
