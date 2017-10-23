package io.github.movementspeed.nhglib.input.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import io.github.movementspeed.nhglib.input.interfaces.InputHandler;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualInput;
import io.github.movementspeed.nhglib.interfaces.Updatable;

public class InputProxy implements SystemInputHandler.Interface, VirtualInputHandler.Interface, Updatable {
    private InputMultiplexer inputMultiplexer;
    private InputHandler systemInputHandler;
    private InputHandler virtualInputHandler;

    public InputProxy() {
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);

        virtualInputHandler = new VirtualInputHandler(
                this, inputMultiplexer, null);

        systemInputHandler = new SystemInputHandler(
                this, inputMultiplexer, null);
    }

    @Override
    public void update() {
        systemInputHandler.update();
    }

    @Override
    public void onSystemInput(NhgInput input) {
    }

    @Override
    public void onVirtualInput(NhgVirtualInput input) {
    }
}
