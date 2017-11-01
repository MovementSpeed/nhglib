package io.github.movementspeed.nhglib.input.handler;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import io.github.movementspeed.nhglib.input.enums.InputAction;
import io.github.movementspeed.nhglib.input.interfaces.InputHandler;
import io.github.movementspeed.nhglib.input.models.InputType;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.input.models.impls.system.NhgKeyboardButtonInput;
import io.github.movementspeed.nhglib.input.models.impls.system.NhgMouseButtonInput;
import io.github.movementspeed.nhglib.input.models.impls.system.NhgTouchInput;

public class SystemInputHandler implements InputHandler {
    private Interface systemInputInterface;
    private Vector2 vec0;

    private Array<Integer> activeKeyboardButtonInputs;
    private Array<Integer> activeMouseButtonInputs;
    private Array<Integer> activeTouchInputs;

    private IntMap<NhgKeyboardButtonInput> keyboardButtonInputs;
    private IntMap<NhgMouseButtonInput> mouseButtonInputs;
    private IntMap<NhgTouchInput> touchInputs;

    public SystemInputHandler(Interface systemInputInterface, InputMultiplexer inputMultiplexer, Array<NhgInput> systemInputArray) {
        this.systemInputInterface = systemInputInterface;

        vec0 = new Vector2();

        keyboardButtonInputs = new IntMap<>();
        mouseButtonInputs = new IntMap<>();
        touchInputs = new IntMap<>();

        activeKeyboardButtonInputs = new Array<>();
        activeMouseButtonInputs = new Array<>();
        activeTouchInputs = new Array<>();

        mapSystemInput(systemInputArray);
        handleSystemInput(inputMultiplexer);
    }

    @Override
    public void update() {
        for (Integer pointer : activeTouchInputs) {
            systemInputInterface.onSystemInput(touchInputs.get(pointer));
        }

        for (Integer keyCode : activeKeyboardButtonInputs) {
            systemInputInterface.onSystemInput(keyboardButtonInputs.get(keyCode));
        }

        for (Integer button : activeMouseButtonInputs) {
            systemInputInterface.onSystemInput(mouseButtonInputs.get(button));
        }
    }

    private void mapSystemInput(Array<NhgInput> systemInputArray) {
        for (NhgInput nhgInput : systemInputArray) {
            InputType inputType = nhgInput.getType();

            switch (inputType) {
                case KEYBOARD_BUTTON:
                    NhgKeyboardButtonInput keyboardButtonInput = ((NhgKeyboardButtonInput) nhgInput);
                    int keyCode = keyboardButtonInput.getKeyCode();
                    keyboardButtonInputs.put(keyCode, keyboardButtonInput);
                    break;

                case MOUSE_BUTTON:
                    NhgMouseButtonInput mouseButtonInput = ((NhgMouseButtonInput) nhgInput);
                    int buttonCode = mouseButtonInput.getButtonCode();
                    mouseButtonInputs.put(buttonCode, mouseButtonInput);
                    break;

                case TOUCH:
                    NhgTouchInput touchInput = ((NhgTouchInput) nhgInput);
                    int pointerNumber = touchInput.getPointerNumber();
                    touchInputs.put(pointerNumber, touchInput);
                    break;
            }
        }
    }

    private void handleSystemInput(InputMultiplexer inputMultiplexer) {
        InputProcessor ip = new InputProcessor() {
            @Override
            public boolean keyDown(int keyCode) {
                NhgKeyboardButtonInput input = keyboardButtonInputs.get(keyCode);

                if (input != null) {
                    input.setAction(InputAction.DOWN);

                    switch (input.getMode()) {
                        case REPEAT:
                            if (!activeKeyboardButtonInputs.contains(keyCode, true)) {
                                activeKeyboardButtonInputs.add(keyCode);
                            }
                            break;

                        default:
                            systemInputInterface.onSystemInput(input);
                            break;
                    }
                }

                return false;
            }

            @Override
            public boolean keyUp(int keyCode) {
                NhgKeyboardButtonInput input = keyboardButtonInputs.get(keyCode);

                if (input != null) {
                    input.setAction(InputAction.UP);

                    switch (input.getMode()) {
                        case REPEAT:
                            activeKeyboardButtonInputs.removeValue(keyCode, true);
                            break;

                        default:
                            systemInputInterface.onSystemInput(input);
                            break;
                    }
                }

                return false;
            }

            @Override
            public boolean keyTyped(char c) {
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                NhgInput input;

                if (isDesktop()) {
                    input = mouseButtonInputs.get(button);

                    if (input != null) {
                        switch (input.getMode()) {
                            case REPEAT:
                                if (!activeMouseButtonInputs.contains(button, true)) {
                                    activeMouseButtonInputs.add(button);
                                }
                                break;

                            default:
                                systemInputInterface.onSystemInput(input);
                                break;
                        }
                    }
                } else {
                    input = touchInputs.get(pointer);

                    if (input != null) {
                        switch (input.getMode()) {
                            case REPEAT:
                                if (!activeTouchInputs.contains(pointer, true)) {
                                    activeTouchInputs.add(pointer);
                                }
                                break;

                            default:
                                systemInputInterface.onSystemInput(input);
                                break;
                        }
                    }
                }

                if (input != null) {
                    input.setAction(InputAction.DOWN);
                    input.setValue(vec0.set(screenX, screenY));
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                NhgInput input;

                if (isDesktop()) {
                    input = mouseButtonInputs.get(button);

                    if (input != null) {
                        switch (input.getMode()) {
                            case REPEAT:
                                activeMouseButtonInputs.removeValue(button, true);
                                break;

                            default:
                                systemInputInterface.onSystemInput(input);
                                break;
                        }
                    }
                } else {
                    input = touchInputs.get(pointer);

                    if (input != null) {
                        switch (input.getMode()) {
                            case REPEAT:
                                activeTouchInputs.removeValue(pointer, true);
                                break;

                            default:
                                systemInputInterface.onSystemInput(input);
                                break;
                        }
                    }
                }

                if (input != null) {
                    input.setAction(InputAction.UP);
                    input.setValue(vec0.set(screenX, screenY));
                }

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
        };

        inputMultiplexer.addProcessor(ip);
    }

    private boolean isDesktop() {
        return Gdx.app.getType() == Application.ApplicationType.Desktop;
    }

    public interface Interface {
        void onSystemInput(NhgInput input);
    }
}
