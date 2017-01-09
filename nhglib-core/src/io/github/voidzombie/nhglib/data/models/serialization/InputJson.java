package io.github.voidzombie.nhglib.data.models.serialization;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import io.github.voidzombie.nhglib.input.*;
import io.github.voidzombie.nhglib.interfaces.JsonParseable;

/**
 * Created by Fausto Napoli on 08/01/2017.
 */
public class InputJson implements JsonParseable<Array<InputContext>> {
    private Array<InputContext> inputContexts;

    public InputJson() {
        inputContexts = new Array<>();
    }

    @Override
    public void parse(JsonValue jsonValue) {
        JsonValue contextsJson = jsonValue.get("contexts");

        for (JsonValue contextJson : contextsJson) {
            String name = contextJson.getString("name");
            JsonValue inputsJson = contextJson.get("inputs");

            InputContext inputContext = new InputContext(name);

            for (JsonValue inputJson : inputsJson) {
                String inputName = inputJson.getString("name");
                String inputSourceString = inputJson.getString("source");
                String inputTypeString = inputJson.getString("type");
                JsonValue configJson = inputJson.get("config");

                InputSource inputSource = InputSource.fromString(inputSourceString);
                InputType inputType = InputType.fromString(inputTypeString);

                InputConfig inputConfig = new InputConfig();

                try {
                    inputConfig.setKeycode(configJson.getInt("keycode"));
                } catch (IllegalArgumentException e) {}

                try {
                    inputConfig.setMinValue(configJson.getFloat("min"));
                } catch (IllegalArgumentException e) {}

                try {
                    inputConfig.setMaxValue(configJson.getFloat("max"));
                } catch (IllegalArgumentException e) {}

                try {
                    inputConfig.setSensitivity(configJson.getFloat("sensitivity"));
                } catch (IllegalArgumentException e) {}

                try {
                    inputConfig.setInputMode(InputMode.fromString(configJson.getString("inputMode")));
                } catch (IllegalArgumentException e) {}

                NHGInput input = new NHGInput(inputName);
                input.setSource(inputSource);
                input.setType(inputType);
                input.setConfig(inputConfig);

                inputContext.addInput(input);
            }

            inputContexts.add(inputContext);
        }
    }

    @Override
    public Array<InputContext> get() {
        return inputContexts;
    }
}
