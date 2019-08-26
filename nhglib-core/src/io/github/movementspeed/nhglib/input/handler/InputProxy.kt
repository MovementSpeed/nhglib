package io.github.movementspeed.nhglib.input.handler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Predicate;
import io.github.movementspeed.nhglib.input.interfaces.InputHandler;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.InputContext;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput;
import io.github.movementspeed.nhglib.interfaces.Updatable;

public class InputProxy implements Updatable {
    private InputMultiplexer inputMultiplexer;
    private InputListener inputListener;
    private InputHandler systemInputHandler;
    private InputHandler virtualInputHandler;

    private Array<InputContext> inputContexts;

    public InputProxy() {
        inputMultiplexer = new InputMultiplexer();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void update() {
        virtualInputHandler.update();
        systemInputHandler.update();
    }

    public void setInputListener(InputListener inputListener) {
        this.inputListener = inputListener;
    }

    public void setEnableContext(String name, boolean enable) {
        for (InputContext inputContext : inputContexts) {
            if (inputContext.is(name)) {
                inputContext.setEnabled(enable);
                break;
            }
        }
    }

    public void build(Array<InputContext> inputContexts, Array<NhgInput> systemInputArray, Array<NhgInput> virtualInputArray) {
        this.inputContexts = inputContexts;

        virtualInputHandler = new VirtualInputHandler(
                this, inputMultiplexer, virtualInputArray);

        systemInputHandler = new SystemInputHandler(
                this, inputMultiplexer, systemInputArray);
    }

    public VirtualInputHandler getVirtualInputHandler() {
        return (VirtualInputHandler) virtualInputHandler;
    }

    void onInput(NhgInput input) {
        inputListener.onInput(input);
    }
}
