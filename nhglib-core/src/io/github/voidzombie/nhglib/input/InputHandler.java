package io.github.voidzombie.nhglib.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.controllers.mappings.Xbox;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ArrayMap;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.Nhg;
import io.github.voidzombie.nhglib.data.models.serialization.InputJson;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputHandler implements ControllerListener, InputProcessor {
    private Array<InputContext> activeContexts;
    private Array<InputListener> inputListeners;

    private ArrayMap<Integer, NhgInput> keyInputsMap;
    private ArrayMap<Integer, Array<NhgInput>> stickInputsMap;
    private ArrayMap<Integer, NhgInput> activeKeyCodes;
    private ArrayMap<String, InputContext> inputContexts;

    public InputHandler() {
        activeContexts = new Array<>();
        inputListeners = new Array<>();
        inputContexts = new ArrayMap<>();
        activeKeyCodes = new ArrayMap<>();
        keyInputsMap = new ArrayMap<>();
        stickInputsMap = new ArrayMap<>();

        Controllers.addListener(this);
        Gdx.input.setInputProcessor(this);
    }

    public void update() {
        dispatchKeyInputs();
        dispatchStickInputs();
    }

    // ControllerListener interface --------------------------------------

    @Override
    public void connected(Controller controller) {
        Nhg.logger.log(this, Nhg.strings.messages.controllerConnected, controller.getName());
        mapStickInputs();
    }

    @Override
    public void disconnected(Controller controller) {
        Nhg.logger.log(this, Nhg.strings.messages.controllerDisconnected, controller.getName());
        mapStickInputs();
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }

    // InputProcessor interface --------------------------------------

    @Override
    public boolean keyDown(int keycode) {
        NhgInput input = keyInputsMap.get(keycode);

        if (input != null) {
            if (input.getConfig().getInputMode() == InputMode.REPEAT) {
                activeKeyCodes.put(keycode, input);
            } else {
                dispatchKeyInput(input);
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (activeKeyCodes.containsKey(keycode)) {
            activeKeyCodes.removeKey(keycode);
        }

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
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

        mapKeyInputs();
        mapStickInputs();
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

    private void dispatchKeyInput(NhgInput input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.onKeyInput(input);
        }
    }

    private void dispatchStickInput(NhgInput input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.onStickInput(input);
        }
    }

    private void mapKeyInputs() {
        for (int keyCode = 0; keyCode < 256; keyCode++) {
            keyInputsMap.put(keyCode, getKeyInputWithKeycode(keyCode));
        }
    }

    private void mapStickInputs() {
        Array<Controller> availableControllers = Controllers.getControllers();

        for (int id = 0; id < availableControllers.size; id++) {
            Array<NhgInput> input = getStickInputsWithControllerId(id);
            stickInputsMap.put(id, input);
        }
    }

    private void dispatchKeyInputs() {
        if (activeKeyCodes.size > 0) {
            ArrayMap.Keys<Integer> keyCodes = activeKeyCodes.keys();

            for (Integer keyCode : keyCodes) {
                dispatchKeyInput(activeKeyCodes.get(keyCode));
            }
        }
    }

    private void processStickInput(Controller controller, NhgInput input) {
        Vector2 axis = VectorPool.getVector2();
        axis.set(0, 0);

        StickType stickType = input.getConfig().getStickType();

        switch (stickType) {
            case LEFT:
                if (Xbox.isXboxController(controller)) {
                    axis.x = controller.getAxis(ControllerCodes.Xbox360.STICK_LEFT_HORIZONTAL);
                    axis.y = controller.getAxis(ControllerCodes.Xbox360.STICK_LEFT_VERTICAL);
                } else if (Ouya.isRunningOnOuya()) {
                    axis.x = controller.getAxis(Ouya.AXIS_LEFT_X);
                    axis.y = controller.getAxis(Ouya.AXIS_LEFT_Y);
                }
                break;

            case RIGHT:
                if (Xbox.isXboxController(controller)) {
                    axis.x = controller.getAxis(ControllerCodes.Xbox360.STICK_RIGHT_HORIZONTAL);
                    axis.y = controller.getAxis(ControllerCodes.Xbox360.STICK_RIGHT_VERTICAL);
                } else if (Ouya.isRunningOnOuya()) {
                    axis.x = controller.getAxis(Ouya.AXIS_RIGHT_X);
                    axis.y = controller.getAxis(Ouya.AXIS_RIGHT_Y);
                }
                break;

            case VIRTUAL:
                break;
        }

        InputSource inputSource = input.getInputSource();
        inputSource.setName(controller.getName());
        inputSource.setValue(axis);

        float deadZone = input.getConfig().getStickDeadZoneRadius();

        if (Math.abs(axis.x) < deadZone) {
            axis.x = 0;
        }

        if (Math.abs(axis.y) < deadZone) {
            axis.y = 0;
        }

        axis.scl(input.getConfig().getSensitivity());
    }

    private void dispatchStickInputs() {
        Array<Controller> availableControllers = Controllers.getControllers();

        for (int id = 0; id < availableControllers.size; id++) {
            Controller controller = availableControllers.get(id);

            if (controller != null) {
                Array<NhgInput> stickInputs = stickInputsMap.get(id);

                if (stickInputs != null) {
                    for (NhgInput input : stickInputs) {
                        if (input != null) {
                            processStickInput(controller, input);
                            dispatchStickInput(input);
                        }
                    }
                }
            }
        }
    }

    private NhgInput getKeyInputWithKeycode(int keycode) {
        NhgInput res = null;

        for (InputContext context : inputContexts.values()) {
            ArrayMap.Values<NhgInput> inputs = context.getInputs();

            for (NhgInput in : inputs) {
                if (in.getType() == InputType.KEY && in.getConfig().getKeycode().compareTo(keycode) == 0) {
                    res = in;
                    break;
                }
            }
        }

        return res;
    }

    private Array<NhgInput> getStickInputsWithControllerId(int id) {
        Array<NhgInput> res = new Array<>();

        for (InputContext context : inputContexts.values()) {
            ArrayMap.Values<NhgInput> inputs = context.getInputs();

            for (NhgInput in : inputs) {
                if (in.getType() == InputType.STICK &&
                        in.getConfig().getControllerId() != null &&
                        in.getConfig().getControllerId().compareTo(id) == 0) {
                    res.add(in);
                }
            }
        }

        return res;
    }
}
