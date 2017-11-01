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
import io.github.movementspeed.nhglib.input.handler.InputProxy;
import io.github.movementspeed.nhglib.input.models.InputMode;
import io.github.movementspeed.nhglib.input.models.InputType;
import io.github.movementspeed.nhglib.input.models.base.NhgInput;
import io.github.movementspeed.nhglib.input.models.impls.system.NhgKeyboardButtonInput;
import io.github.movementspeed.nhglib.input.models.impls.system.NhgTouchInput;
import io.github.movementspeed.nhglib.input.models.impls.virtual.NhgVirtualButtonInput;
import io.github.movementspeed.nhglib.input.utils.InputUtil;

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

        for (JsonValue inputContextJson : inputContextsJsonArray) {
            String inputContextName = inputContextJson.getString("name");
            JsonValue inputsJsonArray = inputContextJson.get("inputs");

            for (JsonValue inputJson : inputsJsonArray) {
                InputType inputType = InputType.fromString(inputJson.getString("inputType"));
                String inputName = inputJson.getString("inputName");
                InputMode inputMode = InputMode.fromString(inputJson.getString("inputMode"));

                switch (inputType) {
                    case TOUCH:
                        int pointerNumber = inputJson.getInt("pointerNumber");

                        NhgTouchInput touchInput = new NhgTouchInput(inputName);
                        touchInput.setMode(inputMode);
                        touchInput.setPointerNumber(pointerNumber);
                        systemInputs.add(touchInput);
                        break;

                    case MOUSE_BUTTON:
                        break;

                    case KEYBOARD_BUTTON:
                        String key = inputJson.getString("key").toUpperCase();

                        NhgKeyboardButtonInput keyboardButtonInput = new NhgKeyboardButtonInput(inputName);
                        keyboardButtonInput.setMode(inputMode);
                        keyboardButtonInput.setKeyCode(InputUtil.keyCodeFromName(key));
                        systemInputs.add(keyboardButtonInput);
                        break;

                    case VIRTUAL_BUTTON:
                        String actorName = inputJson.getString("actorName");

                        NhgVirtualButtonInput virtualButtonInput = new NhgVirtualButtonInput(inputName);
                        virtualButtonInput.setMode(inputMode);
                        virtualButtonInput.setActorName(actorName);
                        virtualInputs.add(virtualButtonInput);
                        break;
                }
            }
        }

        inputProxy.build(virtualInputs, systemInputs);
        return inputProxy;
    }

    @Override
    public Array<AssetDescriptor> getDependencies(String s, FileHandle fileHandle, InputProxyParameter inputProxyParameter) {
        return null;
    }

    public static class InputProxyParameter extends AssetLoaderParameters<InputProxy> {
    }
}
