package io.github.movementspeed.nhglib.assets.loaders;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import io.github.movementspeed.nhglib.input.enums.InputMode;
import io.github.movementspeed.nhglib.input.enums.InputType;
import io.github.movementspeed.nhglib.input.enums.TouchInputType;
import io.github.movementspeed.nhglib.input.handler.InputProxy;
import io.github.movementspeed.nhglib.input.models.InputContext;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.input.models.impls.system.NhgKeyboardButtonInput;
import io.github.movementspeed.nhglib.input.models.impls.system.NhgTouchInput;
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput;
import io.github.movementspeed.nhglib.input.utils.InputUtil;
import io.github.movementspeed.nhglib.utils.debug.NhgLogger;

public class InputLoader extends AsynchronousAssetLoader<InputProxy, InputLoader.InputProxyParameter> {
    public InputLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    @Override
    public void loadAsync(AssetManager assetManager, String s, FileHandle fileHandle, InputProxyParameter inputProxyParameter) {

    }

    @Override
    public InputProxy loadSync(AssetManager assetManager, String s, FileHandle fileHandle, InputProxyParameter inputProxyParameter) {
        InputProxy inputProxy = new InputProxy();
        JsonValue jsonValue = new JsonReader().parse(fileHandle);
        JsonValue inputContextsJsonArray = jsonValue.get("inputContexts");

        Array<NhgInput> systemInputs = new Array<>();
        Array<NhgInput> virtualInputs = new Array<>();
        Array<InputContext> inputContexts = new Array<>();

        for (JsonValue inputContextJson : inputContextsJsonArray) {
            String inputContextName = inputContextJson.getString("name");
            JsonValue inputsJsonArray = inputContextJson.get("inputs");

            InputContext inputContext = new InputContext(inputContextName);
            inputContexts.add(inputContext);

            for (JsonValue inputJson : inputsJsonArray) {
                NhgInput nhgInput = null;

                String inputName = inputJson.getString("inputName");
                InputType inputType = InputType.fromString(inputJson.getString("inputType"));
                InputMode inputMode = InputMode.fromString(inputJson.getString("inputMode"));

                switch (inputType) {
                    case TOUCH:
                        int pointerNumber = inputJson.getInt("pointerNumber");
                        Array<TouchInputType> touchInputTypes = new Array<>();

                        if (inputJson.has("touchInputTypes")) {
                            String inputTypes = inputJson.getString("touchInputTypes");
                            String[] inputTypesArray = inputTypes.split("\\|");

                            for (String touchInputType : inputTypesArray) {
                                touchInputTypes.add(TouchInputType.fromString(touchInputType));
                            }
                        }

                        if (touchInputTypes.size == 0) {
                            TouchInputType touchInputType = TouchInputType.TAP;
                            touchInputTypes.add(touchInputType);
                        }

                        NhgTouchInput touchInput = new NhgTouchInput(inputName);
                        touchInput.setPointerNumber(pointerNumber);
                        touchInput.setTouchInputTypes(touchInputTypes);

                        nhgInput = touchInput;
                        systemInputs.add(nhgInput);
                        break;

                    case MOUSE_BUTTON:
                        break;

                    case KEYBOARD_BUTTON:
                        String key = inputJson.getString("key").toUpperCase();

                        NhgKeyboardButtonInput keyboardButtonInput = new NhgKeyboardButtonInput(inputName);
                        keyboardButtonInput.setKeyCode(InputUtil.keyCodeFromName(key));
                        nhgInput = keyboardButtonInput;
                        systemInputs.add(nhgInput);
                        break;

                    case VIRTUAL_BUTTON:
                        String actorName = inputJson.getString("actorName");

                        NhgVirtualButtonInput virtualButtonInput = new NhgVirtualButtonInput(inputName);
                        virtualButtonInput.setActorName(actorName);
                        nhgInput = virtualButtonInput;
                        virtualInputs.add(nhgInput);
                        break;
                }

                if (nhgInput != null) {
                    nhgInput.setMode(inputMode);
                    nhgInput.setContext(inputContext);
                } else {
                    NhgLogger.log("InputLoader", "ignored input \"%s\"", inputName);
                }
            }
        }

        inputProxy.build(inputContexts, systemInputs, virtualInputs);
        return inputProxy;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String s, FileHandle fileHandle, InputProxyParameter inputProxyParameter) {
        return null;
    }

    public static class InputProxyParameter extends AssetLoaderParameters<InputProxy> {
    }
}
