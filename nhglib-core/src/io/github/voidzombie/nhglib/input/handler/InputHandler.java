package io.github.voidzombie.nhglib.input.handler;

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
import io.github.voidzombie.nhglib.data.models.serialization.InputConfigurationsJson;
import io.github.voidzombie.nhglib.data.models.serialization.InputJson;
import io.github.voidzombie.nhglib.input.configuration.InputConfigurations;
import io.github.voidzombie.nhglib.input.configuration.impls.KeyInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.MouseInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.PointerInputConfiguration;
import io.github.voidzombie.nhglib.input.configuration.impls.StickInputConfiguration;
import io.github.voidzombie.nhglib.input.controllers.ControllerCodes;
import io.github.voidzombie.nhglib.input.controllers.ControllerConfiguration;
import io.github.voidzombie.nhglib.input.controllers.StickConfiguration;
import io.github.voidzombie.nhglib.input.enums.InputAction;
import io.github.voidzombie.nhglib.input.interfaces.InputListener;
import io.github.voidzombie.nhglib.input.models.*;
import io.github.voidzombie.nhglib.utils.data.VectorPool;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputHandler implements ControllerListener, InputProcessor {
    private InputConfigurations config;
    private Array<InputContext> activeContexts;
    private Array<InputListener> inputListeners;

    private ArrayMap<Integer, NhgInput> keyInputsMap;
    private ArrayMap<Integer, NhgInput> pointerInputsMap;
    private ArrayMap<MouseSourceType, NhgInput> mouseInputsMap;
    private ArrayMap<Integer, Array<NhgInput>> stickInputsMap;

    private ArrayMap<Integer, NhgInput> activeKeyCodes;
    private ArrayMap<Integer, NhgInput> activePointers;
    private ArrayMap<MouseSourceType, NhgInput> activeMouseInputs;
    private ArrayMap<String, InputContext> inputContexts;

    public InputHandler() {
        activeContexts = new Array<>();
        activeKeyCodes = new ArrayMap<>();
        activePointers = new ArrayMap<>();
        activeMouseInputs = new ArrayMap<>();

        inputListeners = new Array<>();
        inputContexts = new ArrayMap<>();

        keyInputsMap = new ArrayMap<>();
        pointerInputsMap = new ArrayMap<>();
        stickInputsMap = new ArrayMap<>();
        mouseInputsMap = new ArrayMap<>();

        Controllers.addListener(this);
        Gdx.input.setInputProcessor(this);
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
            input.setInputAction(InputAction.DOWN);
            KeyInputConfiguration conf = config.getKeyConfiguration(input.getName());

            if (conf.getInputMode() == InputMode.REPEAT) {
                activeKeyCodes.put(keycode, input);
            } else {
                dispatchKeyInput(input);
            }
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        NhgInput input = keyInputsMap.get(keycode);

        if (input != null) {
            input.setInputAction(InputAction.UP);
            dispatchKeyInput(input);
        }

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
        NhgInput input = pointerInputsMap.get(pointer);

        if (input != null) {
            input.setInputAction(InputAction.DOWN);
            activePointers.put(pointer, input);
        }

        MouseSourceType sourceType = MouseSourceType.fromButtonCode(button);
        NhgInput mouseInput = mouseInputsMap.get(sourceType);

        if (mouseInput != null) {
            activeMouseInputs.put(sourceType, mouseInput);
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        NhgInput input = pointerInputsMap.get(pointer);

        if (input != null) {
            input.setInputAction(InputAction.UP);
        }

        MouseSourceType sourceType = MouseSourceType.fromButtonCode(button);
        NhgInput mouseInput = mouseInputsMap.get(sourceType);

        if (mouseInput != null) {
            mouseInput.setInputAction(InputAction.UP);
        }

        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        NhgInput input = mouseInputsMap.get(MouseSourceType.MOUSE_XY);
        activeMouseInputs.put(MouseSourceType.MOUSE_XY, input);

        return true;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void update() {
        dispatchKeyInputs();
        dispatchStickInputs();
        dispatchPointerInputs();
        dispatchMouseInputs();
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

        InputConfigurationsJson configurationsJson = new InputConfigurationsJson();
        configurationsJson.parse(jsonValue);
        config = configurationsJson.get();

        mapKeyInputs();
        mapStickInputs();
        mapPointerInputs();
        mapMouseInputs();
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

    private void mapKeyInputs() {
        for (int keyCode = 0; keyCode < 256; keyCode++) {
            NhgInput input = getKeyInputWithKeycode(keyCode);

            if (input != null) {
                keyInputsMap.put(keyCode, input);
            }
        }
    }

    private void mapStickInputs() {
        Array<Controller> availableControllers = Controllers.getControllers();

        for (int id = 0; id < availableControllers.size; id++) {
            Array<NhgInput> input = getStickInputsWithControllerId(id);

            if (input.size > 0) {
                stickInputsMap.put(id, input);
            }
        }
    }

    private void mapPointerInputs() {
        for (int pointer = 0; pointer < 10; pointer++) {
            NhgInput input = getPointerInputWithId(pointer);

            if (input != null) {
                pointerInputsMap.put(pointer, input);
            }
        }
    }

    private void mapMouseInputs() {
        for (MouseSourceType sourceType : MouseSourceType.values()) {
            NhgInput input = getMouseInputWithSourceType(sourceType);

            if (input != null) {
                mouseInputsMap.put(sourceType, input);
            }
        }
    }

    private void processStickInput(Integer controllerId, Controller controller, NhgInput input) {
        if (config != null) {
            Vector2 axis = VectorPool.getVector2();
            axis.set(0, 0);

            boolean invertHorizontalAxis = false;
            boolean invertVerticalAxis = false;

            float deadZone = 0f;
            float horizontalSensitivity = 1f;
            float verticalSensitivity = 1f;

            StickInputConfiguration conf = config.getStickConfiguration(input.getName());
            StickType stickType = conf.getStickType();

            ControllerConfiguration controllerConf = config.getControllerConfiguration(controllerId);

            switch (stickType) {
                case LEFT:
                    if (Xbox.isXboxController(controller)) {
                        axis.x = controller.getAxis(ControllerCodes.Xbox360.STICK_LEFT_HORIZONTAL);
                        axis.y = controller.getAxis(ControllerCodes.Xbox360.STICK_LEFT_VERTICAL);
                    } else if (Ouya.isRunningOnOuya()) {
                        axis.x = controller.getAxis(Ouya.AXIS_LEFT_X);
                        axis.y = controller.getAxis(Ouya.AXIS_LEFT_Y);
                    }

                    StickConfiguration stickConfiguration = controllerConf.getLeftStick();

                    invertHorizontalAxis = stickConfiguration.getInvertHorizontalAxis();
                    invertVerticalAxis = stickConfiguration.getInvertVerticalAxis();

                    deadZone = stickConfiguration.getDeadZoneRadius();
                    horizontalSensitivity = stickConfiguration.getHorizontalSensitivity();
                    verticalSensitivity = stickConfiguration.getVerticalSensitivity();
                    break;

                case RIGHT:
                    if (Xbox.isXboxController(controller)) {
                        axis.x = controller.getAxis(ControllerCodes.Xbox360.STICK_RIGHT_HORIZONTAL);
                        axis.y = controller.getAxis(ControllerCodes.Xbox360.STICK_RIGHT_VERTICAL);
                    } else if (Ouya.isRunningOnOuya()) {
                        axis.x = controller.getAxis(Ouya.AXIS_RIGHT_X);
                        axis.y = controller.getAxis(Ouya.AXIS_RIGHT_Y);
                    }

                    stickConfiguration = controllerConf.getRightStick();

                    invertHorizontalAxis = stickConfiguration.getInvertHorizontalAxis();
                    invertVerticalAxis = stickConfiguration.getInvertVerticalAxis();

                    deadZone = stickConfiguration.getDeadZoneRadius();
                    horizontalSensitivity = stickConfiguration.getHorizontalSensitivity();
                    verticalSensitivity = stickConfiguration.getVerticalSensitivity();
                    break;

                case VIRTUAL:
                    break;
            }

            if (invertHorizontalAxis) {
                axis.x *= -1;
            }

            if (invertVerticalAxis) {
                axis.y *= -1;
            }

            if (Math.abs(axis.x) < deadZone) {
                axis.x = 0;
            }

            if (Math.abs(axis.y) < deadZone) {
                axis.y = 0;
            }

            axis.scl(horizontalSensitivity, verticalSensitivity);

            InputSource inputSource = input.getInputSource();
            inputSource.setName(controller.getName());
            inputSource.setValue(axis);
        }
    }

    private void processPointerInput(Integer pointer, NhgInput input) {
        if (config != null) {
            Vector2 axis = VectorPool.getVector2();
            PointerInputConfiguration conf = config.getPointerConfiguration(input.getName());

            switch (conf.getPointerSourceType()) {
                case POINTER_DELTA_XY:
                    axis.set(Gdx.input.getDeltaX(pointer), Gdx.input.getDeltaY(pointer));
                    axis.scl(conf.getHorizontalSensitivity(), conf.getVerticalSensitivity());
                    break;

                case POINTER_XY:
                    axis.set(Gdx.input.getX(pointer), Gdx.input.getY(pointer));
                    break;
            }

            InputSource inputSource = input.getInputSource();
            inputSource.setName(input.getName());
            inputSource.setValue(axis);
        }
    }

    private void processMouseInput(NhgInput input) {
        if (config != null) {
            MouseInputConfiguration conf = config.getMouseConfiguration(input.getName());

            if (conf.getMouseSourceType() == MouseSourceType.MOUSE_XY) {
                Vector2 axis = VectorPool.getVector2();

                axis.set(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
                axis.scl(conf.getHorizontalSensitivity(), conf.getVerticalSensitivity());

                InputSource inputSource = input.getInputSource();
                inputSource.setName(input.getName());
                inputSource.setValue(axis);

                activeMouseInputs.removeKey(MouseSourceType.MOUSE_XY);
            }
        }
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

    private void dispatchPointerInput(NhgInput input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.onPointerInput(input);
        }
    }

    private void dispatchMouseInput(NhgInput input) {
        for (InputListener inputListener : inputListeners) {
            inputListener.onMouseInput(input);
        }
    }

    private void dispatchKeyInputs() {
        if (activeKeyCodes.size > 0) {
            ArrayMap.Keys<Integer> keyCodes = activeKeyCodes.keys();

            for (Integer keyCode : keyCodes) {
                NhgInput input = activeKeyCodes.get(keyCode);

                if (input != null && isValidInput(input)) {
                    dispatchKeyInput(input);
                }
            }
        }
    }

    private void dispatchStickInputs() {
        Array<Controller> availableControllers = Controllers.getControllers();

        for (int id = 0; id < availableControllers.size; id++) {
            Controller controller = availableControllers.get(id);

            if (controller != null) {
                Array<NhgInput> stickInputs = stickInputsMap.get(id);

                if (stickInputs != null) {
                    for (NhgInput input : stickInputs) {
                        if (input != null && isValidInput(input)) {
                            processStickInput(id, controller, input);
                            dispatchStickInput(input);
                        }
                    }
                }
            }
        }
    }

    private void dispatchPointerInputs() {
        if (activePointers.size > 0) {
            ArrayMap.Keys<Integer> pointers = activePointers.keys();

            for (Integer pointer : pointers) {
                NhgInput input = activePointers.get(pointer);

                if (input != null && isValidInput(input)) {
                    processPointerInput(pointer, input);
                    dispatchPointerInput(input);

                    if (input.getInputAction() == InputAction.UP) {
                        if (activePointers.containsKey(pointer)) {
                            activePointers.removeKey(pointer);
                        }
                    }
                }
            }
        }
    }

    private void dispatchMouseInputs() {
        if (activeMouseInputs.size > 0) {
            ArrayMap.Keys<MouseSourceType> mouseInputs = activeMouseInputs.keys();

            for (MouseSourceType mouseInput : mouseInputs) {
                NhgInput input = activeMouseInputs.get(mouseInput);

                if (input != null && isValidInput(input)) {
                    processMouseInput(input);
                    dispatchMouseInput(input);

                    if (input.getInputAction() == InputAction.UP) {
                        if (activeMouseInputs.containsKey(mouseInput)) {
                            activeMouseInputs.removeKey(mouseInput);
                        }
                    }
                }
            }
        }
    }

    private boolean isValidInput(NhgInput input) {
        boolean validInput = false;

        for (InputContext context : activeContexts) {
            if (context.getInput(input.getName()) != null) {
                validInput = true;
            }
        }

        return validInput;
    }

    private NhgInput getKeyInputWithKeycode(int keycode) {
        NhgInput res = null;

        for (InputContext context : inputContexts.values()) {
            ArrayMap.Values<NhgInput> inputs = context.getInputs();

            for (NhgInput in : inputs) {
                KeyInputConfiguration conf = config.getKeyConfiguration(in.getName());

                if (in.getType() == InputType.KEY && conf.getKeyCode().compareTo(keycode) == 0) {
                    res = in;
                    break;
                }
            }
        }

        return res;
    }

    private NhgInput getPointerInputWithId(int id) {
        NhgInput res = null;

        for (InputContext context : inputContexts.values()) {
            ArrayMap.Values<NhgInput> inputs = context.getInputs();

            for (NhgInput in : inputs) {
                PointerInputConfiguration conf = config.getPointerConfiguration(in.getName());

                if (in.getType() == InputType.POINTER && conf.getId().compareTo(id) == 0) {
                    res = in;
                    break;
                }
            }
        }

        return res;
    }

    private NhgInput getMouseInputWithSourceType(MouseSourceType sourceType) {
        NhgInput res = null;

        for (InputContext context : inputContexts.values()) {
            ArrayMap.Values<NhgInput> inputs = context.getInputs();

            for (NhgInput in : inputs) {
                MouseInputConfiguration conf = config.getMouseConfiguration(in.getName());

                if (in.getType() == InputType.MOUSE && conf.getMouseSourceType().button == sourceType.button) {
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
                StickInputConfiguration conf = config.getStickConfiguration(in.getName());

                if (in.getType() == InputType.STICK &&
                        conf.getControllerId() != null &&
                        conf.getControllerId().compareTo(id) == 0) {
                    res.add(in);
                }
            }
        }

        return res;
    }
}
