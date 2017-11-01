package io.github.movementspeed.nhglib.core.ecs.systems.impl;

import com.artemis.BaseSystem;
import com.badlogic.gdx.utils.Array;
import io.github.movementspeed.nhglib.Nhg;
import io.github.movementspeed.nhglib.assets.Asset;
import io.github.movementspeed.nhglib.assets.Assets;
import io.github.movementspeed.nhglib.core.messaging.Message;
import io.github.movementspeed.nhglib.input.handler.InputProxy;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.reactivex.functions.Consumer;

public class InputSystem extends BaseSystem implements InputListener {
    private InputProxy inputProxy;
    private Array<InputListener> inputListeners;

    public InputSystem() {
        inputListeners = new Array<>();
        /*inputProxy = new InputProxy();
        inputProxy.setInputListener(this);*/
    }

    @Override
    public void onInput(NhgInput input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.onInput(input);
        }
    }

    @Override
    protected void processSystem() {
        if (inputProxy != null) {
            inputProxy.update();
        }
    }

    public void addInputListener(InputListener inputListener) {
        inputListeners.add(inputListener);
    }

    public void loadMapping(Assets assets, String fileName) {
        inputProxy = assets.loadAssetSync(new Asset("nhgInputMap", fileName, InputProxy.class));
        inputProxy.setInputListener(this);
    }

    public InputProxy getInputProxy() {
        return inputProxy;
    }
}
