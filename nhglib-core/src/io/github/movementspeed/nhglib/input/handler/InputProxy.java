package io.github.movementspeed.nhglib.input.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.input.interfaces.InputHandler;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput;
import io.github.movementspeed.nhglib.interfaces.Updatable;

public class InputProxy implements SystemInputHandler.Interface, VirtualInputHandler.Interface, Updatable {
    private InputMultiplexer inputMultiplexer;
    private InputListener inputListener;
    private InputHandler systemInputHandler;
    private InputHandler virtualInputHandler;

    public InputProxy() {
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void update() {
        virtualInputHandler.update();
        systemInputHandler.update();
    }

    @Override
    public void onSystemInput(NhgInput input) {
        inputListener.onInput(input);
    }

    @Override
    public void onVirtualInput(NhgVirtualButtonInput input) {
        inputListener.onInput(input);
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void build(Array<NhgInput> virtualInputArray, Array<NhgInput> systemInputArray) {
        virtualInputHandler = new VirtualInputHandler(
                this, inputMultiplexer, virtualInputArray);

        systemInputHandler = new SystemInputHandler(
                this, inputMultiplexer, systemInputArray);
    }

    public VirtualInputHandler getVirtualInputHandler() {
        return (VirtualInputHandler) virtualInputHandler;
    }
}
