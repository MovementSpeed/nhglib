package io.github.voidzombie.nhglib.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.NHG;
import io.github.voidzombie.nhglib.data.models.serialization.InputJson;
import io.github.voidzombie.nhglib.interfaces.Updatable;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class Input implements Updatable {
    private final ArrayMap<String, InputContext> inputContexts;
    private final Array<InputContext> activeContexts;
    private final Array<InputListener> inputListeners;

    public Input() {
        inputContexts = new ArrayMap<>();
        activeContexts = new Array<>();
        inputListeners = new Array<>();
    }

    @Override
    public void update() {
        Array<InputContext> activeContexts = NHG.input.getActiveContexts();

        for (InputContext context : activeContexts) {
            ArrayMap.Values<NHGInput> inputs = context.getInputs();

            for (NHGInput in : inputs) {
                InputConfig config = in.getConfig();

                switch (in.getSource()) {
                    case BUTTON:
                        switch (config.getInputMode()) {
                            case REPEAT:
                                if (Gdx.input.isKeyPressed(config.getKeycode())) {
                                    sendInput(in);
                                }
                                break;

                            case SINGLE:
                                if (Gdx.input.isKeyJustPressed(config.getKeycode())) {
                                    sendInput(in);
                                }
                                break;
                        }
                        break;

                    case CUSTOM:
                        break;

                    case INPUT_XY:
                        break;
                }
            }
        }
    }

    public void setActive(String contextName, Boolean active) {
        if (active != null) {
            InputContext inputContext = inputContexts.get(contextName);

            if (inputContext != null) {
                inputContext.setActive(active);

                if (active) {
                    activeContexts.add(inputContext);
                } else {
                    activeContexts.removeValue(inputContext, true);
                }
            }
        }
    }

    public void addContext(InputContext context) {
        if (context != null) {
            inputContexts.put(context.getName(), context);
        }
    }

    public void fromJson(JsonValue jsonValue) {
        InputJson inputJson = new InputJson();
        inputJson.parse(jsonValue);

        for (InputContext inputContext : inputJson.get()) {
            addContext(inputContext);
        }
    }

    public void addListener(InputListener inputListener) {
        if (inputListener != null) {
            if (!inputListeners.contains(inputListener, true)) {
                inputListeners.add(inputListener);
            }
        }
    }

    public void removeListener(InputListener inputListener) {
        if (inputListener != null) {
            inputListeners.removeValue(inputListener, true);
        }
    }

    public Boolean isActive(String contextName) {
        Boolean res = false;
        InputContext inputContext = inputContexts.get(contextName);

        if (inputContext != null) {
            res = inputContext.isActive();
        }

        return res;
    }

    public InputContext getContext(String name) {
        return inputContexts.get(name);
    }

    public Array<InputContext> getActiveContexts() {
        return activeContexts;
    }

    private void sendInput(NHGInput input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.onInput(input);
        }
    }
}
