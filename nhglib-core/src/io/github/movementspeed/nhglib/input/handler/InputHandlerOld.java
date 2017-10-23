package io.github.movementspeed.nhglib.input.handler;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import io.github.movementspeed.nhglib.data.models.serialization.InputConfigurationsJson;
import io.github.movementspeed.nhglib.data.models.serialization.InputJson;
import io.github.movementspeed.nhglib.input.configuration.InputConfigurations;
import io.github.movementspeed.nhglib.input.configuration.impls.KeyInputConfiguration;
import io.github.movementspeed.nhglib.input.configuration.impls.MouseInputConfiguration;
import io.github.movementspeed.nhglib.input.configuration.impls.PointerInputConfiguration;
import io.github.movementspeed.nhglib.input.configuration.impls.StickInputConfiguration;
import io.github.movementspeed.nhglib.input.controllers.ControllerCodes;
import io.github.movementspeed.nhglib.input.controllers.ControllerConfiguration;
import io.github.movementspeed.nhglib.input.controllers.StickConfiguration;
import io.github.movementspeed.nhglib.input.enums.InputAction;
import io.github.movementspeed.nhglib.input.interfaces.InputListener;
import io.github.movementspeed.nhglib.input.models.*;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.utils.data.Strings;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputHandlerOld implements ControllerListener, InputProcessor {
    private InputConfigurations config;
    private InputMultiplexer inputMultiplexer;
    private Vector2 tempVec;

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

    public InputHandlerOld() {
        tempVec = new Vector2();

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

        inputMultiplexer = new InputMultiplexer(this);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    // ControllerListener interface --------------------------------------

    @Override
    public void connected(Controller controller) {
        NhgLogger.log(this, Strings.Messages.controllerConnected, controller.getName());
        mapStickInputs();
    }

    @Override
    public void disconnected(Controller controller) {
        NhgLogger.log(this, Strings.Messages.controllerDisconnected, controller.getName());
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
        keyDown(input, keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        NhgInput input = keyInputsMap.get(keycode);
        keyUp(input, keycode);
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Application.ApplicationType type = Gdx.app.getType();

        switch (type) {
            case Android:
            case iOS:
                NhgInput input = pointerInputsMap.get(pointer);
                touchDownPointer(input, pointer);
                break;

            case Desktop:
                MouseSourceType sourceType = MouseSourceType.fromButtonCode(button);
                NhgInput mouseInput = mouseInputsMap.get(sourceType);
                touchDownMouse(mouseInput, sourceType);
                break;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Application.ApplicationType type = Gdx.app.getType();

        switch (type) {
            case Android:
            case iOS:
                NhgInput input = pointerInputsMap.get(pointer);
                touchUpPointer(input);
                break;

            case Desktop:
                MouseSourceType sourceType = MouseSourceType.fromButtonCode(button);
                NhgInput mouseInput = mouseInputsMap.get(sourceType);
                touchUpMouse(mouseInput);
                break;
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        NhgInput input = mouseInputsMap.get(MouseSourceType.MOUSE_XY);
        mouseMoved(input);
        return false;
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

    public void setActiveContext(String contextName, boolean active) {
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

    public void addContext(InputContext context) {
        if (context != null) {
            inputContexts.put(context.getName(), context);
        }
    }

    public void addInputProcessor(InputProcessor inputProcessor) {
        inputMultiplexer.addProcessor(inputProcessor);
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

    public void buttonDown(NhgInput input) {

    }

    public void keyDown(NhgInput input, int keycode) {
        if (input != null) {
            input.setAction(InputAction.DOWN);
            KeyInputConfiguration conf = config.getKeyConfiguration(input.getName());

            if (conf.getInputMode() == InputMode.REPEAT) {
                activeKeyCodes.put(keycode, input);
            } else {
                dispatchKeyInput(input);
            }
        }
    }

    public void keyUp(NhgInput input, int keycode) {
        if (input != null) {
            input.setAction(InputAction.UP);
            dispatchKeyInput(input);
        }

        if (activeKeyCodes.containsKey(keycode)) {
            activeKeyCodes.removeKey(keycode);
        }
    }

    public void touchDownMouse(NhgInput input, MouseSourceType sourceType) {
        if (input != null) {
            activeMouseInputs.put(sourceType, input);
        }
    }

    public void touchDownPointer(NhgInput input, int pointer) {
        if (input != null) {
            input.setAction(InputAction.DOWN);
            activePointers.put(pointer, input);
        }
    }

    public void touchUpMouse(NhgInput input) {
        if (input != null) {
            input.setAction(InputAction.UP);
        }
    }

    public void touchUpPointer(NhgInput input) {
        if (input != null) {
            input.setAction(InputAction.UP);
        }
    }

    public void mouseMoved(NhgInput input) {
        activeMouseInputs.put(MouseSourceType.MOUSE_XY, input);
    }

    public boolean isActive(String contextName) {
        boolean res = false;
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

    private void processStickInput(int controllerId, Controller controller, NhgInput input) {
        if (config != null) {
            tempVec.set(0, 0);

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
                        tempVec.x = controller.getAxis(ControllerCodes.Xbox360.STICK_LEFT_HORIZONTAL);
                        tempVec.y = controller.getAxis(ControllerCodes.Xbox360.STICK_LEFT_VERTICAL);
                    } else if (Ouya.isRunningOnOuya()) {
                        tempVec.x = controller.getAxis(Ouya.AXIS_LEFT_X);
                        tempVec.y = controller.getAxis(Ouya.AXIS_LEFT_Y);
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
                        tempVec.x = controller.getAxis(ControllerCodes.Xbox360.STICK_RIGHT_HORIZONTAL);
                        tempVec.y = controller.getAxis(ControllerCodes.Xbox360.STICK_RIGHT_VERTICAL);
                    } else if (Ouya.isRunningOnOuya()) {
                        tempVec.x = controller.getAxis(Ouya.AXIS_RIGHT_X);
                        tempVec.y = controller.getAxis(Ouya.AXIS_RIGHT_Y);
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
                tempVec.x *= -1;
            }

            if (invertVerticalAxis) {
                tempVec.y *= -1;
            }

            if (Math.abs(tempVec.x) < deadZone) {
                tempVec.x = 0;
            }

            if (Math.abs(tempVec.y) < deadZone) {
                tempVec.y = 0;
            }

            tempVec.scl(horizontalSensitivity, verticalSensitivity);

            InputSource inputSource = input.getSource();
            inputSource.setName(controller.getName());
            inputSource.setValue(tempVec);
        }
    }

    private void processPointerInput(int pointer, NhgInput input) {
        if (config != null) {
            PointerInputConfiguration conf = config.getPointerConfiguration(input.getName());

            if (conf != null) {
                switch (conf.getPointerSourceType()) {
                    case POINTER_DELTA_XY:
                        tempVec.set(Gdx.input.getDeltaX(pointer), Gdx.input.getDeltaY(pointer));
                        tempVec.scl(conf.getHorizontalSensitivity(), conf.getVerticalSensitivity());
                        break;

                    case POINTER_XY:
                        tempVec.set(Gdx.input.getX(pointer), Gdx.input.getY(pointer));
                        break;
                }

                InputSource inputSource = input.getSource();
                inputSource.setName(input.getName());
                inputSource.setValue(tempVec);
            }
        }
    }

    private void processMouseInput(NhgInput input) {
        if (config != null) {
            MouseInputConfiguration conf = config.getMouseConfiguration(input.getName());

            if (conf != null) {
                if (conf.getMouseSourceType() == MouseSourceType.MOUSE_XY) {
                    tempVec.set(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
                    tempVec.scl(conf.getHorizontalSensitivity(), conf.getVerticalSensitivity());

                    InputSource inputSource = input.getSource();
                    inputSource.setName(input.getName());
                    inputSource.setValue(tempVec);

                    activeMouseInputs.removeKey(MouseSourceType.MOUSE_XY);
                }
            }
        }
    }

    private void dispatchKeyInputs() {
        if (activeKeyCodes.size > 0) {
            ArrayMap.Keys<Integer> keyCodes = activeKeyCodes.keys();

            for (int keyCode : keyCodes) {
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

            for (int pointer : pointers) {
                NhgInput input = activePointers.get(pointer);

                if (input != null && isValidInput(input)) {
                    processPointerInput(pointer, input);
                    dispatchPointerInput(input);

                    if (input.getAction() == InputAction.UP) {
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

                    if (input.getAction() == InputAction.UP) {
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
                break;
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

                if (in.getType() == InputType.KEYBOARD_BUTTON && conf.getKeyCode() == keycode) {
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

                if (in.getType() == InputType.TOUCH && conf.getId() == id) {
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

                if (in.getType() == InputType.MOUSE_BUTTON && conf.getMouseSourceType().button == sourceType.button) {
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

                if (in.getType() == InputType.CONTROLLER_STICK && conf.getControllerId() == id) {
                    res.add(in);
                }
            }
        }

        return res;
    }
}
